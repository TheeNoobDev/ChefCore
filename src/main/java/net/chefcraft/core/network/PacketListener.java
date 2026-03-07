package net.chefcraft.core.network;

public interface PacketListener {
	
	PacketFlow flow();
	
	public void sendPacket(CorePacket packet);
}
