package net.chefcraft.core.event;

import net.chefcraft.world.player.CorePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CorePlayerJoinEvent extends Event {
	
	private static final HandlerList HANDLER_LIST = new HandlerList();

	private final CorePlayer corePlayer;
	
	public CorePlayerJoinEvent(@NotNull CorePlayer corePlayer) {
		Objects.requireNonNull(corePlayer, "corePlayer cannot be null!");
		this.corePlayer = corePlayer;
	}

	public @NotNull CorePlayer getCorePlayer() {
		return corePlayer;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLER_LIST;
	}
	
	public static @NotNull HandlerList getHandlerList() {
		return HANDLER_LIST;
	}
}
