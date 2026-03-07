package net.chefcraft.core.configuration;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import net.chefcraft.core.util.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class CoreJsonFile extends BaseConfigurationFile {
	
	private JsonElement jsonElement = null;
	
	protected CoreJsonFile(@NotNull InputStream input, @NotNull String outputPath) throws IOException {
		super(input, outputPath);
		this.loadConfiguration();
	}
	
	protected CoreJsonFile(@NotNull String outputPath) throws IOException {
		super(outputPath);
		this.loadConfiguration();
	}

	@Override
	protected void loadConfiguration() {
		try {
			this.jsonElement = JsonParser.parseReader(new FileReader(this.file));
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public JsonElement getConfig() {
		return this.jsonElement;
	}

	@Override
	public void reload() {
		this.loadConfiguration();
	}

	@Override
	public void save() {
		this.loadConfiguration();
	}
	
	@Override
	public void delete() {
		try {
			FileUtils.deleteFile(this.file);
			this.jsonElement = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static CoreJsonFile copyResource(@NotNull InputStream input, @NotNull String outputPath) throws IOException {
		return new CoreJsonFile(input, outputPath);
	}
	
	public static CoreJsonFile create(@NotNull String outputPath) throws IOException {
		return new CoreJsonFile(outputPath);
	}
}
