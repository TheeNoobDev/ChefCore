package net.chefcraft.core.configuration;

import net.chefcraft.core.util.FileUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

public class YamlFile extends BaseConfigurationFile {
	
	private FileConfiguration configuration;
	
	protected YamlFile(@NotNull InputStream input, @NotNull String outputPath) throws IOException {
		super(input, outputPath);
		this.loadConfiguration();
	}
	
	protected YamlFile(@NotNull String outputPath) throws IOException {
		super(outputPath);
		this.loadConfiguration();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public FileConfiguration getConfig() {
		return configuration;
	}
	
	@Override
	protected void loadConfiguration() {
		configuration = new YamlConfiguration();
		try {
			configuration.load(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void reload() {
		try {
			configuration.load(file);
			configuration.save(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void save() {
		try {
			configuration.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void delete() {
		try {
			FileUtils.deleteFile(file);
			configuration = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static YamlFile copyResource(InputStream input, String outputPath) throws IOException {
		return new YamlFile(input, outputPath);
	}
	
	public static YamlFile create(String outputPath) throws IOException {
		return new YamlFile(outputPath);
	}
}
