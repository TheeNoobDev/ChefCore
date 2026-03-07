package net.chefcraft.world.command.argument;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.server.Minigame;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.reflection.base.language.BukkitMessageCompiler;
import net.chefcraft.world.command.PrivateGameCommandManager;
import net.chefcraft.world.command.PrivateGameCommandManager.IPrivateGameCommandHandler;
import net.chefcraft.world.databridge.ServerDataBridge;
import net.chefcraft.world.player.CorePlayer;
import net.chefcraft.world.util.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrivateGameCommand extends Command {

	private static final ChefCore PLUGIN = ChefCore.getInstance();
	private static final String PERM_DEFAULT = "core.services.privategames";
	private static final String PERM_ADMIN = "core.services.privategames_admin";
	
	public PrivateGameCommand() {
		super("pg", "Used for private games", "Usage: /pg", Arrays.asList("privategame"));
	}
	
	public void join(String[] args, CorePlayer corePlayer, Player sender) {
		if (args.length > 1) {
			if (!corePlayer.connectToPrivateGameServer(args[1], false)) {
				BukkitMessageCompiler.sendMessage(PLUGIN, sender, "privateGames.notFound");
				ChefCore.getSoundManager().playSound(sender, "error");
			}
		} else {
			BukkitMessageCompiler.sendMessage(PLUGIN, sender, "privateGames.commands.usage.join");
		}
	}
	
	public void send(String[] args, CorePlayer corePlayer, Player sender) {
		if (args.length > 2) {
			List<String> nameList = new ArrayList<>();
			String serverCode = args[1];
			boolean isAll = args[2].contains("current");
			
			if (isAll) {
				for (CorePlayer corePlayerOther : ChefCore.getCorePlayers()) {
					this.sendPerPlayer(corePlayerOther, corePlayerOther.getPlayer(), serverCode);
				}
			} else {
				for (int i = 2; i < args.length; i++) {
					
					Player other = Bukkit.getPlayerExact(args[i]);
					
					if (other == null) {
						nameList.add(args[i]);
						continue;
					}
					
					CorePlayer otherCorePlayer = ChefCore.getCorePlayerByPlayer(other);
					
					this.sendPerPlayer(otherCorePlayer, other, serverCode);
				}
				
				if (!nameList.isEmpty()) {
					BukkitMessageCompiler.sendMessage(PLUGIN, sender, "privateGames.playerNotFound", Placeholder.of("{PLAYERS}", BukkitUtils.parseList(nameList, ", ")));
				}
			}
		} else {
			BukkitMessageCompiler.sendMessage(PLUGIN, sender, "privateGames.commands.usage.send");
		}
	}
	
	private void sendPerPlayer(CorePlayer otherCorePlayer, Player other, String serverCode) {
		if (otherCorePlayer != null && !otherCorePlayer.connectToPrivateGameServer(serverCode, false)) {
			otherCorePlayer.sendMessage("privateGames.notFound");
			ChefCore.getSoundManager().playSound(other, "error");
		}
	}
	
	public void create(String[] args, CorePlayer corePlayer, Player sender) {
		if (args.length > 2) {
			Minigame minigame = Minigame.matchMinigame(args[1]);
			String map = args[2];
			if (minigame != null) {
				ServerDataBridge.sendPrivateGameRequest(minigame, corePlayer, map);
			} else {
				BukkitMessageCompiler.sendMessage(PLUGIN, sender, "privateGames.minigameNotFound");
			}
		} else {
			BukkitMessageCompiler.sendMessage(PLUGIN, sender, "privateGames.commands.usage.create");
		}
	}
	
	public void cancel(String[] args, CorePlayer corePlayer, Player sender) {
		if (args.length > 0) {
			if (corePlayer.getPrivateGameManager().hasGameRequest()) {
				corePlayer.getPrivateGameManager().stopStatusListener();
				BukkitMessageCompiler.sendMessage(PLUGIN, sender, "privateGames.clear");
			} else {
				BukkitMessageCompiler.sendMessage(PLUGIN, sender, "privateGames.clearEmpty");
			}
		}
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			CorePlayer corePlayer = ChefCore.getCorePlayerByPlayer((Player) sender);
			if (player.hasPermission(PERM_ADMIN)) {
				if (args.length == 0) {
					BukkitMessageCompiler.sendMessage(PLUGIN, player, "privateGames.commands.helpAdmin");
				} else if (args[0].equalsIgnoreCase("join")) {
					this.join(args, corePlayer, player);
				} else if (args[0].equalsIgnoreCase("create")) {
					this.create(args, corePlayer, player);
				} else if (args[0].equalsIgnoreCase("send")) {
					this.send(args, corePlayer, player);
				} else if (args[0].equalsIgnoreCase("cancel")) {
					this.cancel(args, corePlayer, player);
					if (corePlayer.getCurrentMinigame() != null) {
						IPrivateGameCommandHandler handler = PrivateGameCommandManager.getHandler(corePlayer.getCurrentMinigame());
						if (handler != null) {
							handler.cancel(corePlayer);
						}
					}
				} else {
					IPrivateGameCommandHandler handler = PrivateGameCommandManager.getHandler(corePlayer.getCurrentMinigame());
					if (handler == null || corePlayer.getCurrentMinigame() == null) {
						BukkitMessageCompiler.sendMessage(PLUGIN, player, "privateGames.commands.mustBeIngame");
						return true;
					}
					
					if (args[0].equalsIgnoreCase("start")) {
						handler.start(corePlayer);
					} else if (args[0].equalsIgnoreCase("stop")) {
						handler.stop(corePlayer);
					} else if (args[0].equalsIgnoreCase("settings")) {
						handler.settings(corePlayer);
					} else if (args[0].equalsIgnoreCase("pause")) {
						handler.pause(corePlayer);
					} else if (args[0].equalsIgnoreCase("resume")) {
						handler.resume(corePlayer);
					} else if (args[0].equalsIgnoreCase("end")) {
						handler.end(corePlayer);
					}
				}
				
			} else if (player.hasPermission(PERM_DEFAULT)) {
				if (args.length == 0) {
					BukkitMessageCompiler.sendMessage(PLUGIN, player, "privateGames.commands.help");
				} else if (args[0].equalsIgnoreCase("join")) {
					this.join(args, corePlayer, player);
				}
			} else {
				BukkitMessageCompiler.sendMessage(PLUGIN, player, "noPermission");
			}
		}
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String label, String[] args) {
		if (args.length == 1) {
			return sender.hasPermission(PERM_ADMIN) ? BukkitUtils.sortTabCompleterResults(args[0], Arrays.asList("join", "create", "send", "cancel", "start", "stop", "settings", "pause", "resume", "end")) : Arrays.asList("join");
		}
		
		if (args.length > 2 && args[0].equalsIgnoreCase("send")) {
			List<String> arr = ChefCore.getCorePlayersNameList();
			arr.add("current");
			return BukkitUtils.sortTabCompleterResults("send", arr);
		}
		
		return null;
	}
}
