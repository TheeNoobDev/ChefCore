package net.chefcraft.world.listener;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import net.chefcraft.core.ChefCore;
import net.chefcraft.core.util.Reloadable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class VanillaAdvancementListener implements Listener, Reloadable {
	
	private boolean disabled;
	
	public VanillaAdvancementListener(boolean disable) {
		this.disabled = disable;
	}
	
	@EventHandler
	public void saveAdvancement(PlayerAdvancementCriterionGrantEvent event) {
		if (this.disabled) {
			event.setCancelled(true);
		}
	}

    @EventHandler
    public void broadcastAdvancement(PlayerAdvancementDoneEvent event) {
        if (this.disabled) {
        	event.message(null);
        }
    }

	@Override
	public void reload() {
		this.disabled = ChefCore.disableVanillaAdvancements();
	}
}
