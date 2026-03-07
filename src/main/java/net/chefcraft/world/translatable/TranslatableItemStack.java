package net.chefcraft.world.translatable;

import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.Translatable;
import net.chefcraft.core.language.TranslationSource;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.world.util.MethodProvider;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * This class adds translation support by server.
 * @see {@link Translatable}
 * @since 1.2.2
 */
public class TranslatableItemStack implements Translatable {

	private final ItemStack source;
	
	/* Simple data storage way without NMS */
	protected Map<String, Object> customDataMap = null;
	
	protected @Nullable String nameKey = null;
	protected @Nullable String loreKey = null;
	protected boolean glint = false;
	
	/**
	 * Constructor
	 * @param type item material
     * @param amount stack size
	 * @param nameKey Custom Display Name key of the {@link ItemStack}
	 * @param loreKey Custom Lore key of the {@link ItemStack}
	 */
	protected TranslatableItemStack(final Material type, final int amount, @Nullable final String nameKey, @Nullable final String loreKey) {
		this.source = new ItemStack(type, amount);
		this.construct(nameKey, loreKey);
	}
	
	/**
	 * Constructor
	 * @param stack the {@link ItemStack} to copy
	 * @param nameKey Custom Display Name key of the {@link ItemStack}
	 * @param loreKey Custom Lore key of the {@link ItemStack}
	 */
	protected TranslatableItemStack(final ItemStack stack, @Nullable final String nameKey, @Nullable final String loreKey) {
		this.source = stack.clone();
		this.construct(nameKey, loreKey);
	}
	
	/**
	 * A constructor method for repeated codes.
	 * @param nameKey Custom Display Name key of the {@link ItemStack}
	 * @param loreKey Custom Lore key of the {@link ItemStack}
	 */
	protected void construct(@Nullable final String nameKey, @Nullable final String loreKey) {
		this.nameKey = nameKey;
		this.loreKey = loreKey;
	}

	/**
	 * Custom Display Name key of the {@link ItemStack}
	 * @return translation node
	 */
	@Nonnull
	public String getNameKey() {
		return this.nameKey;
	}

	/**
	 * Custom Lore key of the {@link ItemStack}
	 * @return translation node
	 */
	@Nonnull
	public String getLoreKey() {
		return this.loreKey;
	}

	/**
	 * If <b>true</b>, the item appears enchanted in appearance
	 */
	public boolean hasEnchantmentGlint() {
		return this.glint;
	}

	/**
	 * If you make <b>true</b>, the item appears enchanted in appearance
	 * @param enchant boolean value
	 */
	public TranslatableItemStack setEnchantmentGlint(boolean glint) {
		if (this.glint == glint || this.source.getItemMeta().hasEnchants()) return this;
		this.glint = glint;
		
		return this.updateItemMeta(glint ? (meta -> {
				meta.addEnchant(Enchantment.LURE, 1, true);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			}) : (meta -> {
				meta.removeEnchant(Enchantment.LURE);
				meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
			})); 
	}
	
	@Nonnull
	public TranslatableItemStack addExtraLore(@Nonnull MessageHolder holder, boolean addFromFirstLine) {
		return this.updateItemMeta(meta -> {
			MethodProvider.ITEM_META_ADD_EXTRA_LORE.accept(meta, holder, addFromFirstLine);
		});
	}
	
	@Nonnull
	public TranslatableItemStack updateDisplayName(@Nonnull MessageHolder holder) {
		return this.updateItemMeta(meta -> {
			MethodProvider.ITEM_META_SET_DISPLAY_NAME.accept(meta, holder);
		});
	}
	
	@Nonnull
	public TranslatableItemStack updateLore(@Nonnull MessageHolder holder) {
		return this.updateItemMeta(meta -> {
			MethodProvider.ITEM_META_SET_LORE.accept(meta, holder);
		});
	}
	
	@Nonnull
	public ItemStack asBukkit() {
		return this.source; 
	}
	
	public void setAmount(int amount) {
		this.source.setAmount(amount);
	}
	
	public int getAmount() {
		return this.source.getAmount();
	}

	/**
	 * Changes the name or lore of the item on the server side.
	 * @param source {@link TranslationSource} object
	 * @param placeholder A {@link Placeholder}
	 * @return Gives you a translated clone of this item
	 */
	@Nonnull
	public TranslatableItemStack translate(@Nonnull TranslationSource source, @Nullable Placeholder placeholder) {
		return this.clone().localTranslation(source, placeholder);
	}
	
	/**
	 * Changes the name or lore of the item on the server side.
	 * @param source {@link TranslationSource} object
	 * @return Gives you a translated clone of this item
	 */
	@Nonnull
	public TranslatableItemStack translate(@Nonnull TranslationSource source) {
		return this.clone().localTranslation(source, null);
	}
	
