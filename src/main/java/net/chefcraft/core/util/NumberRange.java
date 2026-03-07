package net.chefcraft.core.util;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public enum NumberRange {

	EQUAL("="),
	GREATHER(">"),
	LESS("<"),
	GREATHER_OR_EQUAL(">="),
	LESS_OR_EQUAL("<=");
	
	private final String symbol;
	
	private NumberRange(String symbol) {
		this.symbol = symbol;
	}
	
	public String getSymbol() {
		return this.symbol;
	}
	
	public boolean test(long range, long value) {
		switch (this) {
		case EQUAL:
			return range == value;
		case GREATHER:
			return range < value;
		case GREATHER_OR_EQUAL:
			return range <= value;
		case LESS:
			return range > value;
		case LESS_OR_EQUAL:
			return range >= value;
		default:
			return false;
		}
	}
	
	public boolean test(double range, double value) {
		switch (this) {
		case EQUAL:
			return range == value;
		case GREATHER:
			return range < value;
		case GREATHER_OR_EQUAL:
			return range <= value;
		case LESS:
			return range > value;
		case LESS_OR_EQUAL:
			return range >= value;
		default:
			return false;
		}
	}
	
	public static final NumberRange[] VALUES = NumberRange.values();
	
	@Nullable
	public static NumberRange parseFromString(String text) {
		for (int i = VALUES.length - 1; i >= 0; i--) {
			if (text.contains(VALUES[i].symbol)) {
				return VALUES[i];
			}
		}
		return null;
	}
	
	@NotNull
	public static NumberRange parseFromStringOrDefault(String text, NumberRange defaultValue) {
		for (int i = VALUES.length - 1; i >= 0; i--) {
			if (text.contains(VALUES[i].symbol)) {
				return VALUES[i];
			}
		}	
		return defaultValue;
	}
}
