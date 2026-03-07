package net.chefcraft.world.command.argument;

import net.chefcraft.core.ChefCore;
import net.chefcraft.reflection.base.language.BukkitMessageCompiler;
import net.chefcraft.world.menu.MenuController;
import net.chefcraft.world.menu.game.GameMenuController;
import net.chefcraft.world.util.CommandArg;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.util.List;

public class ReloadCommandArg extends CommandArg {

	public ReloadCommandArg() {}

	@Override
	public String getName() {
		return "reload";
	}

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {
		if (sender.hasPermission("core.commands.reload")) {
			try {
				ChefCore plugin = ChefCore.getInstance();
				plugin.reloadConfig();
				ChefCore.getSoundManager().getYamlFile().reload();
				ChefCore.reloadLanguageConfigs();
				MenuController.reloadMenuConfigs();
				GameMenuController.reloadMenuConfigs();
				BukkitMessageCompiler.sendMessage(plugin, sender, "reload");
			} catch (IOException | InvalidConfigurationException e) {
				ChefCore.getInstance().sendPlainMessage("&cAn error occured reloading the plugin!", e);
			}
		} else {
			BukkitMessageCompiler.sendMessage(ChefCore.getInstance(), sender, "noPermission");
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}
}
