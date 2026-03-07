package net.chefcraft.core.configuration;

import net.chefcraft.core.util.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public abstract class BaseConfigurationFile {

	protected File file = null;
	
	protected BaseConfigurationFile(@NotNull InputStream input, @NotNull String outputPath) throws IOException {
		Objects.requireNonNull(input, "input stream cannot be null!");
		Objects.requireNonNull(outputPath, "output path cannot be null!");
		
		file = new File(outputPath);
		if (!file.exists()) {
			FileUtils.copyInputStreamToFile(input, file);
		}
	}
	
	protected BaseConfigurationFile(@NotNull String outputPath) throws IOException {
		Objects.requireNonNull(outputPath, "output path cannot be null!");
		
		file = new File(outputPath);
		if (!file.exists()) {
			FileUtils.createDirectory(file);
		}
	}
	
	protected abstract void loadConfiguration();
	
	public abstract <T> T getConfig();
	
	public abstract void reload();
	
	public abstract void save();
	
	public abstract void delete();
	
	public File getFile() {
		return this.file;
	}
	
	public String getRawName() {
		return file.getName();
	}
	
	public String getName() {
		return file.getName().replace(".yml", "");
	}
}
