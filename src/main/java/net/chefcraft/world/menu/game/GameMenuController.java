package net.chefcraft.world.menu.game;

import com.google.common.collect.ImmutableList;
import net.chefcraft.core.ChefCore;
import net.chefcraft.core.configuration.CoreConfigKey;
import net.chefcraft.core.configuration.GlobalConfigHandler;
import net.chefcraft.core.configuration.YamlFile;
import net.chefcraft.core.server.Minigame;
import net.chefcraft.core.util.JHelper;
import net.chefcraft.world.databridge.CoreArena;
import net.chefcraft.world.databridge.ServerDataBridge;
import net.chefcraft.world.inventory.CoreInventoryType;
import net.chefcraft.world.menu.MenuController;
import net.chefcraft.world.player.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;

public class GameMenuController {

	public static final Comparator<CoreArena> ARENA_DATA_COMPARATOR = (first, second) -> first.getPlayersPerTeam() - second.getPlayersPerTeam();
	public static final Predicate<CoreArena> SOLO_ARENAS = arenadata -> !arenadata.isPrivate() && arenadata.isSolo();
	public static final Predicate<CoreArena> TEAM_ARENAS = arenadata -> !arenadata.isPrivate() && !arenadata.isSolo();
	public static final Predicate<CoreArena> PRIVATE_ARENAS = arenadata -> arenadata.isPrivate();
	public static final Function<CoreArena, Integer> PLAYER_COUNT_FUNC = x -> x.getPlayersSize();
	
	private static Map<String, Integer> ingamePlayersCount = new HashMap<>();
	private static List<GameMapSelector> gamesSelectorList = new ArrayList<>();
	
	private static BukkitTask updateTask;
	private static File menusDirectory;
	
	private static List<CoreArena> soloArenas = new ArrayList<>();
	private static List<CoreArena> teamArenas = new ArrayList<>();
	private static List<CoreArena> privateArenas = new ArrayList<>();
	
	public static List<GameMapSelector> getGamesSelectorList() {
		return ImmutableList.copyOf(gamesSelectorList);
	}
	
	public static List<CoreArena> getArenaDatasByMapSelectorType(GameMapSelector.MapSelectorType selectorType) {
		switch (selectorType) {
		case PRIVATE:
			return privateArenas;
		case SOLO:
			return soloArenas;
		case TEAM:
			return teamArenas;
		default:
			return null;
		}
	}
	
	public static void updateArenaListByName(String name) {
		GameMapSelector selector = getGameMapSelectorByName(name);
		if (selector == null) return;
		List<CoreArena> dataList = getArenaDatasByMapSelectorType(selector.getMapSelectorType());
		
		if (dataList.isEmpty()) {
			selector.removeLeftoverGameItems(false);
		} else {
			selector.forceUpdateMapList(dataList);
		}
		
		if (name.equalsIgnoreCase("eggwars_team_selector")) {
			ingamePlayersCount.put("eggwars_team", JHelper.calcListIntCount(dataList, PLAYER_COUNT_FUNC));
		} else if (name.equalsIgnoreCase("eggwars_solo_selector")) {
			ingamePlayersCount.put("eggwars_solo", JHelper.calcListIntCount(dataList, PLAYER_COUNT_FUNC));
		} else if (name.equalsIgnoreCase("eggwars_pg_selector")) {
			ingamePlayersCount.put("eggwars_pg", JHelper.calcListIntCount(dataList, PLAYER_COUNT_FUNC));
		}
	}
	
	public static int getCurrentPlayerCountByType(String type) {
		if (ingamePlayersCount.containsKey(type)) {
			return ingamePlayersCount.get(type);
		}
		return 0;
	}
	
	public static void startUpdateTask() {
		stopUpdateTask();
		updateTask = new BukkitRunnable() {
			
			public void run() {
				List<CoreArena> arenaDatas = ServerDataBridge.read(Minigame.EGGWARS);
				Collections.sort(arenaDatas, ARENA_DATA_COMPARATOR);
				teamArenas = JHelper.filterList(arenaDatas, TEAM_ARENAS);
				updateArenaListByName("eggwars_team_selector");
				
				soloArenas = JHelper.filterList(arenaDatas, SOLO_ARENAS);
				updateArenaListByName("eggwars_solo_selector");
				
				privateArenas = JHelper.filterList(arenaDatas, PRIVATE_ARENAS);
				updateArenaListByName("eggwars_pg_selector");
			}
			
		}.runTaskTimerAsynchronously(ChefCore.getInstance(), 40L, 40L);
	}
	
	public static void stopUpdateTask() {
		if (updateTask != null) {
			updateTask.cancel();
		}
	}
	
