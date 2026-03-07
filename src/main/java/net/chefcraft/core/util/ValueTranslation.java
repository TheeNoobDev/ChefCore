package net.chefcraft.core.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/** @since 1.1*/
public class ValueTranslation {
	
	private static final Gson GSON = new Gson();
	
	public static <T> String getAsString(T value) {
		if (value instanceof String) {
			return (String) value;
		}
		return value.toString();
	}

	public static <T> int getAsInt(T value) {
		if (value instanceof Integer) {
			return (int) value;
		}
		return Integer.parseInt("0" + value.toString());
	}
	
	public static <T> long getAsLong(T value) {
		if (value instanceof Long) {
			return (long) value;
		}
		return Long.parseLong("0" + value.toString());
	}
	
	public static <T> double getAsDouble(T value) {
		if (value instanceof Double) {
			return (double) value;
		}
		return Double.parseDouble("0" + value.toString());
	}
	
	public static <T> float getAsFloat(T value) {
		if (value instanceof Float) {
			return (float) value;
		}
		return Float.parseFloat("0" + value.toString());
	}
	
	public static <T> boolean getAsBoolean(T value) {
		if (value instanceof Boolean) {
			return (boolean) value;
		}
		return Boolean.parseBoolean(value.toString().isEmpty() ? "false" : value.toString());
	}
	
	public static JsonElement getAsJsonElement(String value) {
		return GSON.fromJson(value, JsonElement.class);
	}
	
	public static JsonObject getAsJsonObject(String value) {
		return GSON.fromJson(value, JsonObject.class);
	}
}
