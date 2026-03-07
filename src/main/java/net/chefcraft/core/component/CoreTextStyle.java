package net.chefcraft.core.component;

import com.google.common.collect.ImmutableList;
import net.chefcraft.core.registry.CoreRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

/**@since 1.3.0*/
public class CoreTextStyle implements CoreTextBase {

	public static final CoreTextStyle OBFUSCATED 	 = new CoreTextStyle('k', "obfuscated");
	public static final CoreTextStyle BOLD 			 = new CoreTextStyle('l', "bold");
	public static final CoreTextStyle STRIKETHROUGH  = new CoreTextStyle('m', "strikethrough");
	public static final CoreTextStyle UNDERLINED 	 = new CoreTextStyle('n', "underlined");
	public static final CoreTextStyle ITALIC 		 = new CoreTextStyle('o', "italic");
	public static final CoreTextStyle RESET 		 = new CoreTextStyle('r', "reset");
	
	private static final CoreRegistry<CoreTextStyle> REGISTRY = new CoreRegistry<>(CoreTextStyle.class);
	private static final List<CoreTextStyle> LIST = ImmutableList.copyOf(REGISTRY.values());
	
	private final char character;
	private final String name;
	private final String format;
	
	private CoreTextStyle(@NotNull final char character, @NotNull final String name) {
		this.character = character;
		this.name = name;
		this.format = new String(new char[] {ComponentProvider.COLOR_CHAR, character});
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
	
	@Override
	public boolean isStyle() {
		return true;
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
		
		if (other instanceof CoreTextStyle) {
			CoreTextStyle s = (CoreTextStyle) other;
			if (s.name.equals(this.name) && s.character == this.character) {
				return true;
			}
		}
		
		return false;
	}
	
	@Nullable
	public static CoreTextStyle getLastStyle(String input) {
		Matcher matcher = ComponentProvider.TEXT_STYLE_PATTERN.matcher(input);
		CoreTextStyle last = null;
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
	public static List<CoreTextStyle> list() {
		return LIST;
	}
	
	@NotNull
	public static CoreRegistry<CoreTextStyle> registry() {
		return REGISTRY;
	}
	
	@Nullable
	public static CoreTextStyle getByName(@NotNull String name) {
		return REGISTRY.getByKey(Objects.requireNonNull(name, "name cannot be null"));
	}
	
	@Nullable
	public static CoreTextStyle getByChar(char character) {
		return REGISTRY.getByFilter(filter -> filter.character == character);
	}
}
