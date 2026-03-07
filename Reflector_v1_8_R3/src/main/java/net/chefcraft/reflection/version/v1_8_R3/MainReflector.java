package net.chefcraft.reflector.version.v1_8_R3;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.netty.channel.Channel;
import net.chefcraft.core.component.CoreTextBase;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslatablePlayer;
import net.chefcraft.core.registry.CoreRegistry;
import net.chefcraft.core.util.ActivationRange;
import net.chefcraft.core.util.ObjectHelper;
import net.chefcraft.reflection.AbstractReflections;
import net.chefcraft.reflection.base.PacketBuilder;
import net.chefcraft.reflection.world.GameReflections;
import net.chefcraft.reflector.version.v1_8_R3.bossbar.ChefBossBar;
import net.chefcraft.reflector.version.v1_8_R3.itemframe.ChefItemFrame;
import net.chefcraft.reflector.version.v1_8_R3.itemframe.ClientSideItemFrameClickListener;
import net.chefcraft.reflector.version.v1_8_R3.nicknamer.ChefNickNamerService;
import net.chefcraft.reflector.version.v1_8_R3.npc.NPCUtil;
import net.chefcraft.service.nick.NickNamerService;
import net.chefcraft.service.npc.LivingEntityNPC;
import net.chefcraft.service.npc.NonPlayerCharType;
import net.chefcraft.world.boss.CoreBarColor;
import net.chefcraft.world.boss.CoreBarStyle;
import net.chefcraft.world.boss.CoreBossBar;
import net.chefcraft.world.hologram.Hologram;
import net.chefcraft.world.itemframe.CoreItemFrame;
import net.chefcraft.world.particle.CoreParticle;
import net.chefcraft.world.particle.CoreParticleData;
import net.chefcraft.world.scoreboard.CoreNumberFormat;
import net.chefcraft.world.util.BukkitUtils;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapView;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainReflector extends AbstractReflections {
	
	// For Skull Meta Start -> applyCustomValueToSkullMeta
	private static final String MOJANG_TEXTURE_SERVERS_URL = "http://textures.minecraft.net/texture/";
	private static final Map<String, GameProfile> CUSTOM_SKULL_PROFILES_BY_URL = new HashMap<>();
	// For Skull Meta End
	
	private static final GameReflector GAME_REFLECTIONS = new GameReflector();
	
	public MainReflector() {}
	
	@Override
	public void onPlayerJoin(Player player) {
		ClientSideItemFrameClickListener.onPlayerJoined(((CraftPlayer) player).getHandle());
	}
	
	@Override
	public void saveWorld(World world, boolean flush, boolean savingDisabled) {
		try {
			net.minecraft.server.v1_8_R3.WorldServer worldServer = ((CraftWorld) world).getHandle();
			worldServer.savingDisabled = false;
			worldServer.save(flush, null);
			if (flush) {
				worldServer.flushSave();
			}
			worldServer.savingDisabled = savingDisabled;
		} catch (ExceptionWorldConflict e) {
			e.fillInStackTrace();
		}
	}
	
	@Override
	public Sound getSoundByName(@Nonnull String name) {
		return Sound.valueOf(name);
	}

    @Override
    public void sendPacket(Player player, Object packet) {
        if (packet instanceof Packet<?>) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket((Packet<?>) packet);
        } else {
            throw new IllegalArgumentException("param, packet must be instance of 'net.minecraft.network.protocol.Packet'. Invalid object: " + packet.getClass());
        }
    }

    @Override
    public Channel getPlayerChannel(Player player) {
        return ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
    }

    @Override
    public PacketBuilder getPacketBuilderByClass(Class<?> clazz) {
        return ChefPacketBuilder.getByClass(clazz);
    }

    @SuppressWarnings("deprecation")
	@Override
	public int getMapId(MapView view) {
		return view.getId(); //returns as short value
	}
	
	@Override
	public Inventory getPlayerTopOrBottomInventory(Player player, boolean top) {
		return top ? player.getOpenInventory().getTopInventory() : player.getOpenInventory().getBottomInventory();
	}
	
	@Override
	public int getPing(Player player) {
		return ((CraftPlayer) player).getHandle().ping;
	}
	
	@Override
	public String getPlayerLocale(Player player) {
		return ((CraftPlayer) player).getHandle().locale;
	}

	@Override
	public void sendActionbarMessage(Player player, String text) {
		this.sendPacket(player, new PacketPlayOutChat(new ChatComponentText(text), (byte) 2));
	}
	
	@Override
	public void sendTitleMessage(Player player, String title, String subtitle, int fadeIn, int fadeStay, int fadeOut) {
		PacketPlayOutTitle packetTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatComponentText(title));
		PacketPlayOutTitle packetSubtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, new ChatComponentText(subtitle));
		PacketPlayOutTitle fadeAnims = new PacketPlayOutTitle(fadeIn, fadeStay, fadeOut);
		
		PlayerConnection conn = ((CraftPlayer) player).getHandle().playerConnection;
		conn.sendPacket(packetTitle);
		conn.sendPacket(packetSubtitle);
		conn.sendPacket(fadeAnims);
	}
	
	@Override
	public void respawn(Player player) {
		player.spigot().respawn();
	}

	@Override
	public void setPlayerListData(Player player, List<String> header, List<String> footer) {
		PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
		ObjectHelper.setField(packet, "a", new ChatComponentText(BukkitUtils.parseList(header, "\n")));
		ObjectHelper.setField(packet, "b", new ChatComponentText(BukkitUtils.parseList(footer, "\n")));
		this.sendPacket(player, packet);
	}

	@Override
	public void updateInventory(Player player) {
		player.updateInventory();
	}

    @Override
    public CoreRegistry<CoreParticle> getCoreParticleRegistry() {
        return null;
    }

    @Override
	public SkullMeta applyCustomValueToSkullMeta(SkullMeta meta, String value) {
		
		String rawValue = value.replace(MOJANG_TEXTURE_SERVERS_URL, "");
		GameProfile profile;
		
		if (CUSTOM_SKULL_PROFILES_BY_URL.containsKey(rawValue)) {
			profile = CUSTOM_SKULL_PROFILES_BY_URL.get(rawValue);
			
        } else {
        	
        	profile = new GameProfile(UUID.randomUUID(), "");
            profile.getProperties().put("textures", new Property("textures", MOJANG_TEXTURE_SERVERS_URL + value));

            CUSTOM_SKULL_PROFILES_BY_URL.put(rawValue, profile);
        }
		
        try {
        	Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.fillInStackTrace();
        }

        return meta;
	}

    @Override
    public Hologram newHologram(TranslatablePlayer translatablePlayer, Location location, float distanceBetweenLines, MessageHolder text) {
        return null;
    }

    @Override
	public ItemMeta setItemUnbreakable(ItemMeta meta, boolean unbreakable) {
		meta.spigot().setUnbreakable(unbreakable);
		return meta;
	}
	
	@Override
	public LivingEntityNPC newNPC(JavaPlugin plugin, NonPlayerCharType type, Location location, boolean enableTracker, boolean hologramManager) {
		return NPCUtil.createNPC(plugin, type, location, enableTracker, hologramManager);
	}
	
	@SuppressWarnings("deprecation") //MaterialData
	@Override
	public CoreParticleData fromBlockData(@Nonnull Block block) {
		return CoreParticleData.from(new MaterialData(block.getType(), block.getData()));
	}

	@SuppressWarnings("deprecation") //MaterialData
	@Override
	public CoreParticleData fromMaterialData(Material material, int legacyId) {
		return CoreParticleData.from(new MaterialData(material, (byte) legacyId));
	}

	@Override
	public CoreParticleData fromDustOptions(org.bukkit.Color color, float size) {
		return CoreParticleData.from(new float [] {(color.getRed() / 255.0f) + 0.001f, color.getGreen() / 255.0f, color.getBlue() / 255.0f});
	}

	@Override
	public CoreParticleData fromDustTransition(org.bukkit.Color fromColor, org.bukkit.Color toColor, float size) {
		return CoreParticleData.EMPTY;
	}

	@Override
	public CoreParticleData fromVibration(Location origin, Location destination, int arrivalTime) {
		return CoreParticleData.EMPTY;
	}

    @Override
    public CoreParticleData fromVibration(Location origin, Entity destination, int arrivalTime) {
        return CoreParticleData.EMPTY;
    }

    @Override
    public CoreParticleData fromTrail(Location target, Color color, int duration) {
        return CoreParticleData.EMPTY;
    }

    @Override
    public CoreParticleData fromSpell(Color color, float power) {
        return CoreParticleData.EMPTY;
    }

    @Override
    public CoreBossBar newBossBarForPlayer(TranslatablePlayer player, MessageHolder title, CoreBarColor barColor, CoreBarStyle barStyle) {
        return new ChefBossBar(player, title);
    }
	
	@Override
	public GameReflections getGameReflections() {
		return GAME_REFLECTIONS;
	}
	
	@Override
	public CoreNumberFormat newNumberFormatByName(String type) {
		return new CoreNumberFormat() { //create empty instance

            @Override
            public CoreNumberFormat withTextBase(CoreTextBase textBase) {
                return newNumberFormatByName(null); //dont allow null value
            }

            @Override
            public CoreNumberFormat withColor(Color color) {
                return newNumberFormatByName(null); //dont allow null value
            }

            @Override
            public CoreNumberFormat withFixed(MessageHolder fix) {
                return newNumberFormatByName(null); //dont allow null value
            }
        };
	}
	
	@Override
	public NickNamerService newNickNamerService(Player player) {
		return new ChefNickNamerService(player);
	}

	@Override
	public CoreItemFrame newItemFrame(Player player, Location location, BlockFace facing, ActivationRange activationRange, boolean glowing) {
		return new ChefItemFrame(player, location, facing, activationRange, glowing);
	}
}
