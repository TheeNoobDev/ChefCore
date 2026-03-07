package net.chefcraft.core.component;

import com.google.common.collect.ImmutableList;
import net.chefcraft.core.PlatformProvider;
import net.chefcraft.core.registry.CoreRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

/**@since 1.3.0*/
public class CoreTextColor implements CoreTextBase {
	
	public static final CoreTextColor BLACK 		= new CoreTextColor('0', "black", 0x000000);
	public static final CoreTextColor DARK_BLUE 	= new CoreTextColor('1', "dark_blue", 0x0000AA);
	public static final CoreTextColor DARK_GREEN 	= new CoreTextColor('2', "dark_green", 0x00AA00);
	public static final CoreTextColor DARK_AQUA 	= new CoreTextColor('3', "dark_aqua", 0x00AAAA);
	public static final CoreTextColor DARK_RED 		= new CoreTextColor('4', "dark_red", 0xAA0000);
	public static final CoreTextColor DARK_PURPLE 	= new CoreTextColor('5', "dark_purple", 0xAA00AA);
	public static final CoreTextColor GOLD 			= new CoreTextColor('6', "gold", 0xFFAA00);
	public static final CoreTextColor GRAY 			= new CoreTextColor('7', "gray", 0xAAAAAA);
	public static final CoreTextColor DARK_GRAY 	= new CoreTextColor('8', "dark_gray", 0x555555);
	public static final CoreTextColor BLUE 			= new CoreTextColor('9', "blue", 0x5555FF);
	public static final CoreTextColor GREEN 		= new CoreTextColor('a', "green", 0x55FF55);
	public static final CoreTextColor AQUA 			= new CoreTextColor('b', "aqua", 0x55FFFF);
	public static final CoreTextColor RED 			= new CoreTextColor('c', "red", 0xFF5555);
	public static final CoreTextColor LIGHT_PURPLE 	= new CoreTextColor('d', "light_purple", 0xFF55FF);
	public static final CoreTextColor YELLOW 		= new CoreTextColor('e', "yellow", 0xFFFF55);
	public static final CoreTextColor WHITE 		= new CoreTextColor('f', "white", 0xFFFFFF);
	
	private static final CoreRegistry<CoreTextColor> REGISTRY = new CoreRegistry<>(CoreTextColor.class);
	private static final List<CoreTextColor> LIST = ImmutableList.copyOf(REGISTRY.values());

	private final char character;
	private final String name;
	private final String format;
	private final Color color;
	
	private CoreTextColor(@NotNull final char character, @NotNull final String name, @NotNull final int rgb) {
		this(character, name, new Color(rgb));
	}
	
	private CoreTextColor(@NotNull final char character, @NotNull final String name, @NotNull final Color color) {
		this.character = character;
		this.name = name;
		this.color = color;
		this.format = PlatformProvider.paperHasSupportingKyori() ? new String("<" + name + ">") : new String(new char[] {CoreTextBase.LEGACY_COLOR_CHAR, character});
	}

	@Override
	public @NotNull char character() {
		return this.character;
	}
	
	@Override
	public @NotNull String getKey() {
		return this.name;
	}

	@Override
	public @NotNull String name() {
		return this.name;
	}
	
	public @NotNull Color color() {
		return this.color;
	}
	
	@Override
	public boolean isStyle() {
		return false;
	}
	
	@Override
	public String toString() {
		return this.format;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		
		if (other instanceof CoreTextColor) {
			CoreTextColor c = (CoreTextColor) other;
			if (c.name.equals(this.name) && c.character == this.character && c.color.equals(this.color)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Nullable
	public static CoreTextColor getLastColor(String input) {
		Matcher matcher = ComponentProvider.TEXT_COLOR_PATTERN.matcher(input);
		CoreTextColor last = null;
		if (matcher.find()) {
			last = getByName(matcher.group(matcher.groupCount()).replaceAll("[><]", ""));
		}
		
		matcher = ComponentProvider.LEGACY_NAMED_COLOR_CODES_PATTERN.matcher(input);
		if (matcher.find()) {
			last = getByChar(matcher.group(matcher.groupCount()).charAt(1));
		}
		
		return last;
	}
	
	@NotNull
	public static List<CoreTextColor> list() {
		return LIST;
	}
	
	@NotNull
	public static CoreRegistry<CoreTextColor> registry() {
		return REGISTRY;
	}
	
	@Nullable
	public static CoreTextColor getByName(@NotNull String name) {
		return REGISTRY.getByKey(Objects.requireNonNull(name, "name cannot be null"));
	}
	
	@Nullable
	public static CoreTextColor getByChar(char character) {
		return REGISTRY.getByFilter(filter -> filter.character == character);
	}
	
	@Nullable
	public static CoreTextColor getByColor(int rgb) {
		return REGISTRY.getByFilter(filter -> filter.color.getRGB() == rgb);
	}
	
	@Nullable
	public static CoreTextColor getByColor(@NotNull Color color) {
		Objects.requireNonNull(color, "color cannot be null");
		return REGISTRY.getByFilter(filter -> filter.color.equals(color));
	}
}
