package net.chefcraft.core.party;

import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.chefcraft.core.language.TranslationSource;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.proxy.ChefProxyCore;
import net.chefcraft.proxy.player.CoreProxyPlayer;
import net.chefcraft.reflection.base.language.VelocityMessageCompiler;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ProxyPartyCommands implements SimpleCommand {

	public static final String PERM_PARTY = "core.party";

	public ProxyPartyCommands() {
		ProxyServer server = ChefProxyCore.getInstance().getServer();
		CommandMeta.Builder builder = server.getCommandManager()
				.metaBuilder("party").aliases("p", "parti");
		server.getCommandManager().register(builder.build(), this);
	}

	@Override
	public void execute(Invocation invocation) {
		CommandSource commandSender = invocation.source();
		
		if (!commandSender.hasPermission(PERM_PARTY)) {
			VelocityMessageCompiler.sendMessage(ChefProxyCore.getInstance(), commandSender, "noPermission");
			return;
		}
		
		String[] args = invocation.arguments();
		
		if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
			VelocityMessageCompiler.sendMessage(ChefProxyCore.getInstance(), commandSender, "party.help");
			return;
		}
		
		if (commandSender instanceof Player) {
			String arg = args[0];
			CoreProxyPlayer sender = ChefProxyCore.getProxyPlayerByPlayer((Player) commandSender);
			if (arg.equalsIgnoreCase("accept") || arg.equalsIgnoreCase("join")) {


				if (sender.isInParty()) {
					sender.sendMessage("party.alreadyHave");
				} else {
					if (args.length > 1) {
						Player player = this.getPlayerWithFlags(sender, args[1], "party.cannotSendSelf");
						
						if (player != null) {
							ChefProxyCore.getProxyPlayerByPlayer(player).getPartyManager().onPartyInviteAccepted(sender);
						}
						
					} else {
						sender.sendMessage("party.commands.usageAccept");
					}
				}
				
				
			} else if (arg.equalsIgnoreCase("deny")) { 
				
				if (args.length > 1) {
					Player player = this.getPlayerWithFlags(sender, args[1], "party.cannotDenySelf");
					
					if (player != null) {
						ChefProxyCore.getProxyPlayerByPlayer(player).getPartyManager().onPartyInviteDenied(sender);
					}
					
				} else {
					sender.sendMessage("party.commands.usageDeny");
				}
				
			} else if (arg.equalsIgnoreCase("invite") || arg.equalsIgnoreCase("send") || arg.equalsIgnoreCase("add")) { 
				
				if (sender.isInParty() && !sender.isPartyOwner()) {
					sender.sendMessage("party.mustBeOwner");
				} else {
					if (args.length > 1) {
						Player player = this.getPlayerWithFlags(sender, args[1], "party.cannot");
						
						if (player != null) {
							sender.getPartyManager().inviteToYourParty(ChefProxyCore.getProxyPlayerByPlayer(player));
						} 
						
					} else {
						sender.sendMessage("party.commands.usageInvite");
					}
				}
				
			} else if (commandSender.hasPermission("core.party_staff") && (arg.equalsIgnoreCase("addAll") || arg.equalsIgnoreCase("sendAll") || arg.equalsIgnoreCase("inviteAll"))) {
				
				if (sender.isInParty() && !sender.isPartyOwner()) {
					sender.sendMessage("party.mustBeOwner");
					
				} else {
					CompletableFuture.runAsync(() -> {
						for (CoreProxyPlayer corePlayer : ChefProxyCore.getProxyPlayers()) {
							if (corePlayer.equals(sender)) continue; 
							
							sender.getPartyManager().inviteToYourParty(corePlayer);
						}
					});
				}
				
			} else if (arg.equalsIgnoreCase("setOwner")) { 
				
				this.runModeratorActionForParty(sender, args, "usageSetOwner", (other) -> sender.getPartyManager().getParty().changeOwner(other, false));
				
			} else if (arg.equalsIgnoreCase("disband")) { 
				
				if (this.hasPartyModeration(sender)) {
					sender.getPartyManager().getParty().disband(false, false);
				}
				
			} else if (arg.equalsIgnoreCase("kick")) { 
				
				this.runModeratorActionForParty(sender, args, "usageKick", (other) -> { 
					sender.getPartyManager().getParty().leave(other, true, true);
					if (sender.isInParty()) {
						sender.getPartyManager().getParty().broadcastMessage("party.kicked", Placeholder.of("{PLAYER}", PartyPlatformProvider.GET_DISPLAY_NAME.apply(other)));
					}
				});
				
			} else if (arg.equalsIgnoreCase("info") || arg.equalsIgnoreCase("status") || arg.equalsIgnoreCase("list")) { 
				
				if (sender.isInParty()) {
					sender.getPartyManager().getParty().sendPartyInfoMessage(sender);
				} else {
					sender.sendMessage("party.mustBeIn");
				}
				
			} else if (arg.equalsIgnoreCase("leave")) { 
				
				if (sender.isInParty()) {
					sender.getPartyManager().getParty().leave(sender, true, false);
				} else {
					sender.sendMessage("party.mustBeIn");
				}
				
			} else if (arg.equalsIgnoreCase("chat")) { 
				
				if (sender.isInParty()) {
					sender.setChatStatus(sender.getChatStatus().opposite(), true);
				} else {
					sender.sendMessage("party.mustBeIn");
				}
			}
		} else {
			VelocityMessageCompiler.sendMessage(ChefProxyCore.getInstance(), commandSender, "onlyPlayers");
		}
	}
	
	private void runModeratorActionForParty(CoreProxyPlayer corePlayer, String[] args, String usageNode, Consumer<? super TranslationSource> memberFunc) {
		if (this.hasPartyModeration(corePlayer)) {
			
			if (args.length > 1) {
				
				if (args[1].equalsIgnoreCase(corePlayer.getAudience().getUsername())) {
					corePlayer.sendMessage("party.cannot");
				} else {
					
					TranslationSource other = corePlayer.getPartyManager().getParty().findMemberByName(args[1]);
					
					if (other == null) {
						corePlayer.sendMessage("playerNotFound");
					} else {
						memberFunc.accept(other);
					}
				}
			} else {
				corePlayer.sendMessage("party.commands." + usageNode);
			}
			
		}
	}
	
	private Player getPlayerWithFlags(CoreProxyPlayer source, String name, String messageNode) {
		Optional<Player> optionalPlayer = ChefProxyCore.getInstance().getServer().getPlayer(name);
		
		if (optionalPlayer.isEmpty()) {
			source.sendMessage("playerNotFound");
		} else if (optionalPlayer.get().getUsername().equalsIgnoreCase(source.getAudience().getUsername())) {
			source.sendMessage(messageNode);
		} else {
			return optionalPlayer.get();
		}
		
		return null;
	}
	
	private boolean hasPartyModeration(CoreProxyPlayer corePlayer) {
		if (!corePlayer.isInParty()) {
			corePlayer.sendMessage("party.mustBeIn");
			return false;
		} else if (!corePlayer.isPartyOwner()) {
			corePlayer.sendMessage("party.mustBeOwner");
			return false;
		}
		return true;
	}
}
