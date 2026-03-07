package net.chefcraft.world.menu;

import net.chefcraft.core.PluginInstance;
import net.chefcraft.core.language.Translatable;
import net.chefcraft.core.language.TranslatablePlayer;
import net.chefcraft.core.util.Placeholder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractTranslatableContent implements Translatable {

	protected Map<MenuClickType, List<String>> clickActions = new HashMap<>();
	protected boolean isPlayerHead = false;
	protected ItemStack itemStack = null;
	protected Map<String, AbstractTranslatableContent> itemStates = new HashMap<>();
	protected String loreKey = null;
	protected String nameKey = null;
	protected final PluginInstance plugin;
	protected final String rawName;
	protected int slot = -1;
	protected String contentsPrefixKey = "";
	
	protected AbstractTranslatableContent(PluginInstance plugin, String rawName) {
		this.rawName = rawName;
		this.plugin = plugin;
	}
	
	public abstract ItemStack getTranslatedItem(TranslatablePlayer player, int amount, @Nullable Placeholder placeholders, @Nullable String itemState);
	
	public abstract void onClick(TranslatablePlayer player);
	
	public AbstractTranslatableContent findItemStateContent(String itemState) {
		if (!this.itemStates.isEmpty()) {
			for (Map.Entry<String, AbstractTranslatableContent> entrySet : this.itemStates.entrySet()) {
				if (entrySet.getKey().equalsIgnoreCase(itemState)) {
					return entrySet.getValue();
				}
			}
		}
		return this;
	}
	
	public void addItemState(String stateName, AbstractTranslatableContent stateContent) {
		itemStates.put(stateName, stateContent);
	}

	public void clearAllItemStates() {
		itemStates.clear();
	}

	public Map<MenuClickType, List<String>> getClickActions() {
		return clickActions;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public String getLoreKey() {
		return loreKey;
	}

	public String getNameKey() {
		return nameKey;
	}

	public PluginInstance getPlugin() {
		return plugin;
	}

	public String getRawName() {
		return rawName;
	}

	public int getSlot() {
		return slot;
	}

	public boolean hasItemState(String stateName) {
		return itemStates.containsKey(stateName);
	}
	
	public boolean isPlayerHead() {
		return isPlayerHead;
	}

	public void removeItemState(String stateName) {
		itemStates.remove(stateName);
	}

	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}
	
	public void setLoreKey(String loreKey) {
		this.loreKey = loreKey;
	}
	
	public void setNameKey(String nameKey) {
		this.nameKey = nameKey;
	}
	
	public void setPlayerHead(boolean isPlayerHead) {
		this.isPlayerHead = isPlayerHead;
	}
	
	public void setSlot(int slot) {
		this.slot = slot;
	}

	public String getContentsPrefixKey() {
		return contentsPrefixKey;
	}

	public void setContentsPrefixKey(String contentsPrefixKey) {
		this.contentsPrefixKey = contentsPrefixKey;
	}
	
	@Override
	public String toString() {
		return this.rawName;
	}
}
