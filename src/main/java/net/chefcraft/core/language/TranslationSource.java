package net.chefcraft.core.language;

import net.chefcraft.core.PlatformProvider;
import net.chefcraft.core.PluginInstance;
import net.chefcraft.core.component.RandomColor;
import net.chefcraft.core.party.CorePartyManager;
import net.chefcraft.core.util.MinecraftUtil;
import net.chefcraft.core.util.Placeholder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.List;

public interface TranslationSource extends Translatable {

	/**
	 * You must specify the {@link Language} of this {@link Translatable} 
	 * object for {@link MessageCompiler} or something else.
	 */
	@NotNull
	Language getLanguage();
	
	/**
	 * Specifies which plugin instance to use.
	 */
	@NotNull 
	PluginInstance getPlugin();
	
	/**
	 * if you use the “<randomcolor>” key in messages to track it and make sure the transitions are perfect for each TranslationSource
	 * @see {@link RandomColor}
	 */
	@NotNull 
	RandomColor getRandomColor();
	
	boolean isConsole();
	
	/**
	 * 
	 * @param <A> 
	 *  must be {@link net.kyori.adventure.audience.Audience}
	 *  or {@link org.bukkit.entity.Player}
	 *  or {@link org.bukkit.command.CommandSender}
	 *  If you are using Paper 1.16.5 and above,
	 *  the last two already implements the audience.
	 *  
	 *  If you are using velocity, classes that implements
	 *  audience are sufficient :D
	 *  
	 * @returns null or object
	 */
	@Nullable
	<A> A getAudience();
	
	@NotNull
	CorePartyManager getPartyManager();
	
	/**
	 * Compiles messages from current platform
	 * @param key translation key
	 * @param placeholder placeholders
	 * @return compiled message from file
	 */
	default @NotNull MessageHolder getMessage(@NotNull String key, @Nullable Placeholder placeholder) {
		return PlatformProvider.messageCompiler().compile(this.getPlugin(), this, key, placeholder);
	}
	
	/**
	 * Compiles messages from current platform
	 * @param key translation key
	 * @return compiled message from file
	 */
	default @NotNull MessageHolder getMessage(@NotNull String key) {
		return PlatformProvider.messageCompiler().compile(this.getPlugin(), this, key, null);
	}
	
	/**
	 * Compiles messages from current platform
	 * @param key translation key
	 * @param placeholder placeholders
	 * @return compiled message from file
	 */
	default @NotNull String getPlainMessage(@NotNull String key, @Nullable Placeholder placeholder) {
		return PlatformProvider.messageCompiler().compileAsPlain(this.getPlugin(), this, key, placeholder);
	}
	
	/**
	 * Compiles messages from current platform
	 * @param key translation key
	 * @return compiled message from file
	 */
	default @NotNull String getPlainMessage(@NotNull String key) {
		return PlatformProvider.messageCompiler().compileAsPlain(this.getPlugin(), this, key, null);
	}
	
	/**
	 * Messages are compiled based on the platform
	 * @param key translation key
	 * @param placeholder placeholders
	 * @return compiled message as list string from file
	 */
	default @NotNull List<String> getPlainMessageAsList(@NotNull String key, @Nullable Placeholder placeholder) {
		return PlatformProvider.messageCompiler().compileAsPlainToList(this.getPlugin(), this, key, placeholder);
	}
	
	/**
	 * Messages are compiled based on the platform
	 * @param key translation key
	 * @return compiled message as list string from file
	 */
	default @NotNull List<String> getPlainMessageAsList(@NotNull String key) {
		return PlatformProvider.messageCompiler().compileAsPlainToList(this.getPlugin(), this, key, null);
	}
	
	/**
	 * Sends chat message to source
	 * @param key translation key
	 * @param placeholder placeholders
	 */
	default void sendMessage(@NotNull String key, @Nullable Placeholder placeholder) {
		PlatformProvider.messageCompiler().chat(this, key, placeholder);
	}
	
