package net.chefcraft.core.party;

import net.chefcraft.core.collect.ImmutableList;
import net.chefcraft.core.language.TranslationSource;
import net.chefcraft.core.util.JHelper;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.proxy.player.CoreProxyPlayer;
import net.chefcraft.world.player.CorePlayer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class AbstractParty implements PartyListener<TranslationSource> {
	
	public static final String BACKEND_SUB_CHANNEL_NAME = "VelocityParty";

	private static final String MESSAGES_ALREADY_OWNER_KEY = "party.youAlreadyOwner";
	private static final String MESSAGES_INFO_KEY = "party.info";
	private static final String MESSAGES_OWNER_CHANGED_KEY = "party.ownerChanged";
	private static final String MESSAGES_JOINED_KEY = "party.joined";
	private static final String MESSAGES_LEFT_KEY = "party.left";
	private static final String MESSAGES_FULL_KEY = "party.full";
	private static final String MESSAGES_CANNOT_INVITE_KEY = "party.cannotInvite";
	private static final String MESSAGES_DISBAND_KEY = "party.disband";
	private static final String MESSAGES_DISBAND_LEFT_KEY = "party.disbandLeft";
	private static final String SOUNDS_ERROR_KEY = "party.error";
	
	protected TranslationSource owner;
	protected List<TranslationSource> members = new ArrayList<>();
	
	protected AbstractParty(TranslationSource owner) {
		this.owner = owner;
	}
	
	public abstract Placeholder addChatPlaceholders(TranslationSource whoTyped, @NotNull Placeholder current);
	
	public abstract TranslationSource getFallbackOwner();
	
	public TranslationSource getOwner() {
		return this.owner;
	}
	
	public List<? extends TranslationSource> listMembers() {
		return ImmutableList.copyOf(this.members);
	}
	
	public List<? extends TranslationSource> listPlayers() {
		return ImmutableList.copyOfWith(this.members, this.owner);
	}
	
	public int size() {
		return members.size() + 1;
	}
	
	@Nullable
	public TranslationSource findMemberByName(String name) {
		for (int i = 0; i < members.size(); i++) {
			TranslationSource translationSource = this.members.get(i);
			
			if (PartyPlatformProvider.GET_USERNAME.apply(translationSource).equalsIgnoreCase(name)) {
				return translationSource;
			}
		}
		
		return null;
	}
	
	public boolean checkPartySize(int modifier) {
		return this.owner.getPartyManager().getMaxPartySize() > 0 ? this.owner.getPartyManager().getMaxPartySize() >= (members.size() + 1 + modifier) : true;
	}
	
	public boolean checkPartySizeWithNotify(int modifier) {
		boolean flag = this.checkPartySize(modifier);
		if (!flag) {
			this.owner.sendMessage(MESSAGES_CANNOT_INVITE_KEY);
			PartyPlatformProvider.PLAY_KEYED_SOUND.accept(this.owner, SOUNDS_ERROR_KEY);
		}
		return flag;
	}
	
	public void addPlayer(TranslationSource translationSource) {
		if (this.checkPartySize(1)) {
			this.addPlayerForce(translationSource, true);
		} else {
			translationSource.sendMessage(MESSAGES_FULL_KEY);
		}
	}
	
	public boolean addPlayerForce(TranslationSource translationSource, boolean broadcast) {
		if (!this.members.contains(translationSource)) {
			this.members.add(translationSource);
			translationSource.getPartyManager().setParty(this);
			
			if (broadcast) {
				this.broadcastMessage(MESSAGES_JOINED_KEY, Placeholder.of("{PLAYER}", PartyPlatformProvider.GET_DISPLAY_NAME.apply(translationSource)).add("{CURRENT}", members.size() + 1).add("{MAX}", this.owner.getPartyManager().getFormattedMaxPartySize()));
			}
			
			return true;
		}
		return false;
	}
	
	public boolean hasPlayer(TranslationSource translationSource) {
		return this.members.contains(translationSource);
	}
	
	public void changeOwner(TranslationSource newOwner, boolean oldOwnerLeft) {
		if (this.owner.equals(newOwner)) {
			this.owner.sendMessage(MESSAGES_ALREADY_OWNER_KEY);
			PartyPlatformProvider.PLAY_KEYED_SOUND.accept(this.owner, SOUNDS_ERROR_KEY);
		} else {
			
			if (!oldOwnerLeft && !this.members.contains(this.owner)) {
				this.members.add(this.owner);
			}
			
			this.owner = newOwner;
			
			if (this.members.contains(newOwner)) {
				this.members.remove(newOwner);
			}
			
			this.broadcastMessage(MESSAGES_OWNER_CHANGED_KEY, Placeholder.of("{OWNER}", PartyPlatformProvider.GET_DISPLAY_NAME.apply(newOwner)));
			this.onOwnerChanged(newOwner);
			
		}
	}
	
	public Placeholder onTypeToPartyChat(TranslationSource whoTyped, String message) {
		Placeholder holder = this.addChatPlaceholders(whoTyped, Placeholder.of("{MESSAGE}", message));
		
		this.owner.sendMessage("party.chatFormat", holder);
		
		for (int i = 0; i < members.size(); i++) {
			TranslationSource translationSource = this.members.get(i);
			
			translationSource.sendMessage("party.chatFormat", holder);
		}
		
		return holder;
	}
	
	public boolean leave(TranslationSource translationSource, boolean broadcast, boolean kick) {
		if (this.members.contains(translationSource)) {
			this.members.remove(translationSource);
			translationSource.getPartyManager().setParty(null);
			
			if (broadcast) {
				this.broadcastMessage(MESSAGES_LEFT_KEY, Placeholder.of("{PLAYER}", PartyPlatformProvider.GET_DISPLAY_NAME.apply(translationSource)).add("{CURRENT}", members.size() + 1).add("{MAX}", this.owner.getPartyManager().getFormattedMaxPartySize()));
			}
			
			if (kick) {
				translationSource.sendMessage("party.kickedSelf");
				this.onPlayerLeft(translationSource, PartyLeaveCause.KICK);
				
			} else {
				translationSource.sendMessage("party.leftSelf");
				this.onPlayerLeft(translationSource, PartyLeaveCause.LEFT);
			}
			
			if (this.members.isEmpty()) {
				if (kick) {
					this.owner.sendMessage("party.kicked", Placeholder.of("{PLAYER}", PartyPlatformProvider.GET_DISPLAY_NAME.apply(translationSource)));
				}
				this.disband(true, false);
			}
			
			return true;
		} else if (this.owner.equals(translationSource)) {
			translationSource.getPartyManager().setParty(null);
			translationSource.getPartyManager().clearPendingPartyInvites();
			
			if (this.members.size() > 1) {
				if (broadcast) {
					this.broadcastMessage(MESSAGES_LEFT_KEY, Placeholder.of("{PLAYER}", PartyPlatformProvider.GET_DISPLAY_NAME.apply(translationSource)).add("{CURRENT}", members.size()).add("{MAX}", this.owner.getPartyManager().getFormattedMaxPartySize()));
				}
				this.changeOwner(this.getFallbackOwner(), true);
				this.onPlayerLeft(translationSource, PartyLeaveCause.LEADER_MOVED);
			} else {
				
				if (broadcast) {
					this.members.forEach(member -> {
						member.sendMessage(MESSAGES_LEFT_KEY, Placeholder.of("{PLAYER}", PartyPlatformProvider.GET_DISPLAY_NAME.apply(translationSource)).add("{CURRENT}", members.size()).add("{MAX}", this.owner.getPartyManager().getFormattedMaxPartySize()));
					});
				}
				
				this.disband(false, false);
				this.owner = null;
			}
			
			return true;
		}
		return false;
	}
	
	public void disband(boolean allPlayersLeft, boolean silent) {
		if (this.owner != null) {
			if (!silent) {
				this.owner.sendMessage(allPlayersLeft ? MESSAGES_DISBAND_LEFT_KEY : MESSAGES_DISBAND_KEY);
			}
			this.owner.getPartyManager().setParty(null);
		}
		
		this.members.forEach(member -> {
			if (!silent) {
				member.sendMessage(MESSAGES_DISBAND_KEY);
			}
			member.getPartyManager().setParty(null);
		});
		
		this.members.clear();
		
		this.onDisbanded();
	}
	
	public boolean isOwner(TranslationSource translationSource) {
		return this.owner != null && this.owner.equals(translationSource);
	}
	
	public boolean isOwner(UUID uuid) {
		if (this.owner == null) return false;
		if (this.owner.getAudience() instanceof com.velocitypowered.api.proxy.Player player) {
			return player.getUniqueId().equals(uuid);
		}
		
		if (this.owner.getAudience() instanceof org.bukkit.entity.Player player) {
			return player.getUniqueId().equals(uuid);
		}
		
		return false;
	}
	
	public void broadcastMessage(String node, Placeholder placeholder) {
		this.owner.sendMessage(node, placeholder);
		this.members.forEach(member -> member.sendMessage(node, placeholder));
	}
	
	public void sendPartyInfoMessage(TranslationSource translationSource) {
		translationSource.sendMessage(MESSAGES_INFO_KEY, Placeholder.of("{OWNER}", PartyPlatformProvider.GET_DISPLAY_NAME.apply(this.owner)).add("{PLAYERS}",
				JHelper.parseList(this.members, ", ", core -> PartyPlatformProvider.GET_DISPLAY_NAME.apply(core).asString(false))).add("{CURRENT}", members.size() + 1).add("{MAX}", this.owner.getPartyManager().getFormattedMaxPartySize()));
	}
	
	@NotNull
	public static AbstractParty create(@NotNull TranslationSource translationSource) {
		Objects.requireNonNull(translationSource);
		
		if (translationSource instanceof CorePlayer corePlayer) {
			return new BukkitParty(corePlayer);
		}
		
		if (translationSource instanceof CoreProxyPlayer proxyPlayer) {
			return new VelocityParty(proxyPlayer);
		}
		
		throw new IllegalStateException("translationSource must be CorePlayer or CoreProxyPlayer");
	}
	
	public static enum Action {
		
		INVITE_ACCEPTED,
		SERVER_SWITCH,
		PLAYER_LEFT,
		OWNER_CHANGED,
		DISBAND;
		
		public static final Action[] VALUES = Action.values();
	}
}
