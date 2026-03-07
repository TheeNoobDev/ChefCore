package net.chefcraft.reflector.version.v1_8_R3;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.math.SimpleMath;
import net.chefcraft.core.util.ObjectHelper;
import net.chefcraft.reflection.base.utils.ItemCooldownTracker;
import net.chefcraft.reflection.world.ArmorTrimObject;
import net.chefcraft.reflection.world.BlockSoundType;
import net.chefcraft.reflection.world.GameReflections;
import net.chefcraft.world.item.ItemColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.server.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.Block.StepSound;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_8_R3.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftItem;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftContainer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.map.CraftMapView;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapView;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.logging.Level;

public class GameReflector implements GameReflections {
	
	@Override
	public Item setUnlimitedLifetime(Item item) {
		ObjectHelper.setField(((CraftItem) item).getHandle(), "age", -32768);
		return item;
	}
	
	@Override /* just clears */
	public void setArrowsInBodyForLivingEntity(LivingEntity livingEntity, int count, boolean fireEvent) {
		((CraftLivingEntity) livingEntity).getHandle().getDataWatcher().watch(9, (byte) count <= 0 ? -1 : count);
	}
	
	@Override
	public org.bukkit.entity.ItemFrame createItemFrame(Location location, BlockFace facing, boolean glowing) {	
		WorldServer level = ((CraftWorld) location.getWorld()).getHandle();
		BlockPosition pos = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		EnumDirection direction = CraftBlock.blockFaceToNotch(facing);
		
		EntityItemFrame frame = new EntityItemFrame(level, pos, direction);
		frame.noclip = true;
		setEntitySilent(frame, true);
		setEntityInvulnerable(frame, true);
		
		level.addEntity(frame, SpawnReason.CUSTOM);
		
		return (org.bukkit.entity.ItemFrame) frame.getBukkitEntity();
	}
	
	@Override
	public BlockFace getBlockFaceFromLocation(Location location) {
		MaterialData state = location.getBlock().getState().getData();
		
		if (state instanceof Directional) {
			return ((Directional) state).getFacing();
		}

		return BlockFace.SELF;
	}
	
