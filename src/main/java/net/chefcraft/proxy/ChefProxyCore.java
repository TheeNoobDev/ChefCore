package net.chefcraft.proxy;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.chefcraft.core.PluginInstance;
import net.chefcraft.core.component.ComponentSupport;
import net.chefcraft.core.configuration.CoreJsonFile;
import net.chefcraft.core.configuration.CorePropertiesFile;
import net.chefcraft.core.database.AbstractPlayerDatabase;
import net.chefcraft.core.language.Language;
import net.chefcraft.core.language.TranslationSource;
import net.chefcraft.core.library.LibraryManager;
import net.chefcraft.core.party.ProxyPartyCommands;
import net.chefcraft.core.util.FileUtils;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.proxy.command.CommonProxyCommands;
import net.chefcraft.proxy.command.LobbyCommand;
import net.chefcraft.proxy.listener.ProxyChatListener;
import net.chefcraft.proxy.listener.ServerConnectedListener;
import net.chefcraft.proxy.motd.ServerMotdManager;
import net.chefcraft.proxy.player.CoreProxyPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Plugin(id = "chefproxycore", name = "ChefProxyCore", version = "0.0.1-SNAPSHOT",
		url = "https://chefcraft.net", description = "ChefCraft's Proxy core plugin!", authors = {"ChefRido"})
public class ChefProxyCore implements PluginInstance {

	public static final ChannelIdentifier BUNGEE_CHANNEL = MinecraftChannelIdentifier.create("bungeecord", "main");
	
	private static ChefProxyCore instance;
	private static PluginContainer velocityInstance; 
	private final ProxyServer server;
    private final Logger logger;
    private final Path pluginPath;
    private static File pluginDirectory;
    
    private static AbstractPlayerDatabase playerDatabase;
    private static Language defaultLanguage;
	private static CoreJsonFile jsonFile;
	public static LobbyCommand lobbyCommand = null;
	
	private static final Map<String, Language> LANGUAGE_REGISTRY = new HashMap<>();
	private static final Map<UUID, CoreProxyPlayer> PLAYER_REGISTRY = new HashMap<>();

    @Inject
    public ChefProxyCore(ProxyServer server, Logger logger, @DataDirectory Path pluginPath) {
    	instance = this;
        this.server = server;
        this.logger = logger;
        this.pluginPath = pluginPath;
        pluginDirectory = pluginPath.toFile();
    }
    
    @Override
	public @NotNull String getName() {
		return velocityInstance.getDescription().getId();
	}

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
    	velocityInstance = this.server.getPluginManager().getPlugin("chefproxycore").get();
    	this.loadConfigurations();
    	this.createLanguageBase();
    	this.loadSQLDrivers();
    	this.loadPlayerDatabase();
    	ServerMotdManager.load();
    	lobbyCommand = new LobbyCommand(jsonFile);
    	new CommonProxyCommands();
    	new ProxyPartyCommands();
    	
    	this.server.getEventManager().register(this, LoginEvent.class, handler -> {
    		PLAYER_REGISTRY.put(handler.getPlayer().getUniqueId(), new CoreProxyPlayer(handler.getPlayer()));
    	});
    	
    	this.server.getEventManager().register(this, DisconnectEvent.class, handler -> {
    		CoreProxyPlayer proxyPlayer = PLAYER_REGISTRY.remove(handler.getPlayer().getUniqueId());
    		if (proxyPlayer != null && proxyPlayer.isInParty()) {
    			proxyPlayer.getPartyManager().getParty().leave(proxyPlayer, true, false);
    		}
    	});
  
