package net.chefcraft.core.party;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.chefcraft.core.language.TranslationSource;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.proxy.ChefProxyCore;
import net.chefcraft.proxy.player.CoreProxyPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class VelocityParty extends AbstractParty {

	protected VelocityParty(CoreProxyPlayer owner) {
		super(owner);
	}
	
	@Override
	public Placeholder addChatPlaceholders(@NotNull TranslationSource source, @NotNull Placeholder current) {
		return ((CoreProxyPlayer) source).getPlayerMetaDataStorage().addToPlaceholder(current);
	}

	@Override
	public TranslationSource getFallbackOwner() {
		if (this.members.isEmpty()) return null;
		return this.members.get(0);
	}

	@Override
	public void onInviteAccepted(TranslationSource whoAcceptted) {
		if (whoAcceptted.getAudience() instanceof Player player 
				&& this.owner.getAudience() instanceof Player pOwner
				&& player.getCurrentServer().isPresent() 
				&& pOwner.getCurrentServer().isPresent() 
				&& !player.getCurrentServer().get().getServer().equals(pOwner.getCurrentServer().get().getServer())) {
			
			player.createConnectionRequest(pOwner.getCurrentServer().get().getServer()).fireAndForget();
		}
		
		com.velocitypowered.api.proxy.Player pOwner = this.castPlayer(this.owner);
		if (pOwner.getCurrentServer().isPresent()) {
			this.handleChangesToBackend(pOwner.getCurrentServer().get().getServer(), Action.INVITE_ACCEPTED, Optional.of(whoAcceptted));
		}
	}

	@Override
	public void onLeaderSwitchServer(Object server) {
		RegisteredServer registeredServer = (RegisteredServer) server;
		
		this.members.forEach(member -> {
			if (member.getAudience() instanceof Player pMember) {
				if (!pMember.getCurrentServer().get().getServer().equals(registeredServer)) {
					pMember.createConnectionRequest(registeredServer).connect()
			        .thenAccept(result -> {
			            switch (result.getStatus()) {
						case ALREADY_CONNECTED:
							break;
						case CONNECTION_CANCELLED:
							break;
						case CONNECTION_IN_PROGRESS:
							break;
						case SERVER_DISCONNECTED:
							break;
						case SUCCESS:
							this.handleChangesToBackend(registeredServer, Action.SERVER_SWITCH, Optional.empty());
							break;
			            }
			        });
				}
			}
		});
	}
	
	@Override
	public void onPlayerLeft(TranslationSource whoLeft, PartyLeaveCause cause) {
		com.velocitypowered.api.proxy.Player pOwner = this.castPlayer(this.owner);
		if (pOwner.getCurrentServer().isPresent()) {
			this.handleChangesToBackend(pOwner.getCurrentServer().get().getServer(), Action.PLAYER_LEFT, Optional.of(whoLeft));
		}
	}

	@Override
	public void onOwnerChanged(TranslationSource newOwner) {
		com.velocitypowered.api.proxy.Player pOwner = this.castPlayer(this.owner);
		if (pOwner.getCurrentServer().isPresent()) {
			this.handleChangesToBackend(pOwner.getCurrentServer().get().getServer(), Action.OWNER_CHANGED, Optional.of(newOwner));
		}
	}

	@Override
	public void onDisbanded() {
		com.velocitypowered.api.proxy.Player pOwner = this.castPlayer(this.owner);
		if (pOwner.getCurrentServer().isPresent()) {
			this.handleChangesToBackend(pOwner.getCurrentServer().get().getServer(), Action.DISBAND, Optional.empty());
		}
	}
	
	public com.velocitypowered.api.proxy.Player castPlayer(TranslationSource source) {
		if (source.getAudience() instanceof com.velocitypowered.api.proxy.Player player) {
			return player;
		}
		return null;
	}
	
	public boolean handleChangesToBackend(RegisteredServer server, Action action, Optional<TranslationSource> who) {
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(AbstractParty.BACKEND_SUB_CHANNEL_NAME);
		out.writeInt(action.ordinal());
		
		out.writeBoolean(who.isPresent());
		
		if (who.isPresent()) {
			UUID whoId = ((Player) who.get().getAudience()).getUniqueId();
			out.writeLong(whoId.getMostSignificantBits());
			out.writeLong(whoId.getLeastSignificantBits());
		}
		
		UUID ownerId = ((Player) this.owner.getAudience()).getUniqueId();
		out.writeLong(ownerId.getMostSignificantBits());
		out.writeLong(ownerId.getLeastSignificantBits());
		
		if (action != Action.DISBAND) {
			out.writeInt(this.members.size());
			
			this.members.forEach(member -> {
				if (member.getAudience() instanceof Player player) {
					out.writeLong(player.getUniqueId().getMostSignificantBits());
					out.writeLong(player.getUniqueId().getLeastSignificantBits());
				}
			});
		}
		
		return ChefProxyCore.getInstance().getServer().getServer(server.getServerInfo().getName()).get().sendPluginMessage(ChefProxyCore.BUNGEE_CHANNEL, out.toByteArray());
	}
}
