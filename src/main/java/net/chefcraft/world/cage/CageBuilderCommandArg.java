package net.chefcraft.world.cage;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.reflection.base.language.BukkitMessageCompiler;
import net.chefcraft.world.util.CommandArg;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CageBuilderCommandArg extends CommandArg {

	private ChefCore plugin = ChefCore.getInstance();
	
	private static List<PlaceableCage> placeableCages = new ArrayList<>();
	
	public CageBuilderCommandArg() {}
	
	@Override
	public String getName() {
		return "cage";
	}

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {
		if (!sender.hasPermission("gamecore.commands.cage")) {
			BukkitMessageCompiler.sendMessage(plugin, sender, "noPermission");
			return true;
		}
		
		if (!(sender instanceof Player)) {
			BukkitMessageCompiler.sendMessage(plugin, sender, "onlyPlayers");
			return true;
		}
		
		if (args.length < 2) {
			BukkitMessageCompiler.sendMessage(plugin, sender, "cageBuilder.usage.all");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (args[1].equalsIgnoreCase("builder")) {
			
			if (args.length == 4 && args[2].equalsIgnoreCase("create")) {
				
				new CageBuilder(args[3]).givePosSelector((Player) sender);
				BukkitMessageCompiler.sendMessage(plugin, sender, "cageBuilder.builder.created", Placeholder.of("{NAME}", args[3]));
				
			} else if (args.length == 4 && args[2].equalsIgnoreCase("save")) {
				
				CageBuilder builder = CageBuilder.getCageBuilderMap().get(args[3]);
				if (builder != null) {
					builder.save((Player) sender);
				} else {
					BukkitMessageCompiler.sendMessage(plugin, sender, "cageBuilder.builder.notFound");
				}
				
			} else if (args.length == 4 && args[2].equalsIgnoreCase("remove")) {
				
				if (CageBuilder.hasCageBuilder(args[3])) {
					CageBuilder builder = CageBuilder.getCageBuilderMap().remove(args[3]);
					
					builder.deleteYamlFile();
					
					BukkitMessageCompiler.sendMessage(plugin, sender, "cageBuilder.builder.removed", Placeholder.of("{NAME}", args[3]));
					
				} else {
					BukkitMessageCompiler.sendMessage(plugin, sender, "cageBuilder.builder.notFound");
				}
				
			} else {
				BukkitMessageCompiler.sendMessage(plugin, sender, "cageBuilder.usage.builder");
			}
			return true;
		}
		
		if (args[1].equalsIgnoreCase("viewer")) {
			if (args.length == 4 && args[2].equalsIgnoreCase("place")) {
				
				Cage cage = CageUtils.getCageByNamespaceID(args[3]);
				
				if (cage != null) {
					PlaceableCage place = new PlaceableCage();
					place.setCage(cage);
					place.place(player.getLocation(), true);
					
					
					placeableCages.add(place);
					BukkitMessageCompiler.sendMessage(plugin, sender, "cageBuilder.viewer.placed", Placeholder.of("{NAME}", cage.getNamespaceID()));
				} else {
					BukkitMessageCompiler.sendMessage(plugin, sender, "cageBuilder.viewer.notFound");
				}
				
			} else if (args.length == 4 && args[2].equalsIgnoreCase("remove")) {
				
				PlaceableCage cage = getPlaceableCageByName(args[3]);
				
				if (cage != null) {
					cage.remove();
					
					BukkitMessageCompiler.sendMessage(plugin, sender, "cageBuilder.viewer.removed", Placeholder.of("{NAME}", cage.getCage().getNamespaceID()));
				} else {
					BukkitMessageCompiler.sendMessage(plugin, sender, "cageBuilder.viewer.notFound");
				}
				
			} else if (args.length == 3 && args[2].equalsIgnoreCase("removeAll")) {
				if (!placeableCages.isEmpty()) {
					
					for (PlaceableCage cage : placeableCages) {
						cage.remove();
					}
					
					BukkitMessageCompiler.sendMessage(plugin, sender, "cageBuilder.viewer.removedAll");
					
				} else {
					BukkitMessageCompiler.sendMessage(plugin, sender, "cageBuilder.viewer.placedEmpty");
				}
			} else {
				BukkitMessageCompiler.sendMessage(plugin, sender, "cageBuilder.usage.viewer");
			}
		}
		
		return true;
	}
	
	private static PlaceableCage getPlaceableCageByName(String name) {
		for (PlaceableCage cage : placeableCages) {
			if (cage.getCage().getNamespaceID().equalsIgnoreCase(name)) {
				return cage;
			}
		}
		return null;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return args.length == 2 ? Arrays.asList("builder", "viewer") : 
			args.length == 3 ? args[1].equalsIgnoreCase("viewer") ? Arrays.asList("place", "remove", "removeAll") : Arrays.asList("create", "save", "remove")  :null;
	}
}
