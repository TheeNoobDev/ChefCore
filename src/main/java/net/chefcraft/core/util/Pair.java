package net.chefcraft.core.util;

import java.util.Objects;

/** @since 1.0*/
public class Pair<F, S> {

	private F first;
	private S second;
	
	public Pair() {
		this(null, null);
	}
	
	public Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}
	
	public F getFirst() {
		return first;
	}
	
	public S getSecond() {
		return second;
	}
	
	public void setFirst(F first) {
		this.first = first;
	}
	
	public void setSecond(S second) {
		this.second = second;
	}
	
	@Override
	public Pair<F, S> clone() {
		return new Pair<>(this.first, this.second);
	}
	
	@Override
	public String toString() {
		return "Pair{first=" + this.first + ", second=" + this.second + "}";
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		
		if (object instanceof Pair) {
			Pair<?, ?> p = (Pair<?, ?>) object;
			
			if (Objects.equals(this.first, p.first) && Objects.equals(this.second, p.second)) {
				return true;
			}
		}
		
		return false;
	}
	
	private static final Pair<?, ?> EMPTY = new Pair<>(); 
	
	public static <F, S> Pair<F, S> of(F first, S second) {
		return new Pair<>(first, second);
	}
	
	public static final <F, S> Pair<F, S> empty() {
		@SuppressWarnings("unchecked")
		Pair<F, S> p = (Pair<F, S>) EMPTY;
        return p;
	}
}
