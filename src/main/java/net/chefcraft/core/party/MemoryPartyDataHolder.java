package net.chefcraft.core.party;

import com.google.common.collect.ImmutableList;
import net.chefcraft.core.ChefCore;
import net.chefcraft.world.player.CorePlayer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public class MemoryPartyDataHolder implements PartyListener<UUID> {
	
	private static final Map<UUID, MemoryPartyDataHolder> PARTIES_BY_OWNER_ID = new HashMap<>();

	private CorePlayer owner;
	private UUID ownerId;
	protected List<UUID> members = new ArrayList<>();
	
	public MemoryPartyDataHolder(CorePlayer owner) {
		this.owner = owner;
		this.ownerId = owner.getPlayer().getUniqueId();
	}

	public CorePlayer getOwner() {
		return this.owner;
	}
	
	public UUID getOwnerUniqueId() {
		return this.ownerId;
	}

	public List<UUID> getMembers() {
		return ImmutableList.copyOf(this.members);
	}
	
	@Override
	public void onInviteAccepted(UUID whoAcceptted) {
		if (!this.members.contains(whoAcceptted)) {
			this.members.add(whoAcceptted);
			
			CorePlayer corePlayer = ChefCore.getCorePlayerByUniqueId(whoAcceptted);
			if (this.owner.isPartyOwner() && corePlayer != null) {
				this.owner.getPartyManager().getParty().addPlayerForce(corePlayer, false);
			}
		}
	}

	@Override
	public void onPlayerLeft(UUID whoLeft, PartyLeaveCause cause) {
		if (this.members.remove(whoLeft)) {
			
			CorePlayer corePlayer = ChefCore.getCorePlayerByUniqueId(whoLeft);
			if (this.owner.isPartyOwner() && corePlayer != null) {
				this.owner.getPartyManager().getParty().leave(corePlayer, false, false);
			}
		}
	}

	@Override
	public void onOwnerChanged(UUID newOwner) {
		CorePlayer newCoreOwner = ChefCore.getCorePlayerByUniqueId(newOwner);
		
		if (newCoreOwner != null) {
			CorePlayer legacyOwner = ChefCore.getCorePlayerByUniqueId(this.ownerId);
			if (legacyOwner != null) {
				this.members.add(this.ownerId);
				
				if (legacyOwner.isPartyOwner()) {
					legacyOwner.getPartyManager().getParty().disband(false, true);
				}
			}
			
			this.owner = newCoreOwner;
			this.ownerId = newOwner;
			this.members.remove(ownerId);
		}
		
		AbstractParty party = newCoreOwner.getPartyManager().getOrCreateParty();
		
		for (UUID memberId : this.members) {
			CorePlayer coreMember = ChefCore.getCorePlayerByUniqueId(memberId);
			
			if (coreMember != null) {
				party.addPlayerForce(coreMember, false);
			}
		}
	}

	@Override
	public void onDisbanded() {
		if (this.owner.isPartyOwner()) {
			this.owner.getPartyManager().getParty().disband(false, false);
		}
		
		removeIfPresent(this.ownerId);
		this.members.clear();
		this.owner = null;
		this.ownerId = null;
	}

	@Override
	public void onLeaderSwitchServer(Object server) { }
	
	@NotNull
	public static MemoryPartyDataHolder getOrCreate(CorePlayer corePlayer) {
		UUID ownerUUID = corePlayer.getPlayer().getUniqueId();
		MemoryPartyDataHolder holder = PARTIES_BY_OWNER_ID.get(ownerUUID);	
		if (holder == null) {
			holder = new MemoryPartyDataHolder(corePlayer);
			PARTIES_BY_OWNER_ID.put(ownerUUID, holder);
		}
		return holder;
	}
	
	public static boolean hasParty(UUID uuid) {
		return PARTIES_BY_OWNER_ID.containsKey(uuid);
	}
	
	public static boolean removeIfPresent(UUID uuid) {
		return PARTIES_BY_OWNER_ID.remove(uuid) != null;
	}
	
	@Nullable
	public static PartyMember getParty(UUID uuid) {
		for (MemoryPartyDataHolder holder : PARTIES_BY_OWNER_ID.values()) {
			if (holder.ownerId.equals(uuid)) {
				return PartyMember.asOwner(holder);
			}
			
			if (holder.members.contains(uuid)) {
				return PartyMember.asMember(holder);
			}
		}
		return null;
	}
	
	public static interface PartyMember {
		
		boolean isOwner();
		
		MemoryPartyDataHolder getMemoryParty();
		
		static PartyMember asOwner(@NotNull final MemoryPartyDataHolder holder) {
			return new PartyMemberImpl(true, holder);
		}
		
		static PartyMember asMember(@NotNull final MemoryPartyDataHolder holder) {
			return new PartyMemberImpl(false, holder);
		}

		final class PartyMemberImpl implements PartyMember {
			
			private final boolean owner;
			private final MemoryPartyDataHolder holder;
			
			public PartyMemberImpl(boolean owner, MemoryPartyDataHolder holder) {
				this.owner = owner;
				this.holder = holder;
			}

			@Override
			public boolean isOwner() {
				return this.owner;
			}

			@Override
			public MemoryPartyDataHolder getMemoryParty() {
				return this.holder;
			}
			
		}
	}
}
