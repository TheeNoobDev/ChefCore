package net.chefcraft.reflection.base.scoreboard;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.world.scoreboard.CoreCriteria;
import net.chefcraft.world.scoreboard.CoreNumberFormat;
import net.chefcraft.world.scoreboard.CoreScoreRenderType;
import net.chefcraft.world.scoreboard.CoreScoreboardObjective;
import net.chefcraft.world.scoreboard.CoreScoreboardScore;

public class ChefScoreboardObjective implements CoreScoreboardObjective {
	
	private final AbstractChefScoreboard scoreboard;
	private final String name;
	private final CoreCriteria criteria;
	private MessageHolder displayName;
	private CoreScoreRenderType renderType;
	private boolean displayAutoUpdate;
	private @Nullable CoreNumberFormat numberFormat;
	protected final Reference2ObjectOpenHashMap<String, CoreScoreboardScore> scoresByName = new Reference2ObjectOpenHashMap<>(16, 0.5F);
	
	public ChefScoreboardObjective(AbstractChefScoreboard scoreboard, String name, CoreCriteria criteria, MessageHolder displayName, CoreScoreRenderType renderType, boolean displayAutoUpdate, @Nullable CoreNumberFormat numberFormat) {
		this.scoreboard = scoreboard;
		this.name = name;
		this.criteria = criteria;
		this.displayName = displayName;
		this.renderType = renderType;
		this.displayAutoUpdate = displayAutoUpdate;
		this.numberFormat = numberFormat;
	}
	
	public @Nonnull CoreScoreboardScore getOrCreateScore(String scoreName, boolean allowForceModification) {
		CoreScoreboardScore score = this.scoresByName.get(scoreName);
		
		if (score != null) {
			return score;
		} else {
			score = new ChefScoreboardScore(this, scoreName, allowForceModification);
			this.scoresByName.put(scoreName, score);
			
			this.scoreboard.onScoreChanged(this, score);
			return score;
		}
	}
	
	public boolean updateScore(CoreScoreboardScore score) {
		if (this.scoresByName.containsValue(score)) {
			this.scoreboard.onScoreChanged(this, score);
			return true;
		}
		return false;
	}
	
	public boolean removeScore(CoreScoreboardScore score) {
		if (this.scoresByName.containsValue(score)) {
			this.scoresByName.remove(score.getEntry());
			this.scoreboard.onScoreRemoved(this, score);
			return true;
		}
		return false;
	}
	
	public void onScoreLockChanged(CoreScoreboardScore score) {
		if (this.scoresByName.containsValue(score)) {
			this.scoreboard.onScoreLockChanged(this, score);
		}
	}
	
	public void resetScores() {
		for (CoreScoreboardScore score : this.scoresByName.values()) {
			this.scoreboard.onScoreRemoved(this, score);
		}
		this.scoresByName.clear();
	}
	
	public Collection<CoreScoreboardScore> listScores() {
		return this.scoresByName.values();
	}
	
	public AbstractChefScoreboard getScoreboard() {
        return this.scoreboard;
    }

    public String getName() {
        return this.name;
    }

    public CoreCriteria getCriteria() {
        return this.criteria;
    }

    public MessageHolder getDisplayName() {
        return this.displayName;
    }
    
    public CoreScoreRenderType getRenderType() {
        return this.renderType;
    }

    public boolean displayAutoUpdate() {
        return this.displayAutoUpdate;
    }

    public @Nullable CoreNumberFormat getNumberFormat() {
        return this.numberFormat;
    }

    public void setDisplayName(MessageHolder displayName) {
        this.displayName = displayName;
        this.scoreboard.onObjectiveChanged(this);
    }

    public void setRenderType(CoreScoreRenderType renderType) {
    	this.renderType = renderType;
    	this.scoreboard.onObjectiveChanged(this);
    }

    public void setDisplayAutoUpdate(boolean displayAutoUpdate) {
    	this.displayAutoUpdate = displayAutoUpdate;
    	this.scoreboard.onObjectiveChanged(this);
    }

    public void setNumberFormat(@Nullable CoreNumberFormat numberFormat) {
    	this.numberFormat = numberFormat;
    	this.scoreboard.onObjectiveChanged(this);
    }
}
