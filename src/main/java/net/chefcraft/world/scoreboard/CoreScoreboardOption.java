package net.chefcraft.world.scoreboard;

import net.chefcraft.core.util.ObjectKey;

public enum CoreScoreboardOption implements ObjectKey {
	
	ALWAYS("always"), 
	FOR_OTHER_TEAMS("for_other_teams"), 
	FOR_OWN_TEAM("for_own_team"), 
	NEVER("never"); 
	
	private final String name;
	
	private CoreScoreboardOption(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String getKey() {
		return name;
	}
}
