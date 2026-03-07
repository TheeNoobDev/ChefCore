package net.chefcraft.world.listener;

import com.google.gson.JsonObject;
import net.chefcraft.core.ChefCore;
import net.chefcraft.core.database.AbstractPlayerDatabase;
import net.chefcraft.core.event.CorePlayerJoinEvent;
import net.chefcraft.core.event.CorePlayerQuitEvent;
import net.chefcraft.core.event.PlayerLanguageChangeEvent;
import net.chefcraft.core.language.Language;
import net.chefcraft.core.party.BackendPartyDataListener;
import net.chefcraft.core.server.Minigame;
import net.chefcraft.core.util.ErrorCode;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.core.util.ValueTranslation;
import net.chefcraft.reflection.base.language.BukkitMessageCompiler;
import net.chefcraft.world.handler.NameTagHandler;
import net.chefcraft.world.handler.PlayerListHandler;
import net.chefcraft.world.player.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.UUID;

public class PlayerJoinOrQuitListener implements Listener {
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent playerjoinevent) {
		Player player = playerjoinevent.getPlayer();
		CorePlayer corePlayer = createCorePlayer(player);
		
		if (corePlayer == null) {
			ChefCore.getReflector().getGameReflections().kickPlayer(player, "An initial error occurred! Please try re-connect. (Error Code: " + ErrorCode.CREATE_CORE_PLAYER + ")");
			return;
		}
		
		synchronized (corePlayer) {
			
			NameTagHandler.sendPlayerNameTag(corePlayer);
			PlayerListHandler.sendPlayerListHeaderAndFooter(corePlayer);
			ChefCore.getReflector().onPlayerJoin(player);
			
			if (ChefCore.getInstance().getConfig().getBoolean("disable_join_quit_messages")) {
				playerjoinevent.setJoinMessage(null);
			}
			
			BackendPartyDataListener.handleCorePlayerJoin(corePlayer);
			
			Bukkit.getPluginManager().callEvent(new CorePlayerJoinEvent(corePlayer));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
		Player player = playerQuitEvent.getPlayer();
		CorePlayer corePlayer = ChefCore.getCorePlayerByPlayer(player);
		
		synchronized (corePlayer) {
			Bukkit.getPluginManager().callEvent(new CorePlayerQuitEvent(corePlayer));
			
			if (corePlayer != null) {
				NameTagHandler.removePlayerNameTag(corePlayer);
				corePlayer.saveData();
				ChefCore.getReflector().onPlayerQuit(player);
				corePlayer.handlePlayerQuit();
			}
			
			if (ChefCore.getInstance().getConfig().getBoolean("disable_join_quit_messages")) {
				playerQuitEvent.setQuitMessage(null);
			}
		}
	}
	
	public static CorePlayer createCorePlayer(Player player) {		
		AbstractPlayerDatabase database = ChefCore.getPlayerDatabase();
		CorePlayer corePlayer = new CorePlayer(player);
		final UUID uuid = player.getUniqueId();
		
		if (database.exists(uuid)) {
			corePlayer.setLanguage(ChefCore.getLanguageByLocale(database.getData("Language", uuid)), false, null);
			
			database.setData("PlayerName", uuid, player.getName());
			
			try {
				JsonObject lastGame = ValueTranslation.getAsJsonObject(database.getData("LastGame", uuid));
				corePlayer.setLastGameID(Minigame.matchMinigame(lastGame.get("minigame").getAsString()), lastGame.get("id").getAsString());
			} catch (Exception x) { }
			
			Bukkit.getScheduler().runTaskLater(ChefCore.getInstance(), ()-> {
				loadPlayerData(corePlayer, database);
			}, 1L);
			
		} else {
			BukkitMessageCompiler.sendMessage(ChefCore.getInstance(), player, "language.loading");
			Bukkit.getScheduler().runTaskLater(ChefCore.getInstance(), ()-> {
				prepareFirstJoin(corePlayer, database);
			}, 30L);
		}
		
		return corePlayer;
	}
	
	private static void loadPlayerData(CorePlayer corePlayer, AbstractPlayerDatabase database) {
		try {
			final UUID uuid = corePlayer.getPlayer().getUniqueId();
			String playerData = database.getData("PlayerData", uuid);
			String gameData = database.getData("GameUtilsData", uuid);
			
			corePlayer.loadPlayerDataFromJson(ValueTranslation.getAsJsonObject(playerData));
			corePlayer.loadGameUtilsDataFromJson(ValueTranslation.getAsJsonObject(gameData));
		} catch (Exception x) {
			ChefCore.getInstance().sendPlainMessage("An error occurred loading player data for '" + corePlayer.getPlayer().getName() + "'", x);
		}
	}
	
	private static void prepareFirstJoin(CorePlayer corePlayer, AbstractPlayerDatabase database) {
		Player player = corePlayer.getPlayer();
		String locale = ChefCore.getReflector().getPlayerLocale(player);
		Language clientLanguage = locale != null ? ChefCore.getLanguageByLocale(locale.toLowerCase(Locale.ENGLISH)) : null;
		
		if (clientLanguage != null) {
			corePlayer.setLanguage(clientLanguage, true, PlayerLanguageChangeEvent.Cause.FIRST_LOAD);
		}
		
		/*
		 * You may not understand why we used this method here. If the client language does not match the server,
		 * the core player constructor already sets the first language to the server's own language.
	     * That's why language is never null (i hope so :)
		 */
		Language currentLanguage = corePlayer.getLanguage(); 
		
		LinkedHashMap<String, Object> datas = new LinkedHashMap<>();
		datas.put("PlayerName", player.getName());
		datas.put("UUID", player.getUniqueId().toString());
		datas.put("Language", currentLanguage.getLocale());
		datas.put("LastGame", "");
		datas.put("PlayerData", "");
		datas.put("GameUtilsData", "");
		datas.put("SettingsData", "");
		database.createPlayer(player.getUniqueId(), datas);	
		
		corePlayer.saveData();
		corePlayer.sendMessage("language.setted", Placeholder.of("{NAME}", currentLanguage.getName()).add("{LOCALE}", currentLanguage.getLocale()).add("{REGION}", currentLanguage.getRegion()));
	}
	
	public static void onPluginEnabled() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			createCorePlayer(player);
		}
	}
}
