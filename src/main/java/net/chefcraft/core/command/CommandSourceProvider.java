package net.chefcraft.core.command;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.chefcraft.core.PlatformProvider;
import net.chefcraft.core.PluginInstance;
import net.chefcraft.core.component.RandomColor;
import net.chefcraft.core.language.Language;
import net.chefcraft.core.language.TranslationSource;
import net.chefcraft.core.party.CorePartyManager;
import net.chefcraft.proxy.ChefProxyCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CommandSourceProvider<T> implements TranslationSource {
	
	static final Map<PluginInstance, CommandSourceProvider<?>> COMMAND_SUPPORT_MAP = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	public static CommandSourceProvider<org.bukkit.command.ConsoleCommandSender> getOrCreateBukkit(@NotNull PluginInstance plugin, @Nullable Language language) {
		CommandSourceProvider<?> provider = COMMAND_SUPPORT_MAP.get(plugin);
		
		if (provider != null) {
			return (CommandSourceProvider<org.bukkit.command.ConsoleCommandSender>) provider;
		}
		
		provider = new CommandSourceProvider<>(plugin, plugin.getDefaultLanguage(), org.bukkit.Bukkit.getServer().getConsoleSender());
		COMMAND_SUPPORT_MAP.put(plugin, provider);

		return (CommandSourceProvider<org.bukkit.command.ConsoleCommandSender>) provider;
	}
	
	@SuppressWarnings("unchecked")
	public static CommandSourceProvider<com.velocitypowered.api.proxy.ConsoleCommandSource> getOrCreateVelocity(@NotNull PluginInstance plugin, @Nullable Language language) {
		CommandSourceProvider<?> provider = COMMAND_SUPPORT_MAP.get(plugin);
		
		if (provider != null) {
			return (CommandSourceProvider<com.velocitypowered.api.proxy.ConsoleCommandSource>) provider;
		}
		
		provider = new CommandSourceProvider<>(plugin, plugin.getDefaultLanguage(), 
				ChefProxyCore.getInstance().getServer().getConsoleCommandSource());
		
		COMMAND_SUPPORT_MAP.put(plugin, provider);

		return (CommandSourceProvider<com.velocitypowered.api.proxy.ConsoleCommandSource>) provider;
	}
	
	static final CommandSupport COMMAND_SUPPORT_BUKKIT = new CommandSupport() {
		
		@Override
		public @NotNull TranslationSource getCommandSender(@NotNull PluginInstance plugin, @NotNull Object sender) {
			if (sender instanceof org.bukkit.command.ConsoleCommandSender) {
				return getOrCreateBukkit(plugin, plugin.getDefaultLanguage());
			}
			
			if (sender instanceof org.bukkit.entity.Player player) {
				return plugin.getTranslationSourceByUniqueId(player.getUniqueId());
			}
			
			return null;
		}
	};
	
	
	static final CommandSupport COMMAND_SUPPORT_VELOCITY = new CommandSupport() {

		@Override
		public @NotNull TranslationSource getCommandSender(@NotNull PluginInstance plugin, @NotNull Object sender) {
			if (sender instanceof com.velocitypowered.api.proxy.ConsoleCommandSource) {
				return getOrCreateVelocity(plugin, plugin.getDefaultLanguage());
			}
			
			if (sender instanceof com.velocitypowered.api.proxy.Player player) {
				return plugin.getTranslationSourceByUniqueId(player.getUniqueId());
			}
			
			return null;
		}
	};
	
	static final CommandSupport CURRENT = PlatformProvider.usingVelocity() ? COMMAND_SUPPORT_VELOCITY : COMMAND_SUPPORT_BUKKIT;
	
	final T console;
	final Language language;
	final PluginInstance instance;
	final RandomColor randomColor = new RandomColor();
	
	CommandSourceProvider(@NotNull PluginInstance instance, @NotNull Language language, @NotNull T console) {
		this.console = console;
		this.instance = instance; 
		this.language = language;
	}
	
	@SuppressWarnings("unchecked")
	@Override 
	public T getAudience() {
		return this.console;
	}

	@Override
	public Language getLanguage() {
		return this.language;
	}

	@Override
	public PluginInstance getPlugin() {
		return this.instance;
	}

	@Override
	public RandomColor getRandomColor() {
		return this.randomColor;
	}

	@Override
	public boolean isConsole() {
		return true;
	}

	@Override
	@CanIgnoreReturnValue
	public @NotNull CorePartyManager getPartyManager() {
		return CorePartyManager.EMPTY;
	}
}
