package net.chefcraft.world.scoreboard;

import net.chefcraft.core.language.MessageHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public interface CoreScoreboardObjective {

	@Nonnull CoreScoreboardScore getOrCreateScore(String scoreName, boolean allowForceModification);
	
	default @Nonnull CoreScoreboardScore getOrCreateScore(String scoreName) {
		return this.getOrCreateScore(scoreName, true);
	}
	
	boolean updateScore(CoreScoreboardScore score);
	
	boolean removeScore(CoreScoreboardScore score);
	
	void onScoreLockChanged(CoreScoreboardScore score);
	
	void resetScores();

	Collection<CoreScoreboardScore> listScores();

    String getName();

    CoreCriteria getCriteria();

    MessageHolder getDisplayName();
    
    CoreScoreRenderType getRenderType();

    boolean displayAutoUpdate();

    @Nullable CoreNumberFormat getNumberFormat();

    void setDisplayName(MessageHolder displayName);

    void setRenderType(CoreScoreRenderType renderType);

    void setDisplayAutoUpdate(boolean displayAutoUpdate);

    void setNumberFormat(@Nullable CoreNumberFormat numberFormat);
}
