package net.chefcraft.core.util;

import java.util.Arrays;
import java.util.List;

/** @since 1.0*/
public class Placeholder {

	private String[] keys = null;
	private Object[] values = null;
	private int size = 0;
	
	public Placeholder() {}
	
	public Placeholder(String key, Object value) {
		keys = new String[1];
		values = new Object[1];
		keys[0] = key;
		values[0] = value;
		size++;
	}
	
	public int size() {
		return size;
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	public Placeholder add(String key, Object value) {
		keys = Arrays.copyOf(keys, size + 1);
		values = Arrays.copyOf(values, size + 1);
		keys[size] = key;
		values[size] = value;
		size++;
		return this;
	}
	
	public Placeholder merge(Placeholder otherPlaceholder) {
		int i = otherPlaceholder.size;
		int j = i + size;
		
		keys = Arrays.copyOf(keys, j);
		values = Arrays.copyOf(values, j);
		
		int k = 0;
		
		while (j > size && k < i) {
			keys[size] = otherPlaceholder.keys[k];
			values[size] = otherPlaceholder.values[k];
			size++;
			k++;
		}
		return this;
	}
	
	public String getKey(int index) {
		return keys[index];
	}
	
	public Object getValue(int index) {
		return values[index];
	}
	
	public void setValue(int index, Object value) {
		values[index] = value;
	}
	
	public Pair<String, Object> getPlaceholder(int index) {
		return new Pair<>(keys[index], values[index]);
	}
	
	public String replace(String message) {
		for (int i = 0; size > i; i++) {
			message = message.replace(keys[i], values[i].toString());
		}
		return message;
	}
	
	public List<String> replaceAll(List<String> message) {
		for (int i = 0; message.size() > i; i++) {
			String text = message.get(i);
			message.set(i, replace(text));
		}
		return message;
	}
	
	public void reset() {
		keys = null;
		values = null;
		size = 0;
	}
	
	@Override 
	public Placeholder clone() {
		Placeholder placeholder = new Placeholder();
		placeholder.size = this.size;
		placeholder.keys = Arrays.copyOf(this.keys, this.size);
		placeholder.values = Arrays.copyOf(this.values, this.size);
		return placeholder;
	}
	
	@Override
	public String toString() {
		if (size == 0)
			return "{}";
		String[] s = new String[size];
		for (int i = 0; size > i; i++) {
			s[i] = keys[i] + "=" + values[i].toString();
		}
		return "{" + String.join(", ", s) + "}";
	}
	
	public static Placeholder of(String key, Object value) {
		return new Placeholder(key, value);
	}
}
