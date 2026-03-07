package net.chefcraft.world.menu;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.configuration.YamlFile;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.world.inventory.CoreInventoryType;
import net.chefcraft.world.menu.game.GameMenuController;
import net.chefcraft.world.player.CorePlayer;
import net.chefcraft.world.player.InventoryData;
import net.chefcraft.world.util.MethodProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class CoreTranslatableMenu extends AbstractTranslatableMenu {
	
	//pail update menu system

	public CoreTranslatableMenu(String name, YamlFile yamlFile) {
		super(ChefCore.getInstance(), name, yamlFile, true);
	}
	
	public Placeholder placeholderJoinArenaDatas(Placeholder holder) {
		Placeholder newHolder = Placeholder.of("{EGG_SOLO_COUNT}", GameMenuController.getCurrentPlayerCountByType("eggwars_solo"))
				.add("{EGG_TEAM_COUNT}", GameMenuController.getCurrentPlayerCountByType("eggwars_team"))
				.add("{EGG_PG_COUNT}", GameMenuController.getCurrentPlayerCountByType("eggwars_pg"));
		if (holder == null) {
			return newHolder;
		} else {
			return holder.merge(newHolder);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Inventory openMenu(CorePlayer corePlayer, Placeholder titlePlaceholders, Placeholder itemPlaceholders) {
		Player player = corePlayer.getPlayer();
		boolean isHopperMenu = (inventoryType == CoreInventoryType.HOPPER);
		MessageHolder translatedTitle = corePlayer.getMessage(titleKey, titlePlaceholders);
		
		Inventory inv = isHopperMenu ? 
				MethodProvider.CREATE_INVENTORY_BY_TYPE.apply(null, InventoryType.HOPPER, translatedTitle) : 
					MethodProvider.CREATE_INVENTORY.apply(null, 9 * rows, translatedTitle);
		
		int size = inv.getSize();
		for (AbstractTranslatableContent content : items) {
			int slot = content.getSlot();
			if (size > slot) {
				String flag = null;
				for (String itemState : this.parseItemStateForPlayer(corePlayer)) {
					if (content.hasItemState(itemState)) {
						flag = itemState;
					}
				}
				inv.setItem(slot, content.getTranslatedItem(corePlayer, content.itemStack.getAmount(), placeholderJoinArenaDatas(itemPlaceholders), flag));
			} else {
				ChefCore.getInstance().sendPlainMessage("&cError> Items that exceed menu slot! " + "&fMenu Name: &e" + name + " &fContent Name: &e" + content.getRawName());
			}
		}
		player.openInventory(inv);
		
		corePlayer.getInventoryData().setInventory(inv).setTranslatableMenu(this);
		if (updateDelay > 0) {
			startMenuUpdateTask(corePlayer, titlePlaceholders, placeholderJoinArenaDatas(itemPlaceholders));
		}
		return inv;
	}
	
	@Override
	public boolean updateMenu(CorePlayer corePlayer, Placeholder titlePlaceholders, Placeholder itemPlaceholders) {
		InventoryData data = corePlayer.getInventoryData();
		
		if (!data.hasTranslatableMenu() || !data.isActiveTranslatableMenu(this)) {
			return false;
		}
		
		Inventory inv = data.getInventory();
		
		ChefCore.getReflector().getGameReflections().updateOpenedInventoryTitle(corePlayer.getPlayer(), corePlayer.getMessage(titleKey, itemPlaceholders));
		int size = inv.getSize();
		for (AbstractTranslatableContent content : items) {
			int slot = content.getSlot();
			if (size > slot) {
				String flag = null;
				for (String itemState : this.parseItemStateForPlayer(corePlayer)) {
					if (content.hasItemState(itemState)) {
						flag = itemState;
					}
				}
				inv.setItem(slot, content.getTranslatedItem(corePlayer, 1, placeholderJoinArenaDatas(itemPlaceholders), flag));
			} else {
				ChefCore.getInstance().sendPlainMessage("&cError> Items that exceed menu slot! " + "&fMenu Name: &e" + name + " &fContent Name: &e" + content.getRawName());
			}
		}
		return true;
	}
	
	@Override
	public List<String> parseItemStateForPlayer(CorePlayer corePlayer) {
		List<String> resultList = new ArrayList<>();
		for (int i = 0; i < this.itemStates.size(); i++) {
			resultList.add(this.itemStates.get(i).replace("<PLAYER_LOCALE>", corePlayer.getLanguage().getLocale().toLowerCase()));
		}
		return resultList;
	}

}
