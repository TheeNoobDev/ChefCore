package net.chefcraft.world.hologram;

import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslatablePlayer;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PlayerHologramManager extends AbstractHologramManager<String> {

	private final TranslatablePlayer translatablePlayer;
	
	public PlayerHologramManager(@NotNull TranslatablePlayer translatablePlayer) {
		Objects.requireNonNull(translatablePlayer, "translatablePlayer cannot be null!");
		this.translatablePlayer = translatablePlayer;
	}

	@NotNull
	public TranslatablePlayer getTranslatablePlayer() {
		return this.translatablePlayer;
	}
	
	public Hologram registerNew(@NotNull Location location, @NotNull String key, @NotNull MessageHolder lines) {
		return super.registerNewRaw(this.translatablePlayer, location, key, lines);
	}
}
