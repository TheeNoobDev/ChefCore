package net.chefcraft.world.scoreboard;

import net.chefcraft.core.component.CoreTextBase;
import net.chefcraft.core.language.MessageHolder;

import java.util.Collection;

public interface CoreTeamData {
	
	String getName();

	MessageHolder getDisplayName();
	
	MessageHolder getPrefix();
	
	MessageHolder getSuffix();
	
	CoreTextBase getTextModifier();
	
	boolean isAllowFriendlyFire();
	
	boolean canSeeFriendlyInvisibles();
	
	CoreScoreboardOption getCollisionRule();
	
	CoreScoreboardOption getNameTagVisibility();
	
	CoreScoreboardOption getDeathMessageVisibility();
	
	Collection<String> getEntries();
}
