package net.chefcraft.world.listener;

import net.chefcraft.core.event.CorePacketReadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CorePacketReadListener implements Listener {
	
	@EventHandler
	public void onRead(CorePacketReadEvent event) {
		System.out.println("Incoming Packet: " + event.getFlow() + ", Data: " + event.getRawPacket().toString());
	}

}
