package net.chefcraft.world.util;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.math.BoundingBox;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
public class DataSerializers {

	public static final String LOCATION_SERIALIZER_CHAR = ";";
	public static final String GENERATOR_SERIALIZER_CHAR = ":";

	public static <T> List<String> serializeLocationFromList(@Nonnull List<T> list, boolean addYawPitch, Function<T, Location> function) {
		List<String> emptyList = new ArrayList<>();

		for (T t : list) {
			emptyList.add(serializeLocation(function.apply(t), addYawPitch));
		}

		return emptyList;
	}

	public static List<String> serializeListLocation(@Nonnull List<Location> locationList, boolean addYawPitch) {
		List<String> emptyList = new ArrayList<>();

		for (Location pos : locationList) {
			emptyList.add(serializeLocation(pos, addYawPitch));
		}

		return emptyList;
	}

	public static String serializeLocation(@Nonnull Location location, boolean addYawPitch) {
		return    location.getX() + LOCATION_SERIALIZER_CHAR
				+ location.getY() + LOCATION_SERIALIZER_CHAR
				+ location.getZ() + LOCATION_SERIALIZER_CHAR
				+ (addYawPitch ? + location.getYaw() + LOCATION_SERIALIZER_CHAR + location.getPitch() : "");
	}

	public static String serializeLocation(@Nonnull World world, @Nonnull Location location, boolean addYawPitch) {
		return    world.getName() + LOCATION_SERIALIZER_CHAR
				+ location.getX() + LOCATION_SERIALIZER_CHAR
				+ location.getY() + LOCATION_SERIALIZER_CHAR
				+ location.getZ() + LOCATION_SERIALIZER_CHAR
				+ (addYawPitch ? + location.getYaw() + LOCATION_SERIALIZER_CHAR + location.getPitch() : "");
	}

	public static String serializeBoundingBox(@Nonnull BoundingBox box) {
		return    box.minX + LOCATION_SERIALIZER_CHAR
				+ box.minY + LOCATION_SERIALIZER_CHAR
				+ box.minZ + LOCATION_SERIALIZER_CHAR
				+ box.maxX + LOCATION_SERIALIZER_CHAR
				+ box.maxY + LOCATION_SERIALIZER_CHAR
				+ box.maxZ;
	}

	public static Location deserializeLocation(@Nonnull World world, @Nonnull String location) {
		String[] posArr = location.split(LOCATION_SERIALIZER_CHAR);

		double x = Float.valueOf(posArr[0]);
		double y = Float.valueOf(posArr[1]);
		double z = Float.valueOf(posArr[2]);
		if (posArr.length >= 4) {
			float yaw = Float.valueOf(posArr[3]);
			float pitch = Float.valueOf(posArr[4]);
			return new Location(world, x, y, z, yaw, pitch);
		}
		return new Location(world, x, y, z);
	}

	public static Location deserializeLocationWithWorld(@Nonnull String location) {
		String[] posArr = location.split(LOCATION_SERIALIZER_CHAR);

		World world = Bukkit.getWorld(posArr[0]);
		if (world == null) {
			ChefCore.log(Level.SEVERE, ": DataSerializers -> No world named &e'"+ posArr[0] +"' &cwas found. Returns: Main World");
			world = Bukkit.getWorlds().get(0);
		}
		double x = Float.valueOf(posArr[1]);
		double y = Float.valueOf(posArr[2]);
		double z = Float.valueOf(posArr[3]);
		if (posArr.length >= 5) {
			float yaw = Float.valueOf(posArr[4]);
			float pitch = Float.valueOf(posArr[5]);
			return new Location(world, x, y, z, yaw, pitch);
		}
		return new Location(world, x, y, z);
	}

	public static BoundingBox deserializeBoundingBox(@Nonnull String positionsAsString) {
		String[] posArr = positionsAsString.split(LOCATION_SERIALIZER_CHAR);

		if (posArr.length < 6) {
			throw new ArrayIndexOutOfBoundsException("An error occurred deserializing bounding box: array length must be 6! Your: " + posArr.length);
		}

		double fx = Float.parseFloat(posArr[0]),
		fy = Float.parseFloat(posArr[1]),
		fz = Float.parseFloat(posArr[2]),
		sx = Float.parseFloat(posArr[3]),
		sy = Float.parseFloat(posArr[4]),
		sz = Float.parseFloat(posArr[5]);

		return new BoundingBox(fx, fy, fz, sx, sy, sz);

	}
}
