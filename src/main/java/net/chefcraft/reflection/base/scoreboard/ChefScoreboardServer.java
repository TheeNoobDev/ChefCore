package net.chefcraft.reflection.base.scoreboard;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.scoreboard.DisplaySlot;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.chefcraft.core.language.TranslatablePlayer;
import net.chefcraft.core.util.ReferencedRunnable;
import net.chefcraft.reflector.v1_21_R3.scoreboard.packets.ScoreboardClientBoundResetScorePacket;
import net.chefcraft.reflector.v1_21_R3.scoreboard.packets.ScoreboardDisplayObjectivePacket;
import net.chefcraft.reflector.v1_21_R3.scoreboard.packets.ScoreboardObjectivePacket;
import net.chefcraft.reflector.v1_21_R3.scoreboard.packets.ScoreboardScorePacket;
import net.chefcraft.reflector.v1_21_R3.scoreboard.packets.ScoreboardTeamPacket;
import net.chefcraft.world.scoreboard.CoreScoreboardObjective;
import net.chefcraft.world.scoreboard.CoreScoreboardScore;
import net.chefcraft.world.scoreboard.CoreScoreboardTeam;
import net.chefcraft.world.scoreboard.CoreTeamPacketStatus;
import net.minecraft.network.protocol.Packet;

public class ChefScoreboardServer extends AbstractChefScoreboard {

	private final List<? extends TranslatablePlayer> packetConsumer;
	private final Set<CoreScoreboardObjective> trackedObjectives = Sets.newHashSet();
	
	public ChefScoreboardServer(List<? extends TranslatablePlayer> packetConsumer) { 
		this.packetConsumer = packetConsumer;
	}
	
	public List<? extends TranslatablePlayer> getPacketConsumer() {
		return packetConsumer;
	}
	
	private void broadcastPacket(Packet<?> packet) {
		for (int i = 0; i < packetConsumer.size(); i++) {
			this.packetConsumer.get(i).sendPacket(packet);
		}
	}
	
	private void broadcastPacket(List<Packet<?>> packets) {
		for (int i = 0; i < packetConsumer.size(); i++) {
			this.packetConsumer.get(i).sendPackets(packets);
		}
	}

	@Override
	protected void onObjectiveAdded(CoreScoreboardObjective objective) {  }
	
	@Override
	protected void onScoreLockChanged(CoreScoreboardObjective objective, CoreScoreboardScore score) { }

	@Override
	protected void onObjectiveDisplayed(DisplaySlot displaySlot, CoreScoreboardObjective objective) {
		CoreScoreboardObjective occupiedObjective = super.getDisplayObjective(displaySlot);
		
		if (occupiedObjective != null && occupiedObjective != objective) {
			
			if (this.getObjectiveDisplaySlotCount(occupiedObjective) > 0) {
				this.broadcastPacket(ScoreboardDisplayObjectivePacket.build(displaySlot, objective));
			} else {
				this.stopTrackingObjective(occupiedObjective);
			}
		}
		
		if (objective != null) {
			if (this.trackedObjectives.contains(objective)) {
				this.broadcastPacket(ScoreboardDisplayObjectivePacket.build(displaySlot, objective));
			} else {
				this.startTrackingObjective(occupiedObjective);
			}
		}
	}
	
	@Override
	protected void onObjectiveChanged(CoreScoreboardObjective objective) {
		if (this.trackedObjectives.contains(objective)) {
			this.broadcastPacket(ScoreboardObjectivePacket.build(ScoreboardObjectivePacket.METHOD_CHANGE, objective));
		}
	}

	@Override
	protected void onObjectiveRemoved(CoreScoreboardObjective objective) {
		if (this.trackedObjectives.contains(objective)) {
			this.stopTrackingObjective(objective);
		}
	}

	@Override
	protected void onScoreChanged(CoreScoreboardObjective objective, CoreScoreboardScore score) {
		this.broadcastPacket(ScoreboardScorePacket.build(score.getEntry(), objective.getName(), score.getValue(), score.getDisplayText(), score.getNumberFormat()));
	}

	@Override
	protected void onScoreRemoved(CoreScoreboardObjective objective, CoreScoreboardScore score) {
		this.broadcastPacket(ScoreboardClientBoundResetScorePacket.build(score, objective));
	}

	@Override
	protected void onTeamAdded(CoreScoreboardTeam team) {
		this.broadcastPacket(ScoreboardTeamPacket.buildWithTeam(team, CoreTeamPacketStatus.CREATE));
	}

	@Override
	protected void onTeamChanged(CoreScoreboardTeam team) {
		this.broadcastPacket(ScoreboardTeamPacket.buildWithTeam(team, CoreTeamPacketStatus.UPDATE));
	}

	@Override
	protected void onTeamRemoved(CoreScoreboardTeam team) {
		this.broadcastPacket(ScoreboardTeamPacket.buildWithTeam(team, CoreTeamPacketStatus.REMOVE));
	}
	
	@Override
	protected void onJoinToTeam(CoreScoreboardTeam team, String... entries) {
		this.broadcastPacket(ScoreboardTeamPacket.buildJoinOrLeave(team, Arrays.asList(entries), CoreTeamPacketStatus.JOIN));
	}

	@Override
	protected void onLeaveToTeam(CoreScoreboardTeam team, String... entries) {
		this.broadcastPacket(ScoreboardTeamPacket.buildJoinOrLeave(team, Arrays.asList(entries), CoreTeamPacketStatus.LEAVE));
	}
	
	public void startTrackingObjective(CoreScoreboardObjective objective) {
		List<Packet<?>> packetList = Lists.newArrayList();
		
		packetList.add(ScoreboardObjectivePacket.build(ScoreboardObjectivePacket.METHOD_ADD, objective));
		
		super.forDisplayedSlots(objective, displaySlot -> {
			packetList.add(ScoreboardDisplayObjectivePacket.build(displaySlot, objective));
		});
		
		for (CoreScoreboardScore score : objective.listScores()) {
			packetList.add(ScoreboardScorePacket.build(score.getEntry(), objective.getName(), score.getValue(), score.getDisplayText(), score.getNumberFormat()));
		}
		
		this.broadcastPacket(packetList);
		
		this.trackedObjectives.add(objective);
	}
	
	public void stopTrackingObjective(CoreScoreboardObjective objective) {
		List<Packet<?>> packetList = Lists.newArrayList();
		
		packetList.add(ScoreboardObjectivePacket.build(ScoreboardObjectivePacket.METHOD_REMOVE, objective.getName(), null, null, null));
		
		super.forDisplayedSlots(objective, displaySlot -> {
			packetList.add(ScoreboardDisplayObjectivePacket.build(displaySlot, objective));
		});
		
		this.broadcastPacket(packetList);
		
		this.trackedObjectives.remove(objective);
	}
	
	public int getObjectiveDisplaySlotCount(CoreScoreboardObjective objective) {
		
		ReferencedRunnable<Integer> runnable = new ReferencedRunnable<>() {

			int count = 0;
			
			@Override
			public void run() {
				count++;
			}

			@Override
			public Integer get() {
				return count;
			}
		};
		
		super.forDisplayedSlots(objective, runnable);
		
		return runnable.get();
	}
}
