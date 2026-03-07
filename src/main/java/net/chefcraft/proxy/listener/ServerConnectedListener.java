package net.chefcraft.proxy.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import net.chefcraft.core.server.Minigame;
import net.chefcraft.core.util.Pair;
import net.chefcraft.proxy.ChefProxyCore;
import net.chefcraft.proxy.player.CoreProxyPlayer;

public class ServerConnectedListener {

	@Subscribe
	public void onServerConnect(ServerConnectedEvent event) {
		CoreProxyPlayer proxyPlayer = ChefProxyCore.getProxyPlayerByPlayer(event.getPlayer());
		if (proxyPlayer.isPartyOwner()) {
			proxyPlayer.updateLastGameID(true).thenRun(() -> {
				Pair<Minigame, String> lastGame = proxyPlayer.getLastGameID();
				
				proxyPlayer.getPartyManager().getParty().listMembers().forEach(member -> {
					if (member instanceof CoreProxyPlayer pMember) {
						pMember.setLastGameID(lastGame.getFirst(), lastGame.getSecond());
						pMember.updateLastGameID(false);
					}
				});
				
			}).thenRun(() -> {
				proxyPlayer.getPartyManager().getParty().onLeaderSwitchServer(event.getServer());
			});
		}
	}
}
