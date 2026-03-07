package net.chefcraft.world.scoreboard;

import net.chefcraft.core.component.CoreTextBase;
import net.chefcraft.core.language.MessageHolder;

public interface CoreScoreboardTeam extends CoreTeamData {

	boolean hasEntry(String entry);
	
	void setDisplayName(MessageHolder displayName);

	void setTextModifier(CoreTextBase textModifier);
	
	void setCollisionRule(CoreScoreboardOption collisionRule);
	
	void setNameTagVisibility(CoreScoreboardOption nameTagVisibility);
	
	void setDeathMessageVisibility(CoreScoreboardOption deathMessageVisibility);
	
	void setPrefix(MessageHolder prefix);
	
	void setSuffix(MessageHolder suffix);
	
	void setAllowFriendlyFire(boolean friendlyFire);
	
	void setSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles);
}
