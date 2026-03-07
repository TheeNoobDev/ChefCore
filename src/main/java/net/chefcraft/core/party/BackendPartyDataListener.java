package net.chefcraft.core.party;

import com.google.common.io.ByteArrayDataInput;
import net.chefcraft.core.ChefCore;
import net.chefcraft.core.collect.FunctionalEnums;
import net.chefcraft.core.party.AbstractParty.Action;
import net.chefcraft.world.player.CorePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BackendPartyDataListener {
	
	public static void handleCorePlayerJoin(CorePlayer corePlayer) {
		MemoryPartyDataHolder.PartyMember member = MemoryPartyDataHolder.getParty(corePlayer.getPlayer().getUniqueId());
		
		if (member == null) return;
		
		if (member.isOwner()) {
			corePlayer.getPartyManager().getOrCreateParty();
			return;
		}
		
		CorePlayer partyOwner = ChefCore.getCorePlayerByUniqueId(member.getMemoryParty().getOwnerUniqueId());
		if (partyOwner == null || !partyOwner.isPartyOwner()) return;
		
		partyOwner.getPartyManager().getParty().addPlayerForce(corePlayer, false);
	}

	public static void onPartyDataReceived(@NotNull String channel, @NotNull Player player, @NotNull ByteArrayDataInput dataInput) {
		Action action = (Action) FunctionalEnums.getByOrdinal(dataInput.readInt(), Action.VALUES);
		
		UUID whoId = null;
		
		if (dataInput.readBoolean()) {
			long whoMost = dataInput.readLong();
	        long whoLeast = dataInput.readLong();
	        whoId = new UUID(whoMost, whoLeast);
		}
		
		long ownerMost = dataInput.readLong();
        long ownerLeast = dataInput.readLong();
        UUID ownerId = new UUID(ownerMost, ownerLeast);
        
        CorePlayer corePlayer = ChefCore.getCorePlayerByUniqueId(ownerId);
        
        if (corePlayer != null) {
        	MemoryPartyDataHolder holder = MemoryPartyDataHolder.getOrCreate(corePlayer);

            if (action != Action.DISBAND) {
            	
            	holder.members.clear();
            	
            	int memberCount = dataInput.readInt();
                for (int i = 0; i < memberCount; i++) {
                    long most = dataInput.readLong();
                    long least = dataInput.readLong();
                    holder.members.add(new UUID(most, least));
                }
            }
            
            switch (action) {
    		case DISBAND:
    			holder.onDisbanded();
    			break;
    		case INVITE_ACCEPTED:
    			holder.onInviteAccepted(whoId);
    			break;
    		case OWNER_CHANGED:
    			holder.onOwnerChanged(whoId);
    			break;
    		case PLAYER_LEFT:
    			holder.onPlayerLeft(whoId, PartyLeaveCause.LEFT);
    			break;
    		case SERVER_SWITCH:
    			holder.onLeaderSwitchServer(null);
    			break;
    		default:
    			break;
            }
        }
        
	}
}
