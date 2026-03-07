package net.chefcraft.core.collect;

import java.util.*;

/**
 * @author Rido
 * @version 1.0
 */
public class ImmutableList<E> implements List<E>, RandomAccess {

	transient Object[] elementData = {};
	
	ImmutableList() { }
	
	private ImmutableList(List<? extends E> list) {
		int j = list.size();
		for (int i = 0; i < j; i++) {
			immutableAdd(list.get(i));
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ImmutableList(List<? extends E> list, Comparator<? super E> comparator) {
		this(list);
		Arrays.sort(elementData, (Comparator) comparator);
	}
	
	private ImmutableList(Iterable<? extends E> itr) {
		for (Object o : itr) {
			immutableAdd(o);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ImmutableList(Iterable<? extends E> itr, Comparator<? super E> comparator) {
		this(itr);
		Arrays.sort(elementData, (Comparator) comparator);
	}
	
	void immutableAdd(Object o) {
		if (o != null) {
			elementData = Arrays.copyOf(elementData, elementData.length + 1);
			elementData[elementData.length - 1] = o;
		}
	}
	
	@Override
	public int size() {
		return elementData.length;
	}

	@Override
	public boolean isEmpty() {
		return elementData.length == 0;
	}

	@Override
	public boolean contains(Object o) {
		return this.indexOf(o) != -1;
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			
			int i = 0;

			@Override
			public boolean hasNext() {
				return i < elementData.length;
			}

			@SuppressWarnings("unchecked")
			@Override
			public E next() {
				i++;
				return (E) elementData[i - 1];
			}
			
		};
	}

	@Override
	public Object[] toArray() {
		return Arrays.copyOf(elementData, elementData.length);
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException("Immutable list!");
	}

	@Override
	public boolean add(E e) {
		throw new UnsupportedOperationException("Immutable list!");
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("Immutable list!");
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		if (collection.isEmpty() || isEmpty()) {
			return false;
		}
		
		boolean b = true;
		
		for (Object o : collection) {
			if (this.contains(o)) {
				b = false;
			}
		}
		
		return b;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException("Immutable list!");
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException("Immutable list!");
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("Immutable list!");
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("Immutable list!");
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException("Immutable list!");
	}

	@SuppressWarnings("unchecked")
	@Override
	public E get(int index) {
		if (index < 0 || index >= elementData.length) {
			throw new ArrayIndexOutOfBoundsException("Index " + index + " out of bounds for size " + elementData.length);
		}
		return (E) elementData[index];
	}

	@Override
	public E set(int index, E element) {
		throw new UnsupportedOperationException("Immutable list!");
	}

	@Override
	public void add(int index, E element) {
		throw new UnsupportedOperationException("Immutable list!");
	}

	@Override
	public E remove(int index) {
		throw new UnsupportedOperationException("Immutable list!");
	}

	@Override
	public int indexOf(Object o) {
		for (int i = 0; i < elementData.length; i++) {
			Object obj = elementData[i];
			if (obj.equals(o)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		for (int i = elementData.length - 1; i >= 0; i--) {
			Object obj = elementData[i];
			if (obj.equals(o)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public ListIterator<E> listIterator() {
		throw new UnsupportedOperationException("Immutable list!");
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		throw new UnsupportedOperationException("Immutable list!");
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException("Immutable list!");
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append('{');
		
		for (int i = 0; i < elementData.length; i++) {
			builder.append(elementData[i]);
			builder.append(", ");
		}
		
		builder.deleteCharAt(builder.length() - 1);
		builder.deleteCharAt(builder.length() - 1);
		
		builder.append('}');
		
		return builder.toString();
	}
	
	@SafeVarargs
	public static <E> ImmutableList<E> of(E... elems) {
		ImmutableList<E> l = new ImmutableList<>();
		
		switch (elems.length) {
		case 0:
			l.elementData = new Object[0];
			break;
		case 1:
			l.elementData = new Object[1];
			l.elementData[0] = elems[0];
			break;
		default:
			int a = elems.length;
			for (int b = 0; b < a; b++) {
				l.immutableAdd(elems[b]);
			}
			break;
		}
		
		return l;
	}
	
	public static <E> ImmutableList<E> copyOf(List<? extends E> list) {
		return new ImmutableList<>(list);
	}
	
	public static <E> ImmutableList<E> copyOf(Iterable<? extends E> iterable) {
		return new ImmutableList<>(iterable);
	}
	
	public static <E> ImmutableList<E> sortedCopyOf(List<? extends E> list, Comparator<? super E> comparator) {
		return new ImmutableList<>(list, comparator);
	}
	
	public static <E> ImmutableList<E> sortedCopyOf(Iterable<? extends E> iterable, Comparator<? super E> comparator) {
		return new ImmutableList<>(iterable, comparator);
	}
	
	public static <E> ImmutableList<E> copyOf(@SuppressWarnings("unchecked") E...elements) {
		ImmutableList<E> x = new ImmutableList<>();
		
		for (E e : elements) {
			x.immutableAdd(e);
		}
		
		return x;
	}
	
	public static <E> ImmutableList<E> copyOfWith(Iterable<? extends E> iterable, @SuppressWarnings("unchecked") E...with) {
		ImmutableList<E> x = new ImmutableList<>(iterable);
		
		for (E e : with) {
			x.immutableAdd(e);
		}
		
		return x;
	}
}
