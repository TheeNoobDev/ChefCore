package net.chefcraft.world.inventory;

public enum MultiInventorySize {
	
	SMALL(18), 
	MEDIUM(27), 
	LARGE(36),
	XLARGE(45), 
	XXLARGE(54);
	
	protected final int size;
	
	private MultiInventorySize(int size) {
		this.size = size;
	}
	
	public int getSize() {
		return this.size;
	}
	
	/**
	 * @param rows must be between 1-6
	 * @return MultiInventorySize
	 */
	public static MultiInventorySize getByRows(int rows) {
		if (rows >= 6) {
			return XXLARGE;
		} else if (rows == 5) {
			return XLARGE;
		} else if (rows == 4) {
			return LARGE;
		} else if (rows == 3) {
			return MEDIUM;
		} else {
			return SMALL;
		}
	}
}
