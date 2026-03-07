package net.chefcraft.world.menu;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.reflection.base.language.BukkitMessageCompiler;
import net.chefcraft.world.player.CorePlayer;
import net.chefcraft.world.util.BukkitUtils;
import net.chefcraft.world.util.CommandArg;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MenuOpenCommandArg extends CommandArg {

	public MenuOpenCommandArg() {}
	
	@Override
	public String getName() {
		return "openMenu";
	}

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {
		ChefCore plugin = ChefCore.getInstance();
		if (sender.hasPermission("core.commands.openMenu")) {
			if (sender instanceof ConsoleCommandSender) {
				BukkitMessageCompiler.sendMessage(plugin, sender, "onlyPlayers");
			} else {
				CorePlayer corePlayer = ChefCore.getCorePlayerByPlayer((Player) sender);
				
				if (args.length < 2) {
					corePlayer.sendMessage("commands.usage.openMenu");
				} else {
					AbstractTranslatableMenu transMenu = MenuController.getMenuByName(args[1]);
					if (transMenu == null) {
						corePlayer.sendMessage("menuNotFound");
					} else {
						if (!transMenu.getPermission().isEmpty() && sender.hasPermission(transMenu.getPermission())) {
							transMenu.openMenu(corePlayer, null, Placeholder.of("<PLAYER>", sender.getName()));
						} else {
							corePlayer.sendMessage("noPermission");
						}
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
		return args.length == 2 ? BukkitUtils.sortTabCompleterResults(args[1], MenuController.getMenuMap().keySet()) : null;
	}

}
