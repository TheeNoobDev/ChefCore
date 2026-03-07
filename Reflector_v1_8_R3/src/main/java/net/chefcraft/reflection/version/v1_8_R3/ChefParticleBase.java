package net.chefcraft.reflection.version.v1_8_R3.particles;

import net.chefcraft.core.math.SimpleMath;
import net.chefcraft.core.registry.CoreRegistry;
import net.chefcraft.world.particle.CoreParticle;
import net.chefcraft.world.particle.CoreParticleData;
import net.chefcraft.world.particle.CoreParticleDataType;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChefParticleBase implements CoreParticle {

    public static final CoreRegistry<CoreParticle> REGISTRY = new CoreRegistry<>(
            ChefParticleBase.create(EnumParticle.EXPLOSION_NORMAL, "poof"),
            ChefParticleBase.create(EnumParticle.EXPLOSION_LARGE, "explosion"),
            ChefParticleBase.create(EnumParticle.EXPLOSION_HUGE, "explosion_emitter"),
            ChefParticleBase.create(EnumParticle.FIREWORKS_SPARK, "firework"),
            ChefParticleBase.create(EnumParticle.WATER_BUBBLE, "bubble"),
            ChefParticleBase.create(EnumParticle.WATER_SPLASH, "splash"),
            ChefParticleBase.create(EnumParticle.WATER_WAKE, "fishing"),
            ChefParticleBase.create(EnumParticle.SUSPENDED, "underwater"),
            ChefParticleBase.create(EnumParticle.CRIT, "crit"),
            ChefParticleBase.create(EnumParticle.CRIT_MAGIC, "enchanted_hit"),
            ChefParticleBase.create(EnumParticle.SMOKE_NORMAL, "smoke"),
            ChefParticleBase.create(EnumParticle.SMOKE_LARGE, "large_smoke"),
            ChefParticleBase.create(EnumParticle.SPELL, "effect"),
            ChefParticleBase.create(EnumParticle.SPELL_INSTANT, "instant_effect"),
            ChefParticleBase.create(EnumParticle.SPELL_MOB_AMBIENT, "ambient_entity_effect"),
            ChefParticleBase.create(EnumParticle.SPELL_MOB, "entity_effect", CoreParticleDataType.COLOR),
            ChefParticleBase.create(EnumParticle.SPELL_WITCH, "witch"),
            ChefParticleBase.create(EnumParticle.DRIP_WATER, "dripping_water"),
            ChefParticleBase.create(EnumParticle.DRIP_LAVA, "dripping_lava"),
            ChefParticleBase.create(EnumParticle.VILLAGER_ANGRY, "angry_villager"),
            ChefParticleBase.create(EnumParticle.VILLAGER_HAPPY, "happy_villager"),
            ChefParticleBase.create(EnumParticle.TOWN_AURA, "mycelium"),
            ChefParticleBase.create(EnumParticle.NOTE, "note"),
            ChefParticleBase.create(EnumParticle.PORTAL, "portal"),
            ChefParticleBase.create(EnumParticle.ENCHANTMENT_TABLE, "enchant"),
            ChefParticleBase.create(EnumParticle.FLAME, "flame"),
            ChefParticleBase.create(EnumParticle.LAVA, "lava"),
            ChefParticleBase.create(EnumParticle.CLOUD, "cloud"),
            ChefParticleBase.create(EnumParticle.REDSTONE, "dust", CoreParticleDataType.DUST_OPTIONS), // DustOptions.class
            ChefParticleBase.create(EnumParticle.SNOWBALL, "item_snowball"),
            ChefParticleBase.create(EnumParticle.SLIME, "item_slime"),
            ChefParticleBase.create(EnumParticle.HEART, "heart"),
            ChefParticleBase.create(EnumParticle.ITEM_CRACK, "item", CoreParticleDataType.ITEM_STACK), //ItemStack.class
            ChefParticleBase.create(EnumParticle.BLOCK_CRACK, "block", CoreParticleDataType.BLOCK_DATA), //BlockData.class
            ChefParticleBase.create(EnumParticle.WATER_DROP, "rain"),
            ChefParticleBase.create(EnumParticle.MOB_APPEARANCE, "elder_guardian"),
            ChefParticleBase.create(EnumParticle.FIREWORKS_SPARK, "end_rod"),
            ChefParticleBase.create(EnumParticle.VILLAGER_ANGRY, "damage_indicator"),
            ChefParticleBase.create(EnumParticle.EXPLOSION_LARGE, "sweep_attack"),
            ChefParticleBase.create(EnumParticle.BLOCK_DUST, "falling_dust", CoreParticleDataType.BLOCK_DATA), //BlockData.class
            ChefParticleBase.create(EnumParticle.SPELL_INSTANT, "totem_of_undying"),
            ChefParticleBase.create(EnumParticle.CRIT, "spit"),
            ChefParticleBase.create(EnumParticle.CLOUD, "squid_ink"),
            ChefParticleBase.create(EnumParticle.WATER_BUBBLE, "bubble_pop"),
            ChefParticleBase.create(EnumParticle.WATER_BUBBLE, "current_down"),
            ChefParticleBase.create(EnumParticle.WATER_BUBBLE, "bubble_column_up"),
            ChefParticleBase.create(EnumParticle.WATER_WAKE, "nautilus"),
            ChefParticleBase.create(EnumParticle.WATER_BUBBLE, "dolphin"),
            ChefParticleBase.create(EnumParticle.VILLAGER_HAPPY, "sneeze"),
            ChefParticleBase.create(EnumParticle.SMOKE_NORMAL, "campfire_cosy_smoke"),
            ChefParticleBase.create(EnumParticle.SMOKE_NORMAL, "campfire_signal_smoke"),
            ChefParticleBase.create(EnumParticle.VILLAGER_HAPPY, "composter"),
            ChefParticleBase.create(EnumParticle.EXPLOSION_LARGE, "flash"),
            ChefParticleBase.create(EnumParticle.DRIP_LAVA, "falling_lava"),
            ChefParticleBase.create(EnumParticle.DRIP_LAVA, "landing_lava"),
            ChefParticleBase.create(EnumParticle.DRIP_LAVA, "falling_water"),
            ChefParticleBase.create(EnumParticle.DRIP_LAVA, "dripping_honey"),
            ChefParticleBase.create(EnumParticle.DRIP_LAVA, "falling_honey"),
            ChefParticleBase.create(EnumParticle.DRIP_LAVA, "landing_honey"),
            ChefParticleBase.create(EnumParticle.DRIP_LAVA, "falling_nectar"),
            ChefParticleBase.create(EnumParticle.FLAME, "soul_fire_flame"),
            ChefParticleBase.create(EnumParticle.SMOKE_NORMAL, "ash"),
            ChefParticleBase.create(EnumParticle.DRIP_LAVA, "crimson_spore"),
            ChefParticleBase.create(EnumParticle.DRIP_WATER, "warped_spore"),
            ChefParticleBase.create(EnumParticle.DRIP_WATER, "soul"),
            ChefParticleBase.create(EnumParticle.DRIP_WATER, "dripping_obsidian_tear"),
            ChefParticleBase.create(EnumParticle.DRIP_WATER, "falling_obsidian_tear"),
            ChefParticleBase.create(EnumParticle.DRIP_WATER, "landing_obsidian_tear"),
            ChefParticleBase.create(EnumParticle.REDSTONE, "reverse_portal"),
            ChefParticleBase.create(EnumParticle.CLOUD, "white_ash"), //DustTransition.class
            ChefParticleBase.create(EnumParticle.REDSTONE, "dust_color_transition", CoreParticleDataType.DUST_TRANSITION), //Vibration.class
            ChefParticleBase.create(EnumParticle.DRIP_WATER, "vibration", CoreParticleDataType.VIBRATION),
            ChefParticleBase.create(EnumParticle.DRIP_LAVA, "falling_spore_blossom"),
            ChefParticleBase.create(EnumParticle.DRIP_LAVA, "spore_blossom_air"),
            ChefParticleBase.create(EnumParticle.FLAME, "small_flame"),
            ChefParticleBase.create(EnumParticle.CLOUD, "snowflake"),
            ChefParticleBase.create(EnumParticle.DRIP_LAVA, "dripping_dripstone_lava"),
            ChefParticleBase.create(EnumParticle.DRIP_LAVA, "falling_dripstone_lava"),
            ChefParticleBase.create(EnumParticle.DRIP_LAVA, "dripping_dripstone_water"),
            ChefParticleBase.create(EnumParticle.DRIP_LAVA, "falling_dripstone_water"),
            ChefParticleBase.create(EnumParticle.VILLAGER_HAPPY, "glow_squid_ink"),
            ChefParticleBase.create(EnumParticle.FIREWORKS_SPARK, "glow"),
            ChefParticleBase.create(EnumParticle.FIREWORKS_SPARK, "wax_on"),
            ChefParticleBase.create(EnumParticle.FIREWORKS_SPARK, "wax_off"),
            ChefParticleBase.create(EnumParticle.FIREWORKS_SPARK, "electric_spark"),
            ChefParticleBase.create(EnumParticle.FIREWORKS_SPARK, "scrape"),
            ChefParticleBase.create(EnumParticle.EXPLOSION_LARGE, "sonic_boom"),
            ChefParticleBase.create(EnumParticle.CRIT_MAGIC, "sculk_soul"),
            ChefParticleBase.create(EnumParticle.CRIT_MAGIC, "sculk_charge", CoreParticleDataType.FLOAT), //Float.class
            ChefParticleBase.create(EnumParticle.CRIT_MAGIC, "sculk_charge_pop"),
            ChefParticleBase.create(EnumParticle.CRIT_MAGIC, "shriek", CoreParticleDataType.INTEGER), //Integer.class
            ChefParticleBase.create(EnumParticle.FIREWORKS_SPARK, "cherry_leaves"),
            ChefParticleBase.create(EnumParticle.VILLAGER_HAPPY, "egg_crack"),
            ChefParticleBase.create(EnumParticle.CLOUD, "dust_plume"),
            ChefParticleBase.create(EnumParticle.SNOW_SHOVEL, "white_smoke"),
            ChefParticleBase.create(EnumParticle.BARRIER, "block_marker", CoreParticleDataType.BLOCK_DATA), //BlockData.class

            //1.21 -> 1.21.3
            ChefParticleBase.create(EnumParticle.CLOUD, "small_gust"),
            ChefParticleBase.create(EnumParticle.EXPLOSION_NORMAL, "gust"),
            ChefParticleBase.create(EnumParticle.EXPLOSION_LARGE, "gust_emitter_small"),
            ChefParticleBase.create(EnumParticle.EXPLOSION_HUGE, "gust_emitter_large"),
            ChefParticleBase.create(EnumParticle.DRIP_LAVA, "trial_spawner_detection"),
            ChefParticleBase.create(EnumParticle.DRIP_WATER, "trial_spawner_detection_ominous"),
            ChefParticleBase.create(EnumParticle.DRIP_LAVA, "vault_connection"),
            ChefParticleBase.create(EnumParticle.SMOKE_NORMAL, "infested"),
            ChefParticleBase.create(EnumParticle.CLOUD, "item_cobweb"),
            ChefParticleBase.create(EnumParticle.BLOCK_CRACK, "dust_pillar", CoreParticleDataType.BLOCK_DATA),  //BlockData.class
            ChefParticleBase.create(EnumParticle.BLOCK_CRACK, "block_crumble", CoreParticleDataType.BLOCK_DATA), //BlockData.class
            ChefParticleBase.create(EnumParticle.WATER_BUBBLE, "ominous_spawning"),
            ChefParticleBase.create(EnumParticle.SMOKE_NORMAL, "raid_omen"),
            ChefParticleBase.create(EnumParticle.SMOKE_NORMAL, "trial_omen"),

            //1.21.4
            ChefParticleBase.create(EnumParticle.SNOW_SHOVEL, "pale_oak_leaves"),
            ChefParticleBase.create(EnumParticle.FIREWORKS_SPARK, "trail", CoreParticleDataType.TRAIL), //Trail.class

            //1.21.5
            ChefParticleBase.create(EnumParticle.BLOCK_DUST, "tinted_leaves", CoreParticleDataType.COLOR),
            ChefParticleBase.create(EnumParticle.CLOUD, "firefly"),

            //1.21.9
            ChefParticleBase.create(EnumParticle.FLAME, "copper_fire_flame")
    );

	protected final EnumParticle particle;
	protected final String name;
    protected final CoreParticleDataType dataType;
	
	private ChefParticleBase(EnumParticle particle, String name, CoreParticleDataType dataType) {
		this.particle = particle;
		this.name = name;
        this.dataType = dataType;
	}
	
	public EnumParticle getParticle() {
		return this.particle;
	}
	
	@Override
    @NotNull
	public String key() {
		return this.name;
	}

    @Override
    @NotNull
    public CoreParticleDataType getDataType() {
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
	
	public static ChefParticleBase create(EnumParticle particle, String particleName) {
		return new ChefParticleBase(particle, particleName, CoreParticleDataType.VOID);
	}

    public static ChefParticleBase create(EnumParticle particle, String particleName, CoreParticleDataType dataType) {
        return new ChefParticleBase(particle, particleName, dataType);
    }
	
	public static void broadcastParticle(World world, EnumParticle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, CoreParticleData cdata, double distanceSquared) {
		List<EntityHuman> playerList = ((CraftWorld) world).getHandle().players;
		
		int j = playerList.size();
        
        PacketPlayOutWorldParticles packet = handlePacketCreator(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, cdata);
        
		for (int i = 0; i < j; i++) {
			EntityHuman human = playerList.get(i);
			if (human instanceof EntityPlayer && SimpleMath.distanceSquared(human.locX, human.locY, human.locZ, x, y, z) <= distanceSquared) {
				((EntityPlayer) human).playerConnection.sendPacket(packet);
			}
		}
		
		packet = null;
	}
	
	public static void sendParticle(Player player, EnumParticle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, CoreParticleData cdata, double distanceSquared) {
		if (SimpleMath.distanceSquared(player.getLocation(), x, y, z) <= distanceSquared) {
			
			PacketPlayOutWorldParticles packet = handlePacketCreator(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, cdata);
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
			
			packet = null;
		}
	}
	
	private static PacketPlayOutWorldParticles handlePacketCreator(EnumParticle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, CoreParticleData cdata) {
        
        if (cdata != null && (particle == EnumParticle.REDSTONE || particle == EnumParticle.SPELL_MOB || particle == EnumParticle.SPELL_MOB_AMBIENT)) {
        	float[] f = cdata.getData();
        	return new PacketPlayOutWorldParticles(particle, true, (float) x, (float) y, (float) z, f[0], f[1], f[2], 1, 0); 
        }
        
        return new PacketPlayOutWorldParticles(particle, true, (float) x, (float) y, (float) z, 
        		(float) offsetX, (float) offsetY, (float) offsetZ, (float) extra, count, handleParticleData(particle, cdata == null ? null : cdata.getData()));
	}
	
	@SuppressWarnings("deprecation")
	private static <T> int[] handleParticleData(EnumParticle particle, @Nullable T data) {
		switch (particle) {
		case ITEM_CRACK:
			if (data != null) {
				ItemStack itemStack = (ItemStack) data;
	            return new int[] {itemStack.getType().getId(), itemStack.getDurability()};
            }
			return new int[0];
		case BLOCK_CRACK:
		case BLOCK_DUST:
			if (data != null) {
				MaterialData materialData = (MaterialData) data;
	            return new int[] {materialData.getItemTypeId() + ((int) (materialData.getData()) << 12)};
			}
		default: 
			return new int[0];
		}
	}
}
