package net.chefcraft.world.inventory;

public enum PageUtilResult {
	
	NOTHING,
	CLOSED, 
	BACK, 
	NEXT_PAGE, 
	PREVIOUS_PAGE;
	
	public boolean nothing() {
		return this == NOTHING;
	}
	
	public boolean isPageClosed() {
		return this == CLOSED;
	}
	
	public boolean isPageChanged() {
		return this == NEXT_PAGE || this == PREVIOUS_PAGE;
	}
	
	public boolean isHandled() {
		return this == CLOSED || this == NEXT_PAGE || this == PREVIOUS_PAGE || this == BACK;
	}
}