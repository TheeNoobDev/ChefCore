package net.chefcraft.world.scoreboard.custom;

import net.chefcraft.core.component.CoreTextBase;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.world.scoreboard.CoreScoreboardOption;
import net.chefcraft.world.scoreboard.CoreTeamData;

import java.util.List;

public interface CoreTeamHolder extends CoreTeamData {
	
	boolean hasEntry(String entry);
	
	void setDisplayName(MessageHolder displayName);

	void setTextModifier(CoreTextBase textColor);
	
	void setCollisionRule(CoreScoreboardOption collisionRule);
	
	void setNameTagVisibility(CoreScoreboardOption nameTagVisibility);
	
	void setDeathMessageVisibility(CoreScoreboardOption deathMessageVisibility);
	
	void setPrefix(MessageHolder prefix);
	
	void setSuffix(MessageHolder suffix);
	
	void setAllowFriendlyFire(boolean friendlyFire);
	
	void setSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles);
	
	void addEntry(String... entries);
	
	void addEntry(List<String> entryList);
	
	void removeEntry(String... entries);
	
	void removeEntry(List<String> entryList);
}
