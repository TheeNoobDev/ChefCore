package net.chefcraft.world.scoreboard.custom;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.component.CoreTextColor;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslatablePlayer;
import net.chefcraft.world.scoreboard.*;
import net.chefcraft.world.util.MethodProvider;
import org.bukkit.scoreboard.DisplaySlot;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public abstract class AbstractGameScoreboard {

	protected static final String SIDEBAR = "SIDEBAR";
	protected static final String BELOW_NAME = "BELOW_NAME";
	protected static final String PLAYER_LIST = "PLAYER_LIST";
	
	protected final CoreScoreboard scoreboard;
	
	protected CoreScoreboardObjective sidebar = null;
	protected CoreScoreboardObjective belowName = null;
	protected CoreScoreboardObjective playerList = null;
	
	protected AbstractGameScoreboard(@NotNull TranslatablePlayer translatablePlayer) {
		Objects.requireNonNull(translatablePlayer, "cannot construct this from null player!");
		this.scoreboard = ChefCore.getReflector().newScoreboardForPlayer(translatablePlayer);
	}
	
	protected AbstractGameScoreboard(@NotNull CoreScoreboard scoreboard) {
		this.scoreboard = Objects.requireNonNull(scoreboard, "cannot construct this from null scoreboard!");
	}
	
	protected void removeIfExistsSidebarObjective() {
		CoreScoreboardObjective oldObjective = this.scoreboard.getObjectiveByName(SIDEBAR);
		
		if (oldObjective != null) {
			
			for (int i = 1; i <= 15; i++) {
        		CoreScoreboardTeam team = this.scoreboard.getTeamByName("SLOT_" + i);
        		
        		if (team != null) {
        			this.scoreboard.removeTeam(team);
        		}
    		}
			
			this.scoreboard.removeObjective(oldObjective);
		}
	}
	
	protected void removeIfExistsBelowNameObjective() {
		CoreScoreboardObjective oldObjective = this.scoreboard.getObjectiveByName(BELOW_NAME);
		
		if (oldObjective != null) {
			this.scoreboard.removeObjective(oldObjective);		
		}
	}
	
	protected void removeIfExistsPlayerListObjective() {
		CoreScoreboardObjective oldObjective = this.scoreboard.getObjectiveByName(PLAYER_LIST);
		
		if (oldObjective != null) {
			this.scoreboard.removeObjective(oldObjective);		
		}
	}
	
	private String getSiderbarSlotEntry(int slot) {
        return CoreTextColor.list().get(slot).toString();
    }
	
	public CoreScoreboardObjective createSidebar(MessageHolder title, @Nullable CoreNumberFormat numberFormat) {
		this.removeIfExistsSidebarObjective();
		
		this.sidebar = this.scoreboard.createObjective(SIDEBAR, CoreCriteria.DUMMY, title, CoreScoreRenderType.INTEGER, true, numberFormat);
		
		this.scoreboard.setDisplayObjective(DisplaySlot.SIDEBAR, this.sidebar);
		return this.sidebar;
	}
	
	public CoreScoreboardObjective createPlayerListObjective(MessageHolder displayName, CoreCriteria criteria, CoreScoreRenderType renderType, @Nullable CoreNumberFormat numberFormat) {
		this.removeIfExistsPlayerListObjective();
		
		this.playerList = this.scoreboard.createObjective(PLAYER_LIST, criteria, displayName, renderType, true, numberFormat);
		
		this.scoreboard.setDisplayObjective(DisplaySlot.PLAYER_LIST, this.playerList);
		return this.playerList;
	}
	
	public CoreScoreboardObjective createBelowNameObjective(MessageHolder displayName, CoreCriteria criteria, CoreScoreRenderType renderType, @Nullable CoreNumberFormat numberFormat) {
		this.removeIfExistsBelowNameObjective();
		
		this.belowName = this.scoreboard.createObjective(BELOW_NAME, criteria, displayName, renderType, true, numberFormat);
		
		this.scoreboard.setDisplayObjective(DisplaySlot.BELOW_NAME, this.belowName);
		return this.belowName;
	}
	
	public void updateSidebarTitle(MessageHolder title) {
    	Objects.requireNonNull(this.sidebar, "First you need to run the 'createSidebarObjective' method!");
    	
    	this.sidebar.setDisplayName(title);
    }
    
    public void setSidebarSlot(int slot, MessageHolder text) {
    	Objects.requireNonNull(this.sidebar, "First you need to run the 'createSidebarObjective' method!");
    	
    	String slotName = "SLOT_" + slot;
    	
    	CoreScoreboardTeam team = this.scoreboard.getTeamByName(slotName);
    	
    	if (team == null) {
    		team = this.scoreboard.createTeam(slotName);
    	}
    	
		String entry = getSiderbarSlotEntry(slot);
		if (!team.hasEntry(entry)) {
			
			this.scoreboard.addEntryToTeam(team, entry);
			
			this.sidebar.getOrCreateScore(entry, true).setValue(slot);
		}
		
		MethodProvider.SCOREBOARD_TEAM_UPDATE_SCORE_NAME.accept(team, text);
    }
    
    public void removeSidebarSlot(int slot) {
    	Objects.requireNonNull(this.sidebar, "First you need to run the 'createSidebarObjective' method!");
    	
    	CoreScoreboardTeam team = this.scoreboard.getTeamByName("SLOT_" + slot);
        String entry = getSiderbarSlotEntry(slot);
        if (team != null && team.hasEntry(entry)) {
            this.scoreboard.removeEntryFromTeam(team, entry);
            
            CoreScoreboardScore score = this.sidebar.getOrCreateScore(entry);
            this.sidebar.removeScore(score);
        }
    }
    
    public void setSidebarSlotsFromList(List<MessageHolder> list) {
    	Objects.requireNonNull(this.sidebar, "First you need to run the 'createSidebarObjective' method!");
    	
    	int size = list.size();
    	
        for (int i = 1; i <= 15; i++) {
        	if (size >= i) {
        		this.setSidebarSlot(i, list.get(size - i));
        	} else {
        		this.removeSidebarSlot(i);
        	}
        }
    }
    
    public void clearAll() {
    	this.scoreboard.clearDisplayObjectives();
    	this.scoreboard.clearTeams();
    }
    
    public boolean isSidebarDisplayed() {
    	return this.sidebar != null;
    }
    
    public boolean isBelowNameDisplayed() {
    	return this.belowName != null;
    }
    
    public boolean isPlayerListDisplayed() {
    	return this.playerList != null;
    }
    
    @NotNull
    public CoreScoreboard getScoreboard() {
		return scoreboard;
	}

	@Nullable
	public CoreScoreboardObjective getSidebar() {
		return sidebar;
	}
	
	@Nullable
	public CoreScoreboardObjective getPlayerList() {
		return playerList;
	}
	
	@Nullable
	public CoreScoreboardObjective getBelowName() {
		return belowName;
	}

}
