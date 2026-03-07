package net.chefcraft.world.databridge;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.language.MessageCompiler;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslationSource;
import net.chefcraft.core.util.Numbers;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public enum GameStatus {

	WAITING("waiting"),
	STARTING("starting"),
	COUNTING("counting"),
	PLAYING("playing"),
	FINISHING("finishing"),
	SETTING("setting");
	
	private final String name;
	
	private GameStatus(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public @NotNull MessageHolder getTranslatedName(TranslationSource source) {
		return MessageCompiler.getMessage(ChefCore.getInstance(), source, "gameStatus." + this.name);
	}
	
	private static final GameStatus[] VALUES = values();
	
	@Nullable
	public static GameStatus getByName(String name) {
		for (int i = 0; i < VALUES.length; i++) {
			if (VALUES[i].name.equalsIgnoreCase(name)) {
				return VALUES[i];
			}
		}
		
		return null;
	}
	
	@NotNull
	public static GameStatus getByOrdinal(int ordinal) {
		return VALUES[Numbers.checkRange(ordinal, 0, VALUES.length - 1)];
	}
	
	@NotNull
	public static GameStatus[] asArray() {
		return VALUES;
	}
}
