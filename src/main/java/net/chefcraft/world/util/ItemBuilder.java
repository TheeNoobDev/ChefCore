package net.chefcraft.world.util;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.util.Numbers;
import net.chefcraft.reflection.world.CoreMaterial;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @since 0.0.1
 */
public class ItemBuilder {
	
	private static final Material PLAYER_HEAD = CoreMaterial.PLAYER_HEAD.toMaterial();

	private Material material = null;
	private int amount = 1;
	private short durability = -1;
	private MessageHolder name = null;
	private MessageHolder lore = null;
	private boolean enchantmentGlint = false;
	private boolean unbreakable = false;
	private Map<Enchantment, Integer> enchants = null;
	private Set<ItemFlag> itemFlags = null;
	private String skullValue = null;
	
	public ItemStack build() {
		if (this.material == null) throw new NullPointerException("cannot build null material!");
		
		ItemStack item = new ItemStack(this.material);
		item.setAmount(this.amount);
		
		ItemMeta meta = item.getItemMeta();
		
		try {
			if (this.enchantmentGlint) {
				meta.addEnchant(Enchantment.LURE, 1, true);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
			}
			if (this.name != null) {
				MethodProvider.ITEM_META_SET_DISPLAY_NAME.accept(meta, this.name);
			}
			if (this.lore != null) {
				MethodProvider.ITEM_META_SET_LORE.accept(meta, this.lore);
			}
			if (this.unbreakable) {
				ChefCore.getReflector().setItemUnbreakable(meta, true);
			}
			if (this.durability > -1) {
				item.setDurability(this.durability);
			}
			if (this.enchants != null) {
				for (Map.Entry<Enchantment, Integer> entry : this.enchants.entrySet()) {
					meta.addEnchant(entry.getKey(), entry.getValue(), true);
				}
			}
			if (this.itemFlags != null) {
				this.itemFlags.forEach(flag -> meta.addItemFlags(flag));
			}
			
			item.setItemMeta(meta);
			
			if (this.skullValue != null) {
				item.setType(PLAYER_HEAD);
				SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
				
				if (this.skullValue.length() <= 16) {
					skullMeta.setOwner(this.skullValue);
				} else {
					ChefCore.getReflector().applyCustomValueToSkullMeta(skullMeta, this.skullValue);
				}
				
				item.setItemMeta(skullMeta);
			}
		} catch (Exception x) {
			ChefCore.getInstance().sendPlainMessage("&cAn error occured parsing the item meta!", x);
		}
		
		return item;
	}
	
	public ItemBuilder type(@NotNull ItemStack other) {
		this.material = Objects.requireNonNull(other, "cannot build null itemstack!").getType();
		return this;
	}
	
	public ItemBuilder type(@NotNull Material material) {
		this.material = Objects.requireNonNull(material, "cannot build null material!");
		return this;
	}
	
	public ItemBuilder skullValue(@NotNull String skullValue) {
		this.skullValue = Objects.requireNonNull(skullValue, "skullValue cannot be null!");
		return this;
	}

	public ItemBuilder amount(int amount) {
		this.amount = Numbers.checkRange(amount, 1, 64);
		return this;
	}
	
	public ItemBuilder durability(short durability) {
		this.durability = durability;
		return this;
	}

	public ItemBuilder name(@NotNull MessageHolder name) {
		this.name = Objects.requireNonNull(name, "item name cannot be null!");
		return this;
	}

	public ItemBuilder lore(@NotNull MessageHolder lore) {
		this.lore = Objects.requireNonNull(lore, "item lore cannot be null!");;
		return this;
	}

	public ItemBuilder enchantmentGlint(boolean glint) {
		this.enchantmentGlint = glint;
		return this;
	}
	
	public ItemBuilder setUnbreakable(boolean unbreakable) {
		this.unbreakable = unbreakable;
		return this;
	}
	
	public ItemBuilder addEnchant(Enchantment enchantment, int level) {
		Objects.requireNonNull(enchantment, "enchantment cannot be null!");
		if (this.enchants == null) this.enchants = new HashMap<>();
		
		this.enchants.put(enchantment, Numbers.min(level, 0));
		return this;
	}

	public ItemBuilder addItemFlag(ItemFlag itemFlag) {
		Objects.requireNonNull(itemFlag, "itemFlag cannot be null!");
		if (this.itemFlags == null) this.itemFlags = new HashSet<>();
		
		this.itemFlags.add(itemFlag);
		return this;
	}
	
	public ItemBuilder removeEnchant(Enchantment enchantment) {
		Objects.requireNonNull(enchantment, "enchantment cannot be null!");
		if (this.enchants != null) {
			this.enchants.remove(enchantment);
		}
		return this;
	}

	public ItemBuilder removeItemFlag(ItemFlag itemFlag) {
		Objects.requireNonNull(itemFlag, "itemFlag cannot be null!");
		if (this.itemFlags != null) {
			this.itemFlags.remove(itemFlag);
		}
		return this;
	}
	
	public ItemBuilder clear() {
		this.material = null;
		this.skullValue = null;
		this.amount = 1;
		this.durability = -1;
		this.name = null;
		this.lore = null;
		this.enchantmentGlint = false;
		this.unbreakable = false;
		if (this.enchants != null) this.enchants.clear();
		if (this.itemFlags != null) this.itemFlags.clear();
		this.enchants = null;
		this.itemFlags = null;
		return this;
	}
	
	public static ItemBuilder builder() {
		return new ItemBuilder();
	}
}