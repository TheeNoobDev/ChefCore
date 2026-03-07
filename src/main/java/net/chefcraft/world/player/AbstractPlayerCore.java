package net.chefcraft.world.player;

import io.papermc.paper.annotation.DoNotUse;
import net.chefcraft.core.component.RandomColor;
import net.chefcraft.core.language.Language;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslatablePlayer;
import net.chefcraft.core.party.CorePartyManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class AbstractPlayerCore implements TranslatablePlayer {

	private final CorePlayer delegate;
	
	/**
	 * <pre>
	 * This constructor is only for the {@link CorePlayer}
	 * to create itself. Do not use it!
	 * !!{@link CorePlayer} already overrides all methods.!!
	 * (You cannot use this constructor even if you want to. :D ?)
	 */
	@DoNotUse
	AbstractPlayerCore() {
		this.delegate = null;
	}
	
	public AbstractPlayerCore(final @NotNull CorePlayer corePlayer) {
		Objects.requireNonNull(corePlayer, "corePlayer cannot be null!");
		this.delegate = corePlayer;
	}
	
	public CorePlayer getCorePlayer() {
		return this.delegate;
	}
	
	@Override
	public boolean isConsole() {
		return false;
	}
	
	@Override
	public void safelyCloseExistingMenu() {
		this.delegate.safelyCloseExistingMenu();
	}
	
	@Override
	public RandomColor getRandomColor() {
		return this.delegate.getRandomColor();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Player getAudience() {
		return this.delegate.getPlayer();
	}
	
	@Override
	public Player getPlayer() {
		return this.delegate.getPlayer();
	}

	@Override
	public @NotNull CorePartyManager getPartyManager() {
		return this.delegate.getPartyManager();
	}

	@Override
	public @NotNull MessageHolder getDisplayName() {
		return this.delegate.getDisplayName();
	}

	@Override
	public void setDisplayName(@Nullable MessageHolder displayName) {
		this.delegate.setDisplayName(displayName);
	}
	
	@Override
	public Language getLanguage() {
		return this.delegate.getLanguage();
	}
	
	public boolean isInParty() {
		return this.getPartyManager().isInParty();
	}
	
	public boolean isPartyMember() {
		return this.getPartyManager().isInParty() && !this.getPartyManager().isPartyOwner();
	}
	
	public boolean isPartyOwner() {
		return this.getPartyManager().isPartyOwner();
	}
}
