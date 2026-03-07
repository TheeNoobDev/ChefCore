package net.chefcraft.world.listener;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.component.ComponentSupport;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.world.chat.ChatStatus;
import net.chefcraft.world.player.CorePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PaperAsyncChatListener implements Listener {
	
	private static boolean chatEnabled;
	private static String format;
	
	public static void onConfigReload() {
		chatEnabled = ChefCore.getInstance().getConfig().getBoolean("chat.enabled");
		format = ChefCore.getInstance().getConfig().getString("chat.format");
	}
	
	public PaperAsyncChatListener() {
		onConfigReload();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(io.papermc.paper.event.player.AsyncChatEvent event) {
		Player player = event.getPlayer();
		CorePlayer corePlayer = ChefCore.getCorePlayerByPlayer(player);
		String message = PlainTextComponentSerializer.plainText().serialize(event.message());
		boolean startsWith = message.startsWith("!");
		
		if (corePlayer.isInParty() && corePlayer.getChatStatus() == ChatStatus.PARTY) {
			if (!startsWith) {

				logPartyChat(corePlayer.getPartyManager().getParty().onTypeToPartyChat(corePlayer, message));
				
				event.setCancelled(true);
			} else {
				if (chatEnabled && !corePlayer.isInGame()) {
					
					String replaced = corePlayer.getPlayerMetaDataStorage().replaceAllFormats(format).replace("{MESSAGE}", startsWith ? message.substring(1) : message);
					Component component = ComponentSupport.miniMessageSupport().deserialize(replaced, corePlayer.getRandomColor()).asComponent();
					
					event.renderer((source, sourceDisplayName, msg, viewer) -> component);
					
				}
			}
			
		} else if (chatEnabled && !corePlayer.isInGame()) {
			
			String replaced = corePlayer.getPlayerMetaDataStorage().replaceAllFormats(format).replace("{MESSAGE}", startsWith ? message.substring(1) : message);
			Component component = ComponentSupport.miniMessageSupport().deserialize(replaced, corePlayer.getRandomColor()).asComponent();
			
			event.renderer((source, sourceDisplayName, msg, viewer) -> component);
		}
	}
	
	public static void logPartyChat(Placeholder placeholder) {
		ChefCore.getInstance().sendKeyedMessage("party.chatFormat", placeholder);
	}
}
