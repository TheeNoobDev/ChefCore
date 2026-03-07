package net.chefcraft.reflection.world;

import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.world.item.ItemColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @since 1.1.2
 */
public interface GameReflections {
	
	PotionMeta addCustomPotionEffect(PotionMeta meta, PotionEffectType type, int duration, int amplifier, boolean ambient, boolean particles, Color color);
	
	PotionEffect createPotionEffect(PotionEffectType type, int duration, int amplifier, boolean ambient, boolean particles, Color color);
	
	boolean hasItemStackTag(ItemStack itemStack, String key);

	<T> T getItemStackTagValue(Class<?> dataType, ItemStack itemStack, String key);
	
	<T> ItemStack putItemStackTag(Class<?> dataType, ItemStack itemStack, String key, T value);
	
	String getItemTranslationKey(ItemStack itemStack);
	
	Item setUnlimitedLifetime(Item item);
	
	float getAbsorptionAmount(Player player);
	
	void damageOutOfWorld(Player player, float damage);
	
	boolean isItemColorable(Material type);
	
	ItemStack colorizeItemStack(ItemStack item, ItemColor type, Color color);
	
	Material colorizeMaterial(Material material, ItemColor type);

	Sound getBlockSound(Block block, BlockSoundType type);
	
	void playSoundFromBlock(Block block, BlockSoundType type);
	
	void setNoClip(Entity entity, boolean noClip);
	
	void renderMapView(Player player, MapView view, boolean async);
	
	ItemFrame createItemFrame(Location location, BlockFace facing, boolean glowing);
	
    Block setBlockType(Block block, Material material, @Nullable int typeId);
	
	TNTPrimed spawnPrimedTNT(JavaPlugin plugin, Player source, Location location);
	
	boolean placeBlockAsPlayer(Player player, Location location, BlockFace blockFace, ItemStack itemStack, boolean mainHand);
	
	ArmorTrimObject createArmorTrimObject(String trimPattern, String trimMaterial);
	
	/**
	 * @returns if block is directional returns block's face else returns {@link BlockFace.SELF}
	 */
	BlockFace getBlockFaceFromLocation(@Nonnull Location location);
	
	boolean isArmor(Material material);
	
	boolean potionHasPositiveEffects(PotionEffectType type);
	
	int getArmorSlot(Material material);
	
	boolean isBoots(Material material);
	
	boolean isLeatherArmor(Material material);
	
	void kickPlayer(Player player, String reason);
	
	void setItemToFrame(ItemFrame itemFrame, ItemStack itemStack);
	
	MapView getMapView(int mapId);
	
	int getMapViewId(MapView view);
	
	ItemStack applyMapMetaToItemStack(MapView mapView, ItemStack itemStack);
	
	boolean isItemStackEmpty(ItemStack item);
	
	boolean hasWorldImmediateRespawn(World world);
	
	void setPlayerRespawnLocation(Player player, Location location);
	
	void sendComponents(Player player, BaseComponent... components);
	
	void playOpenOrCloseAnimation(Block block, boolean open);
	
	boolean potionHasPositiveEffects(ThrownPotion thrownPotion);
	
	void updateOpenedInventoryTitle(Player player, MessageHolder title);
	
	boolean isInteractableIngame(Material material);
	
	boolean isInteractable(Material material);
	
	void onItemConsumeEventReplaceLeftoversToAir(PlayerItemConsumeEvent playerItemConsumeEvent);
	
	void onProjectileHitSetCancelledEvent(ProjectileHitEvent rojectileHitEvent);
	
	Enchantment getEnchantmentByVanillaName(String vanillaName);
	
	PotionEffectType getPotionEffectTypeByVanillaName(String vanillaName);
	
	PotionMeta setBasePotionType(PotionMeta meta, PotionType type);
	
	Fireball setVisualFireForFireball(Fireball fireball, boolean visualFire);
	
	<T> void setGameRule(World world, String key, T value);
	
	@Nullable ItemStack matchItemStackFromPlayerHands(Player player, Material type);
	
	boolean playerCanAfford(Player player, Material material, int amount);
	
	boolean removeItemFromPlayerHand(Player player, Material material, int amount, boolean offHand);
	
	String getEnchantmentVanillaName(Enchantment enchantment);
	
	void hidePlayer(Player from, JavaPlugin plugin, Player to);
	
	void showPlayer(Player from, JavaPlugin plugin, Player to);
	
	void setSkullOwner(Player player, ItemStack skullItem);
	
	double getMaxHealth(Player player);
	
	void setMaxHealth(Player player, double max);
	
	@Nullable ItemStack getItemFromPlayerHand(Player player, boolean offHand);
	
	boolean hasCooldown(Player player, Material material);
	
	int getCooldown(Player player, Material material);
	
	void setCooldown(Player player, Material material, int ticks);
	
	void handleBlockBreakEventDropItems(BlockBreakEvent event, boolean dropItems);
	
	@Nullable
	Entity getProjectileHitEventHitEntity(ProjectileHitEvent event);
	
	boolean playerInteractEventCheckPlayerHand(PlayerInteractEvent event, boolean mainHand);
	
	ItemStack[] playerInventoryGetExtraContents(Player player);
	
	ItemStack[] inventoryGetStorageContents(Inventory inventory);
	
	void playerInventorySetExtraContents(Player player, ItemStack...itemStacks);
	
	void potionMetaSetColorAndMainEffectLegacy(PotionMeta meta, Color color, PotionEffectType type);
	
	void setEntitySilent(Entity entity, boolean silent);
	
	void setEntityGravity(Entity entity, boolean gravity);
	
	void setArrowsInBodyForLivingEntity(LivingEntity livingEntity, int count, boolean fireEvent);

}
