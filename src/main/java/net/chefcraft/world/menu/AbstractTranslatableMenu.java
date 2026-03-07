package net.chefcraft.world.menu;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.PluginInstance;
import net.chefcraft.core.configuration.YamlFile;
import net.chefcraft.core.language.Translatable;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.reflection.world.GameReflections;
import net.chefcraft.world.inventory.CoreInventoryType;
import net.chefcraft.world.player.CorePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

public abstract class AbstractTranslatableMenu implements Translatable {
	
	private static final GameReflections GAME_REFLECTIONS = ChefCore.getReflector().getGameReflections();
	
	protected CoreInventoryType inventoryType = null;
	protected List<AbstractTranslatableContent> items = new ArrayList<>();
	protected final String name;
	protected String permission = "";
	protected PluginInstance plugin;
	protected boolean publicMenu;
	protected int rows = 3;
	protected String titleKey = null;
	protected int updateDelay = 0;
	protected final YamlFile yamlFile;
	protected List<String> itemStates = new ArrayList<>();
	
	protected AbstractTranslatableMenu(PluginInstance plugin, String name, YamlFile yamlFile, boolean publicMenu) {
		this.plugin = plugin;
		this.name = name;
		this.yamlFile = yamlFile;
		this.publicMenu =  publicMenu;
	}
	
	public abstract List<String> parseItemStateForPlayer(CorePlayer corePlayer);
	
	public abstract <T> T openMenu(CorePlayer corePlayer, Placeholder titlePlaceholders, Placeholder itemPlaceholders);
	
	public abstract boolean updateMenu(CorePlayer corePlayer, Placeholder titlePlaceholders, Placeholder itemPlaceholders);
	
	public void saveTheConfig() {
		FileConfiguration config = yamlFile.getConfig();
		if (this.permission != null) {
			config.set("permission", permission);
		} else {
			config.set("permission", "");
		}
		config.set("contentsPrefixKey", this.name + ".contents");
		config.set("titleKey", this.titleKey);
		config.set("type", this.inventoryType.name());
		config.set("rows", this.rows);
		config.set("updateDelay", this.updateDelay);
		if (!itemStates.isEmpty()) {
			config.set("itemStates", this.itemStates);
		}
		
		for (AbstractTranslatableContent cont : this.items) {
			ConfigurationSection section = config.createSection("contents." + cont.getRawName());
			if (cont.getItemStack() != null && !GAME_REFLECTIONS.isItemStackEmpty(cont.getItemStack())) {
				ItemStack item = cont.getItemStack();
				section.set("material", item.getType().name());
				section.set("amount", item.getAmount());
				if (item.hasItemMeta()) {
					ItemMeta meta = item.getItemMeta();
					if (meta.hasEnchants()) {
						List<String> enchList = new ArrayList<>();
						for (Entry<Enchantment, Integer> enc : meta.getEnchants().entrySet()) {
							enchList.add(GAME_REFLECTIONS.getEnchantmentVanillaName(enc.getKey()).toLowerCase(Locale.ENGLISH) + ";" + enc.getValue());
						}
						section.set("enchants", enchList);
					}
				}
				section.set("slot", cont.getSlot());
				section.set("nameKey", cont.getNameKey());
				section.set("loreKey", cont.getLoreKey());
			}
		}
		yamlFile.save();
		
	}

	public AbstractTranslatableContent getTranslatableContentBySlot(int slot, CorePlayer corePlayer) {
		for (AbstractTranslatableContent content : items) {
			if (content.slot == slot) {
				for (String state : this.parseItemStateForPlayer(corePlayer)) {
					return content.findItemStateContent(state);
				}
				return content;
			}
		}
		return null;
	}
	
	public void startMenuUpdateTask(CorePlayer corePlayer, Placeholder titlePlaceholders, Placeholder itemPlaceholders) {
		if (updateDelay <= 0) return;
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if (updateDelay <= 0 || (!updateMenu(corePlayer, itemPlaceholders, itemPlaceholders))) {
					this.cancel();
				}
			}
		}.runTaskTimer(ChefCore.getInstance(), 20L, updateDelay);
	}
	
	public CoreInventoryType getInventoryType() {
		return inventoryType;
	}
	
	public String getName() {
		return name;
	}

	public String getPermission() {
		return permission;
	}

	public PluginInstance getPlugin() {
		return plugin;
	}

	public int getRows() {
		return rows;
	}
	
	public String getTitleKey() {
		return titleKey;
	}

	public Collection<AbstractTranslatableContent> getTranslatableContents() {
		return items;
	}

	public int getUpdateDelay() {
		return updateDelay;
	}

	public YamlFile getYamlFile() {
		return yamlFile;
	}

	public boolean isPublicMenu() {
		return publicMenu;
	}

	public void setInventoryType(CoreInventoryType inventoryType) {
		this.inventoryType = inventoryType;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}
	
	public void setPublicMenu(boolean publicMenu) {
		this.publicMenu = publicMenu;
	}
	
	public void setRows(int rows) {
		this.rows = rows;
	}

	public void setTitleKey(String titleKey) {
		this.titleKey = titleKey;
	}

	public void setUpdateDelay(int updateDelay) {
		this.updateDelay = updateDelay;
	}

	public List<String> getItemStates() {
		return itemStates;
	}
}
