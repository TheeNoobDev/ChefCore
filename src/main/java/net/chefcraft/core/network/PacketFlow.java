package net.chefcraft.core.network;

import java.util.Locale;

public enum PacketFlow {

	LOBBY, 
	GAME;
	
	private final String name;
	
	private PacketFlow() {
		this.name = this.name().toLowerCase(Locale.ENGLISH);
	}
	
	public String getName() {
		return this.name;
	}
}
