package net.chefcraft.service.tag;

import net.chefcraft.core.component.CoreTextBase;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.world.player.CorePlayer;
import net.chefcraft.world.scoreboard.CoreScoreboardOption;

public interface NameTagService {

	CorePlayer getOwner();
	
	int getTabListPriority();
	
	void setTabListPriority(int priority);
	
	MessageHolder getPrefix();
	
	void setPrefix(MessageHolder prefix);
	
	MessageHolder getSuffix();
	
	void setSuffix(MessageHolder suffix);
	
	String getTagID();
	
	CoreScoreboardOption getNameTagVisibility();
	
	void setNameTagVisibility(CoreScoreboardOption status);
	
	CoreScoreboardOption getCollisionRule();
	
	void setCollisionRule(CoreScoreboardOption status);
	
	CoreTextBase getTextModifier();
	
	void setTextModifier(CoreTextBase textModifier);
	
	boolean canSeeFriendlyInvisibles();
	
	void setSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles);
	
	void update();
	
	void display();
	
	void remove();
}