	/**
	 * Sends chat message to source
	 * @param key translation key
	 */
	default void sendMessage(@NotNull String key) {
		PlatformProvider.messageCompiler().chat(this, key, null);
	}
	
	/**
	 * Sends action bar message to source
	 * @param key translation key
	 * @param placeholder placeholders
	 */
	default void sendActionBar(@NotNull String key, @Nullable Placeholder placeholder) {
		PlatformProvider.messageCompiler().actionBar(this, key, placeholder);
	}
	
	/**
	 * Sends action bar message to source
	 * @param key translation key
	 */
	default void sendActionBar(@NotNull String key) {
		PlatformProvider.messageCompiler().actionBar(this, key, null);
	}
	
	/**
	 * Shows title message to source
	 * @param key translation key
	 * @param fadeIn the duration of the text fade in
	 * @param stay the duration of the text on the screen
	 * @param fadeOut the duration of the text fade out
	 * @param placeholder placeholders
	 */
	default void showTitle(@NotNull String key, int fadeIn, int stay, int fadeOut, @Nullable Placeholder placeholder) {
		PlatformProvider.messageCompiler().title(this, key, 
				MinecraftUtil.durationOfTicks(fadeIn), 
				MinecraftUtil.durationOfTicks(stay), 
				MinecraftUtil.durationOfTicks(fadeOut),
				placeholder);
	}
	
	/**
	 * Shows title message to source
	 * @param key translation key
	 * @param fadeIn the duration of the text fade in
	 * @param stay the duration of the text on the screen
	 * @param fadeOut the duration of the text fade out
	 */
	default void showTitle(@NotNull String key, int fadeIn, int stay, int fadeOut) {
		PlatformProvider.messageCompiler().title(this, key, 
				MinecraftUtil.durationOfTicks(fadeIn), 
				MinecraftUtil.durationOfTicks(stay), 
				MinecraftUtil.durationOfTicks(fadeOut),
				null);
	}
	
	/**
	 * Shows title message to source
	 * @param key translation key
	 * @param fadeIn the duration of the text fade in
	 * @param stay the duration of the text on the screen
	 * @param fadeOut the duration of the text fade out0
	 * @param placeholder placeholders
	 */
	default void showTitle(@NotNull String key, @NotNull Duration fadeIn, 
			@NotNull Duration stay, @NotNull Duration fadeOut, @Nullable Placeholder placeholder) {
		PlatformProvider.messageCompiler().title(this, key, fadeIn, stay, fadeOut, placeholder);
	}
	
	/**
	 * Shows title message to source
	 * @param key translation key
	 * @param fadeIn the duration of the text fade in
	 * @param stay the duration of the text on the screen
	 * @param fadeOut the duration of the text fade out
	 */
	default void showTitle(@NotNull String key, @NotNull Duration fadeIn, 
			@NotNull Duration stay, @NotNull Duration fadeOut) {
		PlatformProvider.messageCompiler().title(this, key, fadeIn, stay, fadeOut, null);
	}
	
	/**
	 * Shows title message to source
	 * @param key translation key
	 * @param placeholder placeholders
	 */
	default void showTitle(@NotNull String key, @Nullable Placeholder placeholder) {
		showTitle(key, MinecraftUtil.TITLE_FADE_IN_DURATION, MinecraftUtil.TITLE_STAY_DURATION, MinecraftUtil.TITLE_FADE_OUT_DURATION, placeholder);
	}
	
	/**
	 * Shows title message to source
	 * @param key translation key
	 */
	default void showTitle(@NotNull String key) {
		showTitle(key, MinecraftUtil.TITLE_FADE_IN_DURATION, MinecraftUtil.TITLE_STAY_DURATION, MinecraftUtil.TITLE_FADE_OUT_DURATION, null);
	}
	
	/**
	 * Resets source's title
	 */
	default void resetTitle() {
		PlatformProvider.messageCompiler().titleReset(this);
	}
}
