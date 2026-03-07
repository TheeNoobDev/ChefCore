package net.chefcraft.reflector.v1_21_R3;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.SoundGroup;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Lidded;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.map.CraftMapView;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.map.MapView;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.reflection.world.ArmorTrimObject;
import net.chefcraft.reflection.world.BlockSoundType;
import net.chefcraft.reflection.world.GameReflections;
import net.chefcraft.reflector.v1_21_R3.utils.MinecraftHelper;
import net.chefcraft.world.item.ItemColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.phys.BlockHitResult;

public class GameReflector implements GameReflections {
	
	private final Map<String, ArmorTrim> armorTrimsByData = new HashMap<>();
	
	@Override
	public void setArrowsInBodyForLivingEntity(LivingEntity livingEntity, int count, boolean fireEvent) {
		livingEntity.setArrowsInBody(count, fireEvent);
	}
	
	@Override
	public Item setUnlimitedLifetime(Item item) {
		item.setUnlimitedLifetime(true);
		return item;
	}
	
	@Override
	public BlockFace getBlockFaceFromLocation(Location location) {	
													//Do not implement wrong directional interface because i did :(
        if (location.getBlock().getBlockData() instanceof org.bukkit.block.data.Directional directional) {
        	return directional.getFacing();
        }
        
		return BlockFace.SELF;
	}
	
	@Override
	public org.bukkit.entity.ItemFrame createItemFrame(Location location, BlockFace facing, boolean glowing) {	
		ServerLevel level = ((CraftWorld) location.getWorld()).getHandle();
		BlockPos pos = CraftLocation.toBlockPosition(location);
		Direction direction = CraftBlock.blockFaceToNotch(facing);
		
		ItemFrame frame = glowing ? new GlowItemFrame(level, pos, direction) : new ItemFrame(level, pos, direction);
		frame.setNoGravity(true);
		frame.setSilent(true);
		frame.setInvulnerable(true);
		
		level.addFreshEntity(frame, SpawnReason.CUSTOM);
		
		return (org.bukkit.entity.ItemFrame) frame.getBukkitEntity();
	}
	
