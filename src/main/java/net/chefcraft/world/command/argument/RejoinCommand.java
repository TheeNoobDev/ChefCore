package net.chefcraft.world.command.argument;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.server.Minigame;
import net.chefcraft.core.util.Pair;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.reflection.base.language.BukkitMessageCompiler;
import net.chefcraft.world.databridge.CoreArena;
import net.chefcraft.world.databridge.ServerDataBridge;
import net.chefcraft.world.handler.AbstractGameHandler;
import net.chefcraft.world.player.CorePlayer;
import net.chefcraft.world.util.ProxyUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RejoinCommand extends Command {

	private static final ChefCore PLUGIN = ChefCore.getInstance();
	
	public RejoinCommand() {
		super("rejoin");
		this.setLabel("rejoin");
		this.setDescription("If the last game you played is available, you can rejoin.");
		this.setUsage("Usage: /rejoin");
	}
	
	public static boolean connectPlayerToLastGame(CorePlayer corePlayer) {
		Pair<Minigame, String> id = corePlayer.getLastGameID();
		
		if (id.getFirst() == null || id.getSecond() == null) {
			return false;
		}
		
		AbstractGameHandler game = AbstractGameHandler.getGameHandlerByMinigame(id.getFirst());
		
		if (game != null) {
			AbstractGameHandler.StateResult state = game.rejoin(corePlayer);
			
			if (state.rejoined()) {
				corePlayer.sendMessage("network.connected", Placeholder.of("{ID}", id.getSecond()));
				return true;
			} else {
				
				if (state.rejoinDisabled()) {
					corePlayer.sendMessage("network.rejoinDisabled");
				} else {
					corePlayer.sendMessage("network.connectionError", Placeholder.of("{STATE}", state));
				}
				
			}
			
		} else if (ProxyUtils.isBungeeModeEnabled()) {
			List<CoreArena> arenas =  ServerDataBridge.read(id.getFirst());
			
			for (CoreArena arena : arenas) {
				if (arena.getNamespaceID().equals(id.getSecond()) && !arena.getServerName().equals(ChefCore.getServerNameFromBungee())) {
					corePlayer.sendMessage("network.connected", Placeholder.of("{ID}", id.getSecond()));
					ProxyUtils.sendPlayerToServer(corePlayer, arena.getServerName());
					return true;
				}
			}
			
			corePlayer.sendMessage("network.connectionError", Placeholder.of("{STATE}", AbstractGameHandler.StateResult.GAME_NOT_FOUND));
			
		} else {
			corePlayer.sendMessage("network.cannotRejoin");
		}
		
		return false;
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if (!sender.hasPermission("gamecore.commands.rejoin")) {
			BukkitMessageCompiler.sendMessage(PLUGIN, sender, "noPermission");
			return true;
		}
		
		if (args.length > 0 && sender.hasPermission("gamecore.commands.rejoin.other")) {
			Player player = Bukkit.getPlayer(args[0]);
			if (player == null) {
				BukkitMessageCompiler.sendMessage(PLUGIN, sender, "playerNotFound");
			} else {
				CorePlayer corePlayer = ChefCore.getCorePlayerByPlayer(player);
				if (corePlayer != null) {
					connectPlayerToLastGame(corePlayer);
				}
			}
			
			
			return true;
		}
		
		if (sender instanceof Player) {
			CorePlayer corePlayer = ChefCore.getCorePlayerByPlayer((Player) sender);
			if (corePlayer != null) {
				connectPlayerToLastGame(corePlayer);
			}
			return true;
		}
		
		return true;
	}
}
