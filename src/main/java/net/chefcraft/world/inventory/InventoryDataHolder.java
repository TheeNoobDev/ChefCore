package net.chefcraft.world.inventory;

import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.world.util.MethodProvider;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public final class InventoryDataHolder<T> implements InventoryHolder {
	
	static final InventoryDataHolder<?> EMPTY = new InventoryDataHolder<>(null, null);
	
	@Nullable
	protected Inventory inventory;
	
	@Nonnull
	private final T data;
	
	private InventoryDataHolder(@Nullable Inventory inventory, @Nonnull T data) {
		this.inventory = inventory;
		this.data = data;
	}
	
	@Override
	@Nullable
	public Inventory getInventory() {
		return this.inventory;
	}
	
	@Nonnull
	public T getData() {
		return this.data;
	}
	
	public boolean isEmpty() {
		return this.data == null;
	}
	
	public boolean isPresent() {
		return this.data != null;
	}
	
	public boolean isPresentFor(@Nonnull Inventory other) {
		return other.getHolder() instanceof InventoryDataHolder && ((InventoryDataHolder<?>) other.getHolder()).data.equals(this.data);
	}
	
	@Nonnull
	public static InventoryDataHolder<?> empty() {
		return EMPTY;
	}
	
	@Nonnull
	public static <T> InventoryDataHolder<T> of(@Nonnull T data) {
		return new InventoryDataHolder<>(null, Objects.requireNonNull(data, "data cannot be null!"));
	}
	
	@Nonnull
	public static <T> InventoryDataHolder<T> of(@Nullable Inventory inventory, @Nonnull T data) {
		return new InventoryDataHolder<>(inventory, Objects.requireNonNull(data, "data cannot be null!"));
	}
	
	@Nonnull
	public static <T> InventoryDataHolder<?> getPresentInventoryDataFromInventory(@Nonnull Inventory other, @Nonnull T data) {
		return (other.getHolder() instanceof InventoryDataHolder && ((InventoryDataHolder<?>) other.getHolder()).data.equals(data)) ? (InventoryDataHolder<?>) other.getHolder() : EMPTY;
	}
	
	@Nonnull
	public static <T> Inventory createInventory(@Nonnull InventoryDataHolder<T> dataHolder, int size, @Nonnull MessageHolder title) {
		Inventory inv = MethodProvider.CREATE_INVENTORY.apply(dataHolder, size, title);
		return inv;
	}
	
	@Nonnull
	public static <T> Inventory createInventory(@Nonnull InventoryDataHolder<T> dataHolder, @Nonnull InventoryType type, @Nonnull MessageHolder title) {
		Inventory inv = MethodProvider.CREATE_INVENTORY_BY_TYPE.apply(dataHolder, type, title);
		return inv;
	}
	
	/**
     * This function calculates the inventory size
     * according to the given range example:
     * 
     * <pre>
	 * range = 5 -> returns 9 (1 row)
	 * range = 20 -> returns 27 (3 rows)
	 * range = 40 -> returns 45 (5 rows)
	 * range = -1 -> returns 9 (1 row)
	 * range = 100 -> returns 54 (6 rows)
	 * </pre>
	 * 
     * so the number always returns 9 for negative numbers
     * 54 and always returns 54 for numbers greater than 54,
     * the reason is that the minimum chest inventory size of 
     * Minecraft is 9 and the maximum is 54
     * 
     * @param range range
     * @return Multiples of 9 (min: 9, max: 54)
     */
    public static int parseInventorySizeFromRange(int range) {
		if (range <= 0) return 9;
		
		int r = (Math.round(range / 9) + (range % 9 == 0 ? 0 : 1)) * 9;
		
		return r >= 54 ? 54 : r;
	}
}