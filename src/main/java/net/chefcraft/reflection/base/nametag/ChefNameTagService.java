package net.chefcraft.reflection.base.nametag;

import java.util.List;

import org.bukkit.craftbukkit.entity.CraftPlayer;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.component.CoreTextBase;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.reflector.v1_21_R3.scoreboard.custom.ChefTeamHolder;
import net.chefcraft.reflector.v1_21_R3.scoreboard.packets.ScoreboardTeamPacket;
import net.chefcraft.service.tag.NameTagService;
import net.chefcraft.service.tag.TagPriorityTracker;
import net.chefcraft.world.player.CorePlayer;
import net.chefcraft.world.scoreboard.CoreScoreboardOption;
import net.chefcraft.world.scoreboard.CoreTeamPacketStatus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class ChefNameTagService implements NameTagService {

	private final ServerPlayer entityPlayer;
	private final CorePlayer corePlayer;
	
	private int tabListPriority;
	private boolean displayed = false;
	private boolean priorityChanged = false;
	
	private ChefTeamHolder scoreboardTeam;
	
	public ChefNameTagService(CorePlayer corePlayer, int tabListPriority) {
		this.corePlayer = corePlayer;
		this.entityPlayer = ((CraftPlayer) corePlayer.getPlayer()).getHandle();
		this.tabListPriority = tabListPriority;
		
		this.scoreboardTeam = new ChefTeamHolder(TagPriorityTracker.createEntry(corePlayer.getPlayer(), tabListPriority));
		this.scoreboardTeam.addEntry(this.entityPlayer.getBukkitEntity().getName());
		
		this.scoreboardTeam.setDisplayName(MessageHolder.text(this.entityPlayer.getBukkitEntity().getName()));
		this.scoreboardTeam.setCollisionRule(CoreScoreboardOption.NEVER);
		this.scoreboardTeam.setNameTagVisibility(CoreScoreboardOption.ALWAYS);
	}
	
	@Override
	public CorePlayer getOwner() {
		return this.corePlayer;
	}
	
	@Override
	public void display() {
		if (!displayed) {
			this.handleChanges(CoreTeamPacketStatus.CREATE);
			displayed = true;
		}
	}
	
	@Override
	public void update() {
		if (priorityChanged) {
			this.remove();
			priorityChanged = false;
			this.display();
		} else {
			this.handleChanges(CoreTeamPacketStatus.UPDATE);
		}
	}
	
	@Override
	public void remove() {
		this.handleChanges(CoreTeamPacketStatus.REMOVE);
		displayed = false;
	}
	
	public void handleChanges(CoreTeamPacketStatus status) {
		ServerGamePacketListenerImpl conn = entityPlayer.connection;
		conn.send(ScoreboardTeamPacket.buildWithTeam(scoreboardTeam, status));
		
		List<CorePlayer> players = ChefCore.getCorePlayers();
		
		for (int i = 0; i < players.size(); i++) {
			CorePlayer otherCore = players.get(i);
			if (corePlayer == otherCore) continue;
			
			ServerPlayer otherPlayer = ((CraftPlayer) otherCore.getPlayer()).getHandle();
			ChefNameTagService service = (ChefNameTagService) otherCore.getNameTagService();
			
			otherPlayer.connection.send(ScoreboardTeamPacket.buildWithTeam(scoreboardTeam, status));
			conn.send(ScoreboardTeamPacket.buildWithTeam(service.scoreboardTeam, status));
		}
	}

	@Override
	public CoreTextBase getTextModifier() {
		return this.scoreboardTeam.getTextModifier();
	}

	@Override
	public CoreScoreboardOption getCollisionRule() {
		return this.scoreboardTeam.getCollisionRule();
	}

	@Override
	public CoreScoreboardOption getNameTagVisibility() {
		return this.scoreboardTeam.getNameTagVisibility();
	}

	@Override
	public String getTagID() {
		return this.scoreboardTeam.getName();
	}

	@Override
	public int getTabListPriority() {
		return this.tabListPriority;
	}

	@Override
	public MessageHolder getPrefix() {
		return this.scoreboardTeam.getPrefix();
	}

	@Override
	public MessageHolder getSuffix() {
		return this.scoreboardTeam.getSuffix();
	}

	@Override
	public void setTabListPriority(int tabListPriority) {
		if (this.tabListPriority != tabListPriority) {
			this.tabListPriority = tabListPriority;
			
			TagPriorityTracker.removeEntry(corePlayer.getPlayer());
			String tagID = TagPriorityTracker.createEntry(corePlayer.getPlayer(), tabListPriority);
			
			this.scoreboardTeam = this.scoreboardTeam.cloneWithNewID(tagID);
			
			priorityChanged = true;
		}
	}

	@Override
	public void setTextModifier(CoreTextBase textModifier) {
		this.scoreboardTeam.setTextModifier(textModifier);
	}

	@Override
	public void setCollisionRule(CoreScoreboardOption collisionRule) {
		this.scoreboardTeam.setCollisionRule(collisionRule);
	}

	@Override
	public void setNameTagVisibility(CoreScoreboardOption nameTagVisibility) {
		this.scoreboardTeam.setNameTagVisibility(nameTagVisibility);
	}

	@Override
	public void setPrefix(MessageHolder prefix) {
		this.scoreboardTeam.setPrefix(prefix);
	}

	@Override
	public void setSuffix(MessageHolder suffix) {
		this.scoreboardTeam.setSuffix(suffix);
	}

	@Override
	public boolean canSeeFriendlyInvisibles() {
		return this.scoreboardTeam.canSeeFriendlyInvisibles();
	}

	@Override
	public void setSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
		this.scoreboardTeam.setSeeFriendlyInvisibles(canSeeFriendlyInvisibles);
	}
	
	public ChefTeamHolder getChefScoreboardTeam() {
		return this.scoreboardTeam;
	}
}
