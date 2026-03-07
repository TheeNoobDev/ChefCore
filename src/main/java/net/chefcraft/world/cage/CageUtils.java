package net.chefcraft.world.cage;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.configuration.CoreConfigKey;
import net.chefcraft.core.configuration.GlobalConfigHandler;
import net.chefcraft.core.configuration.YamlFile;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.world.inventory.MultiInventory;
import net.chefcraft.world.inventory.MultiInventorySize;
import net.chefcraft.world.player.CorePlayer;
import net.chefcraft.world.rarity.CoreRarity;
import net.chefcraft.world.rarity.Evaluable;
import net.chefcraft.world.translatable.TranslatableItemStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class CageUtils {

	private static File cagesDirectory;
	private static Map<String, Cage> cageMap = new HashMap<>();
	
	public static void loadCages() {
		GlobalConfigHandler cfgHandler = ChefCore.getGlobalConfigHandler();
		
		cagesDirectory = cfgHandler.createFileDirectory(CoreConfigKey.CAGES, "cages");
		boolean loadExamples = ChefCore.getInstance().getConfig().getBoolean("load_example_configurations");
		
		if (loadExamples) {
			Cage.loadFromConfig(cfgHandler.copyResource(CoreConfigKey.CAGES, "cages/default.yml")); 
			Cage.loadFromConfig(cfgHandler.copyResource(CoreConfigKey.CAGES, "cages/heaven.yml")); 
		}
		
		for (String name : cagesDirectory.list()) {
			if (name.contains(".yml") && ((!name.contains("default") && !name.contains("heaven")) || !loadExamples)) {
				try {
					Cage.loadFromConfig(YamlFile.create(cagesDirectory.getPath() + File.separator + name));
				} catch (IOException e) {
					e.printStackTrace();
					Bukkit.getLogger().log(Level.SEVERE, "Failed to load '" + name + "' cage!"); 
				}
			}
		}
	}
	
	public static void registerCage(Cage cage) {
		cageMap.put(cage.getNamespaceID(), cage);
	}
	
	@Nullable
	public static Cage getCageByNamespaceID(String id) {
		return cageMap.get(id);
	}
	
	public static Cage getDefaultCage() {
		return cageMap.get("default");
	}
	
	public static File getCagesDirectory() {
		return cagesDirectory;
	}
	
	public static void openCagesMenu(CorePlayer corePlayer) {
		List<? extends Evaluable> list = CoreRarity.getSortedListFromMap(cageMap);
		int j = list.size();
		
		MultiInventory multiInv = MultiInventory.createWithDefaultUtilityItems(corePlayer, MultiInventorySize.MEDIUM, "cages.menu.title");
		Player player = corePlayer.getPlayer();
		
		Cage current = corePlayer.getPlaceableCage().getCage();
		
		for (int i = 0; i < j; i++) {
			Cage cage = (Cage) list.get(i);
			
			boolean flag = current.equals(cage);
			
			final Placeholder holder = Placeholder.of("{NAME}", cage.getTranslatedName(corePlayer))
					.add("{RARITY}", cage.getRarity().getTranslatedName(corePlayer, true))
					.add("{RARITY_COLOR}", cage.getRarity().getTextColor());
			
			
			if (cage.getPermission().isEmpty() || player.hasPermission(cage.getPermission()) || flag) {
				
				TranslatableItemStack stack = TranslatableItemStack.from(cage.getMenuItem(), null, null);
				
				stack.updateDisplayName(corePlayer.getMessage("cages.menu." + (flag ? "selected" : "unlocked") + ".name", holder));
				stack.updateLore(MessageHolder.merge(corePlayer.getMessage("cages.menu." + (flag ? "selected" : "unlocked") + ".lore", holder),
						MessageHolder.text(cage.getTranslatedLore(corePlayer)), "{CAGE_LORE}"));
				
				multiInv.setItem(i, stack.asBukkit());
				
			} else {
				
				TranslatableItemStack stack = TranslatableItemStack.from(cage.getMenuItem(), null, null);
				
				stack.updateDisplayName(corePlayer.getMessage("cages.menu.locked.lore", holder));
				stack.updateLore(MessageHolder.merge(corePlayer.getMessage("cages.menu.locked.lore", holder),
						MessageHolder.text(cage.getTranslatedLore(corePlayer)), "{CAGE_LORE}"));
				
				multiInv.setItem(i, stack.asBukkit());
			}
			
			multiInv.setClickAction(i, () -> {
				final Cage rCage = cage;
				
				if (corePlayer.getPlaceableCage().getCage().equals(rCage)) {
					corePlayer.sendMessage("cages.menu.alreadySelected");
					corePlayer.playSound("error");
				} else if (rCage.getPermission().isEmpty() || corePlayer.getPlayer().hasPermission(rCage.getPermission())) {
					corePlayer.getPlaceableCage().setCage(rCage);
					corePlayer.sendMessage("cages.menu.youSelected", holder);
					corePlayer.playSound("successful");
					corePlayer.safelyCloseExistingMenu();
				} else {
					corePlayer.sendMessage("cages.menu.cantSelect");
					corePlayer.playSound("error");
				}
			});
		}
		
		corePlayer.getInventoryData().setMultiInventory(multiInv);
		multiInv.openFirstPage(corePlayer);
	}
}
