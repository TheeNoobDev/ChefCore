package net.chefcraft.world.command.argument;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.reflection.base.language.BukkitMessageCompiler;
import net.chefcraft.world.player.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class PingCommand extends Command {

	private static final ChefCore PLUGIN = ChefCore.getInstance();
	
	public PingCommand() {
		super("ping");
		this.setLabel("ping");
		this.setDescription("Shows your ping");
		this.setUsage("Usage: /ping");
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if (!sender.hasPermission("gamecore.commands.ping")) {
			BukkitMessageCompiler.sendMessage(PLUGIN, sender, "noPermission");
			return true;
		}
		
		int len = args.length;
		
		if (len > 0) {
			for (int i = 0; i < len; i++) {
				Player player = Bukkit.getPlayer(args[i]);
				if (player == null) {
					BukkitMessageCompiler.sendMessage(PLUGIN, sender, "playerNotFound");
				} else {
					showPlayerPing(player, sender);
				}
			}
			return true;
		}
		
		if (len > 0 && sender instanceof ConsoleCommandSender) {
			Player player = Bukkit.getPlayer(args[0]);
			if (player == null) {
				BukkitMessageCompiler.sendMessage(PLUGIN, sender, "playerNotFound");
			} else {
				showPlayerPing(player, sender);
			}
			return true;
		}
		
		if (sender instanceof Player) {
			showPlayerPing(sender, null);
		} else {
			BukkitMessageCompiler.sendMessage(PLUGIN, sender, "commands.usage.ping");
		}
		return true;
	}

	public static void showPlayerPing(CommandSender player, @Nullable CommandSender pinger) {
		if (pinger == null) {
			BukkitMessageCompiler.sendMessage(PLUGIN, player, "commands.ping", Placeholder.of("{PING}", ChefCore.getReflector().getColoredPing((Player) player)));
		} else {
			if (player instanceof Player bp) {
				CorePlayer bpCore = ChefCore.getCorePlayerByPlayer(bp);
				BukkitMessageCompiler.sendMessage(PLUGIN, pinger, "commands.pingOther", Placeholder.of("{PING}", ChefCore.getReflector().getColoredPing(bp)).add("{PLAYER}", bpCore.getDisplayName()));
			}
		}
	}
}
