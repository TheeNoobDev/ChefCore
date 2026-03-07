package net.chefcraft.core.sound;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class SimpleSoundEntry implements SoundEntry {
	
	private final String key;
	protected final Sound sound;
	protected final float volume;
	protected final float pitch;
	
	public SimpleSoundEntry(@Nonnull String key, @Nonnull Sound sound, float volume, float pitch) { 
		this.key = key;
		this.sound = sound;
		this.volume = volume;
		this.pitch = pitch;
	}
	
	@Nonnull
	@Override
	public String getKey() {
		return this.key;
	}
	
	@Nonnull
	public Sound getSound() {
		return this.sound;
	}
	
	public float getVolume() {
		return this.volume;
	}
	
	public float getPitch() {
		return this.pitch;
	}

	@Override
	public void playSound(Player player) {
		player.playSound(player.getLocation(), this.sound, this.volume, this.pitch);
	}

	@Override
	public void playSound(Location location) {
		location.getWorld().playSound(location, this.sound, this.volume, this.pitch);
	}
}
