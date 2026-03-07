package net.chefcraft.reflection.base.scoreboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.scoreboard.DisplaySlot;

import com.google.common.collect.Lists;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.chefcraft.core.ChefCore;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.world.scoreboard.CoreCriteria;
import net.chefcraft.world.scoreboard.CoreNumberFormat;
import net.chefcraft.world.scoreboard.CoreScoreRenderType;
import net.chefcraft.world.scoreboard.CoreScoreboard;
import net.chefcraft.world.scoreboard.CoreScoreboardObjective;
import net.chefcraft.world.scoreboard.CoreScoreboardScore;
import net.chefcraft.world.scoreboard.CoreScoreboardTeam;

public abstract class AbstractChefScoreboard implements CoreScoreboard {

	private static final DisplaySlot[] DISPLAY_SLOTS = DisplaySlot.values();
	private static final int DISPLAY_SLOTS_LENGHT = DISPLAY_SLOTS.length;
	
	private final Object2ObjectMap<String, CoreScoreboardObjective> objectivesByName = new Object2ObjectOpenHashMap<>(16, 0.5F);
    private final Map<DisplaySlot, CoreScoreboardObjective> displayObjectives = new EnumMap<>(DisplaySlot.class);
    private final Object2ObjectMap<String, CoreScoreboardTeam> teamsByName = new Object2ObjectOpenHashMap<>();
	
    public AbstractChefScoreboard() { }
    
    @Nullable
    public CoreScoreboardObjective getObjectiveByName(String objectiveName) {
    	return this.objectivesByName.get(objectiveName);
    }
    
    public CoreScoreboardObjective createObjective(String objectiveName, CoreCriteria criteria, MessageHolder displayName, CoreScoreRenderType renderType, boolean displayAutoUpdate, @Nullable CoreNumberFormat numberFormat) {
    	
    	if (this.objectivesByName.containsKey(objectiveName)) {
    		ChefCore.log(Level.WARNING, "Objective '" + objectiveName + "' already exists! Returning exiting objective");
    		
    		return this.getObjectiveByName(objectiveName);
    	} else {
    		CoreScoreboardObjective objective = new ChefScoreboardObjective(this, objectiveName, criteria, displayName, renderType, displayAutoUpdate, numberFormat);
        	this.objectivesByName.put(objectiveName, objective);
        	
        	return objective;
    	}
    }
    
    public List<CoreScoreboardScore> listAllScores() {
    	List<CoreScoreboardScore> list = new ArrayList<>();
    	
    	for (CoreScoreboardObjective objective : this.objectivesByName.values()) {
    		list.addAll(objective.listScores());
    	}
    	
    	return list;
    }
    
    public Collection<CoreScoreboardObjective> getObjectives() {
    	return this.objectivesByName.values();
    }
    
    public Collection<String> getObjectiveNames() {
    	return this.objectivesByName.keySet();
    }
    
    public Collection<String> getTeamNames() {
        return this.teamsByName.keySet();
    }

    public Collection<CoreScoreboardTeam> getTeams() {
        return this.teamsByName.values();
    }

    @Nullable
    public CoreScoreboardTeam getTeamByName(String name) {
        return this.teamsByName.get(name);
    }
    
    public void resetAllScores() {
    	for (CoreScoreboardObjective objective : this.objectivesByName.values()) {
    		objective.resetScores();
    	}
    }
    
    @Nullable
    public CoreScoreboardObjective getDisplayObjective(DisplaySlot displaySlot) {
    	return this.displayObjectives.get(displaySlot);
    }
    
    public void setDisplayObjective(@Nonnull DisplaySlot displaySlot, @Nullable CoreScoreboardObjective objective) {
    	this.displayObjectives.put(displaySlot, objective);
    	this.onObjectiveDisplayed(displaySlot,objective);
    }
    
