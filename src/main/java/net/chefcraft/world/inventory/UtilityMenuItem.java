package net.chefcraft.world.inventory;

public enum UtilityMenuItem {
	
	BACK("back"),
	CLOSE("close"),
	NEXT("next"),
	PREVIOUS("previous"),
	CONFIRM("confirm"),
	CANCEL("cancel");
	
	protected final String name;
	
	private UtilityMenuItem(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public String getTranslationNameKey() {
		return "menuUtils." + this.name + "Item.name";
	}
	
	public String getTranslationLoreKey() {
		return "menuUtils." + this.name + "Item.lore";
	}
}
