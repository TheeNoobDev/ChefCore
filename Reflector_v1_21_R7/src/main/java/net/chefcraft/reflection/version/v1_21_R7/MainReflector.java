package net.chefcraft.reflection.v1_21_R7;

import io.netty.channel.Channel;
import io.papermc.paper.adventure.PaperAdventure;
import net.chefcraft.core.ChefCore;
import net.chefcraft.core.component.ComponentBuilder;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.registry.CoreRegistry;
import net.chefcraft.core.util.PreparedConditions;
import net.chefcraft.reflection.AbstractReflections;
import net.chefcraft.reflection.base.PacketBuilder;
import net.chefcraft.reflection.world.CoreEntityType;
import net.chefcraft.reflection.world.GameReflections;
import net.chefcraft.service.nick.NickNamerService;
import net.chefcraft.world.particle.CoreParticle;
import net.chefcraft.world.particle.CoreParticleData;
import net.chefcraft.world.scoreboard.CoreNumberFormat;
import net.kyori.adventure.translation.Translatable;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import org.bukkit.*;
import org.bukkit.Particle.Trail;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.legacy.CraftLegacy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapView;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class MainReflector extends AbstractReflections {
	
	// For Skull Meta Start -> applyCustomValueToSkullMeta
	private static final String MOJANG_TEXTURE_SERVERS_URL = "http://textures.minecraft.net/texture/";
	private static final String AN_UUID = "9f1da623-4be2-44de-9249-6ad70f36ef52"; //ChefRido's UUID (dont allow random uuid because random uuid does not have skin :D (thats my opinion))
	
	@SuppressWarnings("deprecation") //PlayerProfile
	private static final Map<String, PlayerProfile> CUSTOM_SKULL_PROFILES_BY_URL = new HashMap<>();
	// For Skull Meta End
	
	private static final GameReflector GAME_REFLECTIONS = new GameReflector();
	
	public MainReflector() {
        new Thread(() -> {
            ChefCore.log(Level.WARNING, "Legacy support initializing by ChefCore!");
            CraftLegacy.init();
            ChefCore.log(Level.WARNING, "Legacy support initialized!");
        }).start();
    }
	
	@Override
	public void sendPacket(Player player, Object packet) {
		if (packet instanceof Packet<?> mojangPacket) {
			((CraftPlayer) player).getHandle().connection.send(mojangPacket);
		} else {
			throw new IllegalArgumentException("param, packet must be instance of 'net.minecraft.network.protocol.Packet'. Invalid object: " + packet.getClass());
		}
	}

    @Override
    public void onPlayerJoin(Player player) {
        ClientSidePlayerPunchToEntityEventListener.onPlayerJoined(((CraftPlayer) player).getHandle());
    }

    @Override
    public Channel getPlayerChannel(Player player) {
        return ((CraftPlayer) player).getHandle().connection.connection.channel;
    }

    @Override
    public <T extends PacketBuilder> T getPacketBuilderByClass(@NotNull Class<? extends PacketBuilder> clazz) {
        PreparedConditions.notNull(clazz, "clazz");
        return (T) ChefPacketBuilder.getByClass(clazz);
    }

    @Override
	public void saveWorld(World world, boolean flush, boolean skipSave) {
		((CraftWorld) world).getHandle().save(null, flush, skipSave);
	}
	
	@SuppressWarnings("deprecation")	
	@Override
	public Sound getSoundByName(@NotNull String name) {
		return Sound.valueOf(name);
	}
	
	@Override
	public int getMapId(MapView view) {
		return view.getId();
	}
	
	@Override
	public Inventory getPlayerTopOrBottomInventory(Player player, boolean top) {
		return top ? player.getOpenInventory().getTopInventory() : player.getOpenInventory().getBottomInventory();
	}
	
	@Override
	public int getPing(Player player) {
		return player.getPing();
	}
	
	@SuppressWarnings("deprecation") //For Paper: player.getLocale()
	@Override
	public String getPlayerLocale(Player player) {
		return player.getLocale();
	}

	@SuppressWarnings("deprecation") //For Paper: player.sendActionBar
	@Override
	public void sendActionbarMessage(Player player, String text) {
		player.sendActionBar(text);
	}
	
	@SuppressWarnings("deprecation") //For Paper: player.sendTitle
	@Override
	public void sendTitleMessage(Player player, String title, String subtitle, int fadeIn, int fadeStay, int fadeOut) {
		player.sendTitle(title, subtitle, fadeIn, fadeStay, fadeOut);
	}
	
	@Override
	public void respawn(Player player) {
		player.spigot().respawn();
	}

	@Override
	public void setPlayerListData(@NotNull Player player, @NotNull MessageHolder header, @NotNull MessageHolder footer) {
        PreparedConditions.notNull(player, "player");
        PreparedConditions.notNull(header, "header");
        PreparedConditions.notNull(footer, "footer");
        player.sendPlayerListHeaderAndFooter(header.asComponent(), footer.asComponent());
	}

	@Override
	public void updateInventory(Player player) {
		player.updateInventory();
	}

	@Override
	@SuppressWarnings("deprecation") //For Paper: meta.setOwnerProfile, PlayerProfile, getTextures, setTextures, Bukkit.createPlayerProfile
	public SkullMeta applyCustomValueToSkullMeta(SkullMeta meta, String value) {
		
		String rawValue = value.replace(MOJANG_TEXTURE_SERVERS_URL, "");
		
		if (CUSTOM_SKULL_PROFILES_BY_URL.containsKey(rawValue)) {
			meta.setOwnerProfile(CUSTOM_SKULL_PROFILES_BY_URL.get(rawValue));
        } else {
        	PlayerProfile profile = Bukkit.createPlayerProfile(UUID.fromString(AN_UUID));
        	
        	PlayerTextures textures = profile.getTextures();
        	
        	try {
            	textures.setSkin(new URI(MOJANG_TEXTURE_SERVERS_URL + rawValue).toURL());
            } catch (URISyntaxException | MalformedURLException e) {
    			e.fillInStackTrace();
    		}
            
            profile.setTextures(textures);
            CUSTOM_SKULL_PROFILES_BY_URL.put(rawValue, profile);
            
            meta.setOwnerProfile(profile);
        }
		
        return meta;
	}
	
	@Override
	public ItemMeta setItemUnbreakable(ItemMeta meta, boolean unbreakable) {
		meta.setUnbreakable(unbreakable);
		return meta;
	}

    @Override
	public GameReflections getGameReflections() {
		return GAME_REFLECTIONS;
	}
	
	@Override
	public CoreRegistry<CoreParticle> getCoreParticleRegistry() {
		return ChefParticleBase.REGISTRY;
	}
	
	@Override
	public CoreParticleData fromBlockData(@NotNull Block block) {
		return CoreParticleData.from(block.getBlockData());
	}

	@Override
	public CoreParticleData fromMaterialData(Material material, int legacyId) {
		return CoreParticleData.from(material.createBlockData());
	}

	@Override
	public CoreParticleData fromDustOptions(Color color, float size) {
		return CoreParticleData.from(new Particle.DustOptions(color, size));
	}

	@Override
	public CoreParticleData fromDustTransition(Color fromColor, Color toColor, float size) {
		return CoreParticleData.from(new Particle.DustTransition(fromColor, toColor, size));
	}

	@Override
	public CoreParticleData fromVibration(Location origin, Location destination, int arrivalTime) {
		return CoreParticleData.from(new Vibration(new Vibration.Destination.BlockDestination(destination), arrivalTime));
	}
	
	@Override
	public CoreParticleData fromVibration(Location origin, Entity destination, int arrivalTime) {
		return CoreParticleData.from(new Vibration(new Vibration.Destination.EntityDestination(destination), arrivalTime));
	}

	@Override
	public CoreParticleData fromTrail(Location target, Color color, int duration) {
		return CoreParticleData.from(new Trail(target, color, duration));
	}

    @Override
    public CoreParticleData fromSpell(Color color, float power) {
        return CoreParticleData.from(new Particle.DustOptions(color, power));
    }
	
	@Override
	public CoreNumberFormat newNumberFormatByName(String type) {
		return new ChefNumberFormat(type);
	}
	
	@Override
	public NickNamerService newNickNamerService(Player player) {
		return new ChefNickNamerService(player);
	}

    @Override
    public BaseComponent textComponentFromLegacy(String text) {
        return TextComponent.fromLegacy(text);
    }

    @Override
    public ComponentBuilder newComponentBuilder() {
        return new ComponentBuilderImpl();
    }

    @Override
    public @Nullable Entity createEntity(@NotNull CoreEntityType type, @NotNull World world) {
        PreparedConditions.notNull(type, "type");
        PreparedConditions.notNull(world, "world");
        EntityType<?> entityType = EntityType.byString(type.key()).orElse(null);
        if (entityType != null) {
            net.minecraft.world.entity.Entity nms = entityType.create(((CraftWorld) world).getHandle(), EntitySpawnReason.COMMAND);
            return nms != null ? nms.getBukkitEntity() : null;
        }
        return null;
    }

    @Override
    public @Nullable String getTranslationKeyIfTranslatable(@NotNull Object o) {
        PreparedConditions.notNull(o, "o");
        if (o instanceof Translatable translatable) {
            return translatable.translationKey();
        }
        return null;
    }

    @Override
    public String getItemI18Name(@NotNull ItemStack itemStack) {
        PreparedConditions.notNull(itemStack, "itemStack");
        return itemStack.getI18NDisplayName();
    }

    public static Component toVanilla(MessageHolder holder) {
        return PaperAdventure.asVanilla((net.kyori.adventure.text.Component) (holder.asComponent()));
    }
}
