package net.chefcraft.world.command;

import net.chefcraft.world.util.BukkitUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public final class CoreTabCompleter implements TabCompleter {
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 1) {
			return BukkitUtils.sortTabCompleterResults(args[0], CommandManager.getAllCommandArguments());
		} else if (CommandManager.containsArg(args[0])) {
			return CommandManager.getArgByName(args[0]).onTabComplete(sender, label, args);
		}
		return null;
	}
}
