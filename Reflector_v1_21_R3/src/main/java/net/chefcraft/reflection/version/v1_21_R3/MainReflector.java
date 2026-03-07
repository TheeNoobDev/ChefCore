package net.chefcraft.reflector.v1_21_R3;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.Trail;
import org.bukkit.Sound;
import org.bukkit.Vibration;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslatablePlayer;
import net.chefcraft.core.registry.CoreRegistry;
import net.chefcraft.core.util.ActivationRange;
import net.chefcraft.core.util.JHelper;
import net.chefcraft.reflection.AbstractReflections;
import net.chefcraft.reflection.world.GameReflections;
import net.chefcraft.reflector.v1_21_R3.bossbar.ChefBossBar;
import net.chefcraft.reflector.v1_21_R3.hologram.ChefHologram;
import net.chefcraft.reflector.v1_21_R3.itemframe.ChefItemFrame;
import net.chefcraft.reflector.v1_21_R3.itemframe.ClientSideItemFrameClickListener;
import net.chefcraft.reflector.v1_21_R3.nametag.ChefNameTagService;
import net.chefcraft.reflector.v1_21_R3.nicknamer.ChefNickNamerService;
import net.chefcraft.reflector.v1_21_R3.npc.NPCUtil;
import net.chefcraft.reflector.v1_21_R3.particles.ChefParticleBase;
import net.chefcraft.reflector.v1_21_R3.popup.ServerSideBlockInsecureChatPopupListener;
import net.chefcraft.reflector.v1_21_R3.scoreboard.ChefNumberFormat;
import net.chefcraft.reflector.v1_21_R3.scoreboard.ChefScoreboardServer;
import net.chefcraft.reflector.v1_21_R3.scoreboard.custom.ChefTeamHolder;
import net.chefcraft.reflector.v1_21_R3.scoreboard.custom.ChefTeamManager;
import net.chefcraft.reflector.v1_21_R3.utils.PacketInjector;
import net.chefcraft.service.nick.NickNamerService;
import net.chefcraft.service.npc.LivingEntityNPC;
import net.chefcraft.service.npc.NonPlayerCharTypes;
import net.chefcraft.service.tag.NameTagService;
import net.chefcraft.world.boss.CoreBarColor;
import net.chefcraft.world.boss.CoreBarStyle;
import net.chefcraft.world.boss.CoreBossBar;
import net.chefcraft.world.hologram.Hologram;
import net.chefcraft.world.itemframe.CoreItemFrame;
import net.chefcraft.world.particle.CoreParticle;
import net.chefcraft.world.particle.CoreParticleData;
import net.chefcraft.world.player.CorePlayer;
import net.chefcraft.world.scoreboard.CoreNumberFormat;
import net.chefcraft.world.scoreboard.CoreScoreboard;
import net.chefcraft.world.scoreboard.custom.CoreTeamHolder;
import net.chefcraft.world.scoreboard.custom.CoreTeamManager;
import net.chefcraft.world.util.BukkitUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;

public class MainReflector extends AbstractReflections {
	
	// For Skull Meta Start -> applyCustomValueToSkullMeta
	private static final String MOJANG_TEXTURE_SERVERS_URL = "http://textures.minecraft.net/texture/";
	private static final String AN_UUID = "9f1da623-4be2-44de-9249-6ad70f36ef52"; //ChefRido's UUID (dont allow random uuid because random uuid does not have skin :D (thats my opinion))
	
	@SuppressWarnings("deprecation") //PlayerProfile
	private static final Map<String, PlayerProfile> CUSTOM_SKULL_PROFILES_BY_URL = new HashMap<>();
	// For Skull Meta End
	
	private static final GameReflector GAME_REFLECTIONS = new GameReflector();
	
	public MainReflector() {}
	
	@Override
	public void onPlayerJoin(Player player) {
		final ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
		
		ClientSideItemFrameClickListener.onPlayerJoined(serverPlayer);
		
		if (ChefCore.disableInsecureChatMessagePopup()) {
			ServerSideBlockInsecureChatPopupListener.onPlayerJoined(serverPlayer);
		}
	}
	
	@Override
	public void onPlayerQuit(Player player) {
		PacketInjector.removeAllInjections(((CraftPlayer) player).getHandle());
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
	public void saveWorld(World world, boolean flush, boolean skipSave) {
		((CraftWorld) world).getHandle().save(null, flush, skipSave);
	}
	
	@SuppressWarnings("deprecation")	
	@Override
	public Sound getSoundByName(@Nonnull String name) {
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

	@SuppressWarnings("deprecation") //For Paper: player.setPlayerListHeaderFooter
	@Override
	public void setPlayerListData(Player player, List<String> header, List<String> footer) {
		player.setPlayerListHeaderFooter(BukkitUtils.parseList(header, "\n"), BukkitUtils.parseList(footer, "\n"));
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
    			e.printStackTrace();
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
	public Hologram newHologram(TranslatablePlayer translatablePlayer, Location location, float distanceBetweenLines, MessageHolder lines) {
		return new ChefHologram(translatablePlayer, location, distanceBetweenLines, lines);
	}
	
	@Override
	public NameTagService newNameTagTeamService(CorePlayer corePlayer, int tabListPriority) {
		return new ChefNameTagService(corePlayer, tabListPriority);
	}

	@Override
	public LivingEntityNPC newNPC(JavaPlugin plugin, NonPlayerCharTypes type, Location location, boolean enableTracker, boolean hologramManager) {
		return NPCUtil.createNPC(plugin, type, location, enableTracker, hologramManager);
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
	public CoreParticleData fromBlockData(@Nonnull Block block) {
		return CoreParticleData.from(block.getBlockData());
	}

	@Override
	public CoreParticleData fromMaterialData(Material material, int legacyId) {
		return CoreParticleData.from(material.createBlockData());
	}

	@Override
	public CoreParticleData fromDustOptions(org.bukkit.Color color, float size) {
		return CoreParticleData.from(new Particle.DustOptions(color, size));
	}

	@Override
	public CoreParticleData fromDustTransition(org.bukkit.Color fromColor, org.bukkit.Color toColor, float size) {
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
	public CoreParticleData fromTrail(Location target, org.bukkit.Color color, int duration) {
		return CoreParticleData.from(new Trail(target, color, duration));
	}

	@Override
	public CoreScoreboard newScoreboardForPlayer(TranslatablePlayer translatablePlayer) {
		return new ChefScoreboardServer(Arrays.asList(translatablePlayer));
	}
	
	@Override
	public CoreScoreboard newScoreboardForSomePlayers(Iterable<TranslatablePlayer> translatablePlayers) {
		return new ChefScoreboardServer(JHelper.iterablesToList(translatablePlayers));
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
	public CoreTeamHolder newTeamHolder(String teamName) {
		return new ChefTeamHolder(teamName);
	}
	
	@Override
	public CoreTeamManager newTeamManager(Player player) {
		return new ChefTeamManager(player);
	}

	@Override
	public CoreBossBar newBossBarForPlayer(TranslatablePlayer translatablePlayer, MessageHolder title, CoreBarColor barColor, CoreBarStyle barStyle) {
		return new ChefBossBar(translatablePlayer, title, barColor, barStyle);
	}

	@Override
	public CoreItemFrame newItemFrame(Player player, Location location, BlockFace facing, ActivationRange activationRange, boolean glowing) {
		return new ChefItemFrame(player, location, facing, activationRange, glowing);
	}
}
