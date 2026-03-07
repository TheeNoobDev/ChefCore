package net.chefcraft.reflection.base.language;

import net.chefcraft.core.PluginInstance;
import net.chefcraft.core.command.CommandSupport;
import net.chefcraft.core.component.ComponentSupport;
import net.chefcraft.core.language.MessageCompiler;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslationSource;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.proxy.ChefProxyCore;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static java.util.Objects.requireNonNull;

public class VelocityMessageCompiler implements MessageCompiler {
	
	static final VelocityMessageCompiler THIS = new VelocityMessageCompiler();
	
	/**
	 * Compiles messages from current platform
	 * @param plugin plugin instance
	 * @param source source
	 * @param key translation key
	 * @param placeholder placeholders
	 * @return compiled message from file
	 */
	public static @NotNull MessageHolder getMessage(@NotNull PluginInstance plugin, @NotNull Audience source, @NotNull String key, @Nullable Placeholder placeholder) {
		return THIS.compile(plugin, CommandSupport.defineCommandSender(plugin, source), key, placeholder);
	}
	
	/**
	 * Compiles messages from current platform
	 * @param plugin plugin instance
	 * @param source source
	 * @param key translation key
	 * @return compiled message from file
	 */
	public static @NotNull MessageHolder getMessage(@NotNull PluginInstance plugin, @NotNull Audience source, @NotNull String key) {
		return THIS.compile(plugin, CommandSupport.defineCommandSender(plugin, source), key, null);
	}
	
	/**
	 * Compiles messages from current platform
	 * @param plugin plugin instance
	 * @param source source
	 * @param key translation key
	 * @param placeholder placeholders
	 * @return compiled message as string from file
	 */
	public static @NotNull String getPlainMessage(@NotNull PluginInstance plugin, @NotNull Audience source, @NotNull String key, @Nullable Placeholder placeholder) {
		return THIS.compileAsPlain(plugin, CommandSupport.defineCommandSender(plugin, source), key, placeholder);
	}
	
	/**
	 * Compiles messages from current platform
	 * @param plugin plugin instance
	 * @param source source
	 * @param key translation key
	 * @return compiled message as string from file
	 */
	public static @NotNull String getPlainMessage(@NotNull PluginInstance plugin, @NotNull Audience source, @NotNull String key) {
		return THIS.compileAsPlain(plugin, CommandSupport.defineCommandSender(plugin, source), key, null);
	}
	
	/**
	 * Compiles messages from current platform
	 * @param plugin plugin instance
	 * @param source source
	 * @param key translation key
	 * @param placeholder placeholders
	 * @return compiled message as string list from file
	 */
	public static @NotNull List<String> getPlainMessageAsList(@NotNull PluginInstance plugin, @NotNull Audience source, @NotNull String key, @Nullable Placeholder placeholder) {
		return THIS.compileAsPlainToList(plugin, CommandSupport.defineCommandSender(plugin, source), key, placeholder);
	}
	
	/**
	 * Compiles messages from current platform
	 * @param plugin plugin instance
	 * @param source source
	 * @param key translation key
	 * @return compiled message as string list from file
	 */
	public static @NotNull List<String> getPlainMessageAsList(@NotNull PluginInstance plugin, @NotNull Audience source, @NotNull String key) {
		return THIS.compileAsPlainToList(plugin, CommandSupport.defineCommandSender(plugin, source), key, null);
	}
	
	/**
	 * Sends chat message to source
	 * @param source source
	 * @param key translation key
	 * @param placeholder placeholders
	 */
	public static void sendMessage(@NotNull PluginInstance plugin, @NotNull Audience source, @NotNull String key, @Nullable Placeholder placeholder) {
		THIS.chat(CommandSupport.defineCommandSender(plugin, source), key, placeholder);
	}
	
	/**
	 * Sends chat message to source
	 * @param source source
	 * @param key translation key
	 */
	public static void sendMessage(@NotNull PluginInstance plugin, @NotNull Audience source, @NotNull String key) {
		THIS.chat(CommandSupport.defineCommandSender(plugin, source), key, null);
	}

	public static final String FILE_FORMAT = ".properties";
	
	@Override
	public MessageHolder compile(@NotNull PluginInstance plugin, @NotNull TranslationSource source, 
			@NotNull String key, @Nullable Placeholder placeholder) {
		
		String raw = this.getRawMessage(plugin, source, key, placeholder);
		
		if (raw == null) {
			return SingleMessageHolder.emptyAsComponent(key);
		}
		
		return SingleMessageHolder.holdAsComponent(key, raw, source.getRandomColor());
	}
	
