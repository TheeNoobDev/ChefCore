package net.chefcraft.reflection;

import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslatablePlayer;
import net.chefcraft.core.registry.CoreRegistry;
import net.chefcraft.core.server.ServerVersion;
import net.chefcraft.core.util.ActivationRange;
import net.chefcraft.reflection.world.GameReflections;
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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class AbstractReflections {

	public AbstractReflections() { }
	
	/**
	 * This method was added to avoid error "java.lang.IncompatibleClassChangeError: Method 'org.bukkit.Sound org.bukkit.Sound.valueOf(java.lang.String)' must be Methodref constant"
	 * 
	 * @param name sound name
	 * @return sound value
	 */
	public abstract Sound getSoundByName(@Nonnull String name);
	
	public abstract void sendPacket(Player player, Object packet); 
	
	public abstract void saveWorld(World world, boolean flush, boolean skipSave);
	
	public abstract int getMapId(MapView view);
	
	public abstract Inventory getPlayerTopOrBottomInventory(Player player, boolean top);
	
	public abstract SkullMeta applyCustomValueToSkullMeta(SkullMeta meta, String urlValue);
	
	public abstract Hologram newHologram(TranslatablePlayer translatablePlayer, Location location, float distanceBetweenLines, MessageHolder text);
	
	public abstract NameTagService newNameTagTeamService(CorePlayer corePlayer, int tabListPriority);
	
	public abstract LivingEntityNPC newNPC(final JavaPlugin plugin, final NonPlayerCharTypes type, final Location location, boolean enableTracker, boolean hologramManager);
	
	public abstract GameReflections getGameReflections();
	
	public abstract int getPing(Player player);
	
	public abstract String getPlayerLocale(Player player);
	
	public abstract void respawn(Player player);
	
	public abstract void sendActionbarMessage(Player player, String text);
	
	public abstract void sendTitleMessage(Player player, String title, String subtitle, int fadeIn, int fadeStay, int fadeOut);
	
	public abstract ItemMeta setItemUnbreakable(ItemMeta meta, boolean unbreakable);
	
	public abstract void setPlayerListData(Player player, List<String> header, List<String> footer);
	
	public abstract void updateInventory(Player player);
	
	public abstract CoreRegistry<CoreParticle> getCoreParticleRegistry();
	
	public abstract CoreParticleData fromBlockData(Block block);
	
	public abstract CoreParticleData fromMaterialData(Material material, int legacyId);
	
	public abstract CoreParticleData fromDustOptions(org.bukkit.Color color, float size);
	
	public abstract CoreParticleData fromDustTransition(org.bukkit.Color fromColor, org.bukkit.Color toColor, float size);
	
	public abstract CoreParticleData fromVibration(Location origin, Location destination, int arrivalTime);
	
	public abstract CoreParticleData fromVibration(Location origin, Entity destination, int arrivalTime);
	
	public abstract CoreParticleData fromTrail(Location target, org.bukkit.Color color, int duration);
	
	/**
	 * The specified players only receives the packet
	 * @param translatablePlayers iterable {@link TranslatablePlayer}
	 */
	public abstract CoreScoreboard newScoreboardForSomePlayers(Iterable<TranslatablePlayer> translatablePlayers);
	
	/**
	 * The specified player only receives the packet
	 * @param translatablePlayer {@link TranslatablePlayer}
	 */
	public abstract CoreScoreboard newScoreboardForPlayer(TranslatablePlayer translatablePlayer);

	public abstract CoreBossBar newBossBarForPlayer(TranslatablePlayer player, MessageHolder title, CoreBarColor barColor, CoreBarStyle barStyle);
	
	public abstract CoreNumberFormat newNumberFormatByName(String type); 
	
	public abstract NickNamerService newNickNamerService(Player player);
	
	public abstract CoreTeamHolder newTeamHolder(String teamName);
	
	public abstract CoreTeamManager newTeamManager(Player player);
	
	public abstract CoreItemFrame newItemFrame(Player player, Location location, BlockFace facing, ActivationRange activationRange, boolean glowing);
	
	public void onPlayerJoin(Player player) { }
	
	public void onPlayerQuit(Player player) { }
	
	public MessageHolder getColoredPing(Player player) {
		int ping = this.getPing(player);
		return MessageHolder.text((ping <= 55 ? "<green>" : ping <= 70 ? "<yellow>" : ping <= 100 ? "<gold>" : "<red>") + ping + "ms");
	}
	
	public ServerVersion getVersion() {
		return ServerVersion.current();
	}
}