	@Override
	public boolean hasItemStackTag(ItemStack itemStack, String key) {
		net.minecraft.world.item.ItemStack x = CraftItemStack.asNMSCopy(itemStack);
		return x.has(DataComponents.CUSTOM_DATA) ? x.get(DataComponents.CUSTOM_DATA).contains(key) : false;
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "deprecation" })
	public <T> T getItemStackTagValue(Class<?> dataType, ItemStack itemStack, String key) {
		net.minecraft.world.item.ItemStack x = CraftItemStack.asNMSCopy(itemStack);
		if (x.has(DataComponents.CUSTOM_DATA) && x.get(DataComponents.CUSTOM_DATA).contains(key)) { //hasTag //containsKey
			CustomData compound = x.get(DataComponents.CUSTOM_DATA);
			
			if (String.class.isAssignableFrom(dataType)) {
				return (T) compound.getUnsafe().get(key);
			} else if (Integer.class.isAssignableFrom(dataType)) {
				return (T) compound.getUnsafe().get(key);
				
			} else if (Boolean.class.isAssignableFrom(dataType)) {
				return (T) compound.getUnsafe().get(key);
				
			} else if (Float.class.isAssignableFrom(dataType)) {
				return (T) compound.getUnsafe().get(key);
				
			} else if (Double.class.isAssignableFrom(dataType)) {
				return (T) compound.getUnsafe().get(key);
				
			} else if (Byte.class.isAssignableFrom(dataType)) {
				return (T) compound.getUnsafe().get(key);
				
			} else if (Short.class.isAssignableFrom(dataType)) {
				return (T) compound.getUnsafe().get(key);
				
			} else if (Long.class.isAssignableFrom(dataType)) {
				return (T) compound.getUnsafe().get(key);
				
			}
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public <T> ItemStack putItemStackTag(Class<?> dataType, ItemStack itemStack, String key, T value) {
		net.minecraft.world.item.ItemStack x = CraftItemStack.asNMSCopy(itemStack);
		CustomData compound = x.has(DataComponents.CUSTOM_DATA) ? x.get(DataComponents.CUSTOM_DATA) : CustomData.of(new CompoundTag());
		
		if (String.class.isAssignableFrom(dataType)) {
			compound.getUnsafe().putString(key, (String) value);
			return applyItemStackTagCompound(compound, x);
			
		} else if (Integer.class.isAssignableFrom(dataType)) {
			compound.getUnsafe().putInt(key, (Integer) value);
			return applyItemStackTagCompound(compound, x);
			
		} else if (Boolean.class.isAssignableFrom(dataType)) {
			compound.getUnsafe().putBoolean(key, (Boolean) value);
			return applyItemStackTagCompound(compound, x);
			
		} else if (Float.class.isAssignableFrom(dataType)) {
			compound.getUnsafe().putFloat(key, (Float) value);
			return applyItemStackTagCompound(compound, x);
			
		} else if (Double.class.isAssignableFrom(dataType)) {
			compound.getUnsafe().putDouble(key, (Double) value);
			return applyItemStackTagCompound(compound, x);
			
		} else if (Byte.class.isAssignableFrom(dataType)) {
			compound.getUnsafe().putByte(key, (Byte) value);
			return applyItemStackTagCompound(compound, x);
			
		} else if (Short.class.isAssignableFrom(dataType)) {
			compound.getUnsafe().putShort(key, (Short) value);
			return applyItemStackTagCompound(compound, x);
			
		} else if (Long.class.isAssignableFrom(dataType)) {
			compound.getUnsafe().putLong(key, (Long) value);
			return applyItemStackTagCompound(compound, x);
			
		}
		
		throw new IllegalArgumentException("Data type can only be these: String, Integer, Boolean, Float, Double, Byte, Short, Long. Your data type is '" + dataType.getName() + "'!");
	}
	
	private ItemStack applyItemStackTagCompound(CustomData tag, net.minecraft.world.item.ItemStack x) {
		x.set(DataComponents.CUSTOM_DATA, tag);
		return CraftItemStack.asBukkitCopy(x);
	}

	@Override
	public void setNoClip(Entity entity, boolean noClip) {
		((CraftEntity) entity).getHandle().noPhysics = noClip;
	}
	
	@SuppressWarnings("removal")
	@Override
	public String getItemTranslationKey(ItemStack itemStack) {
		return itemStack.getTranslationKey();
	}
	
	@SuppressWarnings({ "deprecation", "removal" }) //MaterialData
	@Override
	public Block setBlockType(Block block, Material material, @Deprecated int typeId) {
		if (!material.isAir() && material.isBlock()) {
			MaterialData data = new MaterialData(material, (byte) typeId);
			BlockState state = block.getState();
			state.setData(data);
			state.update(true, false);
		}
		return block;
	}
	
	@Override
	public float getAbsorptionAmount(Player player) {
		return (float) player.getAbsorptionAmount();
	}
	
	@Override
	public Sound getBlockSound(Block block, BlockSoundType type) {
		return this.matchSoundType(block.getBlockData().getSoundGroup(), type);
	}
	
	@Override
	public void playSoundFromBlock(Block block, BlockSoundType type) {
		SoundGroup group = block.getBlockSoundGroup();
		block.getWorld().playSound(block.getLocation(), this.matchSoundType(group, type), (group.getVolume() + 1.0F) / 2.0F, group.getPitch() * 0.8F);
	}
	
	private Sound matchSoundType(SoundGroup group, BlockSoundType type) {
		switch (type) {
		case BREAK:
			return group.getBreakSound();
		case PLACE:
			return group.getPlaceSound();
		case FALL:
			return group.getFallSound();
		case HIT:
			return group.getHitSound();
		default:
			return group.getStepSound();
		}
	}
	
	@Override
	public ArmorTrimObject createArmorTrimObject(String trimPattern, String trimMaterial) {
		ArmorTrim trim = this.getOrCreateArmorTrim(trimPattern, trimMaterial);
		
		if (trim != null) {
			return (armor) -> {
				if (isArmor(armor.getType())) {
					ArmorMeta armorMeta = (ArmorMeta) armor.getItemMeta();
					armorMeta.setTrim(trim);
					armorMeta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
					armor.setItemMeta(armorMeta);
					return armor;
				}
				return armor;
			};
		} else {
			return armor -> armor;
		}
	}
	
	@SuppressWarnings("deprecation")
	@Nullable
	public ArmorTrim getOrCreateArmorTrim(String trimPattern, String trimMaterial) {
		String ptt = trimPattern.toLowerCase(Locale.ENGLISH);
		String tmm = trimMaterial.toLowerCase(Locale.ENGLISH);
		String data = ptt + tmm;
		
		if (this.armorTrimsByData.containsKey(data)) {
			return this.armorTrimsByData.get(data);
		} else {
			try {
				ArmorTrim trim = new ArmorTrim(Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(tmm)),
						Registry.TRIM_PATTERN.get(NamespacedKey.minecraft(ptt)));
				
				this.armorTrimsByData.put(data, trim);
				return trim;
			} catch (Exception x) {
				ChefCore.log(Level.WARNING, "An error occurred parsing armor trim object request! Check trim pattern " + ptt + " and trim material " + tmm + "!");
				return null;
			}
		}
	}
	
	@Override
	public boolean placeBlockAsPlayer(Player player, Location location, BlockFace blockFace, ItemStack itemStack, boolean mainHand) {
		ServerPlayer entityPlayer = MinecraftServer.getServer().getPlayerList().getPlayer(player.getUniqueId());
		
		if (entityPlayer != null) {
			MinecraftHelper.addPositionOffsetForBlockFace(location, blockFace);
			BlockHitResult positionBlock = BlockHitResult.miss(CraftLocation.toVec3D(location), CraftBlock.blockFaceToNotch(blockFace), CraftLocation.toBlockPosition(location));
			
			if (entityPlayer.gameMode.useItemOn(entityPlayer, entityPlayer.level(), CraftItemStack.asNMSCopy(itemStack), mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND,
					positionBlock).consumesAction()) {
				
				Block block = location.getBlock();
				SoundGroup group = block.getBlockSoundGroup();
				location.getWorld().playSound(location, group.getPlaceSound(), (group.getVolume() + 1.0F) / 2.0F, group.getPitch() * 0.8F);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void damageOutOfWorld(Player player, float damage) {
		EntityDamageEvent event = player.getLastDamageCause();	
		DamageSource damageSource = DamageSource.builder(DamageType.OUT_OF_WORLD).build();
		
		player.damage(damage, damageSource);
		
		if (event instanceof EntityDamageByEntityEvent by) {
			Entity damager = by.getDamager();
			
			if (damager instanceof Player damagerPlayer) {
				player.setKiller(damagerPlayer);
			}
		}
	}

	@Override
	public TNTPrimed spawnPrimedTNT(JavaPlugin plugin, Player source, Location location) {
		ServerLevel world = ((CraftWorld) location.getWorld()).getHandle();
		PrimedTnt tnt = new PrimedTnt(world, location.getBlockX() + 0.5D, location.getBlockY() + 0.0D, location.getBlockZ() + 0.5D, ((CraftLivingEntity) source).getHandle());
		tnt.setDeltaMovement(0.0D, 0.0D, 0.0D);
		world.addFreshEntity(tnt, SpawnReason.CUSTOM);
		return (TNTPrimed) tnt.getBukkitEntity();
	}

	@Override
	public PotionMeta addCustomPotionEffect(PotionMeta meta, PotionEffectType type, int duration, int amplifier, boolean ambient, boolean particles, Color color) {
		meta.addCustomEffect(new PotionEffect(type, duration, amplifier, ambient, particles), true);
		return meta;
	}
	
	@Override
	public void renderMapView(Player player, MapView view, boolean async) {
		if (async) {
			Bukkit.getScheduler().runTaskAsynchronously(ChefCore.getInstance(), ()-> {
				((CraftMapView) view).render((CraftPlayer) player);
			});
		} else {
			((CraftMapView) view).render((CraftPlayer) player);
		}
	}
	
	@Override
	public boolean isItemColorable(Material type) {
		String typeName = type.name();
		return typeName.contains("TERRACOTTA") || typeName.contains("STAINED_GLASS_PANE")
				|| typeName.contains("STAINED_GLASS_PANE_PANE") || typeName.contains("CONCRETE")
				|| typeName.contains("CONCRETE_POWDER") || typeName.contains("WOOL")
				|| typeName.contains("GLAZED_TERRACOTTA");
	}
	
	@Override
	public Material colorizeMaterial(Material material, ItemColor type) {
		String typeName = material.name();
		if (typeName.contains("STAINED_GLASS_PANE")) {
			
			return getGlassPaneValueByItemColor(type);
			
		} else if (typeName.contains("CONCRETE_POWDER")) {
			
			return getConcretePowderValueByItemColor(type);
			
		} else if (typeName.contains("GLAZED_TERRACOTTA")) {
			
			return getGlazedTerracottaValueByItemColor(type);
			
		} else if (typeName.contains("STAINED_GLASS")) {
			
			return getGlassValueByItemColor(type);
			
		} else if (typeName.contains("WOOL")) {
			
			return getWoolValueByItemColor(type);
			
		} else if (typeName.contains("CONCRETE")) {
			
			return getConcreteValueByItemColor(type);
			
		} else if (typeName.contains("TERRACOTTA")) {
			
			return getTerracottaValueByItemColor(type);
			
		}
		
		return material;
	}

	@SuppressWarnings("deprecation")
	@Override
	public ItemStack colorizeItemStack(ItemStack item, ItemColor type, @Nullable Color color) {
		Material material = item.getType();
		switch (material) {
		case LEATHER_HELMET:
		case LEATHER_CHESTPLATE:
		case LEATHER_LEGGINGS:
		case LEATHER_BOOTS:
			if (color != null) {
				LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
				meta.setColor(color);
				item.setItemMeta(meta);
			}
			return item;
		default:
			String typeName = material.name();
			if (typeName.contains("CONCRETE_POWDER")) {
				item.setType(getConcretePowderValueByItemColor(type));
				return item;
			} else if (typeName.contains("STAINED_GLASS_PANE")) {
				item.setType(getGlassPaneValueByItemColor(type));
				return item;
			} else if (typeName.contains("GLAZED_TERRACOTTA")) {
				item.setType(getGlazedTerracottaValueByItemColor(type));
				return item;
			} else if (typeName.contains("STAINED_GLASS")) {
				item.setType(getGlassValueByItemColor(type));
				return item;
			} else if (typeName.contains("WOOL")) {
				item.setType(getWoolValueByItemColor(type));
				return item;
			} else if (typeName.contains("CONCRETE")) {
				item.setType(getConcreteValueByItemColor(type));
				return item;
			} else if (typeName.contains("TERRACOTTA")) {
				item.setType(getTerracottaValueByItemColor(type));
				return item;
			}
		}
		return item;
	}
	
	@Override
	public boolean isArmor(Material material) {
		switch (material) {
		case LEATHER_HELMET:
		case LEATHER_CHESTPLATE:
		case LEATHER_LEGGINGS:
		case LEATHER_BOOTS:
		case CHAINMAIL_HELMET:
		case CHAINMAIL_CHESTPLATE:
		case CHAINMAIL_LEGGINGS:
		case CHAINMAIL_BOOTS:
		case IRON_HELMET:
		case IRON_CHESTPLATE:
		case IRON_LEGGINGS:
		case IRON_BOOTS:
		case DIAMOND_HELMET:
		case DIAMOND_CHESTPLATE:
		case DIAMOND_LEGGINGS:
		case DIAMOND_BOOTS:
		case NETHERITE_HELMET:
		case NETHERITE_CHESTPLATE:
		case NETHERITE_LEGGINGS:
		case NETHERITE_BOOTS:
			return true;
		default:
			return false;
		}
	}
	
	@Override
	public boolean potionHasPositiveEffects(PotionEffectType type) {
		String key = type.getKey().getKey();
		return key.equalsIgnoreCase("speed") 
			|| key.equalsIgnoreCase("haste")
			|| key.equalsIgnoreCase("strength")
			|| key.equalsIgnoreCase("instant_health")
			|| key.equalsIgnoreCase("jump_boost")
			|| key.equalsIgnoreCase("regeneration")
			|| key.equalsIgnoreCase("resistance")
			|| key.equalsIgnoreCase("fire_resistance")
			|| key.equalsIgnoreCase("water_breathing")
			|| key.equalsIgnoreCase("invisibility")
			|| key.equalsIgnoreCase("night_vision")
			|| key.equalsIgnoreCase("health_boost")
			|| key.equalsIgnoreCase("absorption")
			|| key.equalsIgnoreCase("saturation")
			|| key.equalsIgnoreCase("luck")
			|| key.equalsIgnoreCase("slow_falling")
			|| key.equalsIgnoreCase("conduit_power")
			|| key.equalsIgnoreCase("dolphins_grace")
			|| key.equalsIgnoreCase("hero_of_the_village");
	}
	
	@Override
	public int getArmorSlot(Material material) {
		switch (material) {
		case LEATHER_HELMET:
		case CHAINMAIL_HELMET:
		case IRON_HELMET:
		case DIAMOND_HELMET:
			return 39;
		case LEATHER_CHESTPLATE:
		case CHAINMAIL_CHESTPLATE:
		case IRON_CHESTPLATE:
		case DIAMOND_CHESTPLATE:
			return 38;
		case LEATHER_LEGGINGS:
		case CHAINMAIL_LEGGINGS:
		case IRON_LEGGINGS:
		case DIAMOND_LEGGINGS:
			return 37;
		case LEATHER_BOOTS:
		case CHAINMAIL_BOOTS:
		case IRON_BOOTS:
		case DIAMOND_BOOTS:
			return 36;
		default:
			return 0;
		}
	}
	
	@Override
	public boolean isBoots(Material material) {
		switch (material) {
		case LEATHER_BOOTS:
		case CHAINMAIL_BOOTS:
		case IRON_BOOTS:
		case DIAMOND_BOOTS:
		case NETHERITE_BOOTS:
		case GOLDEN_BOOTS:
			return true;
		default:
			return false;
		}
	}
	
	@Override
	public boolean isLeatherArmor(Material material) {
		switch (material) {
		case LEATHER_HELMET:
		case LEATHER_CHESTPLATE:
		case LEATHER_LEGGINGS:
		case LEATHER_BOOTS:
			return true;
		default:
			return false;
		}
	}
	
	public Material getGlassPaneValueByItemColor(ItemColor color) {
		switch (color) {
		case BLUE:
			return Material.BLUE_STAINED_GLASS_PANE;
		case CYAN:
			return Material.CYAN_STAINED_GLASS_PANE;
		case LIME:
			return Material.LIME_STAINED_GLASS_PANE;
		case GRAY:
			return Material.GRAY_STAINED_GLASS_PANE;
		case GREEN:
			return Material.GREEN_STAINED_GLASS_PANE;
		case LIGHT_BLUE:
			return Material.LIGHT_BLUE_STAINED_GLASS_PANE;
		case ORANGE:
			return Material.ORANGE_STAINED_GLASS_PANE;
		case PINK:
			return Material.PINK_STAINED_GLASS_PANE;
		case PURPLE:
			return Material.PURPLE_STAINED_GLASS_PANE;
		case RED:
			return Material.RED_STAINED_GLASS_PANE;
		case BLACK:
			return Material.GRAY_STAINED_GLASS_PANE;
		case WHITE:
			return Material.WHITE_STAINED_GLASS_PANE;
		case YELLOW:
			return Material.YELLOW_STAINED_GLASS_PANE;
		case BROWN:
			return Material.BROWN_STAINED_GLASS_PANE;
		case LIGHT_GRAY:
			return Material.LIGHT_GRAY_STAINED_GLASS_PANE;
		case MAGENTA:
			return Material.MAGENTA_STAINED_GLASS_PANE;
		default: 
			return Material.BARRIER;
		}
	}
	
	public Material getGlassValueByItemColor(ItemColor color) {
		switch (color) {
		case BLUE:
			return Material.BLUE_STAINED_GLASS;
		case CYAN:
			return Material.CYAN_STAINED_GLASS;
		case LIME:
			return Material.LIME_STAINED_GLASS;
		case GRAY:
			return Material.GRAY_STAINED_GLASS;
		case GREEN:
			return Material.GREEN_STAINED_GLASS;
		case LIGHT_BLUE:
			return Material.LIGHT_BLUE_STAINED_GLASS;
		case ORANGE:
			return Material.ORANGE_STAINED_GLASS;
		case PINK:
			return Material.PINK_STAINED_GLASS;
		case PURPLE:
			return Material.PURPLE_STAINED_GLASS;
		case RED:
			return Material.RED_STAINED_GLASS;
		case BLACK:
			return Material.GRAY_STAINED_GLASS;
		case WHITE:
			return Material.WHITE_STAINED_GLASS;
		case YELLOW:
			return Material.YELLOW_STAINED_GLASS;
		case BROWN:
			return Material.BROWN_STAINED_GLASS;
		case LIGHT_GRAY:
			return Material.LIGHT_GRAY_STAINED_GLASS;
		case MAGENTA:
			return Material.MAGENTA_STAINED_GLASS;
		default: 
			return Material.BARRIER;
		}
	}
	
	public Material getConcretePowderValueByItemColor(ItemColor color) {
		switch (color) {
		case BLUE:
			return Material.BLUE_CONCRETE_POWDER;
		case CYAN:
			return Material.CYAN_CONCRETE_POWDER;
		case LIME:
			return Material.LIME_CONCRETE_POWDER;
		case GRAY:
			return Material.GRAY_CONCRETE_POWDER;
		case GREEN:
			return Material.GREEN_CONCRETE_POWDER;
		case LIGHT_BLUE:
			return Material.LIGHT_BLUE_CONCRETE_POWDER;
		case ORANGE:
			return Material.ORANGE_CONCRETE_POWDER;
		case PINK:
			return Material.PINK_CONCRETE_POWDER;
		case PURPLE:
			return Material.PURPLE_CONCRETE_POWDER;
		case RED:
			return Material.RED_CONCRETE_POWDER;
		case BLACK:
			return Material.GRAY_CONCRETE_POWDER;
		case WHITE:
			return Material.WHITE_CONCRETE_POWDER;
		case YELLOW:
			return Material.YELLOW_CONCRETE_POWDER;
		case BROWN:
			return Material.BROWN_CONCRETE_POWDER;
		case LIGHT_GRAY:
			return Material.LIGHT_GRAY_CONCRETE_POWDER;
		case MAGENTA:
			return Material.MAGENTA_CONCRETE_POWDER;
		default: 
			return Material.BARRIER;
		}
	}
	
	public Material getWoolValueByItemColor(ItemColor color) {
		switch (color) {
		case BLUE:
			return Material.BLUE_WOOL;
		case CYAN:
			return Material.CYAN_WOOL;
		case LIME:
			return Material.LIME_WOOL;
		case GRAY:
			return Material.GRAY_WOOL;
		case GREEN:
			return Material.GREEN_WOOL;
		case LIGHT_BLUE:
			return Material.LIGHT_BLUE_WOOL;
		case ORANGE:
			return Material.ORANGE_WOOL;
		case PINK:
			return Material.PINK_WOOL;
		case PURPLE:
			return Material.PURPLE_WOOL;
		case RED:
			return Material.RED_WOOL;
		case BLACK:
			return Material.GRAY_WOOL;
		case WHITE:
			return Material.WHITE_WOOL;
		case YELLOW:
			return Material.YELLOW_WOOL;
		case BROWN:
			return Material.BROWN_WOOL;
		case LIGHT_GRAY:
			return Material.LIGHT_GRAY_WOOL;
		case MAGENTA:
			return Material.MAGENTA_WOOL;
		default: 
			return Material.BARRIER;
		}
	}
	
	public Material getConcreteValueByItemColor(ItemColor color) {
		switch (color) {
		case BLUE:
			return Material.BLUE_CONCRETE;
		case CYAN:
			return Material.CYAN_CONCRETE;
		case LIME:
			return Material.LIME_CONCRETE;
		case GRAY:
			return Material.GRAY_CONCRETE;
		case GREEN:
			return Material.GREEN_CONCRETE;
		case LIGHT_BLUE:
			return Material.LIGHT_BLUE_CONCRETE;
		case ORANGE:
			return Material.ORANGE_CONCRETE;
		case PINK:
			return Material.PINK_CONCRETE;
		case PURPLE:
			return Material.PURPLE_CONCRETE;
		case RED:
			return Material.RED_CONCRETE;
		case BLACK:
			return Material.GRAY_CONCRETE;
		case WHITE:
			return Material.WHITE_CONCRETE;
		case YELLOW:
			return Material.YELLOW_CONCRETE;
		case BROWN:
			return Material.BROWN_CONCRETE;
		case LIGHT_GRAY:
			return Material.LIGHT_GRAY_CONCRETE;
		case MAGENTA:
			return Material.MAGENTA_CONCRETE;
		default: 
			return Material.BARRIER;
		}
	}
	
	public Material getTerracottaValueByItemColor(ItemColor color) {
		switch (color) {
		case BLUE:
			return Material.BLUE_TERRACOTTA;
		case CYAN:
			return Material.CYAN_TERRACOTTA;
		case LIME:
			return Material.LIME_TERRACOTTA;
		case GRAY:
			return Material.GRAY_TERRACOTTA;
		case GREEN:
			return Material.GREEN_TERRACOTTA;
		case LIGHT_BLUE:
			return Material.LIGHT_BLUE_TERRACOTTA;
		case ORANGE:
			return Material.ORANGE_TERRACOTTA;
		case PINK:
			return Material.PINK_TERRACOTTA;
		case PURPLE:
			return Material.PURPLE_TERRACOTTA;
		case RED:
			return Material.RED_TERRACOTTA;
		case BLACK:
			return Material.GRAY_TERRACOTTA;
		case WHITE:
			return Material.WHITE_TERRACOTTA;
		case YELLOW:
			return Material.YELLOW_TERRACOTTA;
		case BROWN:
			return Material.BROWN_TERRACOTTA;
		case LIGHT_GRAY:
			return Material.LIGHT_GRAY_TERRACOTTA;
		case MAGENTA:
			return Material.MAGENTA_TERRACOTTA;
		default: 
			return Material.BARRIER;
		}
	}
	
	public Material getGlazedTerracottaValueByItemColor(ItemColor color) {
		switch (color) {
		case BLUE:
			return Material.BLUE_GLAZED_TERRACOTTA;
		case CYAN:
			return Material.CYAN_GLAZED_TERRACOTTA;
		case LIME:
			return Material.LIME_GLAZED_TERRACOTTA;
		case GRAY:
			return Material.GRAY_GLAZED_TERRACOTTA;
		case GREEN:
			return Material.GREEN_GLAZED_TERRACOTTA;
		case LIGHT_BLUE:
			return Material.LIGHT_BLUE_GLAZED_TERRACOTTA;
		case ORANGE:
			return Material.ORANGE_GLAZED_TERRACOTTA;
		case PINK:
			return Material.PINK_GLAZED_TERRACOTTA;
		case PURPLE:
			return Material.PURPLE_GLAZED_TERRACOTTA;
		case RED:
			return Material.RED_GLAZED_TERRACOTTA;
		case BLACK:
			return Material.GRAY_GLAZED_TERRACOTTA;
		case WHITE:
			return Material.WHITE_GLAZED_TERRACOTTA;
		case YELLOW:
			return Material.YELLOW_GLAZED_TERRACOTTA;
		case BROWN:
			return Material.BROWN_GLAZED_TERRACOTTA;
		case LIGHT_GRAY:
			return Material.LIGHT_GRAY_GLAZED_TERRACOTTA;
		case MAGENTA:
			return Material.MAGENTA_GLAZED_TERRACOTTA;
		default: 
			return Material.BARRIER;
		}
	}

	@Override
	public PotionEffect createPotionEffect(PotionEffectType type, int duration, int amplifier, boolean ambient, boolean particles, Color color) {
		return new PotionEffect(type, duration, amplifier, ambient, particles);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void kickPlayer(Player player, String reason) {
		player.kickPlayer(reason);
	}

	@Override
	public void setItemToFrame(org.bukkit.entity.ItemFrame itemFrame, ItemStack itemStack) {
		itemFrame.setItem(itemStack, false);
	}

	@Override
	public MapView getMapView(int mapId) {
		return Bukkit.getMap(mapId);
	}

	@Override
	public ItemStack applyMapMetaToItemStack(MapView mapView, ItemStack itemStack) {
		if (itemStack.getType() == Material.FILLED_MAP) {
			MapMeta meta = (MapMeta) itemStack.getItemMeta();
			meta.setMapView(mapView);
			itemStack.setItemMeta(meta);
		}
		return itemStack;
	}

	@Override
	public boolean isItemStackEmpty(ItemStack item) {
		return item == null || item.getType() == Material.AIR;
	}

	@Override
	public boolean hasWorldImmediateRespawn(World world) {
		return world.getGameRuleValue(GameRule.DO_IMMEDIATE_RESPAWN);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setPlayerRespawnLocation(Player player, Location location) {
		player.setBedSpawnLocation(location, true);
		player.setRespawnLocation(location, true);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void sendComponents(Player player, BaseComponent... components) {
		player.sendMessage(components);
	}

	@Override
	public void playOpenOrCloseAnimation(Block block, boolean open) {
		if (block.getState() instanceof Lidded lid) {
			if (open && !lid.isOpen()) {
				lid.open();
			} else if (!open) {
				lid.close();
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean potionHasPositiveEffects(ThrownPotion thrownPotion) {
		PotionEffectType type = thrownPotion.getPotionMeta().getBasePotionType().getEffectType();
		type = type == null ? (!thrownPotion.getPotionMeta().getCustomEffects().isEmpty() ? thrownPotion.getPotionMeta().getCustomEffects().get(0).getType() 
				: PotionEffectType.ABSORPTION) : type;
		return this.potionHasPositiveEffects(type);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void updateOpenedInventoryTitle(Player player, MessageHolder title) {
		InventoryView view = player.getOpenInventory();
		if (view != null) {
			view.setTitle(title.asString(true));
		}		
	}

	@Override
	public void onItemConsumeEventReplaceLeftoversToAir(PlayerItemConsumeEvent playerItemConsumeEvent) {
		playerItemConsumeEvent.setReplacement(new ItemStack(Material.AIR));
	}

	@Override
	public void onProjectileHitSetCancelledEvent(ProjectileHitEvent rojectileHitEvent) {
		rojectileHitEvent.setCancelled(true);
	}

	@SuppressWarnings("deprecation")
	@Override
	public Enchantment getEnchantmentByVanillaName(String vanillaName) {
		return Enchantment.getByKey(NamespacedKey.minecraft(vanillaName));
	}

	@SuppressWarnings("deprecation")
	@Override
	public PotionEffectType getPotionEffectTypeByVanillaName(String vanillaName) {
		return PotionEffectType.getByKey(NamespacedKey.minecraft(vanillaName));
	}

	@Override
	public PotionMeta setBasePotionType(PotionMeta meta, PotionType type) {
		meta.setBasePotionType(type);
		return meta;
	}

	@Override
	public Fireball setVisualFireForFireball(Fireball fireball, boolean visualFire) {
		fireball.setVisualFire(visualFire);
		return fireball;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> void setGameRule(World world, String key, T value) {
		GameRule<T> rule = (GameRule<T>) GameRule.getByName(key);
		world.setGameRule(rule, value);
	}

	@Override
	public ItemStack matchItemStackFromPlayerHands(Player player, Material type) {
		PlayerInventory inventory = player.getInventory();
		ItemStack itemStack = inventory.getItemInMainHand();
		if (!this.isItemStackEmpty(itemStack) && itemStack.getType() == type) {
			return itemStack;
		}
		
		itemStack = inventory.getItemInOffHand();
		
		if (!this.isItemStackEmpty(itemStack) && itemStack.getType() == type) {
			return itemStack;
		}
		return null;
	}

	@Override
	public boolean playerCanAfford(Player player, Material material, int amount) {
		boolean canAfford = player.getInventory().contains(material, amount);
		if (canAfford) {
			return true;
		} else {
			ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
			if (!this.isItemStackEmpty(itemInOffHand)) {
				if (itemInOffHand.getType() == material && itemInOffHand.getAmount() >= amount) {
					return true;
				}
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean removeItemFromPlayerHand(Player player, Material material, int amount, boolean offHand) {
		PlayerInventory playerinventory = player.getInventory();
		if (material == null || material == Material.AIR || amount < 1) return false;
		ItemStack itemstack = offHand ? playerinventory.getItemInOffHand() : playerinventory.getItemInMainHand();
		if (itemstack.getType() == material) {
			if(itemstack.getAmount() <= 1) {
                itemstack.setType(Material.AIR);
                if (offHand) {
                	playerinventory.setItemInOffHand(itemstack);
                } else {
                	playerinventory.setItemInMainHand(itemstack);
                }
            } else {
            	itemstack.setAmount(itemstack.getAmount() - 1);
            }
			player.updateInventory();
			return true;
		}
		return false;
	}
	
	@Override
	public String getEnchantmentVanillaName(Enchantment enchantment) {
		return enchantment.getKey().asString();
	}

	@Override
	public void hidePlayer(Player from, JavaPlugin plugin, Player to) {
		from.hidePlayer(plugin, to);
	}

	@Override
	public void showPlayer(Player from, JavaPlugin plugin, Player to) {
		from.showPlayer(plugin, to);
	}

	@Override
	public int getMapViewId(MapView view) {
		return view.getId();
	}

	@Override
	public void setSkullOwner(Player player, ItemStack skullItem) {
		if (skullItem.getType() == Material.PLAYER_HEAD) {
			SkullMeta meta = (SkullMeta) skullItem.getItemMeta();
			meta.setOwningPlayer(player);
			skullItem.setItemMeta(meta);
		}
	}

	@Override
	public ItemStack getItemFromPlayerHand(Player player, boolean offHand) {
		return offHand ? player.getInventory().getItemInOffHand() : player.getInventory().getItemInMainHand();
	}
	
	@Override
	public double getMaxHealth(Player player) {
		return player.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
	}

	@Override
	public void setMaxHealth(Player player, double max) {
		player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(max);
	}
	
	@Override
	public boolean hasCooldown(Player player, Material material) {
		return player.hasCooldown(material);
	}
	
	@Override
	public int getCooldown(Player player, Material material) {
		return player.getCooldown(material);
	}
	
	@Override
	public void setCooldown(Player player, Material material, int ticks) {
		player.setCooldown(material, ticks);
	}
	
	@Override
	public void handleBlockBreakEventDropItems(BlockBreakEvent event, boolean dropItems) {
		event.setDropItems(dropItems);
	}
	
	@Nullable
	@Override
	public Entity getProjectileHitEventHitEntity(ProjectileHitEvent event) {
		return event.getHitEntity();
	}
	
	@Override
	public boolean playerInteractEventCheckPlayerHand(PlayerInteractEvent event, boolean mainHand) {
		return event.getHand() == (mainHand ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND);
	}

	@Override
	public ItemStack[] playerInventoryGetExtraContents(Player player) {
		return player.getInventory().getExtraContents();
	}

	@Override
	public ItemStack[] inventoryGetStorageContents(Inventory inventory) {
		return inventory.getStorageContents();
	}

	@Override
	public void playerInventorySetExtraContents(Player player, ItemStack... itemStacks) {
		player.getInventory().setExtraContents(itemStacks);
	}

	@Override
	public void potionMetaSetColorAndMainEffectLegacy(PotionMeta meta, Color color, PotionEffectType type) {
		meta.setColor(color);
	}

	@Override
	public void setEntitySilent(Entity entity, boolean silent) {
		entity.setSilent(silent);
	}

	@Override
	public void setEntityGravity(Entity entity, boolean gravity) {
		entity.setGravity(gravity);
	}
	
	@Override
	public boolean isInteractableIngame(Material material) {
		switch (material) {
        case ACACIA_HANGING_SIGN:
        case ACACIA_SIGN:
        case ACACIA_WALL_HANGING_SIGN:
        case ACACIA_WALL_SIGN:
        case ANVIL:
        case BAMBOO_HANGING_SIGN:
        case BAMBOO_SIGN:
        case BAMBOO_WALL_HANGING_SIGN:
        case BAMBOO_WALL_SIGN:
        case BARREL:
        case BEACON:
        case BEEHIVE:
        case BEE_NEST:
        case BIRCH_HANGING_SIGN:
        case BIRCH_SIGN:
        case BIRCH_WALL_HANGING_SIGN:
        case BIRCH_WALL_SIGN:
        case BLACK_BED:
        case BLACK_SHULKER_BOX:
        case BLAST_FURNACE:
        case BLUE_BED:
        case BLUE_SHULKER_BOX:
        case BREWING_STAND:
        case BROWN_BED:
        case BROWN_SHULKER_BOX:
        case CAMPFIRE:
        case CARTOGRAPHY_TABLE:
        case CAULDRON:
        case CAVE_VINES:
        case CAVE_VINES_PLANT:
        case CHAIN_COMMAND_BLOCK:
        case CHERRY_HANGING_SIGN:
        case CHERRY_SIGN:
        case CHERRY_WALL_HANGING_SIGN:
        case CHERRY_WALL_SIGN:
        case CHIPPED_ANVIL:
        case CHISELED_BOOKSHELF:
        case COMMAND_BLOCK:
        case COMPARATOR:
        case COMPOSTER:
        case CRAFTER:
        case CRAFTING_TABLE:
        case CRIMSON_HANGING_SIGN:
        case CRIMSON_SIGN:
        case CRIMSON_WALL_HANGING_SIGN:
        case CRIMSON_WALL_SIGN:
        case CYAN_BED:
        case CYAN_SHULKER_BOX:
        case DAMAGED_ANVIL:
        case DARK_OAK_HANGING_SIGN:
        case DARK_OAK_SIGN:
        case DARK_OAK_WALL_HANGING_SIGN:
        case DARK_OAK_WALL_SIGN:
        case DAYLIGHT_DETECTOR:
        case DECORATED_POT:
        case DISPENSER:
        case DROPPER:
        case ENCHANTING_TABLE:
        case FLETCHING_TABLE:
        case FLOWER_POT:
        case FURNACE:
        case GRAY_BED:
        case GRAY_SHULKER_BOX:
        case GREEN_BED:
        case GREEN_SHULKER_BOX:
        case GRINDSTONE:
        case HOPPER:
        case JIGSAW:
        case JUKEBOX:
        case JUNGLE_HANGING_SIGN:
        case JUNGLE_SIGN:
        case JUNGLE_WALL_HANGING_SIGN:
        case JUNGLE_WALL_SIGN:
        case LAVA_CAULDRON:
        case LECTERN:
        case LIGHT:
        case LIGHT_BLUE_BED:
        case LIGHT_BLUE_SHULKER_BOX:
        case LIGHT_GRAY_BED:
        case LIGHT_GRAY_SHULKER_BOX:
        case LIME_BED:
        case LIME_SHULKER_BOX:
        case LOOM:
        case MAGENTA_BED:
        case MAGENTA_SHULKER_BOX:
        case MANGROVE_HANGING_SIGN:
        case MANGROVE_SIGN:
        case MANGROVE_WALL_HANGING_SIGN:
        case MANGROVE_WALL_SIGN:
        case MOVING_PISTON:
        case NETHER_BRICK_FENCE:
        case OAK_HANGING_SIGN:
        case OAK_SIGN:
        case OAK_WALL_HANGING_SIGN:
        case OAK_WALL_SIGN:
        case ORANGE_BED:
        case ORANGE_SHULKER_BOX:
        case PINK_BED:
        case PINK_SHULKER_BOX:
        case POTTED_ACACIA_SAPLING:
        case POTTED_ALLIUM:
        case POTTED_AZALEA_BUSH:
        case POTTED_AZURE_BLUET:
        case POTTED_BAMBOO:
        case POTTED_BIRCH_SAPLING:
        case POTTED_BLUE_ORCHID:
        case POTTED_BROWN_MUSHROOM:
        case POTTED_CACTUS:
        case POTTED_CHERRY_SAPLING:
        case POTTED_CORNFLOWER:
        case POTTED_CRIMSON_FUNGUS:
        case POTTED_CRIMSON_ROOTS:
        case POTTED_DANDELION:
        case POTTED_DARK_OAK_SAPLING:
        case POTTED_DEAD_BUSH:
        case POTTED_FERN:
        case POTTED_FLOWERING_AZALEA_BUSH:
        case POTTED_JUNGLE_SAPLING:
        case POTTED_LILY_OF_THE_VALLEY:
        case POTTED_MANGROVE_PROPAGULE:
        case POTTED_OAK_SAPLING:
        case POTTED_ORANGE_TULIP:
        case POTTED_OXEYE_DAISY:
        case POTTED_PINK_TULIP:
        case POTTED_POPPY:
        case POTTED_RED_MUSHROOM:
        case POTTED_RED_TULIP:
        case POTTED_SPRUCE_SAPLING:
        case POTTED_TORCHFLOWER:
        case POTTED_WARPED_FUNGUS:
        case POTTED_WARPED_ROOTS:
        case POTTED_WHITE_TULIP:
        case POTTED_WITHER_ROSE:
        case POWDER_SNOW_CAULDRON:
        case PUMPKIN:
        case PURPLE_BED:
        case PURPLE_SHULKER_BOX:
        case REDSTONE_ORE:
        case REDSTONE_WIRE:
        case RED_BED:
        case RED_SHULKER_BOX:
        case REPEATER:
        case REPEATING_COMMAND_BLOCK:
        case RESPAWN_ANCHOR:
        case SHULKER_BOX:
        case SMITHING_TABLE:
        case SMOKER:
        case SOUL_CAMPFIRE:
        case SPRUCE_HANGING_SIGN:
        case SPRUCE_SIGN:
        case SPRUCE_WALL_HANGING_SIGN:
        case SPRUCE_WALL_SIGN:
        case STONECUTTER:
        case STRUCTURE_BLOCK:
        case SWEET_BERRY_BUSH:
        case WARPED_HANGING_SIGN:
        case WARPED_SIGN:
        case WARPED_WALL_HANGING_SIGN:
        case WARPED_WALL_SIGN:
        case WATER_CAULDRON:
        case WHITE_BED:
        case WHITE_SHULKER_BOX:
        case YELLOW_BED:
        case YELLOW_SHULKER_BOX:
        case FARMLAND:
            return true;
        default:
        	return false;
		}
	}

	@Override
	public boolean isInteractable(Material material) {
		switch (material) {
        case ACACIA_BUTTON:
        case ACACIA_DOOR:
        case ACACIA_FENCE:
        case ACACIA_FENCE_GATE:
        case ACACIA_HANGING_SIGN:
        case ACACIA_SIGN:
        case ACACIA_TRAPDOOR:
        case ACACIA_WALL_HANGING_SIGN:
        case ACACIA_WALL_SIGN:
        case ANVIL:
        case BAMBOO_BUTTON:
        case BAMBOO_DOOR:
        case BAMBOO_FENCE:
        case BAMBOO_FENCE_GATE:
        case BAMBOO_HANGING_SIGN:
        case BAMBOO_SIGN:
        case BAMBOO_TRAPDOOR:
        case BAMBOO_WALL_HANGING_SIGN:
        case BAMBOO_WALL_SIGN:
        case BARREL:
        case BEACON:
        case BEEHIVE:
        case BEE_NEST:
        case BIRCH_BUTTON:
        case BIRCH_DOOR:
        case BIRCH_FENCE:
        case BIRCH_FENCE_GATE:
        case BIRCH_HANGING_SIGN:
        case BIRCH_SIGN:
        case BIRCH_TRAPDOOR:
        case BIRCH_WALL_HANGING_SIGN:
        case BIRCH_WALL_SIGN:
        case BLACK_BED:
        case BLACK_CANDLE:
        case BLACK_CANDLE_CAKE:
        case BLACK_SHULKER_BOX:
        case BLAST_FURNACE:
        case BLUE_BED:
        case BLUE_CANDLE:
        case BLUE_CANDLE_CAKE:
        case BLUE_SHULKER_BOX:
        case BREWING_STAND:
        case BROWN_BED:
        case BROWN_CANDLE:
        case BROWN_CANDLE_CAKE:
        case BROWN_SHULKER_BOX:
        case CAKE:
        case CAMPFIRE:
        case CANDLE:
        case CANDLE_CAKE:
        case CARTOGRAPHY_TABLE:
        case CAULDRON:
        case CAVE_VINES:
        case CAVE_VINES_PLANT:
        case CHAIN_COMMAND_BLOCK:
        case CHERRY_BUTTON:
        case CHERRY_DOOR:
        case CHERRY_FENCE:
        case CHERRY_FENCE_GATE:
        case CHERRY_HANGING_SIGN:
        case CHERRY_SIGN:
        case CHERRY_TRAPDOOR:
        case CHERRY_WALL_HANGING_SIGN:
        case CHERRY_WALL_SIGN:
        case CHEST:
        case CHIPPED_ANVIL:
        case CHISELED_BOOKSHELF:
        case COMMAND_BLOCK:
        case COMPARATOR:
        case COMPOSTER:
        case COPPER_DOOR:
        case COPPER_TRAPDOOR:
        case CRAFTER:
        case CRAFTING_TABLE:
        case CRIMSON_BUTTON:
        case CRIMSON_DOOR:
        case CRIMSON_FENCE:
        case CRIMSON_FENCE_GATE:
        case CRIMSON_HANGING_SIGN:
        case CRIMSON_SIGN:
        case CRIMSON_TRAPDOOR:
        case CRIMSON_WALL_HANGING_SIGN:
        case CRIMSON_WALL_SIGN:
        case CYAN_BED:
        case CYAN_CANDLE:
        case CYAN_CANDLE_CAKE:
        case CYAN_SHULKER_BOX:
        case DAMAGED_ANVIL:
        case DARK_OAK_BUTTON:
        case DARK_OAK_DOOR:
        case DARK_OAK_FENCE:
        case DARK_OAK_FENCE_GATE:
        case DARK_OAK_HANGING_SIGN:
        case DARK_OAK_SIGN:
        case DARK_OAK_TRAPDOOR:
        case DARK_OAK_WALL_HANGING_SIGN:
        case DARK_OAK_WALL_SIGN:
        case DAYLIGHT_DETECTOR:
        case DECORATED_POT:
        case DEEPSLATE_REDSTONE_ORE:
        case DISPENSER:
        case DRAGON_EGG:
        case DROPPER:
        case ENCHANTING_TABLE:
        case ENDER_CHEST:
        case EXPOSED_COPPER_DOOR:
        case EXPOSED_COPPER_TRAPDOOR:
        case FLETCHING_TABLE:
        case FLOWER_POT:
        case FURNACE:
        case GRAY_BED:
        case GRAY_CANDLE:
        case GRAY_CANDLE_CAKE:
        case GRAY_SHULKER_BOX:
        case GREEN_BED:
        case GREEN_CANDLE:
        case GREEN_CANDLE_CAKE:
        case GREEN_SHULKER_BOX:
        case GRINDSTONE:
        case HOPPER:
        case IRON_DOOR:
        case IRON_TRAPDOOR:
        case JIGSAW:
        case JUKEBOX:
        case JUNGLE_BUTTON:
        case JUNGLE_DOOR:
        case JUNGLE_FENCE:
        case JUNGLE_FENCE_GATE:
        case JUNGLE_HANGING_SIGN:
        case JUNGLE_SIGN:
        case JUNGLE_TRAPDOOR:
        case JUNGLE_WALL_HANGING_SIGN:
        case JUNGLE_WALL_SIGN:
        case LAVA_CAULDRON:
        case LECTERN:
        case LEVER:
        case LIGHT:
        case LIGHT_BLUE_BED:
        case LIGHT_BLUE_CANDLE:
        case LIGHT_BLUE_CANDLE_CAKE:
        case LIGHT_BLUE_SHULKER_BOX:
        case LIGHT_GRAY_BED:
        case LIGHT_GRAY_CANDLE:
        case LIGHT_GRAY_CANDLE_CAKE:
        case LIGHT_GRAY_SHULKER_BOX:
        case LIME_BED:
        case LIME_CANDLE:
        case LIME_CANDLE_CAKE:
        case LIME_SHULKER_BOX:
        case LOOM:
        case MAGENTA_BED:
        case MAGENTA_CANDLE:
        case MAGENTA_CANDLE_CAKE:
        case MAGENTA_SHULKER_BOX:
        case MANGROVE_BUTTON:
        case MANGROVE_DOOR:
        case MANGROVE_FENCE:
        case MANGROVE_FENCE_GATE:
        case MANGROVE_HANGING_SIGN:
        case MANGROVE_SIGN:
        case MANGROVE_TRAPDOOR:
        case MANGROVE_WALL_HANGING_SIGN:
        case MANGROVE_WALL_SIGN:
        case MOVING_PISTON:
        case NETHER_BRICK_FENCE:
        case OAK_BUTTON:
        case OAK_DOOR:
        case OAK_FENCE:
        case OAK_FENCE_GATE:
        case OAK_HANGING_SIGN:
        case OAK_SIGN:
        case OAK_TRAPDOOR:
        case OAK_WALL_HANGING_SIGN:
        case OAK_WALL_SIGN:
        case ORANGE_BED:
        case ORANGE_CANDLE:
        case ORANGE_CANDLE_CAKE:
        case ORANGE_SHULKER_BOX:
        case OXIDIZED_COPPER_DOOR:
        case OXIDIZED_COPPER_TRAPDOOR:
        case PINK_BED:
        case PINK_CANDLE:
        case PINK_CANDLE_CAKE:
        case PINK_SHULKER_BOX:
        case POLISHED_BLACKSTONE_BUTTON:
        case POTTED_ACACIA_SAPLING:
        case POTTED_ALLIUM:
        case POTTED_AZALEA_BUSH:
        case POTTED_AZURE_BLUET:
        case POTTED_BAMBOO:
        case POTTED_BIRCH_SAPLING:
        case POTTED_BLUE_ORCHID:
        case POTTED_BROWN_MUSHROOM:
        case POTTED_CACTUS:
        case POTTED_CHERRY_SAPLING:
        case POTTED_CORNFLOWER:
        case POTTED_CRIMSON_FUNGUS:
        case POTTED_CRIMSON_ROOTS:
        case POTTED_DANDELION:
        case POTTED_DARK_OAK_SAPLING:
        case POTTED_DEAD_BUSH:
        case POTTED_FERN:
        case POTTED_FLOWERING_AZALEA_BUSH:
        case POTTED_JUNGLE_SAPLING:
        case POTTED_LILY_OF_THE_VALLEY:
        case POTTED_MANGROVE_PROPAGULE:
        case POTTED_OAK_SAPLING:
        case POTTED_ORANGE_TULIP:
        case POTTED_OXEYE_DAISY:
        case POTTED_PINK_TULIP:
        case POTTED_POPPY:
        case POTTED_RED_MUSHROOM:
        case POTTED_RED_TULIP:
        case POTTED_SPRUCE_SAPLING:
        case POTTED_TORCHFLOWER:
        case POTTED_WARPED_FUNGUS:
        case POTTED_WARPED_ROOTS:
        case POTTED_WHITE_TULIP:
        case POTTED_WITHER_ROSE:
        case POWDER_SNOW_CAULDRON:
        case PUMPKIN:
        case PURPLE_BED:
        case PURPLE_CANDLE:
        case PURPLE_CANDLE_CAKE:
        case PURPLE_SHULKER_BOX:
        case REDSTONE_ORE:
        case REDSTONE_WIRE:
        case RED_BED:
        case RED_CANDLE:
        case RED_CANDLE_CAKE:
        case RED_SHULKER_BOX:
        case REPEATER:
        case REPEATING_COMMAND_BLOCK:
        case RESPAWN_ANCHOR:
        case SHULKER_BOX:
        case SMITHING_TABLE:
        case SMOKER:
        case SOUL_CAMPFIRE:
        case SPRUCE_BUTTON:
        case SPRUCE_DOOR:
        case SPRUCE_FENCE:
        case SPRUCE_FENCE_GATE:
        case SPRUCE_HANGING_SIGN:
        case SPRUCE_SIGN:
        case SPRUCE_TRAPDOOR:
        case SPRUCE_WALL_HANGING_SIGN:
        case SPRUCE_WALL_SIGN:
        case STONECUTTER:
        case STONE_BUTTON:
        case STRUCTURE_BLOCK:
        case SWEET_BERRY_BUSH:
        case TNT:
        case TRAPPED_CHEST:
        case WARPED_BUTTON:
        case WARPED_DOOR:
        case WARPED_FENCE:
        case WARPED_FENCE_GATE:
        case WARPED_HANGING_SIGN:
        case WARPED_SIGN:
        case WARPED_TRAPDOOR:
        case WARPED_WALL_HANGING_SIGN:
        case WARPED_WALL_SIGN:
        case WATER_CAULDRON:
        case WAXED_COPPER_DOOR:
        case WAXED_COPPER_TRAPDOOR:
        case WAXED_EXPOSED_COPPER_DOOR:
        case WAXED_EXPOSED_COPPER_TRAPDOOR:
        case WAXED_OXIDIZED_COPPER_DOOR:
        case WAXED_OXIDIZED_COPPER_TRAPDOOR:
        case WAXED_WEATHERED_COPPER_DOOR:
        case WAXED_WEATHERED_COPPER_TRAPDOOR:
        case WEATHERED_COPPER_DOOR:
        case WEATHERED_COPPER_TRAPDOOR:
        case WHITE_BED:
        case WHITE_CANDLE:
        case WHITE_CANDLE_CAKE:
        case WHITE_SHULKER_BOX:
        case YELLOW_BED:
        case YELLOW_CANDLE:
        case YELLOW_CANDLE_CAKE:
        case YELLOW_SHULKER_BOX:
        case FARMLAND:
            return true;
        default:
            return false;
		}
	}
}
