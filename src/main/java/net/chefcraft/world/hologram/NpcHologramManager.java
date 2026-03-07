package net.chefcraft.world.hologram;

import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslatablePlayer;
import net.chefcraft.service.npc.LivingEntityNPC;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NpcHologramManager extends AbstractHologramManager<TranslatablePlayer> {

	private static final Vector LOCATION_OFFSET = new Vector(0.0f, 0.6f, 0.0);
	
	private final LivingEntityNPC owner;

	public NpcHologramManager(@NotNull LivingEntityNPC owner) {
		Objects.requireNonNull(owner, "owner cannot be null!");
		this.owner = owner;
	}
	
	@NotNull
	public LivingEntityNPC getOwner() {
		return this.owner;
	}
	
	public Hologram registerNew(@NotNull TranslatablePlayer translatablePlayer, @NotNull MessageHolder lines) {
		return super.registerNewRaw(translatablePlayer, 
				owner.getEntity().getEyeLocation().add(LOCATION_OFFSET),
				translatablePlayer, lines);
	}
}
