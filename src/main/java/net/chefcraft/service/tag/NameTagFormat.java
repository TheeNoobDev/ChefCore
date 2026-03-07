package net.chefcraft.service.tag;

public enum NameTagFormat {

	PLAYER_LIST_PREFIX("player_list_prefix"),
	PLAYER_LIST_SUFFIX("player_list_suffix"),
	NAME_TAG_PREFIX("name_tag_prefix"),
	NAME_TAG_SUFFIX("name_tag_suffix"),
	DISPLAY_NAME("display_name"),
	TAB_HEIGHT("tab_height");
	
	private final String configPath;
	
	private NameTagFormat(String configPath) {
		this.configPath = configPath;
	}

	public String getConfigPath() {
		return configPath;
	}
}