	@Override
	public String compileAsPlain(@NotNull PluginInstance plugin, @NotNull TranslationSource source, 
			@NotNull String key, @Nullable Placeholder placeholder) {
		
		String raw = this.getRawMessage(plugin, source, key, placeholder);
		return raw != null ? raw : key;
	}
	
	@Override
	public @NotNull List<String> compileAsPlainToList(@NotNull PluginInstance plugin, @NotNull TranslationSource source,
			@NotNull String key, @Nullable Placeholder placeholder) {
		
		String raw = this.getRawMessage(plugin, source, key, placeholder);
		return raw != null ? Arrays.asList(raw.split("<newline>")) : Arrays.asList(key);
	}
	
	@Override
	public void chat(@NotNull TranslationSource source, @NotNull String key, @Nullable Placeholder placeholder) {
		Audience audience = this.castAudience(source);
		String raw = this.getRawMessage(source.getPlugin(), source, key, placeholder);
		
		if (raw != null) {
			audience.sendMessage((@NotNull Component) ComponentSupport.miniMessageSupport().deserialize(raw, source.getRandomColor()));
		} else {
			audience.sendMessage(Component.text(key));
		}
	}
	
	@Override
	public void actionBar(@NotNull TranslationSource source, @NotNull String key, @Nullable Placeholder placeholder) {
		Audience audience = this.castAudience(source);
		String raw = this.getRawMessage(source.getPlugin(), source, key, placeholder);
		
		if (raw != null) {
			audience.sendActionBar((@NotNull Component) ComponentSupport.miniMessageSupport().deserialize(raw, source.getRandomColor()));
		} else {
			audience.sendActionBar(Component.text(key));
		}
	}

	@Override
	public void title(@NotNull TranslationSource source, @NotNull String key, 
			@NotNull Duration fadeIn, @NotNull Duration stay, @NotNull Duration fadeOut,
			@Nullable Placeholder placeholder) {
		Audience audience = this.castAudience(source);
		String raw = this.getRawMessage(source.getPlugin(), source, key, placeholder);
		
		if (raw != null) {
			Component comp = (@NotNull Component) ComponentSupport.miniMessageSupport().deserialize(raw, source.getRandomColor());
			List<Component> list = comp.children();
			if (list.size() < 2) {
				audience.showTitle(Title.title(comp, Component.empty(), Title.Times.times(fadeIn, stay, fadeOut)));
			} else {
				audience.showTitle(Title.title(list.get(0), list.get(1), Title.Times.times(fadeIn, stay, fadeOut)));
			}
		} else {
			audience.showTitle(Title.title(Component.text(key), Component.empty()));
		}
	}

	@Override
	public void titleReset(@NotNull TranslationSource source) {
		this.castAudience(source).resetTitle();
	}
	
	private Audience castAudience(@NotNull TranslationSource source) {
		Objects.requireNonNull(source, "source cannot be null!");
		
		Object obj = source.getAudience();
		
		if (obj == null) {
			throw new IllegalArgumentException("source cannot be null!");
		}
		
		if (!(obj instanceof Audience)) {
			throw new IllegalArgumentException("'TranslationSource#getAudience()' must be implements 'net.kyori.adventure.audience.Audience'");
		}
		
		return (Audience) obj;
	}
	
	private String getRawMessage(@NotNull PluginInstance plugin, @NotNull TranslationSource source, @NotNull String key, @Nullable Placeholder placeholder) {
		requireNonNull(plugin, "plugin cannot be null!");
		requireNonNull(source, "source cannot be null!");
		requireNonNull(key, "key cannot be null!");

		Properties properties = source.getLanguage().getLanguageFile(plugin).getConfig();
		String message = properties.getProperty(key);
		
		if (message == null) {
			//lets check fallback message 
			properties = ChefProxyCore.getInstance().getDefaultLanguage().getLanguageFile(plugin).getConfig();
			message = properties.getProperty(key);
			
			if (message == null) {
				return null;
			}
		}
		
		if (message.contains("<prefix>")) {
			String prefix = properties.getProperty("prefix");
			message = message.replace("<prefix>", prefix != null ? prefix : "");
		}
		
		if (placeholder != null) {
			message = placeholder.replace(message);
		}
		
		return message;
	}
}
