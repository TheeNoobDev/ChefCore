package net.chefcraft.world.item;

public enum ItemColor {

	BLACK(15),
	BLUE(11),
	BROWN(12),
	CYAN(9),
	GRAY(7),
	GREEN(13),
	LIGHT_BLUE(3),
	LIGHT_GRAY(8),
	LIME(5),
	MAGENTA(2),
	ORANGE(1),
	PINK(6),
	PURPLE(10),
	RED(14),
	WHITE(0),
	YELLOW(4);
	
	private final byte legacyData;

	ItemColor(int data) {
		this.legacyData = (byte) data;
	}
	
	public byte getLegacyData() {
		return legacyData;
	}
}
