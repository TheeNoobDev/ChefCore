package net.chefcraft.core.configuration;

import net.chefcraft.core.util.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Properties;

public class CorePropertiesFile extends BaseConfigurationFile {
	
	private Properties properties = new Properties();

	protected CorePropertiesFile(@NotNull InputStream input, @NotNull String outputPath) throws IOException {
		super(input, outputPath);
		this.loadConfiguration();
	}
	
	protected CorePropertiesFile(@NotNull String outputPath) throws IOException {
		super(outputPath);
		this.loadConfiguration();
	}
	
	@Override
	protected void loadConfiguration() {
		try {
			properties.load(new FileReader(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Properties getConfig() {
		return properties;
	}

	@Override
	public void reload() {
		this.loadConfiguration();
	}

	@Override
	public void save() {
		try {
			properties.store(new FileOutputStream(file), LocalDateTime.now().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void delete() {
		try {
			FileUtils.deleteFile(file);
			properties = new Properties();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static CorePropertiesFile copyResource(@NotNull InputStream input, @NotNull String outputPath) throws IOException {
		return new CorePropertiesFile(input, outputPath);
	}
	
	public static CorePropertiesFile create(@NotNull String outputPath) throws IOException {
		return new CorePropertiesFile(outputPath);
	}

}
