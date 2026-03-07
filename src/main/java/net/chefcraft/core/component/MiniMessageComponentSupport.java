package net.chefcraft.core.component;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**@since 1.3.0*/
public class MiniMessageComponentSupport implements ComponentSupport {
	
	private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
	
	@Override
	public @NotNull Component deserialize(@NotNull String input, RandomColor randomColor) {
		Objects.requireNonNull(input, "cannot deserialize null input!");
		input = ComponentProvider.legacyColorCodesToMiniMessageCodes(input);
		input = deserializeRandomColor(input, randomColor != null ? randomColor : ComponentProvider.RANDOM_COLOR, false);
		return MINI_MESSAGE.deserialize(input);
	}

	@Override
	public @NotNull String strip(@NotNull String input) {
		return MINI_MESSAGE.stripTags(ComponentProvider.COMPONENT_SUPPORT_PLATFORM_BUKKIT.strip(input));
	}
}
