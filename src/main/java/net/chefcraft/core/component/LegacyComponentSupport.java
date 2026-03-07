package net.chefcraft.core.component;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

/**@since 1.3.0*/
public class LegacyComponentSupport implements ComponentSupport {
	
	public static final Pattern CHAT_COLOR_PATTERN = Pattern.compile("(?i)[§&][0-9A-FK-ORX]");
	
	@Override
	public @NotNull String deserialize(@NotNull String input, RandomColor randomColor) {
		Objects.requireNonNull(input, "cannot deserialize null input!");
		input = ComponentProvider.translateAlternateColorCodes('&', input);
		input = deserializeTextColors(input);
		input = deserializeTextStyles(input);
		input = deserializeHexColors(input);
		input = deserializeRandomColor(input, randomColor != null ? randomColor : ComponentProvider.RANDOM_COLOR, true);
		input = deserializeGradient(input);
		return input;
	}

	@Override
	public @NotNull String strip(@NotNull String input) {
		Objects.requireNonNull(input, "input cannot be null!");
		return CHAT_COLOR_PATTERN.matcher(input).replaceAll("").replaceAll("<(randomcolor|center|/center)>", "");
	}
}
