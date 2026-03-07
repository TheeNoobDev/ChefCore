package net.chefcraft.world.listener;

import net.chefcraft.core.ChefCore;
import net.chefcraft.world.inventory.MultiInventory;
import net.chefcraft.world.inventory.PageUtilResult;
import net.chefcraft.world.menu.AbstractTranslatableContent;
import net.chefcraft.world.menu.AbstractTranslatableMenu;
import net.chefcraft.world.menu.MenuActionType;
import net.chefcraft.world.menu.MenuClickType;
import net.chefcraft.world.player.CorePlayer;
import net.chefcraft.world.player.InventoryData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class MenuClickListener implements Listener {
	
	@EventHandler
	public void inventoryClickEvent(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();
		if (item == null || item.getType() == Material.AIR) return;
		Player player = (Player) event.getWhoClicked();
		CorePlayer corePlayer = ChefCore.getCorePlayerByPlayer(player);
		
		InventoryData data = corePlayer.getInventoryData();
		Inventory eventInv = event.getInventory();
		
		if (!data.hasTranslatableMenu()) return;
		
		event.setResult(Result.DENY);
		event.setCancelled(true);
		
		int slot = event.getRawSlot();
		int eventInvSize = eventInv.getSize();
		
		if (data.hasInventory() && data.isActiveInventory(eventInv) && slot < eventInvSize) {
			
			AbstractTranslatableContent content = data.getTranslatableMenu().getTranslatableContentBySlot(slot, corePlayer);
			
			if (content != null) {
				content.onClick(corePlayer);
				runActions(event, content, corePlayer);
			}
		} else if (data.isActiveMultiInventoryPage(eventInv)) {
			MultiInventory multi = data.getMultiInventory();
			
			if (data.hasTranslatableMenu()) { //For Game Selector menu
				AbstractTranslatableMenu currMenu = data.getTranslatableMenu();
				
				PageUtilResult result = multi.handleMenuClickForPageUtils(eventInv, slot, corePlayer, true, true);
				if (result.nothing()) {
					
					AbstractTranslatableContent content = currMenu.getTranslatableContentBySlot(multi.getClickedRawSlotForEvent(eventInv, slot), corePlayer);
					
					if (content != null) {
						content.onClick(corePlayer);
						runActions(event, content, corePlayer);
					}
				} else if (!result.isPageClosed()) {
					data.setMultiInventory(multi).setTranslatableMenu(currMenu);
				}
			} else { //For Cages or kill messages selector menu
				
				PageUtilResult result = multi.handleMenuClickForPageUtils(eventInv, slot, corePlayer, true, true);
				
				if (result.nothing()) {
					if (multi.hasGlobalClickAction()) {
						multi.getGlobalClickAction().run();
					} 
					
					Runnable runnable = multi.getClickAction(multi.getClickedRawSlotForEvent(eventInv, slot));
					
					if (runnable != null) {
						runnable.run();
					}
				} else if (!result.isPageClosed()) {
					data.setMultiInventory(multi);
				}
			}
		}
	}
	
	public void runActions(InventoryClickEvent event, AbstractTranslatableContent content, CorePlayer player) {
		Map<MenuClickType, List<String>> actions = content.getClickActions();
		
		if (content != null && !actions.isEmpty()) {
			List<String> middle = actions.get(MenuClickType.MIDDLE);
			List<String> right = actions.get(MenuClickType.RIGHT);
			List<String> left = actions.get(MenuClickType.LEFT);
			List<String> all = actions.get(MenuClickType.ALL);
			if (event.getClick() == ClickType.MIDDLE && middle != null) {
				MenuActionType.runMenuActions(player, middle);
			} else if(event.getClick().isRightClick() && right != null) {
				MenuActionType.runMenuActions(player, right);
			} else if(event.getClick().isLeftClick() && left != null) {
				MenuActionType.runMenuActions(player, left);
			}
			if (all != null) {
				MenuActionType.runMenuActions(player, all);
			}
		}
	}
}
