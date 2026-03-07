package net.chefcraft.core;

import net.chefcraft.core.language.Language;
import net.chefcraft.core.language.TranslationSource;
import net.chefcraft.core.util.Placeholder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;
import java.util.logging.Logger;

public interface PluginInstance {
	
	@NotNull <P> P instance();
	
	@NotNull Language getDefaultLanguage();

	@NotNull String getName();
	
	@NotNull Logger getLogger();
	
	@NotNull File getDataFolder();

	@NotNull InputStream getResource(@NotNull String resource);
	
	@Nullable TranslationSource getTranslationSourceByUniqueId(UUID uniqueId);
	
	void sendPlainMessage(@NotNull String message);
	
	void sendKeyedMessage(@NotNull String key, @Nullable Placeholder placeholder);
	
	default void sendKeyedMessage(@NotNull String key) {
		this.sendKeyedMessage(key, null);
	}
	
	default void sendPlainMessage(@NotNull String message, Exception x) {
		this.sendPlainMessage(message + "<white> | <red>Error Message: <white>" + x.getMessage() + " | <red>Error Cause: " + x.getCause());
	}
}
