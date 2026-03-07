package net.chefcraft.core;

import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.chefcraft.core.collect.ImmutableMap;
import net.chefcraft.core.configuration.BaseConfigurationFile;
import net.chefcraft.core.configuration.CoreConfigKey;
import net.chefcraft.core.configuration.GlobalConfigHandler;
import net.chefcraft.core.configuration.YamlFile;
import net.chefcraft.core.database.AbstractPlayerDatabase;
import net.chefcraft.core.language.Language;
import net.chefcraft.core.language.MessageCompiler;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslationSource;
import net.chefcraft.core.library.LibraryManager;
import net.chefcraft.core.network.ConnectionManager;
import net.chefcraft.core.party.AbstractParty;
import net.chefcraft.core.party.BackendPartyDataListener;
import net.chefcraft.core.party.CorePartyCommands;
import net.chefcraft.core.server.Minigame;
import net.chefcraft.core.server.ServerVersion;
import net.chefcraft.core.sound.AbstractSoundManager;
import net.chefcraft.core.util.JHelper;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.core.util.ServerTickClock;
import net.chefcraft.reflection.AbstractReflections;
import net.chefcraft.reflection.base.language.BukkitMessageCompiler;
import net.chefcraft.service.luckperms.LuckPermsUtils;
import net.chefcraft.service.nick.GameProfilePool;
import net.chefcraft.service.nick.NickCommand;
import net.chefcraft.service.npc.NPCTracker;
import net.chefcraft.world.cage.CageBuilder;
import net.chefcraft.world.cage.CageBuilderCommandArg;
import net.chefcraft.world.cage.CageUtils;
import net.chefcraft.world.command.CommandManager;
import net.chefcraft.world.command.CoreCommandExecutor;
import net.chefcraft.world.command.CoreTabCompleter;
import net.chefcraft.world.command.argument.*;
import net.chefcraft.world.databridge.DataBridgeDatabase;
import net.chefcraft.world.databridge.ServerDataBridge;
import net.chefcraft.world.level.LevelCommandArg;
import net.chefcraft.world.listener.*;
import net.chefcraft.world.loot.EventSymbols;
import net.chefcraft.world.loot.KillMessages;
import net.chefcraft.world.menu.MenuBuilderCommandArg;
import net.chefcraft.world.menu.MenuController;
import net.chefcraft.world.menu.MenuOpenCommandArg;
import net.chefcraft.world.menu.game.GameMenuController;
import net.chefcraft.world.menu.game.GameMenuOpenCommandArg;
import net.chefcraft.world.player.CorePlayer;
import net.chefcraft.world.util.BukkitUtils;
import net.chefcraft.world.util.MethodProvider;
import net.chefcraft.world.util.PapiHook;
import net.chefcraft.world.util.ProxyUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ChefCore extends JavaPlugin implements PluginMessageListener, PluginInstance {

	private static AbstractPlayerDatabase playerDatabase;
	private static AbstractReflections reflector;
	private static AbstractSoundManager soundManager;
	private static GlobalConfigHandler globalConfigHandler;
	private static ConnectionManager connectionManager;
	private static ChefCore instance;
	private static YamlFile configYamlFile;
	private static YamlFile symbolsYamlFile;
	private static YamlFile nameTagsYamlFile;
	private static YamlFile levelManagerYamlFile;
	
	private static final Map<String, Language> LANGUAGE_REGISTRY = new HashMap<>();
	private static final Map<UUID, CorePlayer> CORE_PLAYER_REGISTRY = new HashMap<>();
	private static final Map<Language, String[]> TIME_UNIT_TRANSLATION_REGISTRY = new ImmutableMap<>();
	
	// Functions
	private static final Function<CorePlayer, String> CORE_PLAYER_NAME_FUNC = corePlayer -> corePlayer.getPlayer().getName();
	private static final Function<Language, String> LANGUAGE_TO_LOCALE_FUNC = Language::getLocale;
	
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final String CHANNEL_BUNGEE = "BungeeCord";
	private static String bungeeServerName = null;
	
	@Override
	public void onEnable() {
		instance = this;
		reflector = LibraryManager.newServerReflector();
		if (reflector != null) {
			this.createConfigurations();
			this.createLanguageBase();
			this.registerListeners();
			this.registerCommands();
			this.loadPlayerDatabase();
			
			DataBridgeDatabase.loadDatabase();
			ServerDataBridge.init();
			MenuController.loadMenus();
			GameMenuController.loadMenus();
			GameProfilePool.bootstrap();
			KillMessages.bootstrap();
			CageUtils.loadCages();
			LuckPermsUtils.subscribeListeners();
			PapiHook.papiHookRegistry();
			EventSymbols.setInstance(new EventSymbols(symbolsYamlFile));
			
			PlayerJoinOrQuitListener.onPluginEnabled();
			ServerTickClock.start(false);
			
			dataBridgeRegistry();
			
			super.getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL_BUNGEE);
			super.getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL_BUNGEE, this);
			
		}
	}
	
	@Override
	public void onDisable() {
		NPCTracker.removeEntries();
		super.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
		super.getServer().getMessenger().unregisterIncomingPluginChannel(this);
		
		if (this.getConfig().getBoolean("data_bridge.enable_for_EggWars")) {
			ServerDataBridge.deleteAll(Minigame.EGGWARS, getServerNameFromConfig());
		}
		
		playerDatabase.disconnect();
		DataBridgeDatabase.getInstance().disconnect();
		
		if (connectionManager != null) {
			connectionManager.disconnect();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public @NotNull ChefCore instance() {
		return instance;
	}
	
	public static void dataBridgeRegistry() {
		FileConfiguration config = instance.getConfig();
		
		if (config.getBoolean("data_bridge.enable_for_EggWars")) {
			
			Bukkit.getScheduler().runTaskLater(instance, ()-> {
				ServerDataBridge.deleteAll(Minigame.EGGWARS, getServerNameFromConfig());
				GameMenuController.startUpdateTask();
			}, 20L);
		}
		/*
		PacketFlow flow = config.getBoolean("data_bridge.socket.is_game_server") ? PacketFlow.GAME : PacketFlow.LOBBY;
		
		connectionManager = new ConnectionManager(flow, config.getString("data_bridge.socket.host"), config.getInt("data_bridge.socket.port"));
		
		Bukkit.getScheduler().runTaskLater(instance, ()-> {
			connectionManager.connect();
		}, 20L);*/
	}
	
	private void registerListeners() {
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(new PlayerJoinOrQuitListener(), instance);
		pm.registerEvents(new MenuCloseListener(), instance);
		pm.registerEvents(new MenuClickListener(), instance);
		pm.registerEvents(new CageBuilder.BuilderListener(), instance);
		pm.registerEvents(new ConfirmMenuClickListener(), instance);
		pm.registerEvents(new CorePacketReadListener(), instance);
		pm.registerEvents(new VanillaAdvancementListener(disableVanillaAdvancements()), instance);
		
		if (PlatformProvider.hasKyoriAdventure()) {
			pm.registerEvents(new PaperAsyncChatListener(), instance);
		} else {
			pm.registerEvents(new BukkitAsyncPlayerChatListener(), instance);
		}
	}
	
	private void registerCommands() {
		super.getCommand("chefcore").setExecutor(new CoreCommandExecutor());
		super.getCommand("chefcore").setTabCompleter(new CoreTabCompleter());
		CommandManager.registerCommandArg(new MenuOpenCommandArg());
		CommandManager.registerCommandArg(new SetLangCommandArg());
		CommandManager.registerCommandArg(new ReloadCommandArg());
		CommandManager.registerCommandArg(new CageBuilderCommandArg());
		CommandManager.registerCommandArg(new LevelCommandArg());
		CommandManager.registerCommandArg(new MenuBuilderCommandArg());
		CommandManager.registerCommandArg(new GameMenuOpenCommandArg());
		CommandManager.registerCommandArg(new SelectorMenuCommandArg());
		BukkitUtils.registerCommand("nick", new NickCommand());
		BukkitUtils.registerCommand("ping", new PingCommand());
		BukkitUtils.registerCommand("privategame", new PrivateGameCommand());
		BukkitUtils.registerCommand("rejoin", new RejoinCommand());
		
		if (disableProxyPartyListener() || !ProxyUtils.isBungeeModeEnabled()) {
			BukkitUtils.registerCommand("party", new CorePartyCommands());
		}
	}
	
	private void loadPlayerDatabase() {
		playerDatabase = new AbstractPlayerDatabase() {
			@Override
			public String getUsername() {
				return instance.getConfig().getString("database.username");
			}

			@Override
			public String getPassword() {
				return instance.getConfig().getString("database.password");
			}

			@Override
			public String getTableName() {
				return instance.getConfig().getString("database.tableName");
			}
			
			@Override
			public String getDriverUrl() {
				return instance.getConfig().getString("database.driver_url");
			}

			@Override
			public PluginInstance getPlugin() {
				return instance;
			}
		};
		
		playerDatabase.createTable("(PlayerName VARCHAR(16), UUID VARCHAR(36), Language VARCHAR(5), LastGame TINYTEXT, PlayerData LONGTEXT, GameUtilsData LONGTEXT, SettingsData LONGTEXT);");
				
	}
	
	private void createConfigurations() {
		try {
			configYamlFile = YamlFile.copyResource(super.getResource("config.yml"), super.getDataFolder() + File.separator + "config.yml");
			
		} catch (IOException e) {
			e.printStackTrace();
			log(Level.SEVERE, "Failed to load 'config.yml' file!"); 
		}
		
		if (getServerNameFromConfig().isEmpty()) {
			configYamlFile.getConfig().set("server_name", UUID.randomUUID().toString());
			configYamlFile.save();
		}
		
		globalConfigHandler = new GlobalConfigHandler(this, configYamlFile.getConfig());

		soundManager = new AbstractSoundManager(this, globalConfigHandler.copyResource(CoreConfigKey.SOUNDS, getSoundYamlFileNameByVersion(ServerVersion.current()) + ".yml")) { };
		
		symbolsYamlFile = globalConfigHandler.copyResource(CoreConfigKey.SYMBOLS, "symbols.yml");
		
		nameTagsYamlFile = globalConfigHandler.copyResource(CoreConfigKey.NAME_TAGS, "name_tags.yml");
		
		levelManagerYamlFile = globalConfigHandler.copyResource(CoreConfigKey.LEVEL_MANAGER, "level_manager.yml");
	}
	
	@Override
	public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
		if (channel.equalsIgnoreCase(CHANNEL_BUNGEE)) {
			ByteArrayDataInput in = ByteStreams.newDataInput(message);
			String subchannel = in.readUTF();
			
			if (subchannel.equalsIgnoreCase("GetServer")) {
				bungeeServerName = in.readUTF();
				
			} else if (subchannel.equalsIgnoreCase(AbstractParty.BACKEND_SUB_CHANNEL_NAME)) {
				BackendPartyDataListener.onPartyDataReceived(channel, player, in);
			}
		}
	}
	
	@Override
	public @NotNull FileConfiguration getConfig() {
		return configYamlFile.getConfig();
	}
	
	@Override
	public void sendPlainMessage(@NotNull String message) {
		MethodProvider.SEND_MESSAGE_TO_CONSOLE.accept(this.getServer().getConsoleSender(), MessageHolder.text("<white>[<green>ChefCore<white>] " + message));
	}
	
	@Override
	public void sendKeyedMessage(@NotNull String key, @org.jetbrains.annotations.Nullable Placeholder placeholder) {
		BukkitMessageCompiler.sendMessage(instance, getServer().getConsoleSender(), key, placeholder);
	}
	
	@Override
	public @NotNull Language getDefaultLanguage() {
		Language lang = LANGUAGE_REGISTRY.get(instance.getConfig().getString("language.default_lang").toLowerCase());
		if (lang == null) {
			lang = LANGUAGE_REGISTRY.get("en_us");
			this.sendPlainMessage("&cThe method you call returns null, check the config! Returning plugins default (en_us).");
		}
		return lang;
	}
	
	@Override
	public @org.jetbrains.annotations.Nullable TranslationSource getTranslationSourceByUniqueId(UUID uniqueId) {
		return getCorePlayerByUniqueId(uniqueId);
	}
	
	public static boolean disableInsecureChatMessagePopup() {
		return instance.getConfig().getBoolean("disable_insecure_chat_message_popup");
	}

	public static ChefCore getInstance() {
		return instance;
	}
	
	public static AbstractPlayerDatabase getPlayerDatabase() {
		return playerDatabase;
	}
	
	public static AbstractReflections getReflector() {
		return reflector;
	}
	
	public static AbstractSoundManager getSoundManager() {
		return soundManager;
	}
	
	public static ConnectionManager getConnectionManager() {
		return connectionManager;
	}
	
	public static GlobalConfigHandler getGlobalConfigHandler() {
		return globalConfigHandler;
	}
	
	public static YamlFile getConfigurationYamlFile() {
		return configYamlFile;
	}
	
	public static YamlFile getNameTagsYamlFile() {
		return nameTagsYamlFile;
	}
	
	public static YamlFile getSymbolsYamlFile() {
		return symbolsYamlFile;
	}
	
	public static YamlFile getLevelManagerYamlFile() {
		return levelManagerYamlFile;
	}
	
	public static String getServerNameFromBungee() {
		return bungeeServerName != null ? bungeeServerName : getServerNameFromConfig();
	}
	
	public static String getServerNameFromConfig() {
		return configYamlFile.getConfig().getString("server_name");
	}
	
	public static @NotNull String getSystemDate() {
		return DATE_FORMATTER.format(LocalDateTime.now());
	}
	
	public static boolean disableVanillaAdvancements() {
		return configYamlFile.getConfig().getBoolean("disable_vanilla_advancements");
	}
	
	public static boolean disableProxyPartyListener() {
		return configYamlFile.getConfig().getBoolean("disable_proxy_party_listener");
	}
	
	public static @NotNull String getSoundYamlFileNameByVersion(@NotNull ServerVersion version) {
		if (version.isHigherThan(ServerVersion.v1_12_2)) {
			return "sounds_1_13";
		} else if (version.isHigherThan(ServerVersion.v1_8_8)) {
			return "sounds_1_9";
		} else {
			return "sounds_1_8";
		}
	}
	
	public static @NotNull @Unmodifiable List<Language> getLanguages() {
		return ImmutableList.copyOf(LANGUAGE_REGISTRY.values());
	}
	
	public static @NotNull @Unmodifiable List<CorePlayer> getCorePlayers() {
		return ImmutableList.copyOf(CORE_PLAYER_REGISTRY.values());
	}
	
	public static @NotNull List<String> getCorePlayersNameList() {
		return JHelper.mapCollectionToList(CORE_PLAYER_REGISTRY.values(), CORE_PLAYER_NAME_FUNC);
	}

	public static @NotNull List<String> getLanguageLocales() {
		return JHelper.mapCollectionToList(LANGUAGE_REGISTRY.values(), LANGUAGE_TO_LOCALE_FUNC);
	}
	
	public static Language getLanguageByLocale(String locale) {
		return LANGUAGE_REGISTRY.get(locale);
	}

	public static @NotNull CorePlayer getCorePlayerByPlayer(@NotNull Player player) {
		return CORE_PLAYER_REGISTRY.get(player.getUniqueId());
	}
	
	public static CorePlayer getCorePlayerByUniqueId(UUID uuid) {
		return CORE_PLAYER_REGISTRY.get(uuid);
	}
	
	//For SimpleMath class
	public static String[] getTimeUnitValueByLanguage(Language language) {
		return TIME_UNIT_TRANSLATION_REGISTRY.get(language);
	}
	
	public static void registerLanguage(Language language) {
		LANGUAGE_REGISTRY.put(language.getLocale(), language);
	}
	
	public static void registerCorePlayer(CorePlayer corePlayer) {
		CORE_PLAYER_REGISTRY.put(corePlayer.getPlayer().getUniqueId(), corePlayer);
	}
	
	public static void unregisterCorePlayer(@NotNull Player player) {
		CORE_PLAYER_REGISTRY.remove(player.getUniqueId());
	}
	
	public static void unregisterCorePlayer(UUID uuid) {
		CORE_PLAYER_REGISTRY.remove(uuid);
	}
	
	public static void loadLanguages(@Nonnull PluginInstance plugin, @Nullable File directory) {
		
		if (directory == null) {
			directory = new File(plugin.getDataFolder() + File.separator + "languages");
		}
		
		for (Language language : LANGUAGE_REGISTRY.values()) {
			String locale = language.getLocale();
			String langCfg = locale + ".yml";
			
			YamlFile langFile = null;
			
			try {
				
				InputStream in = plugin.getResource("languages/" + langCfg);
				
				//Double check (without directory)
				in = in == null ? plugin.getResource(langCfg) : in;
				
				if (in != null) {
					langFile = YamlFile.copyResource(in, directory.getAbsolutePath() + File.separator + langCfg);
				} else {
					langFile = YamlFile.create(directory.getAbsolutePath() + File.separator + langCfg);
					log(Level.SEVERE, "An error occured creating language file: 'languages/" + langCfg + "' not found in '" + plugin.getName() + "' plugin, creating an empty language file!");
				}
			} catch (Exception x) {
				x.printStackTrace();
				log(Level.SEVERE, "An error occured creating '" + locale + "' language file!");
				
				try {
					langFile = YamlFile.create(directory.getAbsolutePath() + File.separator + "languages" + File.separator + locale + ".yml");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (langFile != null) {
				if (!langFile.getConfig().isSet("prefix")) {
					langFile.getConfig().set("prefix", "");
					langFile.save();
				}
			} else {
				throw new NullPointerException("Language file is null! [Locale: " + locale + ", Plugin: " + plugin.getName() + "]");
			}
			language.addLanguageFile(plugin, langFile);
			
		}
		instance.sendPlainMessage("All languages loaded for &a" + plugin.getName());
	}
	
	public static void log(Level level, String message) {
		instance.getLogger().log(level, message);
	}
	
	public static void log(Level level, String message, Throwable throwable) {
		instance.getLogger().log(level, message, throwable);
	}
	
	public static void reloadLanguageConfigs() throws IOException, InvalidConfigurationException {
		for (Language language : LANGUAGE_REGISTRY.values()) {
			for (BaseConfigurationFile file : language.getYamlFiles()) {
				file.reload();
			}
		}
	}
	
	private void createLanguageBase() {
		List<String> langList = instance.getConfig().getStringList("language.list");
		if (langList.isEmpty()) {
			
			registerLanguage(new Language("English", "en_us", "United States"));
			
		} else {
			for (String text : langList) {
				
				String[] arr = text.split(", ");
				registerLanguage(new Language(arr[0], arr[1], arr[2]));
			}
		}
		
		// Let's load languages for this plugin
		loadLanguages(instance, globalConfigHandler.createFileDirectory(CoreConfigKey.LANGUAGES, "languages"));
		
		// For SimpleMath class -> start
		for (Language language : LANGUAGE_REGISTRY.values()) {
			String[] vals = new String[9];
			vals[0] = MessageCompiler.getMessage(instance, language, "timeTranslate.and", null).asString(false);
			vals[1] = MessageCompiler.getMessage(instance, language, "timeTranslate.second", null).asString(false);
			vals[2] = MessageCompiler.getMessage(instance, language, "timeTranslate.minute", null).asString(false);
			vals[3] = MessageCompiler.getMessage(instance, language, "timeTranslate.hour", null).asString(false);
			vals[4] = MessageCompiler.getMessage(instance, language, "timeTranslate.day", null).asString(false);
			vals[5] = MessageCompiler.getMessage(instance, language, "timeTranslate.seconds", null).asString(false);
			vals[6] = MessageCompiler.getMessage(instance, language, "timeTranslate.minutes", null).asString(false);
			vals[7] = MessageCompiler.getMessage(instance, language, "timeTranslate.hours", null).asString(false);
			vals[8] = MessageCompiler.getMessage(instance, language, "timeTranslate.days", null).asString(false);
			TIME_UNIT_TRANSLATION_REGISTRY.put(language, vals);
		}
		// For SimpleMath class -> end
	}

	@Override
	public @NotNull String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull File getDataFolder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull InputStream getResource(@NotNull String resource) {
		// TODO Auto-generated method stub
		return null;
	}
}
