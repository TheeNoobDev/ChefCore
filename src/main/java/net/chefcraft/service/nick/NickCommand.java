package net.chefcraft.service.nick;

import net.chefcraft.core.ChefCore;
import net.chefcraft.reflection.base.language.BukkitMessageCompiler;
import net.chefcraft.service.nick.NickNamerService.ActionStatus;
import net.chefcraft.world.player.CorePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class NickCommand extends Command {

	private static final ChefCore PLUGIN = ChefCore.getInstance();
	
	public NickCommand() {
		super("nick", "Used for disguise", "Usage: /nick", Arrays.asList("disguise"));
		this.setPermission("core.services.nick");
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if (!sender.hasPermission("core.services.nick")) {
			BukkitMessageCompiler.sendMessage(PLUGIN, sender, "noPermission");
			return true;
		}
		
		if (sender instanceof ConsoleCommandSender) {
			BukkitMessageCompiler.sendMessage(PLUGIN, sender, "onlyPlayers");
			return true;
		}
		
		if (!ChefCore.getInstance().getConfig().getBoolean("nicknamer.enabled")) {
			BukkitMessageCompiler.sendMessage(PLUGIN, sender, "commands.nick.disabled");
			return true;
		}
		
		if (sender instanceof Player) {
			
			Player player = (Player) sender;
			CorePlayer corePlayer = ChefCore.getCorePlayerByPlayer(player);
			
			if (args.length > 1 && args[0].equalsIgnoreCase("setskin")) {
				if (args.length == 1) {
					BukkitMessageCompiler.sendMessage(PLUGIN, sender, "commands.nick.setSkinUsage");
				} else {
					
					ActionStatus status = corePlayer.getNickNamerService().changeSkin(args[1]);
					if (status == ActionStatus.EXECUTED) {
						BukkitMessageCompiler.sendMessage(PLUGIN, sender, "commands.nick.skinSetted");
						corePlayer.getNickNamerService().updateProfile();
					} else {
						BukkitMessageCompiler.sendMessage(PLUGIN, sender, "commands.nick.invalid");
						ChefCore.getSoundManager().playSound(player, "error");
					}
				}
			} else if (args.length == 1 && args[0].equalsIgnoreCase("restore")) {
				
				corePlayer.getNickNamerService().restoreProfile();
				BukkitMessageCompiler.sendMessage(PLUGIN, sender, "commands.nick.restored");
				
			} else if (args.length == 1 && args[0].equalsIgnoreCase("random")) {
				
				corePlayer.getNickNamerService().randomProfile();
				BukkitMessageCompiler.sendMessage(PLUGIN, sender, "commands.nick.nicked");
				
			} else if (args.length == 1) {
				
				if (corePlayer.getNickNamerService().changeBothAndUpdate(args[0])) {
					BukkitMessageCompiler.sendMessage(PLUGIN, sender, "commands.nick.nicked");
				} else {
					BukkitMessageCompiler.sendMessage(PLUGIN, sender, "commands.nick.invalid");
					ChefCore.getSoundManager().playSound(player, "error");
				}
				
			} else {
				BukkitMessageCompiler.sendMessage(PLUGIN, sender, "commands.nick.help");
			}
		}
		return true;
	}

}