	@Override
	public boolean hasItemStackTag(ItemStack itemStack, String key) {
		net.minecraft.server.v1_8_R3.ItemStack x = CraftItemStack.asNMSCopy(itemStack);
		return x.hasTag() && x.getTag().hasKey(key);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getItemStackTagValue(Class<?> dataType, ItemStack itemStack, String key) {
		net.minecraft.server.v1_8_R3.ItemStack x = CraftItemStack.asNMSCopy(itemStack);
		if (x.hasTag() && x.getTag().hasKey(key)) { //hasTag //containsKey
			NBTTagCompound compound = x.getTag();
			
			if (String.class.isAssignableFrom(dataType)) {
				return (T) compound.getString(key);
				
			} else if (Integer.class.isAssignableFrom(dataType)) {
				return (T) ((Integer) compound.getInt(key));
				
			} else if (Boolean.class.isAssignableFrom(dataType)) {
				return (T) ((Boolean) compound.getBoolean(key));
				
			} else if (Float.class.isAssignableFrom(dataType)) {
				return (T) ((Float) compound.getFloat(key));
				
			} else if (Double.class.isAssignableFrom(dataType)) {
				return (T) ((Double) compound.getDouble(key));
				
			} else if (Byte.class.isAssignableFrom(dataType)) {
				return (T) ((Byte) compound.getByte(key));
				
			} else if (Short.class.isAssignableFrom(dataType)) {
				return (T) ((Short) compound.getShort(key));
				
			} else if (Long.class.isAssignableFrom(dataType)) {
				return (T) ((Long) compound.getLong(key));
				
			}
		}
		return null;
	}
	
	@Override
	public <T> ItemStack putItemStackTag(Class<?> dataType, ItemStack itemStack, String key, T value) {
		net.minecraft.server.v1_8_R3.ItemStack x = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound = x.hasTag() ? x.getTag() : new NBTTagCompound();
		
		if (String.class.isAssignableFrom(dataType)) {
			compound.setString(key, (String) value);
			return applyItemStackTagCompound(compound, x);
			
		} else if (Integer.class.isAssignableFrom(dataType)) {
			compound.setInt(key, (Integer) value);
			return applyItemStackTagCompound(compound, x);
			
		} else if (Boolean.class.isAssignableFrom(dataType)) {
			compound.setBoolean(key, (Boolean) value);
			return applyItemStackTagCompound(compound, x);
			
		} else if (Float.class.isAssignableFrom(dataType)) {
			compound.setFloat(key, (Float) value);
			return applyItemStackTagCompound(compound, x);
			
		} else if (Double.class.isAssignableFrom(dataType)) {
			compound.setDouble(key, (Double) value);
			return applyItemStackTagCompound(compound, x);
			
		} else if (Byte.class.isAssignableFrom(dataType)) {
			compound.setByte(key, (Byte) value);
			return applyItemStackTagCompound(compound, x);
			
		} else if (Short.class.isAssignableFrom(dataType)) {
			compound.setShort(key, (Short) value);
			return applyItemStackTagCompound(compound, x);
			
		} else if (Long.class.isAssignableFrom(dataType)) {
			compound.setLong(key, (Long) value);
			return applyItemStackTagCompound(compound, x);
			
		}
		
		throw new IllegalArgumentException("Data type can only be these: String, Integer, Boolean, Float, Double, Byte, Short, Long. Your data type is '" + dataType.getName() + "'!");
	}
	
	private ItemStack applyItemStackTagCompound(NBTTagCompound tag, net.minecraft.server.v1_8_R3.ItemStack x) {
		x.setTag(tag);
		return CraftItemStack.asBukkitCopy(x);
	}
	
	@Override
	public void setNoClip(Entity entity, boolean noClip) {
		((CraftEntity) entity).getHandle().noclip = noClip;
	}
	
	@Override
	public String getItemTranslationKey(ItemStack itemStack) {
		net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(itemStack);
		return LocaleI18n.get(nms.getItem().j(nms) + ".name").trim();
	}
	
	@Override
	public float getAbsorptionAmount(Player player) {
		return ((CraftPlayer) player).getHandle().getAbsorptionHearts();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public org.bukkit.block.Block setBlockType(org.bukkit.block.Block block, Material material, @Deprecated int typeId) {
		if (material != Material.AIR && material.isBlock()) {
			block.setTypeIdAndData(material.getId(), (byte) typeId, true);
		}
		return block;
	}
	
	@Override
	public Sound getBlockSound(org.bukkit.block.Block block, BlockSoundType type) {
		Block bblock = CraftMagicNumbers.getBlock(block);
		try {
			return Sound.valueOf(this.matchSoundType(bblock.stepSound, type).replace(".", "_").toUpperCase(Locale.ENGLISH));
		} catch (Exception x) {
			ChefCore.log(Level.WARNING, "getBlockSound() error from reflection! returning sound stone:" + x.getMessage());
			return Sound.STEP_STONE;
		}
	}
	
	@Override
	public void playSoundFromBlock(org.bukkit.block.Block block, BlockSoundType type) {
		Location loc = block.getLocation();
		StepSound sound = CraftMagicNumbers.getBlock(block).stepSound;
		
		((CraftWorld) loc.getWorld()).getHandle().makeSound(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), this.matchSoundType(sound, type), sound.getVolume1(), sound.getVolume2());
	}
	
	private String matchSoundType(StepSound group, BlockSoundType type) {
		switch (type) {
		case BREAK:
			return group.getBreakSound();
		case PLACE:
			return group.getPlaceSound();
		default:
			return group.getStepSound();
		}
	}
	
	@Override
	public ArmorTrimObject createArmorTrimObject(String trimPattern, String trimMaterial) {
		return armor -> armor;
	}
	
	@Override
	public boolean placeBlockAsPlayer(Player player, Location location, BlockFace blockFace, ItemStack itemStack, boolean mainHand) {
		EntityPlayer entityPlayer = MinecraftServer.getServer().getPlayerList().a(player.getUniqueId());
		if (entityPlayer != null) {
			SimpleMath.addPositionOffsetForBlockFace(location, blockFace);
			
			net.minecraft.server.v1_8_R3.ItemStack item = CraftItemStack.asNMSCopy(itemStack);
			BlockPosition pos = new BlockPosition(location.getX(), location.getY(), location.getZ());
			
			boolean result = item.placeItem(entityPlayer, entityPlayer.world, pos, CraftBlock.blockFaceToNotch(blockFace), 0, 0, 0);
			
			if (result && item.getItem() instanceof ItemBlock) {
				
				Block.StepSound sound = ((ItemBlock) item.getItem()).d().stepSound;
				entityPlayer.world.makeSound(pos.getX(), pos.getY(), pos.getZ(), sound.getPlaceSound(), sound.getVolume1(), sound.getVolume2());
				this.removeItemFromPlayerHand(player, itemStack.getType(), 1, false);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void damageOutOfWorld(Player player, float damage) {
		
		EntityDamageEvent event = player.getLastDamageCause();	
		
		EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
		entityPlayer.damageEntity(DamageSource.OUT_OF_WORLD, damage);
		
		if (event instanceof EntityDamageByEntityEvent) {
			Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
			
			if (damager instanceof Player) {
				Player damagerPlayer = (Player) damager;
				
				entityPlayer.killer = ((CraftPlayer) damagerPlayer).getHandle();
			}
		}
	}

	@Override
	public TNTPrimed spawnPrimedTNT(JavaPlugin plugin, Player source, Location location) {
		WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
		EntityTNTPrimed tnt = new EntityTNTPrimed(world, location.getBlockX() + 0.5D, location.getBlockY() + 0.0D, location.getBlockZ() + 0.5D, (EntityLiving) (((CraftLivingEntity) source).getHandle()));
		tnt.forceExplosionKnockback = true;
		tnt.motX = 0.0D;
		tnt.motY = 0.0D;
		tnt.motZ = 0.0D;
		tnt.velocityChanged = true;
		world.addEntity(tnt, SpawnReason.CUSTOM);
		return (TNTPrimed) tnt.getBukkitEntity();
	}

	@Override
	public PotionMeta addCustomPotionEffect(PotionMeta meta, PotionEffectType type, int duration, int amplifier, boolean ambient, boolean particles, Color color) {
		if (type != null) {
			meta.clearCustomEffects();
			meta.addCustomEffect(new PotionEffect(type, duration, amplifier, ambient, particles), true);
			meta.setMainEffect(type);
		}
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
		return typeName.contains("STAINED_CLAY") || typeName.contains("STAINED_GLASS")
				|| typeName.contains("STAINED_GLASS_PANE") || typeName.contains("WOOL");
	}
	
	@Override
	public Material colorizeMaterial(Material material, ItemColor type) {
		return material;
	}

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
			if (typeName.contains("STAINED_GLASS") 
					|| typeName.contains("STAINED_GLASS_PANE") 
					|| typeName.contains("WOOL")
					|| typeName.contains("STAINED_CLAY")) {
				
				item.setDurability(type.getLegacyData());
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
			return true;
		default:
			return false;
		}
	}
	
	@Override
	public boolean potionHasPositiveEffects(PotionEffectType type) {
		String key = type.getName().toLowerCase(Locale.ENGLISH);
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
		case GOLD_BOOTS:
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

	@Override
	public PotionEffect createPotionEffect(PotionEffectType type, int duration, int amplifier, boolean ambient, boolean particles, Color color) {
		return new PotionEffect(type, duration, amplifier, ambient, particles);
	}

	@Override
	public void kickPlayer(Player player, String reason) {
		player.kickPlayer(reason);
	}

	@Override
	public void setItemToFrame(ItemFrame itemFrame, ItemStack itemStack) {
		itemFrame.setItem(itemStack);
	}

	@SuppressWarnings("deprecation")
	@Override
	public MapView getMapView(int mapId) {
		return Bukkit.getMap((short) mapId);
	}

	@SuppressWarnings("deprecation")
	@Override
	public ItemStack applyMapMetaToItemStack(MapView mapView, ItemStack itemStack) {
		itemStack.setDurability(mapView.getId());
		return itemStack;
	}

	@Override
	public boolean isItemStackEmpty(ItemStack item) {
		return item == null || item.getType() == Material.AIR;
	}

	@Override
	public boolean hasWorldImmediateRespawn(org.bukkit.World world) {
		return false;
	}

	@Override
	public void setPlayerRespawnLocation(Player player, Location location) {
		player.setBedSpawnLocation(location, true);
	}

	@Override
	public void sendComponents(Player player, BaseComponent... components) {
		player.spigot().sendMessage(components);
	}

	@Override
	public void playOpenOrCloseAnimation(org.bukkit.block.Block block, boolean open) {
		Location location = block.getLocation();
		
		World world = ((CraftWorld) location.getWorld()).getHandle();
		BlockPosition position = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		TileEntity tileEntity = world.getTileEntity(position);
		world.playBlockAction(position, tileEntity.w(), 1, open ? 1 : 0);
	}

	@Override
	public boolean potionHasPositiveEffects(ThrownPotion thrownPotion) {
		PotionEffectType type = PotionEffectType.ABSORPTION;
		for (PotionEffect x : thrownPotion.getEffects()) {
			type = x.getType();
			break;
		}
		return this.potionHasPositiveEffects(type);
	}

    @Override
    public void updateOpenedInventoryTitle(Player player, MessageHolder title) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        Container container = entityPlayer.activeContainer;
        InventoryView view = player.getOpenInventory();
        if (container == null || view == null) return;
        Inventory top = view.getTopInventory();
        if (top == null) return;

        PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(container.windowId, CraftContainer.getNotchInventoryType(top.getType()), new ChatComponentText(title.asString(true)), top.getSize());
        entityPlayer.playerConnection.sendPacket(packet);
        entityPlayer.updateInventory(container);
    }

	@Override
	public void onItemConsumeEventReplaceLeftoversToAir(PlayerItemConsumeEvent playerItemConsumeEvent) {
		Material type = playerItemConsumeEvent.getItem().getType();
		
		if (type == Material.POTION || type == Material.MILK_BUCKET || type == Material.MUSHROOM_SOUP) {
			Material leftOver = this.matchLeftoverForConsumeEvent(type);
			Bukkit.getScheduler().runTaskLater(ChefCore.getInstance(), ()-> {
				if (this.removeItemFromPlayerHand(playerItemConsumeEvent.getPlayer(), leftOver, 1, false)) {
					this.removeItemFromPlayerHand(playerItemConsumeEvent.getPlayer(), leftOver, 1, true);
				}
			}, 1L);
		}
	}
	
	private Material matchLeftoverForConsumeEvent(Material source) {
		switch (source) {
		case POTION:
			return Material.GLASS_BOTTLE;
		case MILK_BUCKET:
			return Material.BUCKET;
		case MUSHROOM_SOUP:
			return Material.BOWL;
		default:
			return Material.AIR;
		}
	}

	@Override
	public void onProjectileHitSetCancelledEvent(ProjectileHitEvent projectileHitEvent) {
		
	}

	@SuppressWarnings("deprecation")
	@Override
	public org.bukkit.enchantments.Enchantment getEnchantmentByVanillaName(String vanillaName) {
		Enchantment ench = Enchantment.getByName(vanillaName);
		if (ench == null) {
			return org.bukkit.enchantments.Enchantment.LUCK;
		}
		return CraftEnchantment.getById(ench.id);
	}

	@Override
	public PotionEffectType getPotionEffectTypeByVanillaName(String vanillaName) {
		switch (vanillaName.toLowerCase(Locale.ENGLISH)) {
		case "speed":
			return PotionEffectType.SPEED;
		case "slowness":
			return PotionEffectType.SLOW;
		case "haste":
			return PotionEffectType.FAST_DIGGING;
		case "mining_fatigue":
			return PotionEffectType.SLOW_DIGGING;
		case "strength":
			return PotionEffectType.INCREASE_DAMAGE;
		case "instant_health":
			return PotionEffectType.HEAL;
		case "instant_damage":
			return PotionEffectType.HARM;
		case "jump_boost":
			return PotionEffectType.JUMP;
		case "nausea":
			return PotionEffectType.CONFUSION;
		case "regeneration":
			return PotionEffectType.REGENERATION;
		case "resistance":
			return PotionEffectType.DAMAGE_RESISTANCE;
		case "fire_resistance":
			return PotionEffectType.FIRE_RESISTANCE;
		case "water_breathing":
			return PotionEffectType.WATER_BREATHING;
		case "invisibility":
			return PotionEffectType.INVISIBILITY;
		case "blindness":
			return PotionEffectType.BLINDNESS;
		case "night_vision":
			return PotionEffectType.NIGHT_VISION;
		case "hunger":
			return PotionEffectType.HUNGER;
		case "weakness":
			return PotionEffectType.WEAKNESS;
		case "poison":
			return PotionEffectType.POISON;
		case "wither":
			return PotionEffectType.WITHER;
		case "health_boost":
			return PotionEffectType.HEALTH_BOOST;
		case "absorption":
			return PotionEffectType.ABSORPTION;
		case "saturation":
			return PotionEffectType.SATURATION;
		}
		return PotionEffectType.SATURATION;
	}

	@Override
	public PotionMeta setBasePotionType(PotionMeta meta, PotionType type) {
		if (type != null) {
			meta.setMainEffect(type.getEffectType());
		}
		return meta;
	}

	@Override
	public Fireball setVisualFireForFireball(Fireball fireball, boolean visualFire) {
		return fireball;
	}
	
	@Override
	public <T> void setGameRule(org.bukkit.World world, String key, T value) {
		world.setGameRuleValue(key, String.valueOf(value));
	}

	@Override
	public ItemStack matchItemStackFromPlayerHands(Player player, Material type) {
		PlayerInventory inventory = player.getInventory();
		ItemStack itemStack = inventory.getItemInHand();
		if (!this.isItemStackEmpty(itemStack) && itemStack.getType() == type) {
			return itemStack;
		}
		return null;
	}

	@Override
	public boolean playerCanAfford(Player player, Material material, int amount) {
		return player.getInventory().contains(material, amount);
	}

	@Override
	public boolean removeItemFromPlayerHand(Player player, Material material, int amount, boolean offHand) {
		PlayerInventory playerinventory = player.getInventory();
		if (material == null || material == Material.AIR || amount < 1) return false;
		ItemStack itemstack = playerinventory.getItemInHand();
		if (itemstack.getType() == material) {
			if (itemstack.getAmount() <= 1) {
                itemstack.setType(Material.AIR);
                playerinventory.setItemInHand(itemstack);
            } else {
            	itemstack.setAmount(itemstack.getAmount() - 1);
            }
			player.updateInventory();
			return true;
		}
		return false;
	}
	
	@Override
	public String getEnchantmentVanillaName(org.bukkit.enchantments.Enchantment enchantment) {
		return CraftEnchantment.getRaw(enchantment).a().split("\\.")[1];
	}

	@Override
	public void hidePlayer(Player from, JavaPlugin plugin, Player to) {
		from.hidePlayer(to);
	}

	@Override
	public void showPlayer(Player from, JavaPlugin plugin, Player to) {
		from.showPlayer(to);
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getMapViewId(MapView view) {
		return view.getId();
	}

	@Override
	public void setSkullOwner(Player player, ItemStack skullItem) {
		if (skullItem.getType() == Material.SKULL_ITEM) {
			SkullMeta meta = (SkullMeta) skullItem.getItemMeta();
			meta.setOwner(player.getName());
			skullItem.setItemMeta(meta);
		}
	}

	@Override
	public ItemStack getItemFromPlayerHand(Player player, boolean offHand) {
		return player.getInventory().getItemInHand();
	}
	
	@Override
	public double getMaxHealth(Player player) {
		return player.getMaxHealth();
	}

	@Override
	public void setMaxHealth(Player player, double max) {
		player.setMaxHealth(max);
	}
	
	@Override
	public boolean hasCooldown(Player player, Material material) {
		return ItemCooldownTracker.hasCooldown(player, material);
	}
	
	@Override
	public int getCooldown(Player player, Material material) {
		return ItemCooldownTracker.getCooldown(player, material);
	}
	
	@Override
	public void setCooldown(Player player, Material material, int ticks) {
		ItemCooldownTracker.setCooldown(player, material, ticks);
	}
	
	@Override
	public void handleBlockBreakEventDropItems(BlockBreakEvent event, boolean dropItems) {
		event.getBlock().getDrops().clear();
	}
	
	@Nullable
	@Override
	public Entity getProjectileHitEventHitEntity(ProjectileHitEvent event) {
		World world = ((CraftEntity) event.getEntity()).getHandle().getWorld();
		net.minecraft.server.v1_8_R3.Entity result = null;
		Location loc = event.getEntity().getLocation();
		
		for (net.minecraft.server.v1_8_R3.Entity entity : world.entityList) {
			if (SimpleMath.distanceSquared(loc, entity.locX, entity.locY, entity.locZ) < 2.5D && entity.getBoundingBox().a(new Vec3D(entity.locX, entity.locY, entity.locZ))) {
				result = entity;
				break;
			}
		}
		return result != null ? result.getBukkitEntity() : null;
	}
	
	@Override
	public boolean playerInteractEventCheckPlayerHand(PlayerInteractEvent event, boolean mainHand) {
		return true;
	}

	@Override
	public ItemStack[] playerInventoryGetExtraContents(Player player) {
		return null;
	}

	@Override
	public ItemStack[] inventoryGetStorageContents(Inventory inventory) {
		return inventory.getContents();
	}

	@Override
	public void playerInventorySetExtraContents(Player player, ItemStack... itemStacks) { }

	@Override
	public void potionMetaSetColorAndMainEffectLegacy(PotionMeta meta, Color color, PotionEffectType type) {
		meta.setMainEffect(type);
	}

	@Override
	public void setEntitySilent(Entity entity, boolean silent) {
		setEntitySilent(((CraftEntity) entity).getHandle(), silent);
	}

	@Override
	public void setEntityGravity(Entity entity, boolean gravity) {
		setEntityGravity(((CraftEntity) entity).getHandle(), gravity);
	}
	
	public static void setEntityGravity(net.minecraft.server.v1_8_R3.Entity entity, boolean flag) {
		entity.noclip = !flag;
	}
	
	public static void setEntitySilent(net.minecraft.server.v1_8_R3.Entity entity, boolean flag) {
		entity.b(true); //setSlient
    }
	
	public static void setEntityInsentientNoAI(net.minecraft.server.v1_8_R3.EntityInsentient entityInsentient, boolean flag) {
		entityInsentient.k(flag); //setNoAI
    }
	
	public static void setEntityInvulnerable(net.minecraft.server.v1_8_R3.Entity entity, boolean flag) {
		entity.getDataWatcher().watch(10, flag ? (byte) 1 : 0); //setInvulnerable
    }
	
	@Override
	public boolean isInteractableIngame(Material material) {
		switch (material) {
        case SIGN:
        case ANVIL:
        case BEACON:
        case BED:
        case BREWING_STAND:
        case CAULDRON:
        case COMMAND:
        case COMMAND_MINECART:
        case REDSTONE_COMPARATOR:
        case REDSTONE_COMPARATOR_ON:
        case REDSTONE_COMPARATOR_OFF:
        case WORKBENCH:
        case DAYLIGHT_DETECTOR:
        case DISPENSER:
        case DROPPER:
        case ENCHANTMENT_TABLE:
        case FLOWER_POT:
        case FURNACE:
        case HOPPER:
        case JUKEBOX:
        case PISTON_MOVING_PIECE:
        case NETHER_FENCE:
        case PUMPKIN:
        case REDSTONE_ORE:
        case REDSTONE_WIRE:
        case SOIL:
            return true;
        default:
        	return false;
		}
	}

	@Override
	public boolean isInteractable(Material material) {
		switch (material) {
		case SIGN:
        case ANVIL:
        case BEACON:
        case BED:
        case BREWING_STAND:
        case CAULDRON:
        case COMMAND:
        case COMMAND_MINECART:
        case REDSTONE_COMPARATOR:
        case REDSTONE_COMPARATOR_ON:
        case REDSTONE_COMPARATOR_OFF:
        case WORKBENCH:
        case DAYLIGHT_DETECTOR:
        case DISPENSER:
        case DROPPER:
        case ENCHANTMENT_TABLE:
        case FLOWER_POT:
        case FURNACE:
        case HOPPER:
        case JUKEBOX:
        case PISTON_MOVING_PIECE:
        case NETHER_FENCE:
        case PUMPKIN:
        case REDSTONE_ORE:
        case REDSTONE_WIRE:
        case SOIL:
        case ACACIA_DOOR:
        case ACACIA_FENCE:
        case ACACIA_FENCE_GATE:
        case BIRCH_DOOR:
        case BIRCH_FENCE:
        case BIRCH_FENCE_GATE:
        case CAKE:
        case CHEST:
        case DARK_OAK_DOOR:
        case DARK_OAK_FENCE:
        case DARK_OAK_FENCE_GATE:
        case DRAGON_EGG:
        case ENDER_CHEST:
        case IRON_DOOR:
        case IRON_TRAPDOOR:
        case JUNGLE_DOOR:
        case JUNGLE_FENCE:
        case JUNGLE_FENCE_GATE:
        case LEVER:
        case SPRUCE_DOOR:
        case SPRUCE_FENCE:
        case SPRUCE_FENCE_GATE:
        case STONE_BUTTON:
        case TNT:
        case TRAPPED_CHEST:
            return true;
        default:
            return false;
		}
	}
}