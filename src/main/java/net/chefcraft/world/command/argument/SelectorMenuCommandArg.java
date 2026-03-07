package net.chefcraft.world.command.argument;

import net.chefcraft.core.ChefCore;
import net.chefcraft.reflection.base.language.BukkitMessageCompiler;
import net.chefcraft.world.cage.CageUtils;
import net.chefcraft.world.loot.KillMessages;
import net.chefcraft.world.player.CorePlayer;
import net.chefcraft.world.util.BukkitUtils;
import net.chefcraft.world.util.CommandArg;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.logging.Level;
public class SelectorMenuCommandArg extends CommandArg {

	public SelectorMenuCommandArg() {}
	
	@Override
	public String getName() {
		return "openSelectorMenu";
	}

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {
		ChefCore plugin = ChefCore.getInstance();
		if (sender.hasPermission("core.commands.openSelectorMenu")) {
			if (sender instanceof ConsoleCommandSender) {
				BukkitMessageCompiler.sendMessage(plugin, sender, "onlyPlayers");
			} else {
				if (args.length < 2) {
					BukkitMessageCompiler.sendMessage(plugin, sender, "commands.usage.openSelectorMenu");
				} else if (sender instanceof Player) {

					CorePlayer corePlayer = ChefCore.getCorePlayerByPlayer(((Player) sender));
					
					if (corePlayer == null) {
						ChefCore.log(Level.SEVERE, "Core player is null! (SelectorMenuCommandArg.class)");
						return true;
					}
					
					if (args[1].equalsIgnoreCase("killMessages")) {
						KillMessages.openKillMessagesMenu(corePlayer);
					} else if (args[1].equalsIgnoreCase("cages")) {
						CageUtils.openCagesMenu(corePlayer);
					}
				}
			}
		} else {
			BukkitMessageCompiler.sendMessage(plugin, sender, "noPermission");
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return args.length == 2 ? BukkitUtils.sortTabCompleterResults(args[1], "killMessages", "cages") : null;
	}

}
