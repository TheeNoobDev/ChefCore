package net.chefcraft.world.level;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.reflection.base.language.BukkitMessageCompiler;
import net.chefcraft.world.player.CorePlayer;
import net.chefcraft.world.util.CommandArg;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LevelCommandArg extends CommandArg {

	private ChefCore plugin = ChefCore.getInstance();
	
	public LevelCommandArg() {}
	
	@Override
	public String getName() {
		return "level";
	}

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {
		if (!sender.hasPermission("gamecore.commands.level")) {
			BukkitMessageCompiler.sendMessage(plugin, sender, "noPermission");
			return true;
		}
		
		if (!(sender instanceof Player)) {
			BukkitMessageCompiler.sendMessage(plugin, sender, "onlyPlayers");
			return true;
		}
		
		if (args.length < 2) {
			BukkitMessageCompiler.sendMessage(plugin, sender, "network.levelSystem.usage.all");
			return true;
		}
		
		Player player = (Player) sender;
		CorePlayer corePlayer = ChefCore.getCorePlayerByPlayer(player);
		
		if (args[1].equalsIgnoreCase("exp")) {
			
			if (args.length == 4 && args[2].equalsIgnoreCase("add")) {
				
				corePlayer.getLevelManager().addExp(Float.parseFloat(args[3]));
				BukkitMessageCompiler.sendMessage(plugin, sender, "network.levelSystem.exp.added", Placeholder.of("{NAME}", args[3]));
				
				
			} else {
				BukkitMessageCompiler.sendMessage(plugin, sender, "network.levelSystem.usage.exp");
			}
			return true;
		}
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}
}
