package net.chefcraft.world.command;

import net.chefcraft.core.ChefCore;
import net.chefcraft.reflection.base.language.BukkitMessageCompiler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class CoreCommandExecutor implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			BukkitMessageCompiler.sendMessage(ChefCore.getInstance(), sender, "help");
		} else if (CommandManager.containsArg(args[0])) {
			return CommandManager.getArgByName(args[0]).onCommand(sender, label, args);
		}
		return true;
	}
}






































