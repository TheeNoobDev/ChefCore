package net.chefcraft.reflection.base.scoreboard;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.world.scoreboard.CoreNumberFormat;
import net.chefcraft.world.scoreboard.CoreScoreboardObjective;
import net.chefcraft.world.scoreboard.CoreScoreboardScore;

public class ChefScoreboardScore implements CoreScoreboardScore {
	
	public static final short MAX_ENTRY_LENGTH = 32767;
	
	private final CoreScoreboardObjective objective;
	private final boolean modifiable;
	private final String entry;
	
	private int value = 0;
	private boolean locked = true;
	private MessageHolder displayText = MessageHolder.empty();
	private @Nullable CoreNumberFormat numberFormat;
	
	public ChefScoreboardScore(CoreScoreboardObjective objective, String entry, boolean allowForceModification) {
		Preconditions.checkArgument(entry != null, "Entry cannot be null");
        Preconditions.checkArgument(entry.length() <= MAX_ENTRY_LENGTH, "Score '" + entry + "' is longer than the limit of " + MAX_ENTRY_LENGTH + " characters");
		
		this.objective = objective;
		this.modifiable = allowForceModification;
		this.entry = entry;
		this.numberFormat = objective.getNumberFormat();
	}

	public CoreScoreboardObjective getObjective() {
		return objective;
	}
	
	public boolean isModifiable() {
		return modifiable;
	}
	
	public String getEntry() {
		return this.entry;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
		if (modifiable) {
			this.objective.updateScore(this);
		}
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
		this.objective.onScoreLockChanged(this);
		this.objective.updateScore(this);
	}
	
	public MessageHolder getDisplayText() {
		return this.displayText;
	}
	
	public void setDisplayText(MessageHolder displayText) {
		this.displayText = displayText;
		this.objective.updateScore(this);
	}
	
	@Nullable
	public CoreNumberFormat getNumberFormat() {
		return this.numberFormat;
	}
	
	public void setNumberFormat(@Nullable CoreNumberFormat numberFormat) {
		this.numberFormat = numberFormat;
		this.objective.updateScore(this);
	}
}