	/**
	 * Changes the name or lore of the item on the server side.
	 * @param source {@link TranslationSource} object
	 * @param placeholder A {@link Placeholder}
	 * @return Translates this item directly and returns to this item
	 */
	@Nonnull
	public TranslatableItemStack localTranslation(@Nonnull TranslationSource source, @Nullable Placeholder placeholder) {
		if (this.nameKey != null && this.loreKey != null) {
			return this.updateItemMeta(meta -> { 
				MethodProvider.ITEM_META_SET_DISPLAY_NAME.accept(meta, source.getMessage(this.nameKey, placeholder));
				MethodProvider.ITEM_META_SET_LORE.accept(meta, source.getMessage(this.loreKey, placeholder));
			});
		} else if (this.nameKey != null) {
			return this.updateItemMeta(meta -> { 
				MethodProvider.ITEM_META_SET_DISPLAY_NAME.accept(meta, source.getMessage(this.nameKey, placeholder));
			});
		} else if (this.loreKey != null) {
			return this.updateItemMeta(meta -> { 
				MethodProvider.ITEM_META_SET_LORE.accept(meta, source.getMessage(this.loreKey, placeholder));
			});
		}
		
		return this;
	}
	
	/**
	 * Changes the name or lore of the item on the server side.
	 * @param source {@link TranslationSource} object
	 * @return Translates this item directly and returns to this item
	 */
	@Nonnull
	public TranslatableItemStack localTranslation(@Nonnull TranslationSource source) {
		return this.localTranslation(source, null);
	}
	
	/**
	 * @param action item meta consumer
	 */
	public TranslatableItemStack updateItemMeta(@Nonnull Consumer<ItemMeta> action) {
		ItemMeta meta = this.source.getItemMeta();
		action.accept(meta);
		this.source.setItemMeta(meta);
		return this;
	}
	
	/**
	 * @param key key
	 * @return if custom data map contains "key" returns true
	 */
	public boolean hasCustomData(@Nonnull String key) {
		Objects.requireNonNull(key, "custom data key cannot be null!");
		if (this.customDataMap == null) return false;
		return this.customDataMap.containsKey(key);
	}
	
	/**
	 * @param key key
	 * @return If key ​​matches the value, it returns that value, otherwise it returns null.
	 */
	@Nullable
	public Object getCustomData(@Nonnull String key) {
		Objects.requireNonNull(key, "custom data key cannot be null!");
		if (this.customDataMap == null) return null;
		return this.customDataMap.get(key);
	}
	
	/**
	 * Storages custom data in a map without NMS, like bukkit's metadata value system
	 * @param key key
	 * @param value value
	 * @return this TranslatableItemStack
	 */
	public TranslatableItemStack storeCustomData(@Nonnull String key, @Nullable Object value) {
		Objects.requireNonNull(key, "custom data key cannot be null!");
		if (this.customDataMap == null) {
			this.customDataMap = new HashMap<>();
		}
		
		this.customDataMap.put(key, value);
		return this;
	}
	
	/**
	 * @param key key
	 * @returns true if the key exists in the custom data map and was removed.
	 */
	public boolean removeCustomData(@Nonnull String key) {
		Objects.requireNonNull(key, "custom data key cannot be null!");
		if (this.customDataMap == null) return false;
		
		boolean removed = this.customDataMap.remove(key) != null;
		
		if (removed && this.customDataMap.isEmpty()) {
			this.customDataMap = null;
		}
		
		return removed;
	}
	
	/**
	 * Copies with new name key and lore key
	 * @param nameKey Custom Display Name key of the {@link ItemStack} 
	 * @param loreKey Custom Lore key of the {@link ItemStack}
	 */
	@Nonnull
	public TranslatableItemStack copyWith(@Nullable String nameKey, @Nullable String loreKey) {
		TranslatableItemStack clone = new TranslatableItemStack(this.source, nameKey, loreKey) {};
		clone.glint = this.glint;
		
		if (this.customDataMap != null && this.customDataMap.size() != 0) {
			clone.customDataMap = new HashMap<>();
			clone.customDataMap.putAll(this.customDataMap);
		}
		return clone;
	}
	
	/** Copies without custom data */
	@Nonnull
	public TranslatableItemStack copyWithoutCustomData() {
		TranslatableItemStack clone = new TranslatableItemStack(this.source, this.nameKey, this.loreKey) {};
		clone.glint = this.glint;
		return clone;
	}
	
	@Nonnull
	@Override
	public TranslatableItemStack clone() {
		return this.copyWith(this.nameKey, this.loreKey);
	}
	
	/**
	 * Static constructor
	 * @param type item material
     * @param amount stack size
	 * @param nameKey Custom Display Name key of the {@link ItemStack}
	 * @param loreKey Custom Lore key of the {@link ItemStack}
	 */
	@Nonnull
	public static TranslatableItemStack of(@Nonnull final Material type, final int amount, @Nullable final String nameKey, @Nullable final String loreKey) {
		return new TranslatableItemStack(type, amount, nameKey, loreKey) {};
	}
	
	/**
	 * Static constructor
	 * @param type item material
	 * @param nameKey Custom Display Name key of the {@link ItemStack}
	 * @param loreKey Custom Lore key of the {@link ItemStack}
	 */
	@Nonnull
	public static TranslatableItemStack of(@Nonnull final Material type, @Nullable final String nameKey, @Nullable final String loreKey) {
		return new TranslatableItemStack(type, 1, nameKey, loreKey);
	}
	
	/**
	 * Static constructor
	 * @param stack the {@link ItemStack} to copy
	 * @param nameKey Custom Display Name key of the {@link ItemStack}
	 * @param loreKey Custom Lore key of the {@link ItemStack}
	 */
	@Nonnull
	public static TranslatableItemStack from(@Nonnull final ItemStack stack, @Nullable final String nameKey, @Nullable final String loreKey) {
		return new TranslatableItemStack(stack, nameKey, loreKey);
	}
}