    public void removeObjective(@Nonnull CoreScoreboardObjective objective) {
    	this.objectivesByName.remove(objective.getName());
    	
    	this.forDisplayedSlots(objective, (displaySlot)-> {
    		this.setDisplayObjective(displaySlot, null);
    	});
    	
    	objective.resetScores();
    	
    	this.onObjectiveRemoved(objective);
    }
    
    public void clearDisplayObjectives() {
    	Lists.newArrayList(this.objectivesByName.values()).forEach(objective -> this.removeObjective(objective));
    	this.displayObjectives.clear();
    }
	
	public CoreScoreboardTeam createTeam(@Nonnull String teamName) {
		CoreScoreboardTeam team = this.getTeamByName(teamName);
		
		if (team == null) {
			team = new ChefScoreboardTeam(this, teamName);
			this.teamsByName.put(teamName, team);
			this.onTeamAdded(team);
		}
		
		return team;
		
	}

	public void removeTeam(CoreScoreboardTeam team) {
		if (this.teamsByName.containsValue(team)) {
			
			this.teamsByName.remove(team.getName());
			this.onTeamRemoved(team);
		}
	}
	
	public void clearTeams() {
		for (CoreScoreboardTeam team : this.teamsByName.values()) {
			this.onTeamRemoved(team);
		}
		
		this.teamsByName.clear();
	}
	
	public void addEntryToTeam(CoreScoreboardTeam team, String entry) {
		this.removeEntryFromJoinedTeam(entry);
		
		team.getEntries().add(entry);
		
		this.onJoinToTeam(team, entry);
	}
	
	public boolean removeEntryFromTeam(CoreScoreboardTeam team, String entry) {
		if (team.hasEntry(entry)) {
			team.getEntries().remove(entry);
			this.onLeaveToTeam(team, entry);
			return true;
		}
		return false;
	}
	
	public boolean removeEntryFromJoinedTeam(String entry) {
		boolean bool = false;
		
		for (CoreScoreboardTeam team : this.teamsByName.values()) {
			if (team.hasEntry(entry)) {
				team.getEntries().remove(entry);
				this.onLeaveToTeam(team, entry);
				bool = true;
			}
		}
		
		return bool;
	}
	
	public void forDisplayedSlots(CoreScoreboardObjective objective, Runnable runnable) {
		for (int i = 0; i < DISPLAY_SLOTS_LENGHT; i++) {
    		DisplaySlot slot = DISPLAY_SLOTS[i];
    		
    		if (this.getDisplayObjective(slot) == objective) {
    			runnable.run();
    		}
    	}
	}
	
	public void forDisplayedSlots(CoreScoreboardObjective objective, Consumer<DisplaySlot> consumer) {
		for (int i = 0; i < DISPLAY_SLOTS_LENGHT; i++) {
    		DisplaySlot slot = DISPLAY_SLOTS[i];
    		
    		if (this.getDisplayObjective(slot) == objective) {
    			consumer.accept(slot);
    		}
    	}
	}
	
	protected abstract void onObjectiveAdded(CoreScoreboardObjective objective);
	
	protected abstract void onObjectiveDisplayed(DisplaySlot displaySlot, CoreScoreboardObjective objective);
	
	protected abstract void onObjectiveChanged(CoreScoreboardObjective objective);
	
	protected abstract void onObjectiveRemoved(CoreScoreboardObjective objective);

	protected abstract void onScoreChanged(CoreScoreboardObjective objective, CoreScoreboardScore score);
	
	protected abstract void onScoreRemoved(CoreScoreboardObjective objective, CoreScoreboardScore score);
	
	protected abstract void onScoreLockChanged(CoreScoreboardObjective objective, CoreScoreboardScore score);
	
	protected abstract void onJoinToTeam(CoreScoreboardTeam team, String... entries);
	
	protected abstract void onLeaveToTeam(CoreScoreboardTeam team, String... entries);
	
	protected abstract void onTeamAdded(CoreScoreboardTeam team);
	
	protected abstract void onTeamChanged(CoreScoreboardTeam team);
	
	protected abstract void onTeamRemoved(CoreScoreboardTeam team);
}