	public static void reloadMenuConfigs() {
		for (CorePlayer player : ChefCore.getCorePlayers()) {
			player.safelyCloseExistingMenu();
		}
		gamesSelectorList.clear();
		
		Bukkit.getScheduler().runTaskLater(ChefCore.getInstance(), () -> {
			menusDirectory.mkdirs();
			for (File file : menusDirectory.listFiles()) {
				if (!file.isDirectory() && file.getName().contains(".yml")) {
					try {
						gamesSelectorList.add(loadMenuFromConfig(YamlFile.create(file.getAbsolutePath())));
					} catch (IOException e) {
						e.printStackTrace();
						Bukkit.getLogger().log(Level.SEVERE, "Failed to load '" + file.getName() + "' game menu!"); 
					}
				}
			}
			ChefCore.getInstance().sendPlainMessage("&aTotal " + gamesSelectorList.size() + " game menu" + ((gamesSelectorList.size() > 1) ? "s" : "") + " re-loaded.");
		}, 5L);
	}
	
	public static void loadMenus() {
		ChefCore plugin = ChefCore.getInstance();
		GlobalConfigHandler cfgHandler = ChefCore.getGlobalConfigHandler();
		menusDirectory = cfgHandler.createFileDirectory(CoreConfigKey.GAME_MENUS, "game_menus");
		
		if (plugin.getConfig().getBoolean("load_example_configurations")) {
			cfgHandler.copyResource(CoreConfigKey.GAME_MENUS, "game_menus/eggwars_pg_selector.yml");
			cfgHandler.copyResource(CoreConfigKey.GAME_MENUS, "game_menus/eggwars_solo_selector.yml");
			cfgHandler.copyResource(CoreConfigKey.GAME_MENUS, "game_menus/eggwars_team_selector.yml");
		}
		
		for (File file : menusDirectory.listFiles()) {
			if (!file.isDirectory() && file.getName().contains(".yml")) {
				try {
					gamesSelectorList.add(loadMenuFromConfig(YamlFile.create(file.getAbsolutePath())));
				} catch (IOException e) {
					e.printStackTrace();
					Bukkit.getLogger().log(Level.SEVERE, "Failed to load '" + file.getName() + "' game menu!"); 
				}
			}
		}
		
		Bukkit.getScheduler().runTaskLater(ChefCore.getInstance(), ()-> {
			ChefCore.getInstance().sendPlainMessage("&aTotal " + gamesSelectorList.size() + " game menu" + ((gamesSelectorList.size() > 1) ? "s" : "") + " loaded.");
		}, 40L);
	}
	
	public static GameMapSelector loadMenuFromConfig(YamlFile yaml) {
		FileConfiguration config = yaml.getConfig();
		GameMapSelector menu = new GameMapSelector(yaml.getName().replace(".yml", ""), yaml, 
				Minigame.valueOf(config.getString("minigame").toUpperCase()), GameMapSelector.MapSelectorType.valueOf(config.getString("mapSelectorType").toUpperCase()));
		
		menu.setDynamicItemWaitingNameKey(config.getString("dynamicGameItem.waiting.name"));
		menu.setDynamicItemWaitingLoreKey(config.getString("dynamicGameItem.waiting.lore"));
		
		menu.setDynamicItemIngameNameKey(config.getString("dynamicGameItem.ingame.name"));
		menu.setDynamicItemIngameLoreKey(config.getString("dynamicGameItem.ingame.lore"));
		
		menu.setPermission(config.getString("permission"));
		menu.setTitleKey(config.getString("titleKey"));
		menu.setInventoryType(CoreInventoryType.valueOf(config.getString("type").toUpperCase()));
		menu.setRows(config.getInt("rows"));
		menu.setUpdateDelay(config.getInt("updateDelay"));
		if (config.isSet("itemStates")) {
			for (String itemState : config.getStringList("itemStates")) {
				menu.getItemStates().add(itemState);
			}
		}
		for (String itemNode : config.getConfigurationSection("contents").getKeys(false)) {
			ConfigurationSection section = config.getConfigurationSection("contents." + itemNode);
			menu.getTranslatableContents().add(MenuController.loadTranslatableContent(ChefCore.getInstance(), section, config.getString("contentsPrefixKey")));
		}
		
		return menu;
	}
	
	public static GameMapSelector findGameMapSelector(Minigame minigame, GameMapSelector.MapSelectorType mapSelectorType) {
		for (GameMapSelector selector : gamesSelectorList) {
			if (selector.getMinigame() == minigame && selector.getMapSelectorType() == mapSelectorType) {
				return selector;
			}
		}
		return null;
	}
	
	public static GameMapSelector getGameMapSelectorByName(String name) {
		for (GameMapSelector selector : gamesSelectorList) {
			if (selector.getName().equalsIgnoreCase(name)) {
				return selector;
			}
		}
		return null;
	}

	public static List<CoreArena> getSoloArenas() {
		return soloArenas;
	}

	public static List<CoreArena> getTeamArenas() {
		return teamArenas;
	}

	public static List<CoreArena> getPrivateArenas() {
		return privateArenas;
	}
}
