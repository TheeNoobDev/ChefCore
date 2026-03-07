package net.chefcraft.world.cage;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.component.CoreTextColor;
import net.chefcraft.core.configuration.CoreConfigKey;
import net.chefcraft.core.configuration.YamlFile;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.reflection.base.language.BukkitMessageCompiler;
import net.chefcraft.reflection.world.CoreMaterial;
import net.chefcraft.reflection.world.GameReflections;
import net.chefcraft.world.cage.CageLayer.CageBlock;
import net.chefcraft.world.util.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CageBuilder {
	
	private static final GameReflections GAME_REFLECTOR = ChefCore.getReflector().getGameReflections();
	
	private static Map<String, CageBuilder> cageBuilderMap = new HashMap<>();
	
	public static Map<String, CageBuilder> getCageBuilderMap() {
		return cageBuilderMap;
	}
	
	public static boolean hasCageBuilder(String namespaceid) {
		for (String cg : cageBuilderMap.keySet()) {
			if (cg.equalsIgnoreCase(namespaceid)) {
				return true;
			}
		}
		return false;
	}
	
	private final Cage cage;
	private YamlFile yamlFile;
	private Location pos1 = null;
	private Location pos2 = null;
	
	public CageBuilder(String namespaceID) {
		this.cage = new Cage(namespaceID);
		this.cage.setPrefixKey("cages." + namespaceID);
		cageBuilderMap.put(namespaceID.toLowerCase(), this);
	}
	
	public void givePosSelector(Player player) {
		ItemBuilder builder = ItemBuilder.builder()
			.type(CoreMaterial.STICK.toMaterial())
			.name(MessageHolder.text(CoreTextColor.LIGHT_PURPLE + "Cage Corners Selector"))
			.lore(MessageHolder.text(this.cage.getNamespaceID()));
		
		player.getInventory().setItem(0, builder.build());
	}
	
	public boolean save(Player player) {
		if (pos1 != null && pos2 != null) {
			
			int xMin = Math.min(pos1.getBlockX(), pos2.getBlockX());
			int xMax = Math.max(pos1.getBlockX(), pos2.getBlockX());
			int yMin = Math.min(pos1.getBlockY(), pos2.getBlockY());
			int yMax = Math.max(pos1.getBlockY(), pos2.getBlockY());
			int zMin = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
			int zMax = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
			
			if (Math.abs(xMax - xMin) > 10 || Math.abs(xMax - xMin) > 3 || Math.abs(zMin - zMax) > 3) {
				BukkitMessageCompiler.sendMessage(ChefCore.getInstance(), player, "cageBuilder.builder.posTooBig");
			} else {
				
				int layer = 1;
				World world = pos1.getWorld();
				
				for (int y = yMin; y <= yMax; y++) {
					
					CageLayer cageLayer = new CageLayer(layer);
					int placement = 0;
					
					for (int x = xMin; x <= xMax; x++) {
						for (int z = zMin; z <= zMax; z++) {
							Block block = world.getBlockAt(x, y, z);
							placement++;
							
							if (block.getType() == Material.AIR) continue;
							CageBlock cageBlock = new CageBlock();
							cageBlock.setPlacement(placement);
							cageBlock.getMaterialList().add(new MaterialData(block.getType(), block.getData()));
							
							cageLayer.addCageBlock(cageBlock);
							
						}
					}
					
					if (!cageLayer.getCageBlockList().isEmpty()) {
						this.cage.getCageLayerMap().put(layer, cageLayer);
					}
					
					layer++;
				}
				this.yamlFile = ChefCore.getGlobalConfigHandler().create(CoreConfigKey.CAGES, "cages" + File.separator + this.cage.getNamespaceID() + ".yml");
				this.cage.saveToConfig(this.yamlFile);
				CageUtils.registerCage(this.cage);
				
				BukkitMessageCompiler.sendMessage(ChefCore.getInstance(), player, "cageBuilder.builder.done");
				return true;
			}
			
		} else {
			BukkitMessageCompiler.sendMessage(ChefCore.getInstance(), player, "cageBuilder.builder.posCannotBeNull");
		}
		return false;
	}
	
	public YamlFile getYamlFile() {
		return yamlFile;
	}
	
	public void deleteYamlFile() {
		if (this.yamlFile != null) {
			this.yamlFile.delete();
			this.yamlFile = null;
		}
	}
	
	public static class BuilderListener implements Listener {
		
		@EventHandler
		public void onInteract(PlayerInteractEvent event) {
			if (cageBuilderMap.isEmpty()) return;
			if (!event.hasBlock() && !event.hasItem()) return;
			Player player = event.getPlayer();
			Action action = event.getAction();
			
			if (action != Action.RIGHT_CLICK_BLOCK && action != Action.LEFT_CLICK_BLOCK) return;
			ItemStack item = GAME_REFLECTOR.getItemFromPlayerHand(player, false);
			
			if (item == null || item.getType() != Material.STICK || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName() || !item.getItemMeta().getDisplayName().contains("Cage")) return;
			event.setCancelled(true);
			CageBuilder builder = cageBuilderMap.get(item.getItemMeta().getLore().get(0));
			if (builder == null) {
				BukkitMessageCompiler.sendMessage(ChefCore.getInstance(), player, "cageBuilder.builder.notFound");
				return;
			}
			if (action == Action.LEFT_CLICK_BLOCK) {
				Location pos1 = event.getClickedBlock().getLocation();
				builder.pos1 = pos1;
				BukkitMessageCompiler.sendMessage(ChefCore.getInstance(), player, "cageBuilder.builder.pos1Selected", 
						Placeholder.of("{POS}","(X: " + pos1.getBlockX() + ", Y: " + pos1.getBlockY() + ", Z: " +pos1.getBlockZ() + ")"));
			} else if (action == Action.RIGHT_CLICK_BLOCK) {
				Location pos2 = event.getClickedBlock().getLocation();
				builder.pos2 = pos2;
				BukkitMessageCompiler.sendMessage(ChefCore.getInstance(), player, "cageBuilder.builder.pos2Selected", 
						Placeholder.of("{POS}","(X: " + pos2.getBlockX() + ", Y: " + pos2.getBlockY() + ", Z: " +pos2.getBlockZ() + ")"));
			}
			
		}
	}
}
