package net.chefcraft.world.command;

import net.chefcraft.core.PluginInstance;
import net.chefcraft.core.util.PreparedConditions;
import net.chefcraft.world.util.CommandArg;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class CommandManager<S> {

	private final Map<String, CommandArg<S>> arguments = new HashMap<>();
    private final PluginInstance pluginInstance;

    public CommandManager(@NotNull PluginInstance pluginInstance) {
        PreparedConditions.notNull(pluginInstance, "pluginInstance");
        this.pluginInstance = pluginInstance;
    }
	
	public void registerCommandArg(@NotNull CommandArg<S> commandArg) {
        PreparedConditions.notNull(commandArg, "commandArg");
		arguments.put(commandArg.getName().toLowerCase(Locale.ROOT), commandArg);
	}
	
	public void unregisterCommandArg(@NotNull String name) {
        PreparedConditions.notNull(name, "name");
		arguments.remove(name.toLowerCase(Locale.ROOT));
	}

    @Nullable
	public CommandArg<S> getArgByName(@NotNull String name) {
        PreparedConditions.notNull(name, "name");
		return arguments.get(name.toLowerCase(Locale.ROOT));
	}
	
	public boolean containsArg(@NotNull String name) {
        PreparedConditions.notNull(name, "name");
		return arguments.containsKey(name.toLowerCase(Locale.ROOT));
	}

    @NotNull
	public Collection<String> getCommandArgLabels() {
		return arguments.keySet();
	}

    @NotNull
    public Collection<CommandArg<S>> getAllCommandArgs() {
        return arguments.values();
    }

    @NotNull
    public PluginInstance getPluginInstance() {
        return pluginInstance;
    }
}
