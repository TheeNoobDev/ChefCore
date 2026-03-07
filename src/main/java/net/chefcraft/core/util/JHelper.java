package net.chefcraft.core.util;

import com.google.common.collect.ImmutableMap;
import net.chefcraft.core.collect.PreparedCondisions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/** @since 1.0*/
public class JHelper {
	
	private static final Random RANDOM = new Random();
	
	@SafeVarargs
	public static <T> ImmutableMap<String, T> createImmutableMapFromArray(boolean lowerCase, @NotNull T... values) {
		
		ImmutableMap.Builder<String, T> builder = ImmutableMap.<String, T>builderWithExpectedSize(values.length);
		if (lowerCase) {
			for (T t : values) {
				builder.put(t.toString().toLowerCase(), t);
			}
		} else {
			for (T t : values) {
				builder.put(t.toString(), t);
			}
		}
		return builder.build();
	}
	
	@SafeVarargs
	public static <E> List<E> addAllToNewList(Collection<E>... collections) {
		List<E> result = new ArrayList<>();
		
		for (Collection<E> c : collections) {
			if (c == null || c.isEmpty()) continue;
			result.addAll(c);
		}
		
		return result;
	}
	
	public static <E> E getRandomElementFromArray(E[] e) {
		return e[RANDOM.nextInt(e.length)];
	}
	
	public static <E> E getRandomElementFromArrayWithDuplicateCheck(E[] e, List<Integer> intLog) {
		int a = e.length;
		
		int i = RANDOM.nextInt(a);
		
		int timeout = 0;
		while (intLog.contains(i)) {
			i = RANDOM.nextInt(a); 
			if (timeout >= a) {
				break;
			}
			timeout++;
		}
		
		intLog.add(i);
		return e[i];
		
	}
	
	public static <E> E getRandomElement(Collection<E> collection) {
		int s = collection.size();
		
		if (s > 0) {
			int i = RANDOM.nextInt(s);
			int j = 0;
			
			for (E e : collection) {
				
				if (i == j) { return e; }
				j++;
			}
		}
		
		return null;
	}
	
	public static <E> E getRandomElementWithDuplicateCheck(Collection<E> collection, List<Integer> intLog) {
		
		int s = collection.size();
		
		if (s > 0) {
			int i = RANDOM.nextInt(s);
			int j = 0;
			
			int timeout = 0;
			while (intLog.contains(i)) {
				i = RANDOM.nextInt(s); 
				if (timeout >= s) {
					break;
				}
				timeout++;
			}
			
			for (E e : collection) {
				
				if (i == j) { intLog.add(i); return e; }
				j++;
			}
		}
		
		return null;
	}

	public static <E> String parseList(List<E> list, String spliterator, Function<E, String> func) {
		StringBuilder builder = new StringBuilder();
		int listSize = list.size();
		
		for (int i = 0; i < listSize; i++) {
			builder.append(func.apply(list.get(i))).append(spliterator);
		}
		
		return builder.substring(0, builder.length() - spliterator.length());
	}
	
	public static <E> List<String> listToListString(List<E> list, Function<E, String> func) {
		int listSize = list.size();
		List<String> resultList = new ArrayList<>(listSize);
		
		for (int i = 0; i < listSize; i++) {
			resultList.add(i, func.apply(list.get(i)));
		}
		
		return resultList;
	}
	
	public static <E> List<E> replaceIndexToListElements(List<E> mainList, List<E> replacements, E placeholder) {
		int indexOf = mainList.indexOf(placeholder);
		if (indexOf == -1) return mainList;
		
		int total = mainList.size() + replacements.size() - 1;
		List<E> result = new ArrayList<>(total);
		
		for (int i = 0, j = 0, k = indexOf + 1; i < total; i++) {
			if (indexOf > i) {
				result.add(mainList.get(i));
				continue;
			}
			
			if (replacements.size() > j) {
				result.add(replacements.get(j));
				j++;
				continue;
			}
			
			if (mainList.size() > k) {
				result.add(mainList.get(k));
				k++;
			}
		}
		
		return replaceIndexToListElements(result, replacements, placeholder);
	}
	
	public static <E, T> List<E> replaceIndexToListElements(List<E> mainList, List<T> replacements, E placeholder, Function<T, E> function) {
		int indexOf = mainList.indexOf(placeholder);
		if (indexOf == -1) return mainList;
		
		int total = mainList.size() + replacements.size() - 1;
		List<E> result = new ArrayList<>(total);
		
		for (int i = 0, j = 0, k = indexOf + 1; i < total; i++) {
			if (indexOf > i) {
				result.add(mainList.get(i));
				continue;
			}
			
			if (replacements.size() > j) {
				result.add(function.apply(replacements.get(j)));
				j++;
				continue;
			}
			
			if (mainList.size() > k) {
				result.add(mainList.get(k));
				k++;
			}
		}
		
		return replaceIndexToListElements(result, replacements, placeholder, function);
	}
	
