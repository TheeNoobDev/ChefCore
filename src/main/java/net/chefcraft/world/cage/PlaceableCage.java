package net.chefcraft.world.cage;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.math.BoundingBox;
import net.chefcraft.core.util.Pair;
import net.chefcraft.reflection.world.GameReflections;
import net.chefcraft.world.cage.CageLayer.CageBlock;
import net.chefcraft.world.cage.animation.AbstractCageAnimation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class PlaceableCage {

	private static final GameReflections REFLECTOR = ChefCore.getReflector().getGameReflections();
	
	private Cage cage = null;
	private Location location = null;
	private Location modifiedLocation = null;
	private BukkitTask task = null;
	private boolean placed = false;
	private AbstractCageAnimation anim = null;
	private BoundingBox boundingBox = null;
	
	private List<Location> effectedLocations = new ArrayList<>();
	private List<List<Pair<Block, CageBlock>>> animationsListed = new ArrayList<>();
	
	public PlaceableCage() { }
	
	public void place(Location location, boolean animation) {
		if (placed) return; 
		this.placed = true;
		this.location = location.clone();
		
		this.modifiedLocation = location.clone().subtract(1, 2, 1);
		
		this.cage = this.getCage(); //checks for nonnull
		
		Set<Entry<Integer, CageLayer>> mapSet = cage.getCageLayerMap().entrySet();
		
		for (Entry<Integer, CageLayer> entry : mapSet) {
			List<Pair<Block, CageBlock>> anims = new ArrayList<>();
			
			entry.getValue().placeLayer(effectedLocations, anims, this.location.getWorld(), this.modifiedLocation.getBlockX(), 
					this.modifiedLocation.getBlockY() + entry.getKey(), this.modifiedLocation.getBlockZ());
			
			animationsListed.add(anims);
		}
		
		Location first = this.location.clone().add(3.0D, mapSet.size() - 2.0D, 3.0D);
		Location second = this.location.clone().subtract(2.0D, 1.0D, 2.0D);
		this.boundingBox = new BoundingBox(first, second);
		
		if (cage.getAnimation() == null || !cage.isAnimated() || !animation) return;
		
		this.anim = cage.getAnimation().clone();
		this.anim.prepare();
		this.anim.setBoundingBox(this.boundingBox);
		final World world = first.getWorld();
		
		
		this.task = new BukkitRunnable() {

			public void run() {
				anim.tick(world);
				
				for (List<Pair<Block, CageBlock>> anims : animationsListed) {
					for (Pair<Block, CageBlock> pair : anims) {
						MaterialData sec = pair.getSecond().nextMaterial();
						REFLECTOR.setBlockType(pair.getFirst(), sec.getItemType(), sec.getData());
					}
				}
			}
		}.runTaskTimer(ChefCore.getInstance(), 0L, anim.getInterval());
	}
	
	public Location getFixedLocation() {
		return location == null ? null : cage.fixLocation(location.clone());
	}
	
	public void remove() {
		if (!placed) return;
		this.placed = false;
		
		if (task != null) {
			task.cancel();
		}
		
		this.animationsListed.clear();
		
		for (int i = 0; i < effectedLocations.size(); i++) {
			effectedLocations.get(i).getBlock().setType(Material.AIR);
		}
		this.effectedLocations.clear();
		this.location = null;
		this.boundingBox = null;
	}
	
	public boolean stopAnimationIfHas() {
		
		if (task != null) {
			task.cancel();
			return true;
		}
		
		return false;
	}
	
	public Location getPlaceLocation() {
		return this.location;
	}
	
	public BoundingBox getCageBounds() {
		return this.boundingBox;
	}
	
	@Nonnull
	public Cage getCage() {
		if (cage == null) {
			return CageUtils.getDefaultCage();
		}
		return cage;
	}

	public PlaceableCage setCage(Cage cage) {
		this.cage = cage;
		return this;
	}
	
	public boolean isPlaced() {
		return placed;
	}
}
