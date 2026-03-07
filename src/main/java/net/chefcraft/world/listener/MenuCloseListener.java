package net.chefcraft.world.listener;

import net.chefcraft.core.ChefCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class MenuCloseListener implements Listener {

	@EventHandler
	public void inventoryCloseEvent(InventoryCloseEvent event) {
		if (event.getPlayer() instanceof Player) {
			ChefCore.getCorePlayerByPlayer((Player) event.getPlayer()).getInventoryData().resetAll();
		}
	}
}