	@Nullable
	public static <E> E getFirstMatches(@NotNull Iterable<E> itr, @NotNull Predicate<E> flag) {
		for (E e : itr) {
			if (flag.test(e)) {
				return e;
			}
		}
		
		return null;
	}
	
	@Nullable
	public static <E, R> E getFirstMatches(@NotNull Iterable<E> itr, @NotNull Function<E, R> func, @Nullable R result) {
		for (E e : itr) {
			R r = func.apply(e);
			
			if (r != null && r.equals(result)) {
				return e;
			}
		}
		
		return null;
	}
	
	@Nullable
	public static <E> E getFirst(@NotNull Iterable<E> iterable) {
        PreparedCondisions.notNull(iterable, "iterable");

		if (iterable instanceof List<E> list) {
            return list.isEmpty() ? null : list.get(0);
        }

        final Iterator<E> iterator = iterable.iterator();
        return iterator.hasNext() ? iterator.next() : null;
	}
	
	@Nullable
	public static <E> E getLast(@NotNull Iterable<E> iterable) {
        PreparedCondisions.notNull(iterable, "iterable");

        if (iterable instanceof List<E> list) {
            return list.isEmpty() ? null : list.get(list.size() - 1);
        }

		final Iterator<E> iterator = iterable.iterator();
		E last = null;
		
		while (iterator.hasNext()) {
			last = iterator.next();
		}
		
		return last;
	}

    @NotNull
	public static <E> List<E> iterablesToList(@NotNull Iterable<E> iterable, @NotNull List<E> to) {
        PreparedCondisions.notNull(iterable, "iterable");
        PreparedCondisions.notNull(to, "to");

		if (iterable instanceof List<E> list) {
			return list;
		}
		
		List<E> arrayList = new ArrayList<>();
		for (E e : iterable) arrayList.add(e);
		
		return arrayList;
	}

	public static <E> List<E> filterList(List<E> list, Predicate<E> predicate) {
		List<E> resultList = new ArrayList<>();
		int listSize = list.size();
		
		for (int i = 0; i < listSize; i++) {
			E element = list.get(i);
			if (predicate.test(element)) {
				resultList.add(element);
			}
		}
		
		return resultList;
	}
	
	public static <E> List<E> filterCollection(Collection<E> collection, Predicate<E> predicate) {
		List<E> resultList = new ArrayList<>();
		
		for (E element : collection) {
			if (predicate.test(element)) {
				resultList.add(element);
			}
		}
		
		return resultList;
	}
	
	public static <E, R> List<R> mapCollectionToList(Collection<E> collection, Function<E, R> function) {
		List<R> resultList = new ArrayList<>();
		Iterator<E> iterator = collection.iterator();
		
		while (iterator.hasNext()) {
			resultList.add(function.apply(iterator.next()));
		}
		
		return resultList;
	}
	
	public static <E, R> List<R> mapList(List<E> list, Function<E, R> function) {
		int listSize = list.size();
		List<R> resultList = new ArrayList<>(listSize);
		
		for (int i = 0; i < listSize; i++) {
			E element = list.get(i);
			resultList.add(i, function.apply(element));
		}
		
		return resultList;
	}
	
	public static <E> Integer calcListIntCount(List<E> list, Function<E, Integer> function) {
		int result = 0;
		
		for (int i = 0; i < list.size(); i++) {
			result += function.apply(list.get(i));
		}
		
		return result;
	}
	
	public static <E> Integer countElements(Iterable<E> iterable) {
		int result = 0;
		Iterator<E> itr = iterable.iterator();
		
		while (itr.hasNext()) {
			result++;
		}
		
		return result;
	}
	
	public static <E> Integer countElements(Iterable<E> iterable, Predicate<E> filter) {
		int result = 0;
		Iterator<E> itr = iterable.iterator();
		
		while (itr.hasNext()) {
			if (filter.test(itr.next())) {
				result++;
			}
		}
		
		return result;
	}
	
	public static <E, R> List<R> filterAndMapList(List<E> list, Predicate<E> predicate, Function<E, R> function) {
		List<R> resultList = new ArrayList<>();
		
		for (int i = 0; i < list.size(); i++) {
			E element = list.get(i);
			if (predicate.test(element)) {
				resultList.add(function.apply(element));
			}
		}
		
		return resultList;
	}
	
	public static <E, R> List<R> filterAndMapCollection(Collection<E> collection, Predicate<E> predicate, Function<E, R> function) {
		List<R> resultList = new ArrayList<>();
		
		for (E element : collection) {
			if (predicate.test(element)) {
				resultList.add(function.apply(element));
			}
		}
		
		return resultList;
	}
	
	public static String nullOrEmptyString(String text) {
		return text != null ? text : "";
	}
	
	public static String nullOrEmptyEnum(Enum<?> e) {
		return e != null ? e.name() : "";
	}
	
	public static String emptyOrElseString(String text, String other) {
		return text != null ? text : other;
	}
	
	public static int nullOrEmptyInt(String text) {
		try {
			return Integer.parseInt(text);
		} catch (Exception x) {
			return 0;
		}
	}
}
