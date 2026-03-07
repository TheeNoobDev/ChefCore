package net.chefcraft.reflection.version.v1_21_R3.itemframe;

import net.chefcraft.core.annotation.Clientbound;
import net.chefcraft.core.math.SimpleMath;
import net.chefcraft.core.util.ActivationRange;
import net.chefcraft.core.util.PreparedConditions;
import net.chefcraft.world.itemframe.CoreItemFrame;
import net.chefcraft.world.itemframe.CoreMapPatch;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ChefItemFrame implements CoreItemFrame {
	
	public static final Map<Integer, ChefItemFrame> ITEM_FRAME_TRACKER = new ConcurrentHashMap<>();
	
	private static final MapId EMPTY_OR_NULL = new MapId(-1);

	protected final ServerPlayer player;
	private final ItemFrame itemFrame;
    private final Optional<MapId> mapId;
	private Location location;
	private boolean alive = false;
	private CoreMapPatch patch = null;
	private ActivationRange activationRange;
    private net.minecraft.world.item.ItemStack mcItemStack;
	
	public ChefItemFrame(Player bukkitPlayer, Location location, BlockFace facing, ActivationRange activationRange, int mapId, boolean glowing) {
		this.player = ((CraftPlayer) bukkitPlayer).getHandle();
		this.location = location.clone();
		this.activationRange = activationRange;
        this.mapId = Optional.of(new MapId(mapId));
		
		ServerLevel level = ((CraftWorld) location.getWorld()).getHandle();
		BlockPos pos = CraftLocation.toBlockPosition(location);
		Direction direction = CraftBlock.blockFaceToNotch(facing);
		
		this.itemFrame = glowing ? new GlowItemFrame(level, pos, direction) : new ItemFrame(level, pos, direction);
		this.itemFrame.setNoGravity(true);
		this.itemFrame.setSilent(true);
		this.itemFrame.setInvulnerable(true);
				
	}
	
	public ClientboundAddEntityPacket createAddPacket() {
		
		return new ClientboundAddEntityPacket(this.itemFrame.getId(),
				this.itemFrame.getUUID(),
				this.itemFrame.getX(), 
				this.itemFrame.getY(), 
				this.itemFrame.getZ(),
				this.itemFrame.getXRot(),
				this.itemFrame.getYRot(),
				this.itemFrame.getType(),
				this.itemFrame.getDirection().get3DDataValue(), //direction
				Vec3.ZERO,
				this.itemFrame.getYHeadRot());
	}
	
	public ClientboundMapItemDataPacket createMapItemDataPacket() {
		return new ClientboundMapItemDataPacket(this.mapId.orElse(EMPTY_OR_NULL), (byte) 0, false, Optional.empty(),
				Optional.ofNullable(patch != null ? new MapItemSavedData.MapPatch(patch.minX, patch.minY, patch.maxX, patch.maxY, patch.colors) : null));
	}
	
	private void sendPacket(Packet<?> packet) {
		this.player.connection.send(packet);
	}
	
	@Override
	public boolean spawn() {
		if (!this.alive) {
			this.alive = true;
			this.sendPacket(this.createAddPacket());
			this.updateEntityData();
			
			this.sendPacket(this.createMapItemDataPacket());
			
			ITEM_FRAME_TRACKER.put(this.itemFrame.getId(), this);
			return true;
		}
		return false;
	}
	
	/**
	 * This just defines the object in the class. If you want the updated to be sent as a packet, do this: {@link ChefItemFrame#updateMapItemData()}
	 * 
	 * <pre>
	 * !! Do not forget to set the id of the map, 
	 * otherwise the client will not render the 
	 * map patch you applied. !!
	 * <newline>
	 * @param patch {@link CoreMapPatch}
	 */
	@Override
	public void setMapPatch(@NotNull CoreMapPatch patch) {
		this.patch = patch;
	}
	
	@Override
	public int getMapId() {
		return this.mapId.orElse(EMPTY_OR_NULL).id();
	}
	
	@Override
	public void updateMapItemData() {
		if (this.alive && SimpleMath.distanceSquared(this.player.getX(), this.player.getY(), this.player.getZ(),
                this.itemFrame.getX(), this.itemFrame.getY(), this.itemFrame.getZ()) <= this.activationRange.getDistanceSquared()) {
			this.sendPacket(this.createMapItemDataPacket());
		}
	}
	
	@Override
	public org.bukkit.entity.ItemFrame getBukkitHandle() {
		return (org.bukkit.entity.ItemFrame) this.itemFrame.getBukkitEntity();
	}
	
	@Override
	public void setBlockFace(BlockFace blockFace) {
		this.itemFrame.setDirection(CraftBlock.blockFaceToNotch(blockFace));
		this.updateEntityData();
	}
	
	@Override
	public void setItem(ItemStack item) {
        this.mcItemStack = CraftItemStack.asNMSCopy(item);
        this.mcItemStack.set(DataComponents.MAP_ID, this.mapId.orElse(EMPTY_OR_NULL));
		this.itemFrame.setItem(this.mcItemStack, false, false);
		this.updateEntityData();
	}

	@Override
	public Location getLocation() {
		return location.clone();
	}
	
	@Override
	public boolean remove() {
        if (this.alive) {
        	sendPacket(new ClientboundRemoveEntitiesPacket(this.itemFrame.getId()));
        	this.alive = false;
        	ITEM_FRAME_TRACKER.remove(this.itemFrame.getId());
        	return true;
        }
        return false;
	}
	
	@Override
	public void updateLocation(Location location) {
		if (!this.location.equals(location)) {
			this.location = location.clone();
			this.itemFrame.setLevel(((CraftWorld) location.getWorld()).getHandle());
			this.itemFrame.setPos(location.getX(), location.getY(), location.getZ());
			this.remove();
			this.createAddPacket();
		}
	}
	
	@Override
	public void updateEntityData() {
		if (this.alive) {
			sendPacket(new ClientboundSetEntityDataPacket(this.itemFrame.getId(),
                    this.itemFrame.getEntityData().packAll()));
		}
	}

    @Override
    public boolean isAlive() {
        return this.alive;
    }

	@Override
	public ActivationRange getActivationRange() {
		return activationRange;
	}

	@Override
	public void setActivationRange(ActivationRange activationRange) {
        PreparedConditions.notNull(activationRange, "activationRange");
        this.activationRange = activationRange;
	}
}
