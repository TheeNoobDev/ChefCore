package net.chefcraft.core.sound;

import net.chefcraft.core.language.TranslatablePlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public interface SoundEntry {
	
	@Nonnull String getKey();
	
	void playSound(@Nonnull Location location);
	
	void playSound(@Nonnull Player player);
	
	default void playSound(@Nonnull Iterable<Player> players) {
		for (Player player : players) {
			this.playSound(player);
		}
	}
	
	default void playSound(@Nonnull Player...players) {
		for (Player player : players) {
			this.playSound(player);
		}
	}
	
	default void playSound(@Nonnull TranslatablePlayer translatablePlayer) {
		this.playSound(translatablePlayer.getPlayer());
	}
	
	default void playSound(@Nonnull TranslatablePlayer...translatablePlayers) {
		for (TranslatablePlayer translatablePlayer : translatablePlayers) {
			this.playSound(translatablePlayer.getPlayer());
		}
	}
}
