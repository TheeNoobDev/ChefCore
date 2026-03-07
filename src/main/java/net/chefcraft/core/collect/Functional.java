package net.chefcraft.core.collect;

import net.chefcraft.core.util.JHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface Functional<T> extends Iterable<T> {
	
	Collection<T> values();
	
	default Stream<T> stream() {
		return this.values().stream();
	}
	
	default @NotNull Iterator<T> iterator() {
		return this.values().iterator();
	}
	
	default Collection<T> filter(@NotNull Predicate<T> filter) {
		return JHelper.filterCollection(this.values(), filter);
	}
	
	default <R> Collection<R> map(@NotNull Function<T, R> function) {
		return JHelper.mapCollectionToList(this.values(), function);
	}
	
	default <R> Collection<R> filterAndMap(@NotNull Predicate<T> filter, @NotNull Function<T, R> function) {
		return JHelper.filterAndMapCollection(this.values(), filter, function);
	}
	
	default T getByFilter(@NotNull Predicate<T> filter) {
		return JHelper.getFirstMatches(this, filter);
	}
	
	default <R> T getByFunction(@NotNull Function<T, R> function, R result) {
		return JHelper.getFirstMatches(this, function, result);
	}
	
	@Nullable
	default T first() {
		return JHelper.getFirst(this);
	}
	
	@Nullable
	default T last() {
		return JHelper.getLast(this);
	}
}
