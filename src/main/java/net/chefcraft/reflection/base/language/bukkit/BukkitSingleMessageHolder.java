package net.chefcraft.reflection.base.language.bukkit;

import com.google.common.collect.ImmutableList;
import net.chefcraft.core.component.ComponentSupport;
import net.chefcraft.core.component.RandomColor;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.reflection.base.language.MessageHolderUtil;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @param <C> type of component
 */
public class SingleMessageHolder<C> implements MessageHolder {
	
	String key;
	List<String> asStringList;
	List<C> asComponentList;
	
	SingleMessageHolder() { }

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public @NotNull String asString(boolean deserializeFormats) {
		return deserializeFormats ? MessageHolderUtil.DESERIALIZE_TEXT.apply(asStringList.get(0)) : asStringList.get(0);
	}

	@Override
	public @NotNull List<String> asStringList(boolean deserializeFormats) {
		return deserializeFormats ? MessageHolderUtil.DESERIALIZE_TEXT_LIST.apply(asStringList) : asStringList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public @NotNull C asComponent() {
		return asComponentList.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public @NotNull List<C> asComponentList() {
		return asComponentList;
	}
	
	@Override
	public String toString() {
		return asStringList.get(0);
	}
	
	public static SingleMessageHolder<BaseComponent> emptyAsBaseComponent(@NotNull String key) {
		SingleMessageHolder<BaseComponent> holder = new SingleMessageHolder<>();
		holder.key = key;
		holder.asStringList = ImmutableList.of(key);
		holder.asComponentList = ImmutableList.of(new TextComponent(key));
		
		return holder;
	}
	
	public static SingleMessageHolder<Component> emptyAsComponent(@NotNull String key) {
		SingleMessageHolder<Component> holder = new SingleMessageHolder<>();
		holder.key = key;
		holder.asStringList = ImmutableList.of(key);
		holder.asComponentList = ImmutableList.of(Component.text(key));
		
		return holder;
	}
	
	public static SingleMessageHolder<BaseComponent> holdAsBaseComponent(@NotNull String key, @NotNull String text, @Nullable RandomColor randomColor) {
		SingleMessageHolder<BaseComponent> holder = new SingleMessageHolder<>();
		holder.key = key;
		
		String deserialized = ComponentSupport.legacySupport().deserialize(text, randomColor);
		holder.asStringList = ImmutableList.of(deserialized);
		holder.asComponentList = ImmutableList.of(TextComponent.fromLegacy(deserialized));
		
		return holder;
	}
	
	public static SingleMessageHolder<Component> holdAsComponent(@NotNull String key, @NotNull String text, @Nullable RandomColor randomColor) {
		SingleMessageHolder<Component> holder = new SingleMessageHolder<>();
		holder.key = key;

		holder.asStringList = ImmutableList.of(text);
		holder.asComponentList = ImmutableList.of(ComponentSupport.miniMessageSupport().deserialize(text, randomColor));
		
		return holder;
	}
}
