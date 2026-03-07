package net.chefcraft.core.language;

import net.chefcraft.core.PlatformProvider;
import net.chefcraft.core.PluginInstance;
import net.chefcraft.core.util.MinecraftUtil;
import net.chefcraft.core.util.Placeholder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;

/**@since 1.3.0*/
public interface MessageCompiler {

	/**
	 * Messages are compiled based on the platform
	 * @param plugin plugin instance
	 * @param source source
	 * @param key translation key
	 * @param placeholder placeholders
	 * @return compiled message from file
	 */
	@NotNull MessageHolder compile(@NotNull PluginInstance plugin, @NotNull TranslationSource source, @NotNull String key, @Nullable Placeholder placeholder);
	
	/**
	 * Messages are compiled based on the platform
	 * @param plugin plugin instance
	 * @param source source
	 * @param key translation key
	 * @param placeholder placeholders
	 * @return compiled message as string from file
	 */
	@NotNull String compileAsPlain(@NotNull PluginInstance plugin, @NotNull TranslationSource source, @NotNull String key, @Nullable Placeholder placeholder);
	
	/**
	 * Messages are compiled based on the platform
	 * @param plugin plugin instance
	 * @param source source
	 * @param key translation key
	 * @param placeholder placeholders
	 * @return compiled message as list string from file
	 */
	@NotNull List<String> compileAsPlainToList(@NotNull PluginInstance plugin, @NotNull TranslationSource source, @NotNull String key, @Nullable Placeholder placeholder);
	
	/**
	 * Sends chat message to source
	 * @param source source
	 * @param key translation key
	 * @param placeholder placeholders
	 */
	void chat(@NotNull TranslationSource source, @NotNull String key, @Nullable Placeholder placeholder);
	
	/**
	 * Sends action bar message to source
	 * @param source source
	 * @param key translation key
	 * @param placeholder placeholders
	 */
	void actionBar(@NotNull TranslationSource source, @NotNull String key, @Nullable Placeholder placeholder);
	
	/**
	 * Shows title message to source
	 * @param source source
	 * @param key translation key
	 * @param fadeIn the duration of the text fade in
	 * @param stay the duration of the text on the screen
	 * @param fadeOut the duration of the text fade out
	 * @param placeholder placeholders
	 */
	void title(@NotNull TranslationSource source, @NotNull String key, @NotNull Duration fadeIn, @NotNull Duration stay, @NotNull Duration fadeOut, @Nullable Placeholder placeholder);
	
	/**
	 * Resets source's title
	 * @param source source
	 */
	void titleReset(@NotNull TranslationSource source);
	
	/**
	 * Compiles messages from current platform
	 * @param plugin plugin instance
	 * @param source source
	 * @param key translation key
	 * @param placeholder placeholders
	 * @return compiled message from file
	 */
	static @NotNull MessageHolder getMessage(@NotNull PluginInstance plugin, @NotNull TranslationSource source, @NotNull String key, @Nullable Placeholder placeholder) {
		return PlatformProvider.messageCompiler().compile(plugin, source, key, placeholder);
	}
	
	/**
	 * Compiles messages from current platform
	 * @param plugin plugin instance
	 * @param source source
	 * @param key translation key
	 * @return compiled message from file
	 */
	static @NotNull MessageHolder getMessage(@NotNull PluginInstance plugin, @NotNull TranslationSource source, @NotNull String key) {
		return PlatformProvider.messageCompiler().compile(plugin, source, key, null);
	}
	
	/**
	 * Messages are compiled based on the platform
	 * @param plugin plugin instance
	 * @param source source
	 * @param key translation key
	 * @param placeholder placeholders
	 * @return compiled message as string from file
	 */
	static @NotNull String getPlainMessage(@NotNull PluginInstance plugin, @NotNull TranslationSource source, @NotNull String key, @Nullable Placeholder placeholder) {
		return PlatformProvider.messageCompiler().compileAsPlain(plugin, source, key, placeholder);
	}
	
	/**
	 * Messages are compiled based on the platform
	 * @param plugin plugin instance
	 * @param source source
	 * @param key translation key
	 * @return compiled message as string from file
	 */
	static @NotNull String getPlainMessage(@NotNull PluginInstance plugin, @NotNull TranslationSource source, @NotNull String key) {
		return PlatformProvider.messageCompiler().compileAsPlain(plugin, source, key, null);
	}
	
	/**
	 * Messages are compiled based on the platform
	 * @param plugin plugin instance
	 * @param source source
	 * @param key translation key
	 * @param placeholder placeholders
	 * @return compiled message as list string from file
	 */
	static @NotNull List<String> getPlainMessageAsList(@NotNull PluginInstance plugin, @NotNull TranslationSource source, @NotNull String key, @Nullable Placeholder placeholder) {
		return PlatformProvider.messageCompiler().compileAsPlainToList(plugin, source, key, placeholder);
	}
	
