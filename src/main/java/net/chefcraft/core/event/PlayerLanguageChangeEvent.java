package net.chefcraft.core.event;

import net.chefcraft.core.language.Language;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class PlayerLanguageChangeEvent<P> extends Event {

	private static final HandlerList HANDLER_LIST = new HandlerList();
	
	private final Cause cause;
	private final P player;
	private final Language language;
	
	public PlayerLanguageChangeEvent(@Nonnull Cause cause, @Nonnull P player, @Nonnull Language language) {
		this.cause = cause;
		this.player = player;
		this.language = language;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	@Nonnull
	public Cause getCause() {
		return this.cause;
	}

	@Nonnull
	public P getPlayer() {
		return this.player;
	}

	@Nonnull
	public Language getLanguage() {
		return this.language;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}
	
	public static enum Cause {
		CONSOLE_COMMAND, 
		PLAYER_COMMAND, 
		FIRST_LOAD, 
		UNKNOWN;
	}
}
