package net.chefcraft.reflection.base.utils;

import net.chefcraft.core.math.SimpleMath;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.Comparator;

public class DistanceComparator implements Comparator<Entity> {

	public double x;
	public double y;
	public double z;
	
	public DistanceComparator(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public DistanceComparator(Location loc) {
		this.x = loc.getX();
		this.y = loc.getY();
		this.z = loc.getZ();
	}
	
	@Override
	public int compare(Entity first, Entity second) {
		return (int) (SimpleMath.distanceSquared(x, y, z, first.getX(), first.getY(), first.getZ())
                - SimpleMath.distanceSquared(x, y, z, second.getX(), second.getY(), second.getZ()));
	}

}
