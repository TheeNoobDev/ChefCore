package net.chefcraft.world.hologram;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslatablePlayer;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractHologramManager<K> {
	
	protected Map<K, Hologram> hologramsByKey = new HashMap<>();
	protected float distanceBetweenLines = 0.3F;
	
	protected Hologram registerNewRaw(@NotNull TranslatablePlayer translatablePlayer, @NotNull Location location, @NotNull K key, @NotNull MessageHolder lines) {
		Objects.requireNonNull(translatablePlayer, "translatablePlayer cannot be null!");
		Objects.requireNonNull(location, "location cannot be null!");
		Objects.requireNonNull(key, "key cannot be null!");
		Objects.requireNonNull(lines, "lines cannot be null!");
		
		if (!this.hologramsByKey.containsKey(key)) {
			Hologram hologram = ChefCore.getReflector().newHologram(translatablePlayer, location.clone(), distanceBetweenLines, lines);
			this.hologramsByKey.put(key, hologram);
			return hologram;
		} else {
			throw new IllegalStateException("hologram '" + key + "' alread exist!");
		}
	}
	
	public boolean hasHologram(@NotNull K key) {
		return this.hologramsByKey.containsKey(key);
	}
	
	/**
	 * @param key key
	 * @return if hologram is not null returns its self else throws {@link IllegalStateException}
	 */
	public Hologram getHologramByKey(@NotNull K key) {
		Hologram hologram = this.hologramsByKey.get(key);
		
		if (hologram == null) {
			throw new IllegalStateException("hologram '" + key + "' not exist!");
		}
		
		return hologram;
	}
	
	public void reloadAll() {
		for (Hologram hologram : this.hologramsByKey.values()) {
			hologram.reload();
		}
	}
	
	/**
	 * @param key key
	 * @returns reloaded {@link Hologram} or if hologram is null throws {@link IllegalStateException}
	 */
	public Hologram reload(@NotNull K key) {
		Hologram hologram = this.getHologramByKey(key);
		hologram.reload();
		return hologram;
	}
	
	public void removeAll() {
		for (Hologram hologram : this.hologramsByKey.values()) {
			hologram.remove();
		}
		this.hologramsByKey.clear();
	}
	
	/**
	 * @param key key
	 * @returns removed {@link Hologram} or if hologram is null throws {@link IllegalStateException}
	 */
	public Hologram remove(@NotNull K key) {
		Hologram hologram = this.getHologramByKey(key);
		hologram.remove();
		this.hologramsByKey.remove(key);
		return hologram;
	}
	
	public Hologram updateHologramLines(@NotNull K key, @NotNull MessageHolder lines) {
		Hologram hologram = this.getHologramByKey(key);
		hologram.setText(lines);
		return hologram;
	}
	
	public Hologram updateHologramLocation(@NotNull K key, @NotNull Location location) {
		Hologram hologram = this.getHologramByKey(key);
		hologram.updateLocation(location.clone());
		return hologram;
	}
	
	public void updateLocation(@NotNull Location location) {
		for (Hologram hologram : this.hologramsByKey.values()) {
			hologram.updateLocation(location);
		}
	}

	public float getDistanceBetweenLines() {
		return distanceBetweenLines;
	}

	public void setDistanceBetweenLines(float distanceBetweenLines) {
		this.distanceBetweenLines = distanceBetweenLines;
	}
}
