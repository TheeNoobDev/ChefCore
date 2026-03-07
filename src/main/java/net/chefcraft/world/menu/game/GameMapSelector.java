package net.chefcraft.world.menu.game;

import com.google.common.collect.ImmutableList;
import net.chefcraft.core.ChefCore;
import net.chefcraft.core.configuration.YamlFile;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.server.Minigame;
import net.chefcraft.core.util.JHelper;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.world.databridge.CoreArena;
import net.chefcraft.world.inventory.MultiInventory;
import net.chefcraft.world.inventory.MultiInventorySize;
import net.chefcraft.world.inventory.UtilityMenuItem;
import net.chefcraft.world.menu.AbstractTranslatableContent;
import net.chefcraft.world.menu.AbstractTranslatableMenu;
import net.chefcraft.world.menu.MenuController;
import net.chefcraft.world.player.CorePlayer;
import net.chefcraft.world.player.InventoryData;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class GameMapSelector extends AbstractTranslatableMenu {
	
	private static final Predicate<AbstractTranslatableContent> CONTENT_FILTER = content -> content instanceof GameMapSelectorItem;
	
	private final MapSelectorType mapSelectorType;
	private final Minigame minigame;
	
	private String dynamicItemWaitingNameKey = "";
	private String dynamicItemWaitingLoreKey = "";
	private String dynamicItemIngameNameKey = "";
	private String dynamicItemIngameLoreKey = "";
	private final List<Integer> waitingSlots;
	private final List<Integer> ingameSlots;
	
	public boolean lock = false;

	public GameMapSelector(String name, YamlFile file, Minigame minigame, MapSelectorType mapSelectorType) {
		super (ChefCore.getInstance(), name, file, true);
		this.minigame = minigame;
		this.mapSelectorType = mapSelectorType;
		FileConfiguration config = file.getConfig();
		this.waitingSlots = config.getIntegerList("waitingSlots");
		this.ingameSlots = config.getIntegerList("ingameSlots");
	}
	
	//Quick Fix Method
	public void removeLeftoverGameItems(boolean force) {
		if (!lock || force) {
			super.items.removeIf(CONTENT_FILTER);
			lock = true;
		}
	}
	
	public void forceUpdateMapList(List<CoreArena> arenaDataList) {
		Collection<AbstractTranslatableContent> filterGameItems = JHelper.filterCollection(super.items, CONTENT_FILTER);
		super.items.removeAll(filterGameItems);
		
		int a = waitingSlots.size();
		int b = ingameSlots.size();
		
		int waitSlot = 0;
		int ingameSlot = 0;
		
		int realPageSize = super.rows * 9;
		int virtualSize = realPageSize - 9;
		
		int x = 1;
		int y = 1;
		
		for (int i = 0; i < arenaDataList.size(); i++) {
			CoreArena arenaData = arenaDataList.get(i);
			GameMapSelectorItem selectorItem = new GameMapSelectorItem(arenaData.getNamespaceID(), this);
			selectorItem.setArenaData(arenaData);
			
			if (arenaData.isInWaitingSlot()) {
				int slot = waitingSlots.get(waitSlot);
				
				slot = ((x - 1) * virtualSize) + slot;
				
				selectorItem.setSlot(slot);
				waitSlot++;
				
				if (waitSlot >= a) {
					waitSlot = 0;
					x++;
				}
			} else {
				int slot = ingameSlots.get(ingameSlot);
				
				slot = ((y - 1) * virtualSize) + slot;
				
				selectorItem.setSlot(slot);
				ingameSlot++;
				
				if (ingameSlot >= b) {
					ingameSlot = 0;
					y++;
				}
			}
			
			super.items.add(selectorItem);
		}
		
		lock = false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public MultiInventory openMenu(CorePlayer corePlayer, Placeholder titlePlaceholders, Placeholder itemPlaceholders) {
		MessageHolder translatedTitle = corePlayer.getMessage(titleKey, titlePlaceholders);
		
		MultiInventory inv = new MultiInventory(MultiInventorySize.getByRows(rows), translatedTitle, corePlayer.getMessage("menuUtils.pageFormat"), 
				MenuController.getTranslatedMenuItem(corePlayer, UtilityMenuItem.PREVIOUS).asBukkit(),
				MenuController.getTranslatedMenuItem(corePlayer, UtilityMenuItem.CLOSE).asBukkit(),
				MenuController.getTranslatedMenuItem(corePlayer, UtilityMenuItem.NEXT).asBukkit());
		
		this.placeMenuItems(corePlayer, inv, titlePlaceholders, itemPlaceholders);
		
		inv.openFirstPage(corePlayer);
		
		corePlayer.getInventoryData().setMultiInventory(inv).setTranslatableMenu(this);
		
		if (updateDelay > 0) {
			startMenuUpdateTask(corePlayer, itemPlaceholders, itemPlaceholders);
		}
		return inv;
	}
	
	@Override
	public boolean updateMenu(CorePlayer corePlayer, Placeholder titlePlaceholders, Placeholder itemPlaceholders) {
		InventoryData inventoryData = corePlayer.getInventoryData();
		
		if (!inventoryData.hasMultiInventory() || !inventoryData.isActiveTranslatableMenu(this)) {
			return false;
		}
		MultiInventory multi = inventoryData.getMultiInventory();
		
		multi.clearSlots();
		
		return this.placeMenuItems(corePlayer, multi, titlePlaceholders, itemPlaceholders);
	}
	
	private boolean placeMenuItems(CorePlayer corePlayer, MultiInventory multiInv, Placeholder titlePlaceholders, Placeholder itemPlaceholders) {
		
		try {
			
			List<AbstractTranslatableContent> list = ImmutableList.copyOf(super.items);
			int j = list.size();
			
			for (int i = 0; i < j; i++) {
				AbstractTranslatableContent content = list.get(i);
				
				if (content instanceof GameMapSelectorItem) {
					GameMapSelectorItem selectorItem = (GameMapSelectorItem) content;
					CoreArena data = selectorItem.getArenaData();
					multiInv.setItem(content.getSlot(), selectorItem.getTranslatedItem(corePlayer, data.getPlayersSize(), null, null));	
				}
			}
			
			for (int i = 0; i < j; i++) {
				AbstractTranslatableContent content = list.get(i);
				
				if (content instanceof GameMapSelectorItem) continue;
				
				String flag = null;
				
				for (String itemState : this.parseItemStateForPlayer(corePlayer)) {
					if (content.hasItemState(itemState)) {
						flag = itemState;
					}
				}
				
				multiInv.setItemToAllPages(content.getSlot(), content.getTranslatedItem(corePlayer, content.getItemStack().getAmount(), itemPlaceholders, flag));
			}
			
			list = null;
			return true;
		} catch (Exception x) {
			ChefCore.getInstance().sendPlainMessage("An error occurred placing items to menu: " + super.name, x);
			return false;
		}
	}
	
	@Override
	public List<String> parseItemStateForPlayer(CorePlayer corePlayer) {
		List<String> resultList = new ArrayList<>();
		for (int i = 0; i < this.itemStates.size(); i++) {
			resultList.add(this.itemStates.get(i).replace("<PLAYER_LOCALE>", corePlayer.getLanguage().getLocale().toLowerCase()));
		}
		return resultList;
	}

	public Minigame getMinigame() {
		return minigame;
	}
	
	public MapSelectorType getMapSelectorType() {
		return mapSelectorType;
	}

	public String getDynamicItemWaitingNameKey() {
		return dynamicItemWaitingNameKey;
	}

	public void setDynamicItemWaitingNameKey(String dynamicItemWaitingNameKey) {
		this.dynamicItemWaitingNameKey = dynamicItemWaitingNameKey;
	}

	public String getDynamicItemWaitingLoreKey() {
		return dynamicItemWaitingLoreKey;
	}

	public void setDynamicItemWaitingLoreKey(String dynamicItemWaitingLoreKey) {
		this.dynamicItemWaitingLoreKey = dynamicItemWaitingLoreKey;
	}

	public String getDynamicItemIngameNameKey() {
		return dynamicItemIngameNameKey;
	}

	public void setDynamicItemIngameNameKey(String dynamicItemIngameNameKey) {
		this.dynamicItemIngameNameKey = dynamicItemIngameNameKey;
	}

	public String getDynamicItemIngameLoreKey() {
		return dynamicItemIngameLoreKey;
	}

	public void setDynamicItemIngameLoreKey(String dynamicItemIngameLoreKey) {
		this.dynamicItemIngameLoreKey = dynamicItemIngameLoreKey;
	}

	public List<Integer> getWaitingSlots() {
		return waitingSlots;
	}

	public List<Integer> getIngameSlots() {
		return ingameSlots;
	}

	public static enum MapSelectorType {
		SOLO, TEAM, PRIVATE;
	}
}
