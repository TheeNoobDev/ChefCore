package net.chefcraft.world.player;

import net.chefcraft.world.inventory.MultiInventory;
import net.chefcraft.world.menu.AbstractTranslatableMenu;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InventoryData implements InventoryHolder {

	private final CorePlayer owner;
	private Inventory inventory = null;
	private MultiInventory multiInventory = null;
	private AbstractTranslatableMenu translatableMenu = null;
	
	public InventoryData(@Nonnull CorePlayer owner) {
		this.owner = owner;
	}
	
	@Nonnull
	public CorePlayer getOwner() {
		return this.owner;
	}
	
	@Nullable
	@Override
	public Inventory getInventory() {
		return this.inventory;
	}
	
	@Nullable
	public AbstractTranslatableMenu getTranslatableMenu() {
		return this.translatableMenu;
	}
	
	@Nullable
	public MultiInventory getMultiInventory() {
		return multiInventory;
	}
	
	public boolean hasInventory() {
		return this.inventory != null;
	}
	
	public boolean hasTranslatableMenu() {
		return this.translatableMenu != null;
	}
	
	public boolean hasMultiInventory() {
		return this.multiInventory != null;
	}
	
	public boolean hasInventoryWithTranslatableMenu() {
		return this.translatableMenu != null && this.inventory != null;
	}
	
	public boolean hasMultiInventoryWithTranslatableMenu() {
		return this.translatableMenu != null && this.multiInventory != null;
	}
	
	public boolean isActiveInventory(@Nonnull Inventory other) {
		return other.equals(this.inventory);
	}
	
	public boolean isActiveTranslatableMenu(@Nonnull AbstractTranslatableMenu other) {
		return other.equals(this.translatableMenu);
	}
	
	public boolean isActiveMultiInventory(@Nonnull MultiInventory other) {
		return other.equals(this.multiInventory);
	}
	
	public boolean isActiveMultiInventoryPage(Inventory other) {
		return this.multiInventory != null && this.multiInventory.containsInventory(other);
	}
	
	public InventoryData setInventory(Inventory inventory) {
		this.inventory = inventory;
		return this;
	}
	
	public InventoryData setTranslatableMenu(AbstractTranslatableMenu translatableMenu) {
		this.translatableMenu = translatableMenu;
		return this;
	}
	
	public InventoryData setMultiInventory(MultiInventory multiInventory) {
		this.multiInventory = multiInventory;
		return this;
	}
	
	public InventoryData resetInventory() {
		this.inventory = null;
		return this;
	}
	
	public InventoryData resetTranslatableMenu() {
		this.translatableMenu = null;
		return this;
	}
	
	public InventoryData resetMultiInventory() {
		this.multiInventory = null;
		return this;
	}
	
	public void resetAll() {
		this.inventory = null;
		this.translatableMenu = null;
		this.multiInventory = null;
	}
}
