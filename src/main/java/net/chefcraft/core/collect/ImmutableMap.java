package net.chefcraft.core.collect;

import java.util.*;

/** 
 * @author Rido
 * @version 1.0
 */
public final class ImmutableMap<K,V> implements Map<K,V> {

    private ImmutableEntry<K,V>[] node;
    private int size = 0;
    private int dataSize = -1;
	
    private Set<K> keys = new HashSet<>();
    private Set<Entry<K,V>> entries = new HashSet<>();
    private Collection<V> values = new HashSet<>();
	
	public ImmutableMap() {}
	
	public ImmutableMap(int dataSize) {
		this.dataSize = dataSize;
	}
	
	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public boolean containsKey(final Object key) {
		if (key == null)
			throw new NullPointerException("key cannot be null!");
		for (int i = 0; size > i; i++) {
			if(node[i].key.equals(key))
				return true;
		}
		return false;
	}

	public boolean containsValue(final Object value) {
		if (value == null)
			throw new NullPointerException("value cannot be null!");
		for (int i = 0; size > i; i++) {
			if(node[i].value.equals(value))
				return true;
		}
		return false;
	}

	public V get(final Object key) {
		if (key == null)
			throw new NullPointerException("key cannot be null!");
		for (int i = 0; size > i; i++) {
			ImmutableEntry<K,V> entry = node[i];
			if (entry != null) {
				K k = entry.key;
				V v = entry.value;
				if (k.equals(key))
					return v;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public V put(final K key, final V value) {
		if (key == null || value == null)
			throw new NullPointerException("key or value cannot be null!");
		if (dataSize != -1 && size >= dataSize)
			throw new UnsupportedOperationException("The data limit for this Immutable Map has reached!");
		
		ImmutableEntry<K,V> entry = new ImmutableEntry<K,V>(key, value);
		if (isEmpty()) {
			node = new ImmutableEntry[1];
			node[0] = entry;
		} else {
			node = Arrays.copyOf(node, size + 1);
			node[size] = entry;
		}
			entries.add(entry);
			keys.add(entry.key);
			values.add(entry.value);
			size++;
		return entry.value;
	}

	public void putAll(final Map<? extends K, ? extends V> map) {
		for (Entry<? extends K, ? extends V> entries : map.entrySet()) {
			K k = entries.getKey();
			V v = entries.getValue();
			if (k == null || v == null) 
				continue;
			put(k, v);
		}
	}

	public Set<K> keySet() {
		return new HashSet<>(keys);
	}

	public Collection<V> values() {
		return new HashSet<>(values);
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return new HashSet<>(entries);
	}
	
	@Override
	public V remove(Object key) {
		new UnsupportedOperationException("Immutable Map!");
		return null;
	}

	@Override
	public void clear() {
		new UnsupportedOperationException("Immutable Map!");
	}
	
	public String toString() {
		if (size == 0)
			return "{}";
		String[] s = new String[size];
		for (int i = 0; size > i; i++) {
			s[i] = node[i].toString();
		}
		return "{" + String.join(", ", s) + "}";
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		} else if (object instanceof ImmutableMap) {
			ImmutableMap<K,V> o = (ImmutableMap<K,V>) object;
			if (o.size != this.size)
				return false;
			for (int i = 0; o.size > i; i++) {
				if (!(o.node[i].equals(this.node[i])))
					return false;
			}
			return true;
		}
		return false;
	}
	
	final static class ImmutableEntry<K,V> implements Entry<K,V> {

		private final K key;
		private final V value;
		
		public ImmutableEntry(K key, V value) {
			if (key == null || value == null)
				throw new NullPointerException("key or value cannot be null!");
			this.key = key;
			this.value = value;	
		}

		@Override
		public final K getKey() {
			return key;
		}
		
		@Override
		public final V getValue() {
			return value;
		}
		
		@Override
		public final V setValue(V value) {
			new UnsupportedOperationException("Immutable Entry!");
			return null;
		}
		
		public static final <K,V> ImmutableEntry<K,V> of(final K key, final V value) {
			return new ImmutableEntry<>(key, value);
		}
		
		@Override
		public final String toString() {
			return key + "=" + value; 
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public final boolean equals(Object object) {
			if (object == this) {
				return true;
			} else if (object instanceof ImmutableEntry) {
				ImmutableEntry<K,V> o = (ImmutableEntry<K,V>) object;
				if(o.getKey() == key && o.getValue() == value) {
					return true;
				}
			}
			return false;
		}
	}
}
