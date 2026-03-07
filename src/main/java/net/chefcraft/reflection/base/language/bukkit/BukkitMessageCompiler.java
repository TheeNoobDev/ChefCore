package net.chefcraft.reflection.base.language;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.PlatformProvider;
import net.chefcraft.core.PluginInstance;
import net.chefcraft.core.command.CommandSupport;
import net.chefcraft.core.language.MessageCompiler;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslationSource;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.world.util.BukkitUtils;
import net.chefcraft.world.util.PapiHook;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import static java.util.Objects.requireNonNull;

/**
 * @since 1.3.0
 */
public class BukkitMessageCompiler implements MessageCompiler {

	static final MessageCompiler BUKKIT_OR_PAPER = PlatformProvider.hasPaper() && PlatformProvider.hasKyoriAdventure() ? new PaperMessageCompiler() : new BukkitMessageCompiler();
	
	/**
	 * Compiles messages from current platform
	 * @param plugin plugin instance
	 * @param source source
	 * @param key translation key
	 * @param placeholder placeholders
	 * @return compiled message from file
	 */
	public static @NotNull MessageHolder getMessage(@NotNull PluginInstance plugin, @NotNull CommandSender source, @NotNull String key, @Nullable Placeholder placeholder) {
		return BUKKIT_OR_PAPER.compile(plugin, CommandSupport.defineCommandSender(plugin, source), key, placeholder);
	}
	
	/**
	 * Compiles messages from current platform
	 * @param plugin plugin instance
	 * @param source source
	 * @param key translation key
	 * @return compiled message from file
	 */
	public static @NotNull MessageHolder getMessage(@NotNull PluginInstance plugin, @NotNull CommandSender source, @NotNull String key) {
		return BUKKIT_OR_PAPER.compile(plugin, CommandSupport.defineCommandSender(plugin, source), key, null);
	}
	
	/**
	 * Compiles messages from current platform
	 * @param plugin plugin instance
	 * @param source source
	 * @param key translation key
	 * @param placeholder placeholders
	 * @return compiled message as string from file
	 */
	public static @NotNull String getPlainMessage(@NotNull PluginInstance plugin, @NotNull CommandSender source, @NotNull String key, @Nullable Placeholder placeholder) {
		return BUKKIT_OR_PAPER.compileAsPlain(plugin, CommandSupport.defineCommandSender(plugin, source), key, placeholder);
	}
	
	/**
	 * Compiles messages from current platform
	 * @param plugin plugin instance
	 * @param source source
	 * @param key translation key
	 * @return compiled message as string from file
	 */
	public static @NotNull String getPlainMessage(@NotNull PluginInstance plugin, @NotNull CommandSender source, @NotNull String key) {
		return BUKKIT_OR_PAPER.compileAsPlain(plugin, CommandSupport.defineCommandSender(plugin, source), key, null);
	}
	
	/**
	 * Messages are compiled based on the platform
	 * @param plugin plugin instance
	 * @param source source
	 * @param key translation key
	 * @param placeholder placeholders
	 * @return compiled message as list string from file
	 */
	public static @NotNull List<String> getPlainMessageAsList(@NotNull PluginInstance plugin, @NotNull TranslationSource source, @NotNull String key, @Nullable Placeholder placeholder) {
		return BUKKIT_OR_PAPER.compileAsPlainToList(plugin, source, key, placeholder);
	}
	
	/**
	 * Messages are compiled based on the platform
	 * @param plugin plugin instance
	 * @param source source
	 * @param key translation key
	 * @return compiled message as list string from file
	 */
	public static @NotNull List<String> getPlainMessageAsList(@NotNull PluginInstance plugin, @NotNull TranslationSource source, @NotNull String key) {
		return BUKKIT_OR_PAPER.compileAsPlainToList(plugin, source, key, null);
	}
	
	/**
	 * Sends chat message to source
	 * @param source source
	 * @param key translation key
	 * @param placeholder placeholders
	 */
	public static void sendMessage(@NotNull PluginInstance plugin, @NotNull CommandSender source, @NotNull String key, @Nullable Placeholder placeholder) {
		BUKKIT_OR_PAPER.chat(CommandSupport.defineCommandSender(plugin, source), key, placeholder);
	}
	
	/**
	 * Sends chat message to source
	 * @param source source
	 * @param key translation key
	 */
	public static void sendMessage(@NotNull PluginInstance plugin, @NotNull CommandSender source, @NotNull String key) {
		BUKKIT_OR_PAPER.chat(CommandSupport.defineCommandSender(plugin, source), key, null);
	}

