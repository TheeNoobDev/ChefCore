package net.chefcraft.core.configuration;

import com.google.common.collect.Sets;
import net.chefcraft.core.util.ObjectKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;

public class GlobalConfigHandler {

	private final JavaPlugin plugin;
	private final FileConfiguration config;
	private final boolean enabled;
	private final Set<String> enabledConfigKeySet = Sets.newHashSet();
	private String directory;
	
	public GlobalConfigHandler(@Nonnull JavaPlugin plugin, @Nonnull FileConfiguration config) {
		this.plugin = plugin;
		this.config = config;
		
		this.enabled = config.getBoolean("global_configurations.enabled");

		if (this.enabled) {
			String rawDir = config.getString("global_configurations.directory");
			this.directory = rawDir + File.separator + plugin.getName();
			
			if (rawDir == null || rawDir.isEmpty() || !rawDir.contains(File.separator)) {
				this.directory = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + File.separator + "game_core_configs" + File.separator + plugin.getName();
				plugin.getLogger().log(Level.SEVERE, "Check your global config directory, it is EMPTY and the path you specified could not be created. The system is trying to find your desktop automatically.");
				plugin.getLogger().log(Level.SEVERE, "Your desktop location was found and the file was created: " + this.directory);
			}
			
			try {
				File dir = new File(this.directory);
				
				if (!dir.exists()) {
					dir.mkdirs();
				}
			} catch (Exception x) {
				plugin.getLogger().log(Level.SEVERE, "Check your global config directory, it is incorrect and the path you specified could not be created. The system is trying to find your desktop automatically.");
				
				this.directory = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + File.separator + "game_core_configs" + File.separator + plugin.getName();
				try {
					File dir = new File(this.directory);
					
					if (!dir.exists()) {
						dir.mkdirs();
					}
					plugin.getLogger().log(Level.SEVERE, "Your desktop location was found and the file was created: " + this.directory);
				} catch (Exception e) {
					e.printStackTrace();
					plugin.getLogger().log(Level.SEVERE, "Global Config system down :(, try to disable this option from config");
				}
			}
		} else {
			this.directory = plugin.getDataFolder().getAbsolutePath();
		}
		
		ConfigurationSection section = config.getConfigurationSection("global_configurations.enable_for");
		
		for (String key : section.getKeys(false)) {
			
			if (section.getBoolean(key)) {
				this.enabledConfigKeySet.add(key);
			}	
		}
	}
	
	public File createFileDirectory(ObjectKey objectKey, String dir) {
		File file = new File((this.isEnabledFor(objectKey) ? this.directory : this.plugin.getDataFolder()) + File.separator + dir);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}
	
	public YamlFile create(ObjectKey configKey, String fileName) {
		try {
			return YamlFile.create((this.isEnabledFor(configKey) ? this.directory : this.plugin.getDataFolder()) + File.separator + fileName);
		} catch (IOException x) {
			x.printStackTrace();
			plugin.getLogger().log(Level.SEVERE, "Failed to load '" + fileName + "' file!"); 
			return null;
		}
	}
	
	public YamlFile copyResource(ObjectKey configKey, String resourceName) {
		try {
			return YamlFile.copyResource(this.plugin.getResource(resourceName), (this.isEnabledFor(configKey) ? this.directory : this.plugin.getDataFolder()) + File.separator + resourceName);
		} catch (IOException x) {
			x.printStackTrace();
			plugin.getLogger().log(Level.SEVERE, "Failed to copy '" + resourceName + "' resource!"); 
			return null;
		}
	}

	public JavaPlugin getPlugin() {
		return this.plugin;
	}

	public FileConfiguration getConfig() {
		return this.config;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public String getDirectory() {
		return this.directory;
	}
	
	public boolean isEnabledFor(ObjectKey objectKey) {
		return this.enabled && this.enabledConfigKeySet.contains(objectKey.getKey());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
