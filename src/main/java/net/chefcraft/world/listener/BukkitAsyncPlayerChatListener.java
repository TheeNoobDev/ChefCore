package net.chefcraft.world.listener;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.component.ComponentSupport;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.world.chat.ChatStatus;
import net.chefcraft.world.player.CorePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class BukkitAsyncPlayerChatListener implements Listener {
	
	private static boolean chatEnabled;
	private static String format;
	
	public static void onConfigReload() {
		chatEnabled = ChefCore.getInstance().getConfig().getBoolean("chat.enabled");
		format = ChefCore.getInstance().getConfig().getString("chat.format");
	}
	
	public BukkitAsyncPlayerChatListener() {
		onConfigReload();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		CorePlayer corePlayer = ChefCore.getCorePlayerByPlayer(player);
		
		if (corePlayer.isInParty() && corePlayer.getChatStatus() == ChatStatus.PARTY) {
			if (!event.getMessage().startsWith("!")) {

				logPartyChat(corePlayer.getPartyManager().getParty().onTypeToPartyChat(corePlayer, event.getMessage()));
				
				event.setCancelled(true);
			} else {
				if (chatEnabled && !corePlayer.isInGame()) {
					
					event.setMessage(format);
					
					
					event.setFormat(ComponentSupport.legacySupport().deserialize(corePlayer.getPlayerMetaDataStorage().replaceAllFormats(format)
							.replace("{MESSAGE}", ComponentSupport.legacySupport().deserialize(event.getMessage().substring(1), corePlayer.getRandomColor()))
							.replaceAll("%", "%%"), corePlayer.getRandomColor()));
					
				}
			}
			
		} else if (chatEnabled && !corePlayer.isInGame()) {
			
			event.setFormat(ComponentSupport.legacySupport().deserialize(corePlayer.getPlayerMetaDataStorage().replaceAllFormats(format)
					.replace("{MESSAGE}", ComponentSupport.legacySupport().deserialize(event.getMessage(), corePlayer.getRandomColor()))
					.replaceAll("%", "%%"), corePlayer.getRandomColor()));
			
		}
	}
	
	public static void logPartyChat(Placeholder placeholder) {
		ChefCore.getInstance().sendKeyedMessage("party.chatFormat", placeholder);
	}
}
