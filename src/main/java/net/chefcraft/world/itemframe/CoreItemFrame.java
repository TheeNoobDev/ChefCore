package net.chefcraft.world.itemframe;

import net.chefcraft.core.util.ActivationRange;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * An interface to spawn item frames with packets
 */
public interface CoreItemFrame {

	ItemFrame getBukkitHandle();
	
	Location getLocation();
	
	ActivationRange getActivationRange();
	
	int getMapId();

	boolean spawn();
	
	boolean remove();
	
	boolean isAlive();

	void setActivationRange(ActivationRange activationRange);
	
	void setMapId(int id);
	
	void setBlockFace(BlockFace blockFace);
	
	void setItem(ItemStack item);
	
	void updateLocation(Location location);
	
	void updateEntityData();
	
	void updateMapItemData();
	
	/**
	 * This just defines the object in the class. If you want the updated to be sent as a packet, do this: {@link ChefItemFrame#updateMapItemData()}
	 * 
	 * <pre>
	 * !! Do not forget to set the id of the map, 
	 * otherwise the client will not render the 
	 * map patch you applied. !!
	 * <newline>
	 * @param patch {@link CoreMapPatch}
	 * @see {@link CoreItemFrame#setMapId()}
	 */
	void setMapPatch(@Nonnull CoreMapPatch patch);
}
