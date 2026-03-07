package net.chefcraft.core.util;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class MinecraftUtil {
	
	public static final Duration TITLE_FADE_IN_DURATION  = durationOfTicks(10);
	public static final Duration TITLE_STAY_DURATION     = durationOfTicks(70);
	public static final Duration TITLE_FADE_OUT_DURATION = durationOfTicks(20);
	
	@NotNull
	public static Duration durationOfTicks(long ticks) {
		return Duration.ofMillis(ticks * 50);
	}

}
