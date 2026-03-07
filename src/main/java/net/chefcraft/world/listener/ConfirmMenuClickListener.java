package net.chefcraft.world.listener;

import net.chefcraft.core.ChefCore;
import net.chefcraft.world.inventory.InventoryDataHolder;
import net.chefcraft.world.menu.ConfirmMenu;
import net.chefcraft.world.player.CorePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

public class ConfirmMenuClickListener implements Listener {
	
	@SuppressWarnings("unchecked")
	@EventHandler
	public void confirmMenuClickEvent(InventoryClickEvent event) {
		if (event.getInventory().getType() != InventoryType.HOPPER) return;
		InventoryHolder dataHolder = event.getInventory().getHolder();
		
		if (dataHolder instanceof InventoryDataHolder && ((InventoryDataHolder<?>) dataHolder).getData() instanceof ConfirmMenu) {

			CorePlayer corePlayer = ChefCore.getCorePlayerByPlayer((Player) event.getWhoClicked());
		
			event.setResult(Event.Result.DENY);
			event.setCancelled(true);

			if (event.getRawSlot() == 1) {
				((InventoryDataHolder<ConfirmMenu>) dataHolder).getData().onConfirmed(corePlayer);
			} else if (event.getRawSlot() == 3) {
				((InventoryDataHolder<ConfirmMenu>) dataHolder).getData().onCancelled(corePlayer);
			}
		}
	}
}
