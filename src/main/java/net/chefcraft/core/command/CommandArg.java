package net.chefcraft.world.util;

import net.chefcraft.core.command.CommandSupport;
import net.chefcraft.core.language.TranslationSource;
import net.chefcraft.core.util.PreparedConditions;
import net.chefcraft.world.command.CommandManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @param <S> CommandSender
 * @since 0.0.1 updated 1.4.0
 */
public abstract class CommandArg<S> {

    private final CommandManager<S> commandManager;
    private final String name;

    public CommandArg(@NotNull CommandManager<S> commandManager, @NotNull String name) {
        PreparedConditions.notNull(commandManager, "commandManager");
        PreparedConditions.notNull(name, "name");
        this.commandManager = commandManager;
        this.name = name;
    }
	
	public abstract boolean onCommand(@NotNull S sender, @NotNull String label, @NotNull String @NotNull [] args);

	public abstract @Nullable List<String> onTabComplete(@NotNull S sender, @NotNull String label, @NotNull String @NotNull [] args);

    public @NotNull String getName() {
        return this.name;
    }

    public @Nullable TranslationSource senderAsTranslationSource(@Nullable S sender) {
        return sender != null ? CommandSupport.defineCommandSender(this.commandManager.getPluginInstance(), sender) : null;
    }

    public void register() {
        this.commandManager.registerCommandArg(this);
    }

    public void unregister() {
        this.commandManager.unregisterCommandArg(this.getName());
    }
}
