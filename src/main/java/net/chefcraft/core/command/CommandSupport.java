package net.chefcraft.core.command;

import net.chefcraft.core.PluginInstance;
import net.chefcraft.core.language.TranslationSource;
import org.jetbrains.annotations.NotNull;

public interface CommandSupport {
	
	@NotNull TranslationSource getCommandSender(@NotNull PluginInstance plugin, @NotNull Object sender);
	
	static @NotNull TranslationSource defineCommandSender(@NotNull PluginInstance plugin, @NotNull Object sender) {
		return CommandSourceProvider.CURRENT.getCommandSender(plugin, sender);
	}
}
