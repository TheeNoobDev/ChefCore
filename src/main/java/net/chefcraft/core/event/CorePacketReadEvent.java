package net.chefcraft.core.event;

import net.chefcraft.core.network.CorePacket;
import net.chefcraft.core.network.PacketFlow;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CorePacketReadEvent extends Event {

	private static final HandlerList HANDLER_LIST = new HandlerList();
	
	private final CorePacket packet;
	private final PacketFlow flow;
	
	public CorePacketReadEvent(@NotNull CorePacket packet, @NotNull PacketFlow flow) {
		super(true);
		this.packet = packet;
		this.flow = flow;
	}
	
	@NotNull
	public CorePacket getRawPacket() {
		return this.packet;
	}
	
	@NotNull
	public PacketFlow getFlow() {
		return this.flow;
	}
	
	@NotNull
	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}
	
	@NotNull
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

}
