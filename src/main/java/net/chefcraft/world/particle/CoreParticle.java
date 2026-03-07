package net.chefcraft.world.particle;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.registry.CoreRegistry;
import net.chefcraft.core.util.ObjectKey;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * @since 1.2.0
 */
public interface CoreParticle extends ObjectKey {
	
	public static final CoreRegistry<CoreParticle> REGISTRY 			= ChefCore.getReflector().getCoreParticleRegistry();
	
	public static final CoreParticle POOF 								= getByName("poof");
	public static final CoreParticle EXPLOSION 							= getByName("explosion");
	public static final CoreParticle EXPLOSION_EMITTER 					= getByName("explosion_emitter");
	public static final CoreParticle FIREWORK 							= getByName("firework");
	public static final CoreParticle BUBBLE 							= getByName("bubble");
	public static final CoreParticle SPLASH 							= getByName("splash");
	public static final CoreParticle FISHING 							= getByName("fishing");
	public static final CoreParticle UNDERWATER 						= getByName("underwater");
	public static final CoreParticle CRIT 								= getByName("crit");
	public static final CoreParticle ENCHANTED_HIT 						= getByName("enchanted_hit");
	public static final CoreParticle SMOKE 								= getByName("smoke");
	public static final CoreParticle LARGE_SMOKE 						= getByName("large_smoke");
	public static final CoreParticle EFFECT 							= getByName("effect");
	public static final CoreParticle INSTANT_EFFECT 					= getByName("instant_effect");
	public static final CoreParticle ENTITY_EFFECT 						= getByName("entity_effect");
	public static final CoreParticle WITCH 								= getByName("witch");
	public static final CoreParticle DRIPPING_WATER 					= getByName("dripping_water");
	public static final CoreParticle DRIPPING_LAVA 						= getByName("dripping_lava");
	public static final CoreParticle ANGRY_VILLAGER 					= getByName("angry_villager");
	public static final CoreParticle HAPPY_VILLAGER 					= getByName("happy_villager");
	public static final CoreParticle MYCELIUM 							= getByName("mycelium");
	public static final CoreParticle NOTE 								= getByName("note");
	public static final CoreParticle PORTAL 							= getByName("portal");
	public static final CoreParticle ENCHANT 							= getByName("enchant");
	public static final CoreParticle FLAME 								= getByName("flame");
	public static final CoreParticle LAVA 								= getByName("lava");
	public static final CoreParticle CLOUD 								= getByName("cloud");
	public static final CoreParticle DUST 								= getByName("dust");
	public static final CoreParticle ITEM_SNOWBALL 						= getByName("item_snowball");
	public static final CoreParticle ITEM_SLIME 						= getByName("item_slime");
	public static final CoreParticle HEART 								= getByName("heart");
	public static final CoreParticle ITEM 								= getByName("item");
	public static final CoreParticle BLOCK 								= getByName("block");
	public static final CoreParticle RAIN 								= getByName("rain");
	public static final CoreParticle ELDER_GUARDIAN 					= getByName("elder_guardian");
	public static final CoreParticle DRAGON_BREATH 						= getByName("dragon_breath");
	public static final CoreParticle END_ROD 							= getByName("end_rod");
	public static final CoreParticle DAMAGE_INDICATOR 					= getByName("damage_indicator");
	public static final CoreParticle SWEEP_ATTACK 						= getByName("sweep_attack");
	public static final CoreParticle FALLING_DUST 						= getByName("falling_dust");
	public static final CoreParticle TOTEM_OF_UNDYING 					= getByName("totem_of_undying");
	public static final CoreParticle SPIT 								= getByName("spit");
	public static final CoreParticle SQUID_INK 							= getByName("squid_ink");
	public static final CoreParticle BUBBLE_POP 						= getByName("bubble_pop");
	public static final CoreParticle CURRENT_DOWN 						= getByName("current_down");
	public static final CoreParticle BUBBLE_COLUMN_UP 					= getByName("bubble_column_up");
	public static final CoreParticle NAUTILUS 							= getByName("nautilus");
	public static final CoreParticle DOLPHIN 							= getByName("dolphin");
	public static final CoreParticle SNEEZE 							= getByName("sneeze");
	public static final CoreParticle CAMPFIRE_COSY_SMOKE 				= getByName("campfire_cosy_smoke");
	public static final CoreParticle CAMPFIRE_SIGNAL_SMOKE 				= getByName("campfire_signal_smoke");
	public static final CoreParticle COMPOSTER 							= getByName("composter");
	public static final CoreParticle FLASH 								= getByName("flash");
	public static final CoreParticle FALLING_LAVA 						= getByName("falling_lava");
	public static final CoreParticle LANDING_LAVA 						= getByName("landing_lava");
	public static final CoreParticle FALLING_WATER 						= getByName("falling_water");
	public static final CoreParticle DRIPPING_HONEY 					= getByName("dripping_honey");
	public static final CoreParticle FALLING_HONEY 						= getByName("falling_honey");
	public static final CoreParticle LANDING_HONEY 						= getByName("landing_honey");
	public static final CoreParticle FALLING_NECTAR 					= getByName("falling_nectar");
	public static final CoreParticle SOUL_FIRE_FLAME 					= getByName("soul_fire_flame");
	public static final CoreParticle ASH 								= getByName("ash");
	public static final CoreParticle CRIMSON_SPORE 						= getByName("crimson_spore");
	public static final CoreParticle WARPED_SPORE 						= getByName("warped_spore");
	public static final CoreParticle SOUL 								= getByName("soul");
	public static final CoreParticle DRIPPING_OBSIDIAN_TEAR 			= getByName("dripping_obsidian_tear");
	public static final CoreParticle FALLING_OBSIDIAN_TEAR 				= getByName("falling_obsidian_tear");
	public static final CoreParticle LANDING_OBSIDIAN_TEAR 				= getByName("landing_obsidian_tear");
	public static final CoreParticle REVERSE_PORTAL 					= getByName("reverse_portal");
	public static final CoreParticle WHITE_ASH 							= getByName("white_ash");
	public static final CoreParticle DUST_COLOR_TRANSITION 				= getByName("dust_color_transition");
	public static final CoreParticle VIBRATION 							= getByName("vibration");
	public static final CoreParticle FALLING_SPORE_BLOSSOM 				= getByName("falling_spore_blossom");
	public static final CoreParticle SPORE_BLOSSOM_AIR 					= getByName("spore_blossom_air");
	public static final CoreParticle SMALL_FLAME 						= getByName("small_flame");
	public static final CoreParticle SNOWFLAKE 							= getByName("snowflake");
	public static final CoreParticle DRIPPING_DRIPSTONE_LAVA 			= getByName("dripping_dripstone_lava");
	public static final CoreParticle FALLING_DRIPSTONE_LAVA 			= getByName("falling_dripstone_lava");
	public static final CoreParticle DRIPPING_DRIPSTONE_WATER 			= getByName("dripping_dripstone_water");
	public static final CoreParticle FALLING_DRIPSTONE_WATER 			= getByName("falling_dripstone_water");
	public static final CoreParticle GLOW_SQUID_INK 					= getByName("glow_squid_ink");
	public static final CoreParticle GLOW 								= getByName("glow");
	public static final CoreParticle WAX_ON 							= getByName("wax_on");
	public static final CoreParticle WAX_OFF 							= getByName("wax_off");
	public static final CoreParticle ELECTRIC_SPARK 					= getByName("electric_spark");
	public static final CoreParticle SCRAPE 							= getByName("scrape");
	public static final CoreParticle SONIC_BOOM 						= getByName("sonic_boom");
	public static final CoreParticle SCULK_SOUL 						= getByName("sculk_soul");
	public static final CoreParticle SCULK_CHARGE 						= getByName("sculk_charge");
	public static final CoreParticle SCULK_CHARGE_POP 					= getByName("sculk_charge_pop");
	public static final CoreParticle SHRIEK 							= getByName("shriek");
	public static final CoreParticle CHERRY_LEAVES 						= getByName("cherry_leaves");
	public static final CoreParticle EGG_CRACK 							= getByName("egg_crack");
	public static final CoreParticle DUST_PLUME 						= getByName("dust_plume");
	public static final CoreParticle WHITE_SMOKE 						= getByName("white_smoke");
	public static final CoreParticle SMALL_GUST 						= getByName("small_gust");
	public static final CoreParticle GUST 								= getByName("gust");
	public static final CoreParticle GUST_EMITTER_LARGE 				= getByName("gust_emitter_large");
	public static final CoreParticle GUST_EMITTER_SMALL 				= getByName("gust_emitter_small");
	public static final CoreParticle TRIAL_SPAWNER_DETECTION 			= getByName("trial_spawner_detection");
	public static final CoreParticle TRIAL_SPAWNER_DETECTION_OMINOUS 	= getByName("trial_spawner_detection_ominous");
	public static final CoreParticle VAULT_CONNECTION 					= getByName("vault_connection");
	public static final CoreParticle INFESTED 							= getByName("infested");
	public static final CoreParticle ITEM_COBWEB 						= getByName("item_cobweb");
	public static final CoreParticle DUST_PILLAR 						= getByName("dust_pillar");
	public static final CoreParticle BLOCK_CRUMBLE 						= getByName("block_crumble");
	public static final CoreParticle TRAIL 								= getByName("trail");
	public static final CoreParticle OMINOUS_SPAWNING 					= getByName("ominous_spawning");
	public static final CoreParticle RAID_OMEN 							= getByName("raid_omen");
	public static final CoreParticle TRIAL_OMEN 						= getByName("trial_omen");
	public static final CoreParticle BLOCK_MARKER 						= getByName("block_marker");
	
