package net.chefcraft.world.menu;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.server.Minigame;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.world.databridge.CoreArena;
import net.chefcraft.world.menu.game.GameMenuController;
import net.chefcraft.world.player.CorePlayer;
import net.chefcraft.world.util.MethodProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum MenuActionType {
	
	CONNECT,
	MSG,
	MSG_KEYED,
	SOUND,
	SOUND_KEYED,
	COMMAND,
	CONSOLE,
	CLOSE,
	UPDATE,
	CONNECT_EGGWARS_SOLO,
	CONNECT_EGGWARS_TEAM;
	
	public static void runMenuActions(CorePlayer corePlayer, List<String> texts) {
		for (String text : texts) {
			runMenuAction(corePlayer, text);
		}
	}
	
	public static void runMenuAction(CorePlayer corePlayer, @Nonnull String text) {
		Player player = corePlayer.getPlayer();
		text = text.replace("<PLAYER>", player.getName());
		if(text.split(" ")[0].equalsIgnoreCase("CONNECT")) {
			connect(corePlayer, text);
		} else if (text.split(" ")[0].equalsIgnoreCase("MSG")) {
			message (corePlayer, text);
		} else if (text.split(" ")[0].equalsIgnoreCase("MSG_KEYED")) {
			messageKeyed(corePlayer, text);
		} else if (text.split(" ")[0].equalsIgnoreCase("SOUND")) {
			sound(player, text);
		} else if (text.split(" ")[0].equalsIgnoreCase("SOUND_KEYED")) {
			soundKeyed(player, text);
		} else if (text.split(" ")[0].equalsIgnoreCase("COMMAND")) {
			command(player, text);
		} else if (text.split(" ")[0].equalsIgnoreCase("CONSOLE")) {
			console(player, text);
		} else if (text.split(" ")[0].equalsIgnoreCase("CLOSE")) {
			close(player);
		} else if (text.split(" ")[0].equalsIgnoreCase("UPDATE")) {
			update(player);
		} else if (text.split(" ")[0].equalsIgnoreCase("CONNECT_EGGWARS_SOLO")) {
			connectEggWarsSolo(corePlayer);
		} else if (text.split(" ")[0].equalsIgnoreCase("CONNECT_EGGWARS_TEAM")) {
			connectEggWarsTeam(corePlayer);
		}
	}
	
	public static final Comparator<CoreArena> ARENA_PLAYER_COUNT_COMPARATOR = new Comparator<CoreArena>() {
		@Override
		public int compare(CoreArena first, CoreArena second) {
			return first.getPlayersSize() - second.getPlayersSize();
		}
	};
	
	public static boolean connectEggWarsSolo(CorePlayer player) {
		List<CoreArena> sortedWaiting = new ArrayList<>(GameMenuController.getSoloArenas());
		Collections.sort(sortedWaiting, ARENA_PLAYER_COUNT_COMPARATOR);
		Collections.reverse(sortedWaiting);
		for (int i = 0; i < sortedWaiting.size(); i++) {
			CoreArena data = sortedWaiting.get(i);
			if (data.isInWaitingSlot()) {
				player.connectToGameServer(Minigame.EGGWARS, data.getServerName(), data.getNamespaceID(), false);
				return true;
			}
		}
		player.sendMessage("bungee.gameNotFound");
		player.playSound("error");
		return false;
	}
	
	public static boolean connectEggWarsTeam(CorePlayer player) {
		List<CoreArena> sortedWaiting = new ArrayList<>(GameMenuController.getTeamArenas());
		Collections.sort(sortedWaiting, ARENA_PLAYER_COUNT_COMPARATOR);
		Collections.reverse(sortedWaiting);
		for (int i = 0; i < sortedWaiting.size(); i++) {
			CoreArena data = sortedWaiting.get(i);
			if (data.isInWaitingSlot()) {
				player.connectToGameServer(Minigame.EGGWARS, data.getServerName(), data.getNamespaceID(), false);
				return true;
			}
		}
		player.sendMessage("bungee.gameNotFound");
		player.playSound("error");
		return false;
	}
	
	public static void connect(CorePlayer player, String text) {
		String server = null;
		try {
			server = text.split(" ")[1];
		} catch (Exception x) {
			ChefCore.getInstance().sendPlainMessage("&cIncorrect parameters in: "+ text + " &a[Correct Usage: CONNECT <server name>]", x);
			return;
		}
		try {
			ByteArrayOutputStream byteArrOut = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(byteArrOut);
			out.writeUTF("Connect");
            out.writeUTF(server);
            player.getPlayer().sendPluginMessage(ChefCore.getInstance(), "BungeeCord", byteArrOut.toByteArray());
            byteArrOut.close();
            out.close();
		} catch (Exception x) {
			player.sendMessage("bungee.connectionFalied", Placeholder.of("{SERVER}", server));
		}
	}
	
	public static void message(CorePlayer player, String text) {
		try {
			MethodProvider.SEND_MESSAGE.accept(player, MessageHolder.text(text.substring(5, text.length() - 1)));
		} catch (Exception x) {
			ChefCore.getInstance().sendPlainMessage("&cError the parsing MSG action!", x);
		}
	}
	
	public static void messageKeyed(CorePlayer player, String text) {
		try {
			player.sendMessage(text.substring(11, text.length() - 1));
		} catch (Exception x) {
			ChefCore.getInstance().sendPlainMessage("&cError the parsing MSG KEYED action!", x);
		}
	}
	
	public static void sound(Player player, String text) {
		try {
			String[] values = text.split(" ")[1].split(";");
			player.playSound(player.getLocation(), ChefCore.getReflector().getSoundByName(values[0]), Float.valueOf(values[1]), Float.valueOf(values[2]));
		} catch (Exception x) {
			ChefCore.getInstance().sendPlainMessage("&cAn error occured playing the sound: &e" + text, x);
		}
	}
	
	public static void soundKeyed(Player player, String text) {
		try {
			ChefCore.getSoundManager().playSound(player, text.split(" ")[1]);
		} catch (Exception x) {
			ChefCore.getInstance().sendPlainMessage("&cAn error occured parsing the node: &e" + text, x);
		}
	}
	
	public static void command(Player player, String text) {
		Bukkit.getServer().dispatchCommand(player, text.substring(8, text.length()));
	}
	
	public static void console(Player player, String text) {
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), text.substring(8, text.length()));
	}
	
	public static void close(Player player) {
		player.closeInventory();
	}
	
	public static void update(Player player) {
		ChefCore.getReflector().updateInventory(player);
	}
}
