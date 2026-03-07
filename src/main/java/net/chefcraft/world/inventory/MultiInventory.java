package net.chefcraft.world.inventory;

import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslatablePlayer;
import net.chefcraft.core.language.TranslationSource;
import net.chefcraft.world.menu.MenuController;
import net.chefcraft.world.util.MethodProvider;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiInventory {
	
	private final Map<String, Object> metadataMap = new HashMap<>();
	private final Map<Integer, Runnable> clickActionsBySlot = new HashMap<>();
	private final List<Inventory> pages = new ArrayList<>();
	private final MultiInventorySize size;
	private final MessageHolder title;
	private final MessageHolder pageFormat;
	private final ItemStack pagePrevious;
	private final ItemStack pageClose;
	private final ItemStack pageNext;
	private final int virtualPageSize;
	private final int realPageSize;
	private Runnable globalClickAction = null;
	
	public MultiInventory(MultiInventorySize size, MessageHolder title, MessageHolder pageFormat, ItemStack pagePrevious, ItemStack pageClose, ItemStack pageNext) {
		this.size = size;
		this.realPageSize = size.size;
		this.virtualPageSize = this.realPageSize - 9; 
		this.title = title;
		this.pageFormat = pageFormat;
		this.pagePrevious = pagePrevious;
		this.pageClose = pageClose;
		this.pageNext = pageNext;
		this.clear();
	}
	
	public boolean hasMetadata(String key) {
		return this.metadataMap.containsKey(key);
	}

	@Nullable
	public Object getMetadata(String key) {
		return this.metadataMap.get(key);
	}
	
	public void setMetadata(String key, Object o) {
		this.metadataMap.put(key, o);
	}
	
	public void removeMetadata(String key) {
		this.metadataMap.remove(key);
	}
	
	public boolean hasClickAction(int slot) {
		return this.clickActionsBySlot.containsKey(slot);
	}
	
	@Nullable
	public Runnable getClickAction(int slot) {
		return this.clickActionsBySlot.get(slot);
	}
	
	public void setClickAction(int slot, Runnable runnable) {
		this.clickActionsBySlot.put(slot, runnable);
	}
	
	public void removeClickAction(int slot) {
		this.clickActionsBySlot.remove(slot);
	}
	
	public boolean hasGlobalClickAction() {
		return this.globalClickAction != null;
	}

	public Runnable getGlobalClickAction() {
		return globalClickAction;
	}

	public void setGlobalClickAction(Runnable globalClickAction) {
		this.globalClickAction = globalClickAction;
	}
	
	public void clearSlots() {
		for (int i = 0; i < this.pages.size(); i++) {
			Inventory inv = this.pages.get(i);
			inv.clear();
		}
		this.applyPageUtils();
	}
	
	public void clear() {
		this.pages.clear();
		this.pages.add(MethodProvider.CREATE_INVENTORY.apply(null, this.realPageSize, this.title));
		this.applyPageUtils();
	}
	
	public boolean containsInventory(Inventory inv) {
		return this.pages.contains(inv);
	}
	
	public int getPageByInventory(Inventory inv) {
		int i = this.pages.indexOf(inv);
		return i != -1 ? i + 1 : -1;
	}
	
	public int getClickedRawSlotForEvent(Inventory inv, int eventRawSlot) {
		int i = this.pages.indexOf(inv);
		return i == -1 ? -1 : (i * (inv.getSize() - 9)) + eventRawSlot;
	}
	
	public MultiInventory setItem(int slot, ItemStack item) {
		int i = 0;
		int j = this.virtualPageSize - 1;
		int k = slot;
		
		while (k > j) {  
			k -= this.virtualPageSize;
			i++;
			if (i == this.pages.size()) {
				this.pages.add(MethodProvider.CREATE_INVENTORY.apply(null, 
						this.realPageSize, 
						MessageHolder.merge(MessageHolder.merge(this.title, this.pageFormat), MessageHolder.text(String.valueOf(i + 1)))));
				this.applyPageUtils();
			}
		}
		
		Inventory inv = pages.get(i);
		
		inv.setItem(k, item);
		return this;
	}
	
	public MultiInventory setItemToAllPages(int slot, ItemStack item) {
		for (int i = 0; i < this.pages.size(); i++) {
			Inventory inv = this.pages.get(i);
			inv.setItem(slot, item);
			
		}
		return this;
	}

	public MultiInventory applyPageUtils() {
		int p = pages.size();
		
		if (p == 1) {
			this.applyPageUtilOptions(this.pages.get(0), 0);
		} else if (p == 2) {
			this.applyPageUtilOptions(this.pages.get(0), 1);
			this.applyPageUtilOptions(this.pages.get(1), 2);
			
		} else {
			this.applyPageUtilOptions(this.pages.get(0), 1);
			
			int j = p - 1;
			
			for (int i = 1; i < j; i++) {
				Inventory inv = this.pages.get(i);
				this.applyPageUtilOptions(inv, 3);
			}
			
			this.applyPageUtilOptions(this.pages.get(j), 2);
		}
		return this;
	}
	
	/**
	 * @param inventory an inventory
	 * @param option 
	 * (0 -> close)
	 * (1 -> close, next)
	 * (2 -> close, previous)
	 * (3 -> close, next, previous)
	 */
	public void applyPageUtilOptions(Inventory inventory, int option) {
		inventory.setItem(this.realPageSize - 5, this.pageClose.clone()); // close
		
		if ((option & 1) == 1) {
			inventory.setItem(this.realPageSize - 1, this.pageNext.clone()); // next
		}
		
		if ((option & 2) == 2) {
			inventory.setItem(this.realPageSize - 9, this.pagePrevious.clone()); // previous
		}
	}
	
	public PageUtilResult handleMenuClickForPageUtils(InventoryClickEvent inventoryClickEvent, TranslatablePlayer playerObject, boolean playClickSound, boolean playCloseSound) {
		return handleMenuClickForPageUtils(inventoryClickEvent.getInventory(), inventoryClickEvent.getRawSlot(), playerObject, playClickSound, playCloseSound);
	}
	
	public PageUtilResult handleMenuClickForPageUtils(Inventory inventory, int rawSlot, TranslatablePlayer playerObject, boolean playClickSound, boolean playCloseSound) {
		int index = this.pages.indexOf(inventory);
		if (index != -1) {
			int i = inventory.getSize();
			
			if ((i - 9) == rawSlot) {
				this.openPage(index, playerObject);
				
				if (playClickSound) {
					playerObject.playSound("click");
				}
				
				return PageUtilResult.PREVIOUS_PAGE;
			} else if ((i - 5) == rawSlot) {
				playerObject.safelyCloseExistingMenu();
				
				if (playCloseSound) {
					playerObject.playSound("close");
				}
				
				return PageUtilResult.CLOSED;
			} else if ((i - 1) == rawSlot) {
				this.openPage(index + 2, playerObject);
				
				if (playClickSound) {
					playerObject.playSound("click");
				}
				
				return PageUtilResult.NEXT_PAGE;
			}
		}
		return PageUtilResult.NOTHING;
	}
	
	@Nullable
	public Inventory openPage(int page, TranslatablePlayer playerObject) {
		int i = page - 1;
		if (i < this.pages.size()) {
			Inventory inv = this.pages.get(i);
			playerObject.getPlayer().openInventory(inv);
			return inv;
		}
		return null;
	}
	
	public Inventory openFirstPage(TranslatablePlayer playerObject) {
		return openPage(1, playerObject);
	}
	
	public MultiInventorySize getSize() {
		return this.size;
	}
	
	public int getPageCount() {
		return this.pages.size();
	}
	
	public int getVirtualPageSize() {
		return this.virtualPageSize;
	}
	
	public int getRealPageSize() {
		return this.realPageSize;
	}
	
	public ItemStack getPagePrevious() {
		return pagePrevious;
	}

	public ItemStack getPageClose() {
		return pageClose;
	}

	public ItemStack getPageNext() {
		return pageNext;
	}
	
	public static MultiInventory createWithDefaultUtilityItems(TranslationSource source, MultiInventorySize size, String titleNode) {
		return new MultiInventory(size, source.getMessage(titleNode), source.getMessage("menuUtils.pageFormat"), 
				MenuController.getTranslatedMenuItem(source, UtilityMenuItem.PREVIOUS).asBukkit(),
				MenuController.getTranslatedMenuItem(source, UtilityMenuItem.CLOSE).asBukkit(),
				MenuController.getTranslatedMenuItem(source, UtilityMenuItem.NEXT).asBukkit());
	}
}
