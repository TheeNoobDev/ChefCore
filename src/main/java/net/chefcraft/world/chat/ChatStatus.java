package net.chefcraft.world.chat;

public enum ChatStatus {

	PARTY, DEFAULT, LOCKED;
	
	public ChatStatus opposite() {
		return this == DEFAULT ? PARTY : DEFAULT;
	}
	
	public boolean locked() {
		return this == LOCKED;
	}
}