	//1.21.5
	public static final CoreParticle TINTED_LEAVES 						= getByName("tinted_leaves");
	public static final CoreParticle FIREFLY 							= getByName("firefly");
	
	//1.21.9
	public static final CoreParticle COPPER_FIRE_FLAME 					= getByName("copper_fire_flame");
	
	@Nullable
	public static CoreParticle getByName(@NotNull String name) {
		Objects.requireNonNull(name, "cannot get particle from null!");
		return REGISTRY.getByKey(name);
	}
	
	@NotNull
	String getName();
	
	@NotNull
	CoreParticleDataType getDataType();

	void spawnParticle(Player player, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable CoreParticleData data, double distanceSquared);
	
	void spawnParticle(World world, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable CoreParticleData data, double distanceSquared);
	
	@Override
	default String getKey() {
		return this.getName();
	}
	
	//With Location - start
	default void spawnParticle(Player player, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable CoreParticleData data, double distanceSquared) {
		this.spawnParticle(player, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra, data, distanceSquared);
	}
	
	default void spawnParticle(World world, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable CoreParticleData data, double distanceSquared) {
		this.spawnParticle(world, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra, data, distanceSquared);
	}
	//With Location - end
	
	//Without distance - start
	default void spawnParticle(Player player, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable CoreParticleData data) {
		this.spawnParticle(player, x, y, z, count, offsetX, offsetY, offsetZ, extra, data, 1024.0F);
	}
	
