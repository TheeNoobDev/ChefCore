package net.chefcraft.reflection.v1_21_R7;

import net.chefcraft.core.math.SimpleMath;
import net.chefcraft.core.registry.CoreRegistry;
import net.chefcraft.world.particle.CoreParticle;
import net.chefcraft.world.particle.CoreParticleData;
import net.chefcraft.world.particle.CoreParticleDataType;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftParticle;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ChefParticleBase implements CoreParticle {
	
	public static final CoreRegistry<CoreParticle> REGISTRY = new CoreRegistry<>(
			ChefParticleBase.create(Particle.POOF, "poof"),
		 	ChefParticleBase.create(Particle.EXPLOSION, "explosion"),
		 	ChefParticleBase.create(Particle.EXPLOSION_EMITTER, "explosion_emitter"),
		 	ChefParticleBase.create(Particle.FIREWORK, "firework"),
		 	ChefParticleBase.create(Particle.BUBBLE, "bubble"),
		 	ChefParticleBase.create(Particle.SPLASH, "splash"),
		 	ChefParticleBase.create(Particle.FISHING, "fishing"),
		 	ChefParticleBase.create(Particle.UNDERWATER, "underwater"),
		 	ChefParticleBase.create(Particle.CRIT, "crit"),
		 	ChefParticleBase.create(Particle.ENCHANTED_HIT, "enchanted_hit"),
		 	ChefParticleBase.create(Particle.SMOKE, "smoke"),
		 	ChefParticleBase.create(Particle.LARGE_SMOKE, "large_smoke"),
		 	ChefParticleBase.create(Particle.EFFECT, "effect"),
		 	ChefParticleBase.create(Particle.INSTANT_EFFECT, "instant_effect"),
		 	ChefParticleBase.create(Particle.ENTITY_EFFECT, "ambient_entity_effect"),
		 	ChefParticleBase.create(Particle.ENTITY_EFFECT, "entity_effect", CoreParticleDataType.COLOR),
		 	ChefParticleBase.create(Particle.WITCH, "witch"),
		 	ChefParticleBase.create(Particle.DRIPPING_WATER, "dripping_water"),
		 	ChefParticleBase.create(Particle.DRIPPING_LAVA, "dripping_lava"),
		 	ChefParticleBase.create(Particle.ANGRY_VILLAGER, "angry_villager"),
		 	ChefParticleBase.create(Particle.HAPPY_VILLAGER, "happy_villager"),
		 	ChefParticleBase.create(Particle.MYCELIUM, "mycelium"),
		 	ChefParticleBase.create(Particle.NOTE, "note"),
		 	ChefParticleBase.create(Particle.PORTAL, "portal"),
		 	ChefParticleBase.create(Particle.ENCHANT, "enchant"),
		 	ChefParticleBase.create(Particle.FLAME, "flame"),
		 	ChefParticleBase.create(Particle.LAVA, "lava"),
		 	ChefParticleBase.create(Particle.CLOUD, "cloud"),
		 	ChefParticleBase.create(Particle.DUST, "dust", CoreParticleDataType.DUST_OPTIONS), // DustOptions.class
		 	ChefParticleBase.create(Particle.ITEM_SNOWBALL, "item_snowball"),
		 	ChefParticleBase.create(Particle.ITEM_SLIME, "item_slime"),
		 	ChefParticleBase.create(Particle.HEART, "heart"),
		 	ChefParticleBase.create(Particle.ITEM, "item", CoreParticleDataType.ITEM_STACK), //ItemStack.class
		 	ChefParticleBase.create(Particle.BLOCK, "block", CoreParticleDataType.BLOCK_DATA), //BlockData.class
		 	ChefParticleBase.create(Particle.RAIN, "rain"),
		 	ChefParticleBase.create(Particle.ELDER_GUARDIAN, "elder_guardian"),
		 	ChefParticleBase.create(Particle.DRAGON_BREATH, "dragon_breath"),
		 	ChefParticleBase.create(Particle.END_ROD, "end_rod"),
		 	ChefParticleBase.create(Particle.DAMAGE_INDICATOR, "damage_indicator"),
		 	ChefParticleBase.create(Particle.SWEEP_ATTACK, "sweep_attack"),
		 	ChefParticleBase.create(Particle.FALLING_DUST, "falling_dust", CoreParticleDataType.BLOCK_DATA), //BlockData.class
		 	ChefParticleBase.create(Particle.TOTEM_OF_UNDYING, "totem_of_undying"),
		 	ChefParticleBase.create(Particle.SPIT, "spit"),
		 	ChefParticleBase.create(Particle.SQUID_INK, "squid_ink"),
		 	ChefParticleBase.create(Particle.BUBBLE_POP, "bubble_pop"),
		 	ChefParticleBase.create(Particle.CURRENT_DOWN, "current_down"),
		 	ChefParticleBase.create(Particle.BUBBLE_COLUMN_UP, "bubble_column_up"),
		 	ChefParticleBase.create(Particle.NAUTILUS, "nautilus"),
		 	ChefParticleBase.create(Particle.DOLPHIN, "dolphin"),
		 	ChefParticleBase.create(Particle.SNEEZE, "sneeze"),
		 	ChefParticleBase.create(Particle.CAMPFIRE_COSY_SMOKE, "campfire_cosy_smoke"),
		 	ChefParticleBase.create(Particle.CAMPFIRE_SIGNAL_SMOKE, "campfire_signal_smoke"),
		 	ChefParticleBase.create(Particle.COMPOSTER, "composter"),
		 	ChefParticleBase.create(Particle.FLASH, "flash"),
		 	ChefParticleBase.create(Particle.FALLING_LAVA, "falling_lava"),
		 	ChefParticleBase.create(Particle.LANDING_LAVA, "landing_lava"),
		 	ChefParticleBase.create(Particle.FALLING_WATER, "falling_water"),
		 	ChefParticleBase.create(Particle.DRIPPING_HONEY, "dripping_honey"),
		 	ChefParticleBase.create(Particle.FALLING_HONEY, "falling_honey"),
		 	ChefParticleBase.create(Particle.LANDING_HONEY, "landing_honey"),
		 	ChefParticleBase.create(Particle.FALLING_NECTAR, "falling_nectar"),
		 	ChefParticleBase.create(Particle.SOUL_FIRE_FLAME, "soul_fire_flame"),
		 	ChefParticleBase.create(Particle.ASH, "ash"),
		 	ChefParticleBase.create(Particle.CRIMSON_SPORE, "crimson_spore"),
		 	ChefParticleBase.create(Particle.WARPED_SPORE, "warped_spore"),
		 	ChefParticleBase.create(Particle.SOUL, "soul"),
		 	ChefParticleBase.create(Particle.DRIPPING_OBSIDIAN_TEAR, "dripping_obsidian_tear"),
		 	ChefParticleBase.create(Particle.FALLING_OBSIDIAN_TEAR, "falling_obsidian_tear"),
		 	ChefParticleBase.create(Particle.LANDING_OBSIDIAN_TEAR, "landing_obsidian_tear"),
		 	ChefParticleBase.create(Particle.REVERSE_PORTAL, "reverse_portal"),
		 	ChefParticleBase.create(Particle.WHITE_ASH, "white_ash"),
		 	ChefParticleBase.create(Particle.DUST_COLOR_TRANSITION, "dust_color_transition", CoreParticleDataType.DUST_TRANSITION), //DustTransition.class
		 	ChefParticleBase.create(Particle.VIBRATION, "vibration", CoreParticleDataType.VIBRATION),
		 	ChefParticleBase.create(Particle.FALLING_SPORE_BLOSSOM, "falling_spore_blossom"),
		 	ChefParticleBase.create(Particle.SPORE_BLOSSOM_AIR, "spore_blossom_air"),
		 	ChefParticleBase.create(Particle.SMALL_FLAME, "small_flame"),
		 	ChefParticleBase.create(Particle.SNOWFLAKE, "snowflake"),
		 	ChefParticleBase.create(Particle.DRIPPING_DRIPSTONE_LAVA, "dripping_dripstone_lava"),
		 	ChefParticleBase.create(Particle.FALLING_DRIPSTONE_LAVA, "falling_dripstone_lava"),
		 	ChefParticleBase.create(Particle.DRIPPING_DRIPSTONE_WATER, "dripping_dripstone_water"),
		 	ChefParticleBase.create(Particle.FALLING_DRIPSTONE_WATER, "falling_dripstone_water"),
		 	ChefParticleBase.create(Particle.GLOW_SQUID_INK, "glow_squid_ink"),
		 	ChefParticleBase.create(Particle.GLOW, "glow"),
		 	ChefParticleBase.create(Particle.WAX_ON, "wax_on"),
		 	ChefParticleBase.create(Particle.WAX_OFF, "wax_off"),
		 	ChefParticleBase.create(Particle.ELECTRIC_SPARK, "electric_spark"),
		 	ChefParticleBase.create(Particle.SCRAPE, "scrape"),
		 	ChefParticleBase.create(Particle.SONIC_BOOM, "sonic_boom"),
		 	ChefParticleBase.create(Particle.SCULK_SOUL, "sculk_soul"),
		 	ChefParticleBase.create(Particle.SCULK_CHARGE, "sculk_charge", CoreParticleDataType.FLOAT), //Float.class
		 	ChefParticleBase.create(Particle.SCULK_CHARGE_POP, "sculk_charge_pop"),
		 	ChefParticleBase.create(Particle.SHRIEK, "shriek", CoreParticleDataType.INTEGER), //Integer.class
		 	ChefParticleBase.create(Particle.CHERRY_LEAVES, "cherry_leaves"),
		 	ChefParticleBase.create(Particle.EGG_CRACK, "egg_crack"),
		 	ChefParticleBase.create(Particle.DUST_PLUME, "dust_plume"),
		 	ChefParticleBase.create(Particle.WHITE_SMOKE, "white_smoke"),
		 	ChefParticleBase.create(Particle.BLOCK_MARKER, "block_marker", CoreParticleDataType.BLOCK_DATA), //BlockData.class
		 	
		 	//1.21 -> 1.21.3
		 	ChefParticleBase.create(Particle.SMALL_GUST, "small_gust"),
		 	ChefParticleBase.create(Particle.GUST, "gust"),
		 	ChefParticleBase.create(Particle.GUST_EMITTER_SMALL, "gust_emitter_small"),
		 	ChefParticleBase.create(Particle.GUST_EMITTER_LARGE, "gust_emitter_large"),
		 	ChefParticleBase.create(Particle.TRIAL_SPAWNER_DETECTION, "trial_spawner_detection"),
		 	ChefParticleBase.create(Particle.TRIAL_SPAWNER_DETECTION_OMINOUS, "trial_spawner_detection_ominous"), 
		 	ChefParticleBase.create(Particle.VAULT_CONNECTION, "vault_connection"), 
		 	ChefParticleBase.create(Particle.INFESTED, "infested"), 
		 	ChefParticleBase.create(Particle.ITEM_COBWEB, "item_cobweb"), 
		 	ChefParticleBase.create(Particle.DUST_PILLAR, "dust_pillar", CoreParticleDataType.BLOCK_DATA),  //BlockData.class
		 	ChefParticleBase.create(Particle.BLOCK_CRUMBLE, "block_crumble", CoreParticleDataType.BLOCK_DATA), //BlockData.class
		 	ChefParticleBase.create(Particle.OMINOUS_SPAWNING, "ominous_spawning"),
		 	ChefParticleBase.create(Particle.RAID_OMEN, "raid_omen"), 
		 	ChefParticleBase.create(Particle.TRIAL_OMEN, "trial_omen"),
		 	
		 	//1.21.4
		 	ChefParticleBase.create(Particle.PALE_OAK_LEAVES, "pale_oak_leaves"),
		 	ChefParticleBase.create(Particle.TRAIL, "trail", CoreParticleDataType.TRAIL), //Trail.class
	
		 	//1.21.5
		 	ChefParticleBase.create(Particle.TINTED_LEAVES, "tinted_leaves", CoreParticleDataType.COLOR),
		 	ChefParticleBase.create(Particle.FIREFLY, "firefly"),
	
		 	//1.21.9
		 	ChefParticleBase.create(Particle.COPPER_FIRE_FLAME, "copper_fire_flame")
	);
	
	protected final Particle particle;
	protected final String name;
	protected final CoreParticleDataType dataType;
	
	private ChefParticleBase(@NotNull Particle particle, @NotNull String name, @NotNull CoreParticleDataType dataType) {
		this.particle = particle;
		this.name = name;
		this.dataType = dataType;
	}
	
	public @NotNull Particle getParticle() {
		return this.particle;
	}
	
	@Override
	public @NotNull String key() {
		return this.name;
	}
	
	@Override
	public @NotNull CoreParticleDataType getDataType() {
		return this.dataType;
	}
	
	@Override
	public void spawnParticle(Player player, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, CoreParticleData data, double distanceSquared) {
		sendParticle(player, this.particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, data, distanceSquared);
	}

	@Override
	public void spawnParticle(World world, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, CoreParticleData data, double distanceSquared) {
		broadcastParticle(world, this.particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, data, distanceSquared);
	}
	
	public static ChefParticleBase create(@NotNull Particle particle, @NotNull String particleName) {
		return new ChefParticleBase(particle, particleName, CoreParticleDataType.VOID);
	}
	
	public static ChefParticleBase create(@NotNull Particle particle, @NotNull String particleName, @NotNull CoreParticleDataType dataType) {
		return new ChefParticleBase(particle, particleName, dataType);
	}
	
	public static void broadcastParticle(World world, Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable CoreParticleData cdata, double distanceSquared) {
		List<ServerPlayer> playerList = ((CraftWorld) world).getHandle().players();
		
		int j = playerList.size();
        
		ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(CraftParticle.createParticleParam(particle, cdata == null ? null : cdata.getData()), false, false,
        		x, y, z, (float) offsetX, (float) offsetY, (float) offsetZ, (float) extra, count);

		for (int i = 0; i < j; i++) {
			ServerPlayer entityPlayer = playerList.get(i);
			if (SimpleMath.distanceSquared(entityPlayer.getX(), entityPlayer.getY(), entityPlayer.getZ(), x, y, z) <= distanceSquared) {
				entityPlayer.connection.send(packet);
			}
		}
		
		packet = null;
	}
	
	public static void sendParticle(Player player, Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable CoreParticleData cdata, double distanceSquared) {
		if (SimpleMath.distanceSquared(player.getLocation(), x, y, z) <= distanceSquared) {
			
			ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(CraftParticle.createParticleParam(particle, cdata == null ? null : cdata.getData()), false, false,
	        		x, y, z, (float) offsetX, (float) offsetY, (float) offsetZ, (float) extra, count);
			((CraftPlayer) player).getHandle().connection.send(packet);
			
			packet = null;
		}
	}
}
