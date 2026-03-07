package net.chefcraft.core.server;

import net.chefcraft.core.util.Numbers;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

public enum Minigame {

	EGGWARS("EggWars", "net.chefcraft.eggwars.EggWars");
	
	private final String stringValue;
	private final String mainClassPath;
	
	Minigame(String stringValue, String mainClassPath) {
		this.stringValue = stringValue;
		this.mainClassPath = mainClassPath;
	}
	
	public String getStringValue() {
		return this.stringValue;
	}
	
	public String getMainClassPath() {
		return this.mainClassPath;
	}
	
	public boolean isEnabled() {
		try {
			Class.forName(this.mainClassPath);
			return true;
		} catch (Exception x) {
			return false;
		}
	}
	
	private static final Minigame[] MINIGAMES = Minigame.values();
	
	@Nullable
	public static Minigame matchMinigame(String value) {
		for (Minigame game : MINIGAMES) {
			if (game.stringValue.equalsIgnoreCase(value)) {
				return game;
			}
		}
		return null;
	}
	
	@NotNull
	public static Minigame getByOrdinal(int ordinal) {
		return MINIGAMES[Numbers.checkRange(ordinal, 0, MINIGAMES.length - 1)];
	}
	
	public static Minigame[] asArray() {
		return MINIGAMES;
	}
	
	public static void forEach(@Nonnull Consumer<Minigame> action) {
		Objects.requireNonNull(action);
		
		for (int i = 0; i < MINIGAMES.length; i++) {
			action.accept(MINIGAMES[i]);
		}
	}
}
