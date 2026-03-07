package net.chefcraft.core.component;

import net.chefcraft.core.util.ObjectKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**@since 1.3.0*/
public interface CoreTextBase extends ObjectKey {
	
	static char LEGACY_COLOR_CHAR = '\u00A7';

	@NotNull char character();
	
	@NotNull String name();
	
	boolean isStyle();
	
	String toString();
	
	@Nullable
	static CoreTextBase getByChar(char c) {
		CoreTextColor color = CoreTextColor.getByChar(c);
		return color != null ? color : CoreTextStyle.getByChar(c);
	}
	
	@Nullable
	static CoreTextBase getByName(String name) {
		CoreTextColor color = CoreTextColor.getByName(name);
		return color != null ? color : CoreTextStyle.getByName(name);
	}
	
	@Nullable
	static CoreTextBase getLastTextBase(String input) {
		CoreTextColor color = CoreTextColor.getLastColor(input);
		return color != null ? color : CoreTextStyle.getLastStyle(input);
	}
}
