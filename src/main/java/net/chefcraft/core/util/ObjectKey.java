package net.chefcraft.core.util;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Used to store, match, path or indicate objects
 * @since 1.3.0
 */
public interface ObjectKey {
	
	@Nonnull
	String getKey();
	
	@Nonnull
	static ObjectKey key(@Nonnull String key) {
		return new ObjectKeyImpl(key);
	}
	
	final class ObjectKeyImpl implements ObjectKey {

		final String key;
		
		ObjectKeyImpl(@Nonnull String key) {
			this.key = Objects.requireNonNull(key, "key cannot be null!");
		}
		
		@Override
		public String getKey() {
			return this.key;
		}
		
		@Override
		public String toString() {
			return "{key=" + this.key + "}";
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}
			if (o instanceof ObjectKey && this.getKey().equals(((ObjectKey) o).getKey())) {
				return true;
			}
			return false;
		}
		
	}
	
}