	public static final String FILE_FORMAT = ".yml";
	
	@Override
	public MessageHolder compile(@NotNull PluginInstance plugin, @NotNull TranslationSource source, 
			@NotNull String key, @Nullable Placeholder placeholder) {
		
		List<String> raw = this.getRawMessage(plugin, source, key, placeholder);
		
		if (raw == null) {
			return SingleMessageHolder.emptyAsBaseComponent(key);
		}
		
		switch (raw.size()) {
		case 0:
			return SingleMessageHolder.holdAsBaseComponent(key, "", source.getRandomColor());
		case 1:
			return SingleMessageHolder.holdAsBaseComponent(key, raw.get(0), source.getRandomColor());
		default:
			return ListMessageHolder.holdAsBaseComponent(key, raw, source.getRandomColor());
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
			return BukkitUtils.parseList(raw, "\\n");
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
		if (!source.isConsole()) {
			Player player = this.castPlayer(source);
			List<String> raw = this.getRawMessage(source.getPlugin(), source, key, placeholder);
			
			if (raw != null) {
				for (int i = 0; i < raw.size(); i++) {
					player.sendMessage(raw.get(i));
				}
			} else {
				player.sendMessage(key);
			}
		} else {
			List<String> raw = this.getRawMessage(source.getPlugin(), source, key, placeholder);
			ConsoleCommandSender sender = source.getAudience();
			if (raw != null) {
				for (int i = 0; i < raw.size(); i++) {
					sender.sendMessage(raw.get(i));
				}
			} else {
				sender.sendMessage(key);
			}
		}
	}
	
	@Override
	public void actionBar(@NotNull TranslationSource source, @NotNull String key, @Nullable Placeholder placeholder) {
		if (!source.isConsole()) {
			Player player = this.castPlayer(source);
			List<String> raw = MessageHolderUtil.deserializeListText(this.getRawMessage(source.getPlugin(), source, key, placeholder), source.getRandomColor());
			
			if (raw != null && raw.size() > 0) {
				ChefCore.getReflector().sendActionbarMessage(player, raw.get(0));
			} else {
				ChefCore.getReflector().sendActionbarMessage(player, key);
			}
		}
	}

	@Override
	public void title(@NotNull TranslationSource source, @NotNull String key, 
			@NotNull Duration fadeIn, @NotNull Duration stay, @NotNull Duration fadeOut,
			@Nullable Placeholder placeholder) {
		
		if (!source.isConsole()) {
			Player player = this.castPlayer(source);
			List<String> raw = MessageHolderUtil.deserializeListText(this.getRawMessage(source.getPlugin(), source, key, placeholder), source.getRandomColor());
			
			if (raw != null) {
				if (raw.size() < 2 && raw.size() > 0) {
					ChefCore.getReflector().sendTitleMessage(player, raw.get(0), "", (int) (fadeIn.toMillis() / 50), (int) (stay.toMillis() / 50), (int) (fadeOut.toMillis() / 50));
				} else {
					ChefCore.getReflector().sendTitleMessage(player, raw.get(0), raw.get(1), (int) (fadeIn.toMillis() / 50), (int) (stay.toMillis() / 50), (int) (fadeOut.toMillis() / 50));
				}
			} else {
				ChefCore.getReflector().sendTitleMessage(player, key, "", (int) (fadeIn.toMillis() / 50), (int) (stay.toMillis() / 50), (int) (fadeOut.toMillis() / 50));
			}
		}
	}

	@Override
	public void titleReset(@NotNull TranslationSource source) {
		if (!source.isConsole()) {
			ChefCore.getReflector().sendTitleMessage(this.castPlayer(source), "", "", 0, 20, 0);
		}
	}
	
	private Player castPlayer(@NotNull TranslationSource source) {
		Object obj = source.getAudience();
		
		if (obj == null) {
			throw new IllegalArgumentException("source cannot be null!");
		}
		
		if (!(obj instanceof Player)) {
			throw new IllegalArgumentException("'TranslationSource#getAudience()' must be implements 'org.bukkit.entity.Player'");
		}
		
		return (Player) obj;
	}
	
	private List<String> getRawMessage(@NotNull PluginInstance plugin, @NotNull TranslationSource source, @NotNull String key, @Nullable Placeholder placeholder) {
		requireNonNull(plugin, "plugin cannot be null!");
		requireNonNull(plugin, "source cannot be null!");
		requireNonNull(plugin, "key cannot be null!");
		
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
