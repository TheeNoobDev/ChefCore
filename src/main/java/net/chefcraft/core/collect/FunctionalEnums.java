package net.chefcraft.core.collect;

import net.chefcraft.core.util.Numbers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class FunctionalEnums {
	
	@NotNull
	public static Iterator<Enum<?>> iterator(@NotNull Enum<?> ...es) {
		return ArrayIterator.of(Objects.requireNonNull(es, "enums cannot be null!"));
	}
	
	@NotNull
	public static List<Enum<?>> filter(@NotNull Predicate<Enum<?>> filter, @NotNull Enum<?> ...es) {
		Objects.requireNonNull(filter, "filter cannot be null!");
		Objects.requireNonNull(es, "enums cannot be null!");
		
		final List<Enum<?>> list = new ArrayList<>();
		
		for (int i = 0; i < es.length; i++) {
			if (filter.test(es[i])) {
				list.add(es[i]);
			}
		}
		return list;
	}
	
	@NotNull
	public static <R> List<R> map(@NotNull Function<Enum<?>, R> function, @NotNull Enum<?> ...es) {
		Objects.requireNonNull(function, "function cannot be null!");
		Objects.requireNonNull(es, "enums cannot be null!");
		
		final List<R> list = new ArrayList<>();
		
		for (int i = 0; i < es.length; i++) {
			list.add(function.apply(es[i]));
		}
		return list;
	}
	
	@NotNull
	public static <R> List<R> filterAndMap(@NotNull Predicate<Enum<?>> filter, @NotNull Function<Enum<?>, R> function, @NotNull Enum<?> ...es) {
		Objects.requireNonNull(filter, "filter cannot be null!");
		Objects.requireNonNull(function, "function cannot be null!");
		Objects.requireNonNull(es, "enums cannot be null!");
		
		final List<R> list = new ArrayList<>();
		
		for (int i = 0; i < es.length; i++) {
			if (filter.test(es[i])) {
				list.add(function.apply(es[i]));
			}
		}
		return list;
	}
	
	@Nullable
	public static Enum<?> getByFilter(@NotNull Predicate<Enum<?>> filter, @NotNull Enum<?> ...es) {
		Objects.requireNonNull(filter, "filter cannot be null!");
		Objects.requireNonNull(es, "enums cannot be null!");

		for (int i = 0; i < es.length; i++) {
			if (filter.test(es[i])) {
				return es[i];
			}
		}
		
		return null;
	}
	
	/**
	 * @param ordinal index
	 * @return value
	 * @throws {@link IllegalArgumentException}
	 */
	@NotNull
	public static Enum<?> getByOrdinal(int ordinal, @NotNull Enum<?> ...es) {
		Objects.requireNonNull(es, "enums cannot be null!");
		return es[Numbers.checkRange(ordinal, 0, es.length - 1)];
	}
	
	@NotNull
	public static Enum<?> first(@NotNull Enum<?> ...es) {
		Objects.requireNonNull(es, "enums cannot be null!");
		return es[0];
	}
	
	@NotNull
	public static Enum<?> last(@NotNull Enum<?> ...es) {
		Objects.requireNonNull(es, "enums cannot be null!");
		return es[es.length - 1];
	}
}
