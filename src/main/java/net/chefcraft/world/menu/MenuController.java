package net.chefcraft.world.menu;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.PluginInstance;
import net.chefcraft.core.configuration.CoreConfigKey;
import net.chefcraft.core.configuration.YamlFile;
import net.chefcraft.core.language.TranslationSource;
import net.chefcraft.reflection.world.CoreMaterial;
import net.chefcraft.reflection.world.GameReflections;
import net.chefcraft.world.inventory.CoreInventoryType;
import net.chefcraft.world.inventory.UtilityMenuItem;
import net.chefcraft.world.player.CorePlayer;
import net.chefcraft.world.translatable.TranslatableItemStack;
import net.chefcraft.world.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class MenuController {
	
	private static final GameReflections GAME_REFLECTIONS = ChefCore.getReflector().getGameReflections();
	private static final Map<String, AbstractTranslatableMenu> menus = new HashMap<>();
	private static final Map<UtilityMenuItem, TranslatableItemStack> MENU_UTILS_BY_NAME = new EnumMap<>(UtilityMenuItem.class);
	
	private static YamlFile menuUtilsYamlFile;
	private static File menusDirectory;
	
	public static Map<String, AbstractTranslatableMenu> getMenuMap() {
		return menus;
	}
	
	public static AbstractTranslatableMenu getMenuByName(String name) {	
		return menus.get(name);
	}
	
	public static TranslatableItemStack getTranslatedMenuItem(TranslationSource source, UtilityMenuItem type) {
		return MENU_UTILS_BY_NAME.get(type).translate(source);
	}
	
	public static boolean isMenuFileExist(String fileName) {
		for (String name : menusDirectory.list()) {
			if (fileName.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	
	public static void reloadMenuConfigs() {
		ChefCore plugin = ChefCore.getInstance();
		
		for (CorePlayer player : ChefCore.getCorePlayers()) {
			player.safelyCloseExistingMenu();
		}
		
		menuUtilsYamlFile = ChefCore.getGlobalConfigHandler().copyResource(CoreConfigKey.MENU_UTILS, "menu_utils.yml");
		
		menus.clear();
		
		loadMenuUtils();
		
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			menusDirectory.mkdirs();
			for (File file : menusDirectory.listFiles()) {
				if (!file.isDirectory() && file.getName().contains(".yml")) {
					try {
						loadMenuFromConfig(plugin, YamlFile.create(file.getAbsolutePath()));
					} catch (IOException e) {
						e.printStackTrace();
						Bukkit.getLogger().log(Level.SEVERE, "Failed to load '" + file.getName() + "' menu!"); 
					}
				}
			}
			ChefCore.getInstance().sendPlainMessage("&aTotal " + menus.size() + " menu" + ((menus.size() > 1) ? "s" : "") + " re-loaded.");
		}, 5L);
	}
	
	public static void loadMenus() {
		ChefCore plugin = ChefCore.getInstance();
		menusDirectory = ChefCore.getGlobalConfigHandler().createFileDirectory(CoreConfigKey.MENUS, "menus");
		menuUtilsYamlFile = ChefCore.getGlobalConfigHandler().copyResource(CoreConfigKey.MENU_UTILS, "menu_utils.yml");
		
		loadMenuUtils();
		
		if (plugin.getConfig().getBoolean("load_example_configurations")) {
			ChefCore.getGlobalConfigHandler().copyResource(CoreConfigKey.MENUS, "menus/lang_menu.yml");
		}
		
		for (File file : menusDirectory.listFiles()) {
			if (!file.isDirectory() && file.getName().contains(".yml")) {
				try {
					loadMenuFromConfig(plugin, YamlFile.create(file.getAbsolutePath()));
				} catch (IOException e) {
					e.printStackTrace();
					Bukkit.getLogger().log(Level.SEVERE, "Failed to load '" + file.getName() + "' menu!"); 
				}
			}
		}
		
		Bukkit.getScheduler().runTaskLater(ChefCore.getInstance(), ()-> {
			ChefCore.getInstance().sendPlainMessage("&aTotal " + menus.size() + " menu" + ((menus.size() > 1) ? "s" : "") + " loaded.");
		}, 40L);
	}
	
	private static void loadMenuUtils() {
		MENU_UTILS_BY_NAME.clear();
		
		FileConfiguration cfg = menuUtilsYamlFile.getConfig();
		
		for (UtilityMenuItem type : UtilityMenuItem.values()) {
			CoreMaterial cm = CoreMaterial.matchByName(cfg.getString("items." + type.getName() + ".type"));
			MENU_UTILS_BY_NAME.put(type, cm.toTranslatableItemStack(type.getTranslationNameKey(), type.getTranslationLoreKey()));
		}
	}
	
	public static AbstractTranslatableMenu readFromInventory(PluginInstance plugin, String name, Inventory inventory) throws IOException {
		YamlFile file = YamlFile.create(menusDirectory.getPath() + File.separator + name + ".yml");
		AbstractTranslatableMenu menu = new CoreTranslatableMenu(name, file);
		menu.setTitleKey("menus." + name + ".title");
		menu.setInventoryType(inventory.getType() == InventoryType.HOPPER ? CoreInventoryType.HOPPER : CoreInventoryType.CHEST);
		menu.setRows((int) inventory.getSize() / 9);
		
		String contentsKey = "menus." + name + ".contents";
		
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack item = inventory.getItem(i);
			if (item != null && !GAME_REFLECTIONS.isItemStackEmpty(item)) {
				AbstractTranslatableContent content = new CoreTranslatableContent(plugin, item.getType().name().toLowerCase() + i);
				content.setContentsPrefixKey(contentsKey);
				content.setNameKey("<KEY>.");
				content.setLoreKey("<KEY>.");
				content.setItemStack(new ItemStack(item));
				content.setSlot(i);
				menu.getTranslatableContents().add(content);
			}
		}
		
		menu.saveTheConfig();
		
		return menu;
	}
	
	public static AbstractTranslatableMenu loadMenuFromConfig(PluginInstance plugin, YamlFile yaml) {
		FileConfiguration config = yaml.getConfig();
		AbstractTranslatableMenu menu = new CoreTranslatableMenu(yaml.getName().replace(".yml", ""), yaml);
		menu.setPermission(config.isSet("permission") ? config.getString("permission") : "");
		menu.setTitleKey(config.getString("titleKey"));
		menu.setInventoryType(CoreInventoryType.valueOf(config.getString("type").toUpperCase()));
		menu.setRows(config.getInt("rows"));
		menu.setUpdateDelay(config.getInt("updateDelay"));
		if (config.isSet("itemStates")) {
			for (String itemState : config.getStringList("itemStates")) {
				menu.getItemStates().add(itemState);
			}
		}
		for (String itemNode : config.getConfigurationSection("contents").getKeys(false)) {
			ConfigurationSection section = config.getConfigurationSection("contents."+itemNode);
			menu.getTranslatableContents().add(loadTranslatableContent(plugin, section, config.getString("contentsPrefixKey")));
		}
		menus.put(menu.getName(), menu);
		return menu;
	}
	
	public static AbstractTranslatableContent loadTranslatableContent(PluginInstance plugin, ConfigurationSection section, String contentsPrefix) {
		AbstractTranslatableContent content = new CoreTranslatableContent(plugin, section.getName());
		content.setContentsPrefixKey(contentsPrefix);
		content.setNameKey(section.getString("nameKey"));
		content.setLoreKey(section.getString("loreKey"));
		content.setSlot(section.getInt("slot"));
		
		content.setItemStack(ItemUtils.deserializeItemStackFromConfigurationSection(ChefCore.getInstance(), section));

		if (section.isSet("clickActions")) {
			ConfigurationSection actionSection = section.getConfigurationSection("clickActions");
			if(actionSection.isSet("right")) {
				content.getClickActions().put(MenuClickType.RIGHT, actionSection.getStringList("right"));
			}
			if(actionSection.isSet("left")) {
				content.getClickActions().put(MenuClickType.LEFT, actionSection.getStringList("left"));
			}
			if(actionSection.isSet("middle")) {
				content.getClickActions().put(MenuClickType.MIDDLE, actionSection.getStringList("middle"));
			}
			if(actionSection.isSet("all")) {
				content.getClickActions().put(MenuClickType.ALL, actionSection.getStringList("all"));
			}
		}
		
		if (section.isSet("itemStates")) {
			ConfigurationSection stateSection = section.getConfigurationSection("itemStates");
			for (String stateNode : stateSection.getKeys(false)) {
				loadStatedTranslatableContent(stateSection.getConfigurationSection(stateNode), content);
			}
		}
		return content;
	}
	
	public static void loadStatedTranslatableContent(ConfigurationSection section, AbstractTranslatableContent mainContent) {
		AbstractTranslatableContent content = new CoreTranslatableContent(mainContent.plugin, mainContent.getRawName());
		content.setContentsPrefixKey(mainContent.getContentsPrefixKey());
		content.setSlot(mainContent.getSlot());
		
		
		if (section.isSet("nameKey")) {
			content.setNameKey(section.getString("nameKey"));
		} else { content.setNameKey(mainContent.getNameKey()); }
		if (section.isSet("loreKey")) {
			content.setLoreKey(section.getString("loreKey"));
		} else { content.setLoreKey(mainContent.getLoreKey()); }
		
		
		if (section.isSet("material")) {
			section.set("material", CoreMaterial.toCore(mainContent.getItemStack().getType()).name());
		}
		
		content.setItemStack(ItemUtils.deserializeItemStackFromConfigurationSection(ChefCore.getInstance(), section));

		if (section.isSet("clickActions")) {
			ConfigurationSection actionSection = section.getConfigurationSection("clickActions");
			if (actionSection.isSet("right")) {
				content.getClickActions().put(MenuClickType.RIGHT, actionSection.getStringList("right"));
			}
			if (actionSection.isSet("left")) {
				content.getClickActions().put(MenuClickType.LEFT, actionSection.getStringList("left"));
			}
			if (actionSection.isSet("middle")) {
				content.getClickActions().put(MenuClickType.MIDDLE, actionSection.getStringList("middle"));
			}
			if (actionSection.isSet("all")) {
				content.getClickActions().put(MenuClickType.ALL, actionSection.getStringList("all"));
			}
		}
		mainContent.addItemState(section.getName(), content);
	}
}
