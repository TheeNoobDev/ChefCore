package net.chefcraft.world.handler;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.language.TranslatablePlayer;
import net.chefcraft.world.util.MethodProvider;
import org.bukkit.Bukkit;

public class PlayerListHandler {

	public static void sendPlayerListHeaderAndFooter(TranslatablePlayer translatablePlayer) {
		ChefCore plugin = ChefCore.getInstance();
		
		if (plugin.getConfig().getBoolean("player_list.enabled")) {
			int delay = plugin.getConfig().getInt("player_list.display_delay");
			
			if (delay <= 0) {
				MethodProvider.SET_PLAYER_LIST_HEADER_FOOTER.accept(translatablePlayer, "tablist.header", "tablist.footer");
			} else {
				Bukkit.getScheduler().runTaskLater(plugin, ()-> {
					MethodProvider.SET_PLAYER_LIST_HEADER_FOOTER.accept(translatablePlayer, "tablist.header", "tablist.footer");
				}, delay);
			}
		}
	}
}
