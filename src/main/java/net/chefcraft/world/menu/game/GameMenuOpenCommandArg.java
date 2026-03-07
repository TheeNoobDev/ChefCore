package net.chefcraft.world.menu.game;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.util.JHelper;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.reflection.base.language.BukkitMessageCompiler;
import net.chefcraft.world.menu.AbstractTranslatableMenu;
import net.chefcraft.world.player.CorePlayer;
import net.chefcraft.world.util.BukkitUtils;
import net.chefcraft.world.util.CommandArg;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GameMenuOpenCommandArg extends CommandArg {

	public GameMenuOpenCommandArg() {}
	
	@Override
	public String getName() {
		return "openGameMenu";
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
					corePlayer.sendMessage("commands.usage.openGameMenu");
				} else {
					AbstractTranslatableMenu mapSelector = GameMenuController.getGameMapSelectorByName(args[1]);
					if (mapSelector == null) {
						corePlayer.sendMessage("menuNotFound");
					} else {
						if (!mapSelector.getPermission().isEmpty() && sender.hasPermission(mapSelector.getPermission())) {
							mapSelector.openMenu(corePlayer, null, Placeholder.of("<PLAYER>", sender.getName()));
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
		return args.length == 2 ? BukkitUtils.sortTabCompleterResults(args[1], JHelper.mapList(GameMenuController.getGamesSelectorList(), val -> val.getName())) : null;
	}
}
