package net.chefcraft.reflection.base.language;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.PluginInstance;
import net.chefcraft.core.language.MessageCompiler;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslationSource;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.world.util.BukkitUtils;
import net.chefcraft.world.util.PapiHook;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import static java.util.Objects.requireNonNull;

public class PaperMessageCompiler implements MessageCompiler {
	
	/**
	 * for static methods go {@link BukkitMessageCompiler}
	 */
	
	public static final String FILE_FORMAT = ".yml";
	
	@Override
	public MessageHolder compile(@NotNull PluginInstance plugin, @NotNull TranslationSource source, 
			@NotNull String key, @Nullable Placeholder placeholder) {
		
		List<String> raw = this.getRawMessage(plugin, source, key, placeholder);
		
		if (raw == null) {
			return SingleMessageHolder.emptyAsComponent(key);
		}
		
		switch (raw.size()) {
		case 0:
			return SingleMessageHolder.holdAsComponent(key, "", source.getRandomColor());
		case 1:
			return SingleMessageHolder.holdAsComponent(key, raw.get(0), source.getRandomColor());
		default:
			return ListMessageHolder.holdAsComponent(key, raw, source.getRandomColor());
		}
	}
	
	@Override
	public String compileAsPlain(@NotNull PluginInstance plugin, @NotNull TranslationSource source, 
			@NotNull String key, @Nullable Placeholder placeholder) {
		
		List<String> raw = this.getRawMessage(plugin, source, key, placeholder);
		if (raw == null) {
			return key;
		}
		
		switch (raw.size()) {
		case 0:
			return "";
		case 1:
			return raw.get(0);
		default:
			return BukkitUtils.parseList(raw, "<newline>");
		}
	}
	
	@Override
	public @NotNull List<String> compileAsPlainToList(@NotNull PluginInstance plugin, @NotNull TranslationSource source,
			@NotNull String key, @Nullable Placeholder placeholder) {
		
		List<String> raw = this.getRawMessage(plugin, source, key, placeholder);
		return raw != null ? raw : Arrays.asList(key);
	}
	
	@Override
	public void chat(@NotNull TranslationSource source, @NotNull String key, @Nullable Placeholder placeholder) {
		Audience audience = this.castAudience(source);
		List<String> raw = this.getRawMessage(source.getPlugin(), source, key, placeholder);
		
		if (raw != null) {
			audience.sendMessage(MessageHolderUtil.deserializeListTextToComponent(raw, source.getRandomColor()));
		} else {
			audience.sendMessage(Component.text(key));
		}
	}
	
	@Override
	public void actionBar(@NotNull TranslationSource source, @NotNull String key, @Nullable Placeholder placeholder) {
		Audience audience = this.castAudience(source);
		List<String> raw = this.getRawMessage(source.getPlugin(), source, key, placeholder);
		
		if (raw != null) {
			audience.sendActionBar(MessageHolderUtil.deserializeListTextToComponent(raw, source.getRandomColor()));
		} else {
			audience.sendActionBar(Component.text(key));
		}
	}

	@Override
	public void title(@NotNull TranslationSource source, @NotNull String key, 
			@NotNull Duration fadeIn, @NotNull Duration stay, @NotNull Duration fadeOut,
			@Nullable Placeholder placeholder) {
		
		Audience audience = this.castAudience(source);
		List<String> raw = this.getRawMessage(source.getPlugin(), source, key, placeholder);
		
		if (raw != null) {
			List<Component> list = MessageHolderUtil.deserializeListTextToListComponent(raw, source.getRandomColor());
			if (list.size() < 2) {
				audience.showTitle(Title.title(list.get(0), Component.text(""), Title.Times.times(fadeIn, stay, fadeOut)));
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
		Object obj = source.getAudience();
		
		if (obj == null) {
			throw new IllegalArgumentException("source cannot be null!");
		}
		
		if (!(obj instanceof Audience)) {
			throw new IllegalArgumentException("'TranslationSource#getAudience()' must be implements 'net.kyori.adventure.audience.Audience'");
		}
		
		return (Audience) obj;
	}
	
	private List<String> getRawMessage(@NotNull PluginInstance plugin, @NotNull TranslationSource source, @NotNull String key, @Nullable Placeholder placeholder) {
		requireNonNull(plugin, "plugin cannot be null!");
		requireNonNull(source, "source cannot be null!");
		requireNonNull(key, "key cannot be null!");

		FileConfiguration config = source.getLanguage().getLanguageFile(plugin).getConfig();
		if (!config.isSet(key)) {
			//lets check fallback message
			config = ChefCore.getInstance().getDefaultLanguage().getLanguageFile(plugin).getConfig();
			if (!config.isSet(key)) {
				return null;
			}
		}
		
		String prefix = config.getString("prefix");
		
		if (config.isList(key)) {
			List<String> texts = config.getStringList(key);
			
			if (placeholder != null) {
				final ListIterator<String> li = texts.listIterator();
		        while (li.hasNext()) {
		            li.set(placeholder.replace(li.next().replace("<prefix>", prefix != null ? prefix : "")));
		        }
			} else {
				final ListIterator<String> li = texts.listIterator();
		        while (li.hasNext()) {
		            li.set(li.next().replace("<prefix>", prefix != null ? prefix : ""));
		        }
			}
			
			return PapiHook.translatePlaceholders(source, texts);
		} else {
			String text = config.getString(key);
			text = text.replace("<prefix>", prefix != null ? prefix : "");
			
			if (placeholder != null) {
				text = placeholder.replace(text);
			}
			
			return Arrays.asList(PapiHook.translatePlaceholders(source, text));
		}
	}
}