	default void spawnParticle(World world, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable CoreParticleData data) {
		this.spawnParticle(world, x, y, z, count, offsetX, offsetY, offsetZ, extra, data, 1024.0F);
	}
	
	default void spawnParticle(Player player, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable CoreParticleData data) {
		this.spawnParticle(player, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra, data, 1024.0F);
	}
	
	default void spawnParticle(World world, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable CoreParticleData data) {
		this.spawnParticle(world, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra, data, 1024.0F);
	}
	//Without distance - end
	
	//Without distance and data - start
	default void spawnParticle(Player player, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra) {
		this.spawnParticle(player, x, y, z, count, offsetX, offsetY, offsetZ, extra, null, 1024.0F);
	}
	
	default void spawnParticle(World world, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra) {
		this.spawnParticle(world, x, y, z, count, offsetX, offsetY, offsetZ, extra, null, 1024.0F);
	}
	
	default void spawnParticle(Player player, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra) {
		this.spawnParticle(player, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra, null, 1024.0F);
	}
	
	default void spawnParticle(World world, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra) {
		this.spawnParticle(world, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra, null, 1024.0F);
	}
	//Without distance and data - end
	
	//Without extra, distance and data - start
	default void spawnParticle(Player player, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ) {
		this.spawnParticle(player, x, y, z, count, offsetX, offsetY, offsetZ, 0, null, 1024.0F);
	}
	
	default void spawnParticle(World world, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ) {
		this.spawnParticle(world, x, y, z, count, offsetX, offsetY, offsetZ, 0, null, 1024.0F);
	}
	
	default void spawnParticle(Player player, Location location, int count, double offsetX, double offsetY, double offsetZ) {
		this.spawnParticle(player, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, 0, null, 1024.0F);
	}
	
	default void spawnParticle(World world, Location location, int count, double offsetX, double offsetY, double offsetZ) {
		this.spawnParticle(world, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, 0, null, 1024.0F);
	}
	//Without extra, distance and data - end
	
	//Without offsets, extra, distance and data - start
	default void spawnParticle(Player player, double x, double y, double z, int count) {
		this.spawnParticle(player, x, y, z, count, 0, 0, 0, 0, null, 1024.0F);
	}
	
	default void spawnParticle(World world, double x, double y, double z, int count) {
		this.spawnParticle(world, x, y, z, count, 0, 0, 0, 0, null, 1024.0F);
	}
	
	default void spawnParticle(Player player, Location location, int count) {
		this.spawnParticle(player, location.getX(), location.getY(), location.getZ(), count, 0, 0, 0, 0, null, 1024.0F);
	}
	
	default void spawnParticle(World world, Location location, int count) {
		this.spawnParticle(world, location.getX(), location.getY(), location.getZ(), count, 0, 0, 0, 0, null, 1024.0F);
	}
	//Without offsets, extra, distance and data - end
	
	//Without offsets, extra and distance - start
	default void spawnParticle(Player player, double x, double y, double z, int count, @Nullable CoreParticleData data) {
		this.spawnParticle(player, x, y, z, count, 0, 0, 0, 0, data, 1024.0F);
	}
	
	default void spawnParticle(World world, double x, double y, double z, int count, @Nullable CoreParticleData data) {
		this.spawnParticle(world, x, y, z, count, 0, 0, 0, 0, data, 1024.0F);
	}
	
	default void spawnParticle(Player player, Location location, int count, @Nullable CoreParticleData data) {
		this.spawnParticle(player, location.getX(), location.getY(), location.getZ(), count, 0, 0, 0, 0, data, 1024.0F);
	}
	
	default void spawnParticle(World world, Location location, int count, @Nullable CoreParticleData data) {
		this.spawnParticle(world, location.getX(), location.getY(), location.getZ(), count, 0, 0, 0, 0, data, 1024.0F);
	}
	//Without offsets, extra and distance - end
}
