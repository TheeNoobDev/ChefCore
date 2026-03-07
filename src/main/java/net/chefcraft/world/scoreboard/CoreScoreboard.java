package net.chefcraft.world.scoreboard;

import net.chefcraft.core.language.MessageHolder;
import org.bukkit.scoreboard.DisplaySlot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public interface CoreScoreboard {

	@Nullable CoreScoreboardObjective getObjectiveByName(String ObjectiveName);
    
    CoreScoreboardObjective createObjective(String objectiveName, CoreCriteria criteria, MessageHolder displayName, CoreScoreRenderType renderType, boolean displayAutoUpdate, @Nullable CoreNumberFormat numberFormat);
    
    List<CoreScoreboardScore> listAllScores();
    
    Collection<CoreScoreboardObjective> getObjectives();
    
    Collection<String> getObjectiveNames();
    
    Collection<String> getTeamNames();

    Collection<? extends CoreScoreboardTeam> getTeams();

    @Nullable CoreScoreboardTeam getTeamByName(String name);
    
    void resetAllScores();
    
    @Nullable CoreScoreboardObjective getDisplayObjective(DisplaySlot displaySlot);
    
    void setDisplayObjective(@Nonnull DisplaySlot displaySlot, @Nullable CoreScoreboardObjective objective);
    
    void removeObjective(@Nonnull CoreScoreboardObjective objective);
    
    void clearDisplayObjectives();
	
	CoreScoreboardTeam createTeam(@Nonnull String teamName);

	void removeTeam(CoreScoreboardTeam team);
	
	void clearTeams();
	
	void addEntryToTeam(CoreScoreboardTeam team, String entry);
	
	boolean removeEntryFromTeam(CoreScoreboardTeam team, String entry);
	
	boolean removeEntryFromJoinedTeam(String entry);
	
	default void remove() {
		this.clearDisplayObjectives();
		this.clearTeams();
	}
}