    	this.server.getEventManager().register(this, new ProxyChatListener());  
    	this.server.getEventManager().register(this, new ServerConnectedListener());	
    	this.server.getChannelRegistrar().register(BUNGEE_CHANNEL);
    }
    
    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
    	if (playerDatabase != null) {
    		playerDatabase.disconnect();
    	}
    }
    
    @SuppressWarnings("unchecked")
	@Override
	public @NotNull ChefProxyCore instance() {
		return instance;
	}
    
    private void loadSQLDrivers() {
		try {
			URL url = new URL("https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.46.0.0/sqlite-jdbc-3.46.0.0.jar");
			LibraryManager.loadSQLDriverFromExternalSources(instance, this.getClass().getClassLoader(), url, "org.sqlite.JDBC", "sqlite-jdbc.jar");
			
			url = new URL("https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.31/mysql-connector-j-8.0.31.jar");
			LibraryManager.loadSQLDriverFromExternalSources(instance, this.getClass().getClassLoader(), url, "com.mysql.cj.jdbc.Driver", "mysql-connector.jar");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    }
    
    private void loadPlayerDatabase() {
    	
    	final JsonObject dbSection = jsonFile.getConfig().getAsJsonObject().get("database").getAsJsonObject();
    	
		playerDatabase = new AbstractPlayerDatabase() {
			@Override
			public String getUsername() {
				return dbSection.get("username").getAsString();
			}

			@Override
			public String getPassword() {
				return dbSection.get("password").getAsString();
			}

			@Override
			public String getTableName() {
				return dbSection.get("tableName").getAsString();
			}
			
			@Override
			public String getDriverUrl() {
				return dbSection.get("driver_url").getAsString();
			}

			@Override
			public PluginInstance getPlugin() {
				return instance;
			}
		};
		
		playerDatabase.createTable("(PlayerName VARCHAR(16), UUID VARCHAR(36), Language VARCHAR(5), LastGame TINYTEXT, PlayerData LONGTEXT, GameUtilsData LONGTEXT, SettingsData LONGTEXT);");
				
	}
    
    public ProxyServer getServer() {
		return server;
	}

    @Override
	public @NotNull Logger getLogger() {
		return logger;
	}
    
    @Override
	public @NotNull File getDataFolder() {
		return pluginDirectory;
	}

	@Override
	public @NotNull InputStream getResource(@NotNull String resource) {
		return instance.getClass().getClassLoader().getResourceAsStream(resource);
	}
	
	@Override
	public void sendPlainMessage(@NotNull String message) {
		server.sendMessage(ComponentSupport.miniMessageSupport().deserialize(message, null));
	}

	@Override
	public void sendKeyedMessage(@NotNull String key, @Nullable Placeholder placeholder) {

	}
	
	@Override
	public @NotNull Language getDefaultLanguage() {
		return defaultLanguage;
	}
	
	@Override
	public @Nullable TranslationSource getTranslationSourceByUniqueId(UUID uniqueId) {
		return getProxyPlayerByUniqueId(uniqueId);
	}
	
	public Path getPluginPath() {
		return this.pluginPath;
	}
	
	private void loadConfigurations() {
    	try {
			jsonFile = CoreJsonFile.copyResource(getResourceAsStream("config.json"), pluginDirectory.getAbsolutePath() + File.separator + "config.json");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void createLanguageBase() {
    	JsonObject obj = jsonFile.getConfig().getAsJsonObject().getAsJsonObject("language");
    	JsonArray array = obj.getAsJsonArray("list");
    	
    	if (array.isEmpty()) {
    		defaultLanguage = new Language("English", "en_us", "United States");
    		LANGUAGE_REGISTRY.put(defaultLanguage.getLocale(), defaultLanguage);
    	} else {
    		String defLang = obj.get("defaultLanguage").getAsString();
        	
        	array.forEach(element -> {
        		JsonObject langObj = element.getAsJsonObject();
        		Language lang = new Language(langObj.get("name").getAsString(), langObj.get("locale").getAsString(), langObj.get("region").getAsString());
        		
        		if (lang.getLocale().equalsIgnoreCase(defLang)) {
        			defaultLanguage = lang;
        		}
        		
        		LANGUAGE_REGISTRY.put(lang.getLocale(), lang);
        	});
        	
        	if (defaultLanguage == null) {
        		for (Language lang : LANGUAGE_REGISTRY.values()) {
        			defaultLanguage = lang;
        			break;
        		}
        	}
    	}
    	
    	loadLanguages(this, this.pluginPath);
    }
    
    public static void log(Level level, String text) {
    	instance.logger.log(level, text);
    }
    
    public static void log(Level level, String text, Exception x) {
    	instance.logger.log(level, text + "Exception message: " + x.getMessage() + ", Exception cause" + x.getCause());
    }
    
    public static AbstractPlayerDatabase getPlayerDatabase() {
    	return playerDatabase;
    }
    
    public static void loadLanguages(@NotNull PluginInstance plugin, @NotNull Path pluginDataFolder) {
    	File outputDir = new File(pluginDataFolder.toString(), "languages");
    	
    	if (!outputDir.exists()) {
    		outputDir.mkdirs();
    	}
    	
		
		for (Language language : LANGUAGE_REGISTRY.values()) {
			String locale = language.getLocale();
			String langCfg = locale + ".properties";
			
			CorePropertiesFile properties = null;
			File outLangFile = null;
			
			try {
				
				InputStream in = plugin.getResource("languages/" + langCfg);
				
				//Double check (without directory)
				in = in == null ? plugin.getResource(langCfg) : in;
				outLangFile = new File(outputDir.getAbsolutePath(), langCfg);
				
				if (in != null) {
					FileUtils.copyInputStreamToFile(in, outLangFile);
				} else {
					FileUtils.createDirectory(outLangFile);
					log(Level.SEVERE, "An error occured creating language file: 'languages/" + langCfg + "' not found in '" + plugin.getName() + "' plugin, creating an empty language file!");
				}
				
				if (outLangFile != null && outLangFile.exists()) {
					properties = CorePropertiesFile.create(outLangFile.getAbsolutePath());
				} else {
					log(Level.SEVERE, "An error occured creating '" + locale + "' language file! (Output file not found)");
				}
			} catch (Exception x) {
				x.printStackTrace();
				log(Level.SEVERE, "An error occured creating '" + locale + "' language file!");
			}
			
			if (outLangFile != null) {
				if (!properties.getConfig().containsKey("prefix")) {
					properties.getConfig().setProperty("prefix", "");
					try {
						properties.save();
					} catch (Exception e) {
						log(Level.SEVERE, "An error occured saving '" + locale + "' language file!");
						e.printStackTrace();
					}
				}
			}
			
			language.addLanguageFile(plugin, properties);
		}
		
		instance.sendPlainMessage("<white>All languages loaded for <green>" + instance.getName());
	}
	
	public static InputStream getResourceAsStream(String resource) {
		return instance.getClass().getClassLoader().getResourceAsStream(resource);
	}
	
	@Nullable
	public static CoreProxyPlayer getProxyPlayerByPlayer(Player player) {
		return PLAYER_REGISTRY.get(player.getUniqueId());
	}
	
	@Nullable
	public static CoreProxyPlayer getProxyPlayerByUniqueId(UUID uuid) {
		return PLAYER_REGISTRY.get(uuid);
	}
	
	public static Collection<CoreProxyPlayer> getProxyPlayers() {
		return PLAYER_REGISTRY.values();
	}
	
	public static CoreJsonFile getConfigJsonFile() {
		return jsonFile;
	}
	
	public static File getPluginDirectory() {
		return pluginDirectory;
	}
    
    public static PluginContainer getVelocityInstance() {
    	return velocityInstance;
    }
    
    public static ChefProxyCore getInstance() {
		return instance;
	}
}
