package net.chefcraft.core.party;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.language.TranslationSource;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.reflection.base.language.BukkitMessageCompiler;
import net.chefcraft.world.player.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.function.Consumer;

public class CorePartyCommands extends Command {
	
	public static final String PERM_PARTY = "core.party";

	public CorePartyCommands() {
		super("party", "Party management commands!", "/party", Arrays.asList("p", "parti"));
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!commandSender.hasPermission(PERM_PARTY)) {
			BukkitMessageCompiler.sendMessage(ChefCore.getInstance(), commandSender, "noPermission");
			return true;
		}
		
		if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
			BukkitMessageCompiler.sendMessage(ChefCore.getInstance(), commandSender, "party.help");
			return true;
		}
		
		if (commandSender instanceof Player) {
			String arg = args[0];
			CorePlayer sender = ChefCore.getCorePlayerByPlayer((Player) commandSender);
			if (arg.equalsIgnoreCase("accept") || arg.equalsIgnoreCase("join")) {


				if (sender.isInParty()) {
					sender.sendMessage("party.alreadyHave");
				} else {
					if (args.length > 1) {
						Player player = this.getPlayerWithFlags(sender, args[1], "party.cannotSendSelf");
						
						if (player != null) {
							ChefCore.getCorePlayerByPlayer(player).getPartyManager().onPartyInviteAccepted(sender);
						}
						
					} else {
						sender.sendMessage("party.commands.usageAccept");
					}
				}
				
				
			} else if (arg.equalsIgnoreCase("deny")) { 
				
				if (args.length > 1) {
					Player player = this.getPlayerWithFlags(sender, args[1], "party.cannotDenySelf");
					
					if (player != null) {
						ChefCore.getCorePlayerByPlayer(player).getPartyManager().onPartyInviteDenied(sender);
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
							sender.getPartyManager().inviteToYourParty(ChefCore.getCorePlayerByPlayer(player));
						} 
						
					} else {
						sender.sendMessage("party.commands.usageInvite");
					}
				}
				
			} else if (commandSender.hasPermission("core.party_staff") && (arg.equalsIgnoreCase("addAll") || arg.equalsIgnoreCase("sendAll") || arg.equalsIgnoreCase("inviteAll"))) {
				
				if (sender.isInParty() && !sender.isPartyOwner()) {
					sender.sendMessage("party.mustBeOwner");
					
				} else {
					for (CorePlayer corePlayer : ChefCore.getCorePlayers()) {
						if (corePlayer.equals(sender)) continue; 
						
						sender.getPartyManager().inviteToYourParty(corePlayer);
					}
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
			BukkitMessageCompiler.sendMessage(ChefCore.getInstance(), commandSender, "onlyPlayers");
		}
		return true;
	}
	
	private void runModeratorActionForParty(CorePlayer corePlayer, String[] args, String usageNode, Consumer<? super TranslationSource> memberFunc) {
		if (this.hasPartyModeration(corePlayer)) {
			
			if (args.length > 1) {
				
				if (args[1].equalsIgnoreCase(corePlayer.getPlayer().getName())) {
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
	
	private Player getPlayerWithFlags(CorePlayer source, String name, String messageNode) {
		Player player = Bukkit.getPlayer(name);
		
		if (player == null) {
			source.sendMessage("playerNotFound");
		} else if (player.getName().equalsIgnoreCase(source.getPlayer().getName())) {
			source.sendMessage(messageNode);
		} else {
			return player;
		}
		
		return null;
	}
	
	private boolean hasPartyModeration(CorePlayer corePlayer) {
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
