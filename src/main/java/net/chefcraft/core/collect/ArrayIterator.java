package net.chefcraft.core.collect;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;
import java.util.RandomAccess;

public final class ArrayIterator<T> implements Iterator<T>, RandomAccess {
	
	private static final ArrayIterator<?> EMPTY = new ArrayIterator<>();

	transient Object[] objects;
	transient int index = 0;
	
	ArrayIterator() {
		this.objects = new Object[0];
	}
	
	ArrayIterator(@SuppressWarnings("unchecked") T...ts) {
		int l = ts.length;
		this.objects = new Object[l];
		
		for (int i = 0; i < l; i++) {
			this.objects[i] = ts[i];
		}
	}
	
	@Override
	public boolean hasNext() {
		if (objects.length > index) {
			return true;
		}
		
		if (objects.length != index) {
			objects = EMPTY.objects;
			index = EMPTY.index;
		}
		
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T next() {
		return (T) objects[index++];
	}
	
	@NotNull
	public static ArrayIterator<?> empty() {
		return EMPTY;
	}
	
	@SuppressWarnings("unchecked")
	@NotNull
	public static <T> ArrayIterator<T> of(@NotNull T t) {
		return new ArrayIterator<>(Objects.requireNonNull(t));
	}
	
	@SuppressWarnings("unchecked")
	@NotNull
	public static <T> ArrayIterator<T> of(@NotNull T t, @NotNull T t1) {
		return new ArrayIterator<>(Objects.requireNonNull(t), Objects.requireNonNull(t1));
	}
	
	@NotNull
	public static <T> ArrayIterator<T> of(@SuppressWarnings("unchecked") @NotNull T...ts) {
		return new ArrayIterator<>(Objects.requireNonNull(ts));
	}
}
