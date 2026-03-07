package net.chefcraft.world.menu;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.reflection.base.language.BukkitMessageCompiler;
import net.chefcraft.world.player.CorePlayer;
import net.chefcraft.world.util.BukkitUtils;
import net.chefcraft.world.util.CommandArg;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class MenuBuilderCommandArg extends CommandArg implements Listener {
	
	//pail fix this class
	
	private final Map<CorePlayer, Inventory> scannerMap = new HashMap<>();
	private boolean scannerEnabled = false;

	public MenuBuilderCommandArg() {}
	
	@EventHandler
	public void onInventoryOpenForScan(InventoryOpenEvent event) {
		if (scannerMap.isEmpty()) return;
		Player player = (Player) event.getPlayer();
		CorePlayer corePlayer = ChefCore.getCorePlayerByPlayer(player);
		if (corePlayer == null) return;
		if (!this.scannerMap.containsKey(corePlayer)) return;
		disableScanner(corePlayer);
		this.scannerMap.put(corePlayer, event.getInventory());
		
		corePlayer.sendMessage("menuBuilder.scanned");
		ChefCore.getSoundManager().playSound(player, "successful");
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {
		ChefCore plugin = ChefCore.getInstance();
		if (sender.hasPermission("core.commands.menuBuilder")) {
			if (!(sender instanceof Player)) {
				BukkitMessageCompiler.sendMessage(plugin, sender, "onlyPlayers");
			} else {
				if (args.length < 2) {
					BukkitMessageCompiler.sendMessage(plugin, sender, "menuBuilder.help");
				} else {
					CorePlayer corePlayer = ChefCore.getCorePlayerByPlayer((Player) sender);
					if (corePlayer == null) { return true; }
					
					if (args[1].equalsIgnoreCase("scan")) {
						enableScanner(corePlayer);
						corePlayer.sendMessage("menuBuilder.scannerEnabled");
					} else if (args[1].equalsIgnoreCase("cancel")) {
						disableScanner(corePlayer);
						corePlayer.sendMessage("menuBuilder.scannerDisabled");
					} else if (args[1].equalsIgnoreCase("save")) {
						if (args.length < 3) {
							corePlayer.sendMessage("menuBuilder.usageSave");
						} else {
							if (MenuController.isMenuFileExist(args[2])) {
								corePlayer.sendMessage("menuBuilder.fileExist");
							} else {
								if (this.scannerMap.containsKey(corePlayer)) {
									AbstractTranslatableMenu menu = null;
									try {
										menu = MenuController.readFromInventory(plugin, args[2], this.scannerMap.get(corePlayer));
									} catch (IOException e) {
										e.printStackTrace();
										Bukkit.getLogger().log(Level.SEVERE, "Failed to create '" + args[2] + "' menu!"); 
									}
									corePlayer.sendMessage("menuBuilder.saved", Placeholder.of("{PATH}", menu.getYamlFile().getFile().getAbsolutePath()));
									corePlayer.playSound("successful");
									disableScanner(corePlayer);
								} else {
									corePlayer.sendMessage("menuBuilder.lostScan");
								}
							}
						}
					} else {
						corePlayer.sendMessage("menuBuilder.incorrectParams");
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
		return args.length == 2 ? BukkitUtils.sortTabCompleterResults(args[1], Arrays.asList("scan", "cancel", "save")) : null;
	}
	
	public void disableScanner(CorePlayer corePlayer) {
		if (this.scannerEnabled && !this.scannerMap.isEmpty()) {
			scannerEnabled = false;
			InventoryOpenEvent.getHandlerList().unregister(this);
		}
		this.scannerMap.remove(corePlayer);
	}
	
	public void enableScanner(CorePlayer corePlayer) {
		if (!this.scannerEnabled && this.scannerMap.isEmpty()) {
			scannerEnabled = false;
			Bukkit.getPluginManager().registerEvents(this, ChefCore.getInstance());
		}
		this.scannerMap.put(corePlayer, null);
	}
	
	@Override
	public String getName() {
		return "menuBuilder";
	}
}