package net.chefcraft.world.event;

import net.chefcraft.world.itemframe.CoreItemFrame;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

/**
 * This event listening for client side entities for gamecore
 * <pre>
 * classes that use this events:
 * - {@link CoreItemFrame}
 */
public class PlayerPunchToEntityEvent extends PlayerInteractAtEntityEvent {
	
	private final boolean attack;

	public PlayerPunchToEntityEvent(@Nonnull Player who, @Nonnull Entity clickedEntity, @Nonnull Vector position, boolean attack) {
		super(who, clickedEntity, position);
		this.attack = attack;
	}
	
	public PlayerPunchToEntityEvent(@Nonnull Player who, @Nonnull Entity clickedEntity, @Nonnull Vector position, @Nonnull EquipmentSlot slot, boolean attack) {
		super(who, clickedEntity, position, slot);
		this.attack = attack;
	}
	
	public boolean isAttack() {
		return this.attack;
	}

}
