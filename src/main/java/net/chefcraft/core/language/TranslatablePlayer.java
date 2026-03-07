package net.chefcraft.core.language;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.annotation.JustBukkit;
import net.chefcraft.core.sound.AbstractSoundManager;
import net.chefcraft.core.util.MinecraftUtil;
import net.chefcraft.world.util.MethodProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;

@JustBukkit
public interface TranslatablePlayer extends TranslationSource {
	
	/**
	 * Gets {@link org.bukkit.entity.Player}
	 * @return player's instance
	 */
	@NotNull 
	@JustBukkit
	org.bukkit.entity.Player getPlayer();
	
	/**
	 * @return {@link AbstractSoundManager}
	 */
	@NotNull 
	AbstractSoundManager getSoundManager();
	
	/**
	 * Adds a slight delay when closing inventory
	 */
	void safelyCloseExistingMenu();
	
	/**
	 * @return display name as {@link MessageHolder}
	 */
	@NotNull
	MessageHolder getDisplayName();
	
	/**
	 * Sets display name
	 * @param displayName display name
	 */
	void setDisplayName(@Nullable MessageHolder displayName);
	
	/**
	 * Send packets to player
	 * @param packet must be {@link net.minecraft.network.protocol.Packet}!
	 */
	default void sendPacket(@NotNull Object packet) {
		Objects.requireNonNull(packet, "packet cannot be null!");
		ChefCore.getReflector().sendPacket(this.getPlayer(), packet);
	}
	
	/**
	 * Send packets to player
	 * @param packets must be {@link Iterable} -> {@link net.minecraft.network.protocol.Packet}!
	 */
	default void sendPackets(@NotNull Iterable<? extends Object> packets) {
		Objects.requireNonNull(packets, "packets cannot be null!");
		for (Object packet : packets) {
			this.sendPacket(packet);
		}
	}
	
	/**
	 * Plays sound from {@link #getSoundManager()}
	 * @param key sound key
	 */
	default void playSound(String key) {
		this.getSoundManager().playSound(this.getPlayer(), key);
	}
	
	/**
	 * Sends chat message to source
	 * @param message message
	 */
	default void sendMessage(@NotNull MessageHolder message) {
		MethodProvider.SEND_MESSAGE.accept(this, message);
	}
	
	/**
	 * Sends action bar message to source
	 * @param message message
	 */
	default void sendActionBar(@NotNull MessageHolder message) {
		MethodProvider.SEND_ACTION_BAR_MESSAGE.accept(this, message);
	}
	
	/**
	 * Shows title message to source
	 * @param title first large text of the screen
	 * @param subtitle second small text of the screen
	 * @param fadeIn the duration of the text fade in
	 * @param stay the duration of the text on the screen
	 * @param fadeOut the duration of the text fade out
	 */
	default void showTitle(@NotNull MessageHolder title, @NotNull MessageHolder subtitle, @NotNull Duration fadeIn, 
			@NotNull Duration stay, @NotNull Duration fadeOut) {
		MethodProvider.SEND_TITLE_MESSAGE.show(this, title, subtitle, fadeIn, stay, fadeOut);
	}
	
	/**
	 * Shows title message to source
	 * @param title first large text of the screen
	 * @param subtitle second small text of the screen
	 */
	default void showTitle(@NotNull MessageHolder title, @NotNull MessageHolder subtitle) {
		MethodProvider.SEND_TITLE_MESSAGE.show(this, title, subtitle, MinecraftUtil.TITLE_FADE_IN_DURATION, MinecraftUtil.TITLE_STAY_DURATION, MinecraftUtil.TITLE_FADE_OUT_DURATION);
	}
}
