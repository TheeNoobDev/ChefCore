package net.chefcraft.world.particle;

import net.chefcraft.core.ChefCore;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface CoreParticleData {
	
	CoreParticleData EMPTY = from(null);
	
	@Nullable
	<T> T getData();
	
	static <T> CoreParticleData from(@Nullable final T data) {
		return new CoreParticleData() {
			@SuppressWarnings("unchecked")
			public T getData() {
				return data;
			}
		};
	}
	
	static CoreParticleData fromColor(Color color) {
		return from(color);
	}
	
	static CoreParticleData fromItemStack(ItemStack itemStack) {
		return from(itemStack);
	}
	
	static CoreParticleData fromFloat(float value) {
		return from(value);
	}
	
	static CoreParticleData fromInteger(int value) {
		return from(value);
	}
	
	static CoreParticleData fromBlockData(Block block) {
		return ChefCore.getReflector().fromBlockData(block);
	}
	
	static CoreParticleData fromMaterialData(Material material, int legacyId) {
		return ChefCore.getReflector().fromMaterialData(material, legacyId);
	}
	
	static CoreParticleData fromDustOptions(Color color, float size) {
		return ChefCore.getReflector().fromDustOptions(color, size);
	}
	
	static CoreParticleData fromDustTransition(Color fromColor, Color toColor, float size) {
		return ChefCore.getReflector().fromDustTransition(fromColor, toColor, size);
	}
	
	static CoreParticleData fromVibration(Location origin, Location destination, int arrivalTime) {
		return ChefCore.getReflector().fromVibration(origin, destination, arrivalTime);
	}
	
	static CoreParticleData fromVibration(Location origin, Entity destination, int arrivalTime) {
		return ChefCore.getReflector().fromVibration(origin, destination, arrivalTime);
	}
	
	static CoreParticleData fromTrail(Location target, Color color, int duration) {
		return ChefCore.getReflector().fromTrail(target, color, duration);
	}
}
