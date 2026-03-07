package net.chefcraft.proxy.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import net.chefcraft.proxy.ChefProxyCore;
import net.chefcraft.proxy.player.CoreProxyPlayer;
import net.chefcraft.world.chat.ChatStatus;

public class ProxyChatListener {

	@Subscribe
	public void onType(PlayerChatEvent event) {
		CoreProxyPlayer proxyPlayer = ChefProxyCore.getProxyPlayerByPlayer(event.getPlayer());
		if (proxyPlayer == null) return;
		
		if (proxyPlayer.getChatStatus() == ChatStatus.PARTY && proxyPlayer.getPartyManager().isInParty()) {
			proxyPlayer.getPartyManager().getParty().onTypeToPartyChat(proxyPlayer, event.getMessage());
			event.setResult(PlayerChatEvent.ChatResult.denied());
		}
	}
}
