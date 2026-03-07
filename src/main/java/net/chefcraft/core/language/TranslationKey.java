package net.chefcraft.core.language;

import javax.annotation.Nonnull;

public interface TranslationKey extends Translatable {

	@Nonnull String getKey();
	
	@Nonnull
	static TranslationKey of(final @Nonnull String key) {
		return () -> key;
	}
}
