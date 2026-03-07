package net.chefcraft.world.cage;

public enum CageAnimation {

	NONE("none"), 
	VORTEX("vortex"), 
	CONFETTI("confetti"),
	TOTEM("totem");
	
	private final String stringValue;
	
	CageAnimation(String stringValue) {
		this.stringValue = stringValue;
	}
	
	public String toString() {
		return stringValue;
	}
}
