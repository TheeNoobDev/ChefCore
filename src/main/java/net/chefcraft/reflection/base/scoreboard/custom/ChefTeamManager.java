package net.chefcraft.reflection.base.scoreboard.custom;

import java.util.Arrays;
import java.util.Collection;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.chefcraft.reflector.v1_21_R3.scoreboard.packets.ScoreboardTeamPacket;
import net.chefcraft.world.scoreboard.CoreTeamPacketStatus;
import net.chefcraft.world.scoreboard.custom.CoreTeamHolder;
import net.chefcraft.world.scoreboard.custom.CoreTeamManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;

public class ChefTeamManager implements CoreTeamManager {

	private Object2ObjectOpenHashMap<String, CoreTeamHolder> teamsMap = new Object2ObjectOpenHashMap<>();
	private final ServerPlayer player;
	
	public ChefTeamManager(Player bukkitPlayer) { 
		this.player = ((CraftPlayer) bukkitPlayer).getHandle();
	}
	
	@Override
	public void registerTeam(CoreTeamHolder coreTeam) {
		String teamName = coreTeam.getName();
		if (teamsMap.containsKey(teamName)) {
			throw new IllegalArgumentException("Team '" + teamName + "' already exist!");
		} else {
			teamsMap.put(teamName, coreTeam);
			this.handleChanges(coreTeam, CoreTeamPacketStatus.CREATE);
		}
	}
	
	@Override
	public void unregisterTeam(String teamName) {
		if (!teamsMap.containsKey(teamName)) {
			throw new IllegalArgumentException("The team named '" + teamName + "' was not found!");
		} else {
			this.handleChanges(teamsMap.remove(teamName), CoreTeamPacketStatus.REMOVE);
		}
	}
	
	@Override
	public void updateTeam(String teamName) {
		if (!teamsMap.containsKey(teamName)) {
			throw new IllegalArgumentException("The team named '" + teamName + "' was not found!");
		} else {
			this.handleChanges(teamsMap.get(teamName), CoreTeamPacketStatus.UPDATE);
		}
	}
	
	@Override
	public void removeTeams() {
		this.handleGlobalChanges(CoreTeamPacketStatus.REMOVE);
	}
	
	@Override
	public void updateTeams() {
		this.handleGlobalChanges(CoreTeamPacketStatus.UPDATE);
	}
	
	@Override
	public boolean hasTeam(String name) {
		return teamsMap.containsKey(name);
	}
	
	@Override
	public CoreTeamHolder getTeam(String name) {
		return teamsMap.get(name);
	}

	@Override
	public CoreTeamHolder findJoinedTeam(String entry) {
		for (CoreTeamHolder team : this.teamsMap.values()) {
			if (team.hasEntry(entry)) {
				return team;
			}
		}
		return null;
	}
	
	@Override
	public boolean moveEntryToOtherTeam(String teamName, String entry) {
		if (!teamsMap.containsKey(teamName)) {
			throw new IllegalArgumentException("The team named '" + teamName + "' was not found!");
		} else {
			CoreTeamHolder currentTeam = this.findJoinedTeam(entry);
			if (currentTeam == null) return false;
			
			currentTeam.removeEntry(entry);
			this.handleJoinLeaveChanges(currentTeam, Arrays.asList(entry), CoreTeamPacketStatus.LEAVE);
			
			CoreTeamHolder newTeam = teamsMap.get(teamName);
			newTeam.addEntry(entry);
			this.handleJoinLeaveChanges(newTeam, Arrays.asList(entry), CoreTeamPacketStatus.JOIN);
			return true;
		}
	}
	
	@Override
	public void joinTeam(String teamName, String entry) {
		if (!teamsMap.containsKey(teamName)) {
			throw new IllegalArgumentException("The team named '" + teamName + "' was not found!");
		} else {
			CoreTeamHolder currentTeam = this.findJoinedTeam(entry);
			if (currentTeam != null) {
				currentTeam.removeEntry(entry);
			}
			
			CoreTeamHolder newTeam = teamsMap.get(teamName);
			newTeam.addEntry(entry);
			this.handleJoinLeaveChanges(newTeam, Arrays.asList(entry), CoreTeamPacketStatus.JOIN);
		}
	}
	
	@Override
	public boolean leaveTeam(String entry) {
		CoreTeamHolder currentTeam = this.findJoinedTeam(entry);
		if (currentTeam != null) {
			currentTeam.removeEntry(entry);
			this.handleJoinLeaveChanges(currentTeam, Arrays.asList(entry), CoreTeamPacketStatus.LEAVE);			
			return true;
		}
		return false;
	}
	
	public void handleGlobalChanges(CoreTeamPacketStatus status) {
		ServerCommonPacketListenerImpl conn = player.connection;
		
		for (CoreTeamHolder team : teamsMap.values()) {
			conn.send(ScoreboardTeamPacket.buildWithTeam(team, status));
		}
		
		if (status.getStatus() == 1) {
			this.teamsMap.clear();
		}
	}
	
	public void handleChanges(CoreTeamHolder team, CoreTeamPacketStatus status) {
		player.connection.send(ScoreboardTeamPacket.buildWithTeam(team, status));
	}
	
	public void handleJoinLeaveChanges(CoreTeamHolder team, Collection<String> entries, CoreTeamPacketStatus status) {
		player.connection.send(ScoreboardTeamPacket.buildJoinOrLeave(team, entries, status));
	}
}
