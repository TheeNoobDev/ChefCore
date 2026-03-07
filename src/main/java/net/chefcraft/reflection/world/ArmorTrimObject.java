package net.chefcraft.reflection.world;

import org.bukkit.inventory.ItemStack;

/**
 * @since 1.2.2
 */
public interface ArmorTrimObject {
	
	ItemStack applyToItem(ItemStack armor);
}
