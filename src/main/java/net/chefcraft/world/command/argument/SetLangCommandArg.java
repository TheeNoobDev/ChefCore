package net.chefcraft.world.command.argument;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.event.PlayerLanguageChangeEvent;
import net.chefcraft.core.language.Language;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.reflection.base.language.BukkitMessageCompiler;
import net.chefcraft.world.player.CorePlayer;
import net.chefcraft.world.util.BukkitUtils;
import net.chefcraft.world.util.CommandArg;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @since 0.0.1
 */
public class SetLangCommandArg extends CommandArg {

	public SetLangCommandArg() {}
	
	@Override
	public String getName() {
		return "setLang";
	}

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {
		ChefCore plugin = ChefCore.getInstance();
		if (sender.hasPermission("core.commands.setlang")) {
			if (sender instanceof ConsoleCommandSender) {
				if (args.length < 3) {
					BukkitMessageCompiler.sendMessage(plugin, sender, "commands.usage.setLang");
				} else {
					Language lang = ChefCore.getLanguageByLocale(args[1]);
					Player player = Bukkit.getPlayerExact(args[2]);
					CorePlayer languagePlayer = player != null ? ChefCore.getCorePlayerByPlayer(player) : null;
					if (lang == null) {
						BukkitMessageCompiler.sendMessage(plugin, sender, "language.notFound", Placeholder.of("{INPUT}", args[1]));
					} else if (languagePlayer == null) {
						BukkitMessageCompiler.sendMessage(plugin, sender, "playerNotFound");
					} else {
						languagePlayer.setLanguage(lang, true, PlayerLanguageChangeEvent.Cause.CONSOLE_COMMAND);
						ChefCore.getPlayerDatabase().setData("Language", player.getUniqueId(), lang.getLocale());
						BukkitMessageCompiler.sendMessage(plugin, languagePlayer.getPlayer(), "language.setted", Placeholder.of("{NAME}", lang.getName()).add("{LOCALE}", lang.getLocale()).add("{REGION}", lang.getRegion()));
						BukkitMessageCompiler.sendMessage(plugin, sender, "language.settedOther", Placeholder.of("{PLAYER}", player.getName()).add("{LOCALE}", lang.getLocale()));
					}
				}
			} else {
				Player playerSender = (Player) sender;
				CorePlayer coreSender = ChefCore.getCorePlayerByUniqueId(playerSender.getUniqueId());
				
				if (args.length < 2) {
					BukkitMessageCompiler.sendMessage(plugin, sender, "commands.usage.setLang");
				} else {
					Language lang = ChefCore.getLanguageByLocale(args[1]);
					if (args.length > 2) {
						Player player = Bukkit.getPlayerExact(args[2]);
						CorePlayer languagePlayer = player != null ? ChefCore.getCorePlayerByPlayer(player) : null;
						if (lang == null) {
							BukkitMessageCompiler.sendMessage(plugin, playerSender, "language.notFound", Placeholder.of("{INPUT}", args[1]));
						} else if (languagePlayer == null) {
							coreSender.sendMessage("playerNotFound");
						} else {
							languagePlayer.setLanguage(lang, true, PlayerLanguageChangeEvent.Cause.CONSOLE_COMMAND);
							languagePlayer.saveData();

							languagePlayer.sendMessage("language.setted", Placeholder.of("{NAME}", lang.getName()).add("{LOCALE}", lang.getLocale()).add("{REGION}", lang.getRegion()));
							coreSender.sendMessage("language.settedOther", Placeholder.of("{PLAYER}", player.getName()).add("{LOCALE}", lang.getLocale()));
							
						}
					} else {
						if (lang == null) {
							coreSender.sendMessage("language.notFound", Placeholder.of("{INPUT}", args[1]));
						} else {
							ChefCore.getCorePlayerByPlayer(playerSender).setLanguage(lang, true, PlayerLanguageChangeEvent.Cause.CONSOLE_COMMAND);
							ChefCore.getCorePlayerByPlayer(playerSender).saveData();
							coreSender.sendMessage("language.setted", Placeholder.of("{NAME}", lang.getName()).add("{LOCALE}", lang.getLocale()).add("{REGION}", lang.getRegion()));
						}
					}
				}
			}
		} else {
			BukkitMessageCompiler.sendMessage(plugin, sender, "noPermission");
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return args.length == 2 ? BukkitUtils.sortTabCompleterResults(args[1], ChefCore.getLanguageLocales()) : null;
	}

}