	/**
	 * Messages are compiled based on the platform
	 * @param plugin plugin instance
	 * @param source source
	 * @param key translation key
	 * @return compiled message as list string from file
	 */
	static @NotNull List<String> getPlainMessageAsList(@NotNull PluginInstance plugin, @NotNull TranslationSource source, @NotNull String key) {
		return PlatformProvider.messageCompiler().compileAsPlainToList(plugin, source, key, null);
	}
	
	/**
	 * Compiles messages from current platform
	 * @param plugin plugin instance
	 * @param key translation key
	 * @param placeholder placeholders
	 * @return compiled message from file
	 */
	static @NotNull MessageHolder getMessageFromDefault(@NotNull PluginInstance plugin, @NotNull String key, @Nullable Placeholder placeholder) {
		return getMessage(plugin, PlatformProvider.getPluginInstance().getDefaultLanguage(), key, placeholder);
	}
	
	/**
	 * Compiles messages from current platform
	 * @param plugin plugin instance
	 * @param key translation key
	 * @return compiled message from file
	 */
	static @NotNull MessageHolder getMessageFromDefault(@NotNull PluginInstance plugin, @NotNull String key) {
		return getMessage(plugin, PlatformProvider.getPluginInstance().getDefaultLanguage(), key, null);
	}
	
	/**
	 * Sends chat message to source
	 * @param source source
	 * @param key translation key
	 * @param placeholder placeholders
	 */
	static void sendMessage(@NotNull TranslationSource source, @NotNull String key, @Nullable Placeholder placeholder) {
		PlatformProvider.messageCompiler().chat(source, key, placeholder);
	}
	
	/**
	 * Sends chat message to source
	 * @param source source
	 * @param key translation key
	 */
	static void sendMessage(@NotNull TranslationSource source, @NotNull String key) {
		PlatformProvider.messageCompiler().chat(source, key, null);
	}
	
	/**
	 * Sends action bar message to source
	 * @param source source
	 * @param key translation key
	 * @param placeholder placeholders
	 */
	static void sendActionBar(@NotNull TranslationSource source, @NotNull String key, @Nullable Placeholder placeholder) {
		PlatformProvider.messageCompiler().actionBar(source, key, placeholder);
	}
	
	/**
	 * Sends action bar message to source
	 * @param source source
	 * @param key translation key
	 */
	static void sendActionBar(@NotNull TranslationSource source, @NotNull String key) {
		PlatformProvider.messageCompiler().actionBar(source, key, null);
	}
	
	/**
	 * Shows title message to source
	 * @param source source
	 * @param key translation key
	 * @param fadeIn the duration of the text fade in
	 * @param stay the duration of the text on the screen
	 * @param fadeOut the duration of the text fade out
	 * @param placeholder placeholders
	 */
	static void showTitle(@NotNull TranslationSource source, @NotNull String key, @NotNull Duration fadeIn, 
			@NotNull Duration stay, @NotNull Duration fadeOut, @Nullable Placeholder placeholder) {
		PlatformProvider.messageCompiler().title(source, key, fadeIn, stay, fadeOut, placeholder);
	}
	
	/**
	 * Shows title message to source
	 * @param source source
	 * @param key translation key
	 * @param fadeIn the duration of the text fade in
	 * @param stay the duration of the text on the screen
	 * @param fadeOut the duration of the text fade out
	 */
	static void showTitle(@NotNull TranslationSource source, @NotNull String key, @NotNull Duration fadeIn, 
			@NotNull Duration stay, @NotNull Duration fadeOut) {
		PlatformProvider.messageCompiler().title(source, key, fadeIn, stay, fadeOut, null);
	}
	
	/**
	 * Shows title message to source
	 * @param source source
	 * @param key translation key
	 * @param placeholder placeholders
	 */
	static void showTitle(@NotNull TranslationSource source, @NotNull String key, @Nullable Placeholder placeholder) {
		showTitle(source, key, MinecraftUtil.TITLE_FADE_IN_DURATION, MinecraftUtil.TITLE_STAY_DURATION, MinecraftUtil.TITLE_FADE_OUT_DURATION, placeholder);
	}
	
	/**
	 * Shows title message to source
	 * @param source source
	 * @param key translation key
	 */
	static void showTitle(@NotNull TranslationSource source, @NotNull String key) {
		showTitle(source, key, MinecraftUtil.TITLE_FADE_IN_DURATION, MinecraftUtil.TITLE_STAY_DURATION, MinecraftUtil.TITLE_FADE_OUT_DURATION, null);
	}
	
	/**
	 * Resets source's title
	 * @param source source
	 */
	static void resetTitle(@NotNull TranslationSource source) {
		PlatformProvider.messageCompiler().titleReset(source);
	}
}
