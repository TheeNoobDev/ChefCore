package net.chefcraft.core.registry;

import com.google.common.collect.ImmutableMap;
import net.chefcraft.core.PlatformProvider;
import net.chefcraft.core.collect.Functional;
import net.chefcraft.core.util.ObjectKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public class CoreRegistry<T> implements Functional<T> {

	private final Class<?> source;
	private final Map<String, T> map;
	
	public CoreRegistry(@NotNull Class<?> source) {
		this.source = Objects.requireNonNull(source, "source class cannot be null!");
		this.map = this.registry();
	}
	
	@SafeVarargs
	public CoreRegistry(@NotNull T...things) {
		Objects.requireNonNull(things, "things cannot be null!");
		if (things.length <= 0) {
			throw new IllegalStateException("array length out of bounds! (length > 0)");
		}
		
		this.source = things[0].getClass();
		if (!ObjectKey.class.isAssignableFrom(this.source)) {
			
			throw new IllegalStateException("An error occurred while register the " + this.source.getCanonicalName()
			+ " Source class must be implement '" + ObjectKey.class.getCanonicalName() + "'");
		}
		
		ImmutableMap.Builder<String, T> builder =  ImmutableMap.builder();
		for (T t : things) {	
			try {
				if (t == null) {
					PlatformProvider.getPluginInstance().getLogger()
					.log(Level.SEVERE, "An error occurred while register the " + this.source.getCanonicalName() + ". Value cannot be null!");
					continue;
				}
				builder.put(((ObjectKey) t).getKey(), t);
				
			} catch (Exception x) {
				x.fillInStackTrace();
			}
		}
		
		this.map = builder.build();
	}
	
	@Unmodifiable
	private Map<String, T> registry() {
		ImmutableMap.Builder<String, T> builder =  ImmutableMap.builder();
		
		Field[] fields = this.source.getFields();
		
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			
			if (this.source.isAssignableFrom(field.getType())
					&& Modifier.isFinal(field.getModifiers())
					&& Modifier.isPublic(field.getModifiers())
					&& Modifier.isStatic(field.getModifiers())) {
				
				if (!ObjectKey.class.isAssignableFrom(field.getType())) {
					PlatformProvider.getPluginInstance().getLogger().log(Level.SEVERE, "An error occurred while register the " + this.source.getCanonicalName() 
					+ " for field '" + field.getName() + "'! Source class must be implement '" + ObjectKey.class.getCanonicalName() + "'");
				}
				
				try {
					@SuppressWarnings("unchecked")
					T obj = (T) field.get(null);
					
					if (obj == null) {
						PlatformProvider.getPluginInstance().getLogger().log(Level.SEVERE, "An error occurred while register the " + this.source.getCanonicalName() 
						+ " for field '" + field.getName() + "'! Cannot be null!");
						continue;
					}
					builder.put(((ObjectKey) obj).getKey(), obj);
					
				} catch (Exception x) {
					x.printStackTrace();
				}				
			}
		}
		
		return builder.build();
	}
	
	@NotNull
	@Unmodifiable
	public Map<String, T> copy() {
		return ImmutableMap.copyOf(this.map);
	}
	
	@NotNull
	public Class<?> getRegistrySource() {
		return this.source;
	}
	
	@Nullable
	public T getByKey(@NotNull String key) {
		return this.map.get(key);
	}
	
	@NotNull
	@Override
	public Collection<T> values() {
		return this.map.values();
	}
}
