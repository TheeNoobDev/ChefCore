package net.chefcraft.world.scoreboard;

import net.chefcraft.core.language.MessageHolder;

public interface CoreScoreboardScore {

	boolean isModifiable();
	
	String getEntry();

	int getValue();

	void setValue(int value);

	boolean isLocked();

	void setLocked(boolean locked);
	
	MessageHolder getDisplayText();
	
	void setDisplayText(MessageHolder displayText);
	
	CoreNumberFormat getNumberFormat();
	
	void setNumberFormat(CoreNumberFormat numberFormat);
}
