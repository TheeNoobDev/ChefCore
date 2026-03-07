package net.chefcraft.world.databridge;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.database.AbstractDatabase;
import net.chefcraft.core.server.Minigame;
import net.chefcraft.world.player.CorePlayer;
import net.chefcraft.world.player.PrivateGameManager;
import net.chefcraft.world.player.PrivateGameStatus;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ServerDataBridge {
	
	private static final String PRIVATE_GAME_TABLE_ID = "private_games";
	private static final String EGGWARS_TABLE_ID = Minigame.EGGWARS.getStringValue();
	private static final List<String> EMPTY_ARENA_DATA = new ArrayList<>();

	private static AbstractDatabase database;
	
	public static void init() {
		database = DataBridgeDatabase.getInstance();
		database.createTable("(id TINYTEXT, item TINYTEXT, status TINYTEXT, current INT, max INT, perTeam INT, private BOOLEAN, server TINYTEXT, displayName TINYTEXT, gameID TINYTEXT);", EGGWARS_TABLE_ID);
		database.createTable("(minigame TINYTEXT, owner VARCHAR(16), map TINYTEXT, status TINYTEXT, id TINYTEXT, server TINYTEXT);", PRIVATE_GAME_TABLE_ID);
	}
	
	public static void sendPrivateGameRequest(Minigame type, CorePlayer owner, String mapName) {
		Player player = owner.getPlayer();
		String playerName = player.getName();
		ServerDataBridge.clearPrivateGameRequest(player.getName());
		
		if (!database.exists(playerName, "owner", PRIVATE_GAME_TABLE_ID)) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("minigame", type.getStringValue());
			map.put("owner", playerName);
			map.put("map", mapName);
			map.put("status", PrivateGameStatus.PENDING.getStringValue());
			map.put("id", "");
			map.put("server", "");
			database.createEntry(playerName, "owner", map, PRIVATE_GAME_TABLE_ID);
			
			owner.getPrivateGameManager().startStatusListener(type, mapName);
		} else {
			owner.sendMessage("privateGames.existsPlayer");
			ChefCore.getSoundManager().playSound(player, "error");
		}
	}
	
	public static void updatePrivateGameStatus(CorePlayer owner) {
		if (!hasPrivateGameRequest(owner)) return;
		try {
			PrivateGameManager manager = owner.getPrivateGameManager();
			
			PreparedStatement ps = database.getConnection().prepareStatement("SELECT * FROM " + PRIVATE_GAME_TABLE_ID + " WHERE owner=?");
			ps.setString(1, owner.getPlayer().getName());
			
			ResultSet results = ps.executeQuery();
			while (results.next()) {
				manager.updateGameData(results.getString(3), results.getString(4), results.getString(5), results.getString(6));
			}
			
			results.close();
			ps.close();
			
		} catch (SQLException x) {
			x.printStackTrace();
		}
	}
	
	public static boolean hasPrivateGameRequest(CorePlayer corePlayer) {
		return database.exists(corePlayer.getPlayer().getName(), "owner", PRIVATE_GAME_TABLE_ID);
	}
	
	public static void clearPrivateGameRequest(String ownerName) {
		database.deleteData(ownerName, "owner", PRIVATE_GAME_TABLE_ID);
	}
	
	public static void deletePrivateGame(String id) {
		database.deleteData(id, "id", PRIVATE_GAME_TABLE_ID);
	}
	
	public static String getPrivateGameCodeByOwner(String ownerName) {
		return database.getData("id", ownerName, "owner", PRIVATE_GAME_TABLE_ID);
	}
	
	public static String getMapNameByCode(String code) {
		return database.getData("map", code, "id", PRIVATE_GAME_TABLE_ID);
		
	}
	
	public static String getServerNameByCode(String code) {
		return database.getData("server", code, "id", PRIVATE_GAME_TABLE_ID);
	}
	
	public static Minigame getMinigameByCode(String code) {
		return Minigame.matchMinigame(database.getData("minigame", code, "id", PRIVATE_GAME_TABLE_ID));
	}
	
	public static boolean hasPrivateGame(String code) {
		return database.exists(code, "id", PRIVATE_GAME_TABLE_ID);
	}
	
	public static void write(Minigame type, CoreArena data) {
		boolean flag = database.exists(data.getNamespaceID(), "id", type.getStringValue());
		if (flag) {
			database.setData("item", data.getNamespaceID(), "id", type.getStringValue(), data.getMenuItem().toStringData());
			database.setData("status", data.getNamespaceID(), "id", type.getStringValue(), data.getGameStatus().name());
			database.setData("current", data.getNamespaceID(), "id", type.getStringValue(), data.getPlayersSize());
			database.setData("max", data.getNamespaceID(), "id", type.getStringValue(), data.getMaxPlayersSize());
			database.setData("perTeam", data.getNamespaceID(), "id", type.getStringValue(), data.getPlayersPerTeam());
			database.setData("private", data.getNamespaceID(), "id", type.getStringValue(), data.isPrivate());	
			database.setData("server", data.getNamespaceID(), "id", type.getStringValue(), data.getServerName());
			database.setData("displayName", data.getNamespaceID(), "id", type.getStringValue(), data.getMapDisplayName());
			database.setData("gameID", data.getNamespaceID(), "id", type.getStringValue(), data.getGameID());
		} else {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("id", data.getNamespaceID());
			map.put("item", data.getMenuItem().toStringData());
			map.put("status", data.getGameStatus().name());
			map.put("current", data.getPlayersSize());
			map.put("max", data.getMaxPlayersSize());
			map.put("perTeam", data.getPlayersPerTeam());
			map.put("private", data.isPrivate());
			map.put("server", data.getServerName());
			map.put("displayName", data.getMapDisplayName());
			map.put("gameID", data.getGameID());
			database.createEntry(data.getNamespaceID(), "id", map, type.getStringValue());
			
			map.clear();
			map = null;
			
		}
	} 
	
	public static void delete(Minigame type, CoreArena data) {
		database.deleteData(data.getNamespaceID(), "id", type.getStringValue());
	}
	
	public static void deleteAll(Minigame type, String serverName) {
		database.deleteData(serverName, "server", type.getStringValue());
	}
	
	public static List<CoreArena> read(Minigame type) {
		
		try {
			PreparedStatement ps = database.getConnection().prepareStatement("SELECT * FROM " + type.getStringValue());
			ResultSet results = ps.executeQuery();
			
			List<CoreArena> datas = new ArrayList<>();
			
			while (results.next()) {
				datas.add(CoreArena.fromResultSet(results));
			}
			
			results.close();
			ps.close();
			
			return datas;
		} catch (SQLException x) {
			x.printStackTrace();
		}
		
		return new ArrayList<>();
	}
	
	public static void writePrivateGameStatus(String owner, PrivateGameStatus status) {
		database.setData("status", owner, "owner", PRIVATE_GAME_TABLE_ID, status.getStringValue());
		
	}
	
	public static void writePrivateGameData(String owner, PrivateGameStatus status, String map, String id, String server) {
		database.setData("status", owner, "owner", PRIVATE_GAME_TABLE_ID, status.getStringValue());
		database.setData("map", owner, "owner", PRIVATE_GAME_TABLE_ID, map);
		database.setData("id", owner, "owner", PRIVATE_GAME_TABLE_ID, id);
		database.setData("server", owner, "owner", PRIVATE_GAME_TABLE_ID, server);
		
	}
	
	public static List<String> readPrivateGameRequests(Minigame type) {
		
		try {
			PreparedStatement ps = database.getConnection().prepareStatement("SELECT * FROM " + PRIVATE_GAME_TABLE_ID);
			ResultSet results = ps.executeQuery();
			
			List<String> datas = new ArrayList<>();
			
			while (results.next()) {
				if (results.getString(1).equalsIgnoreCase(type.getStringValue()) && results.getString(4).equalsIgnoreCase("pending")) {
					datas.add(results.getString(2) + ":" + results.getString(3));
				}
			}
			
			results.close();
			ps.close();
			
			return datas;
		} catch (SQLException x) {
			x.printStackTrace();
		}
		
		EMPTY_ARENA_DATA.clear();
		return EMPTY_ARENA_DATA;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
