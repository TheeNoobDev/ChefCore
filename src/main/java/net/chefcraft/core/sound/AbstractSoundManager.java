package net.chefcraft.core.sound;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.configuration.YamlFile;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public abstract class AbstractSoundManager {

	private final JavaPlugin plugin;
	private final YamlFile yamlFile;
	private final Map<String, SoundEntry> soundRegistryMap = new HashMap<>();
	
	public AbstractSoundManager(@Nonnull JavaPlugin plugin, @Nonnull YamlFile yamlFile) {
		this.plugin = plugin;
		this.yamlFile = yamlFile;
		
		FileConfiguration config = yamlFile.getConfig();
		
		for (String key : config.getKeys(true)) {
			if (!config.isConfigurationSection(key)) {
				try {
					String[] soundData = config.getString(key).split(";");
					
					SimpleSoundEntry entry = new SimpleSoundEntry(key, 
							ChefCore.getReflector().getSoundByName(soundData[0]),
							Float.valueOf(soundData[1]), 
							Float.valueOf(soundData[2]));
					
					this.soundRegistryMap.put(key, entry);
				} catch (Exception x) {
					this.plugin.getLogger().log(Level.SEVERE, "SoundManager: An error occurred registering the sound! Key: " + key + ", Exception Message: " + x.getMessage());
				}
			}
		}
	}
	
	public JavaPlugin getPlugin() {
		return plugin;
	}

	public YamlFile getYamlFile() {
		return yamlFile;
	}
	
	public void createEntry(@Nonnull SoundEntry entry) {
		this.soundRegistryMap.put(entry.getKey(), entry);
	}
	
	@Nullable
	public SoundEntry getEntry(@Nonnull String key) {
		return this.soundRegistryMap.get(key);
	}
	
	public boolean removeEntry(@Nonnull String key) {
		return this.soundRegistryMap.remove(key) != null;
	}
	
	public void playSound(@Nonnull Player player, @Nonnull String key) {
		SoundEntry soundEntry = this.soundRegistryMap.get(key);
		if (soundEntry != null) {
			soundEntry.playSound(player);
		} else {
			this.logUndefinedSound(key);
		}
	}
	
	public void playSound(@Nonnull Location location, @Nonnull String key) {
		SoundEntry soundEntry = this.soundRegistryMap.get(key);
		if (soundEntry != null) {
			soundEntry.playSound(location);
		} else {
			this.logUndefinedSound(key);
		}
	}
	
	public void playSound(@Nonnull Iterable<Player> players, @Nonnull String key) {
		SoundEntry soundEntry = this.soundRegistryMap.get(key);
		if (soundEntry != null) {
			soundEntry.playSound(players);
		} else {
			this.logUndefinedSound(key);
		}
	}
	
	private void logUndefinedSound(String key) {
		this.plugin.getLogger().log(Level.WARNING, "SoundManager: An error occurred playing the sound! Key: " + key);
	}
}
