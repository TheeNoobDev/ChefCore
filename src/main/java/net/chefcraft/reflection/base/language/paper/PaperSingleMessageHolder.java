package net.chefcraft.reflection.base.language.paper;

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

public class PaperSingleMessageHolder implements MessageHolder {
	
	String key;
	List<String> asStringList;
	List<Component> asComponentList;

    PaperSingleMessageHolder() { }

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
	public @NotNull Component asComponent() {
		return asComponentList.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public @NotNull List<Component> asComponentList() {
		return asComponentList;
	}
	
	@Override
	public String toString() {
		return asStringList.get(0);
	}

	public static PaperSingleMessageHolder empty(@NotNull String key) {
		PaperSingleMessageHolder holder = new PaperSingleMessageHolder();
		holder.key = key;
		holder.asStringList = ImmutableList.of(key);
		holder.asComponentList = ImmutableList.of(Component.text(key));
		
		return holder;
	}
	
	public static PaperSingleMessageHolder hold(@NotNull String key, @NotNull String text, @Nullable RandomColor randomColor) {
		PaperSingleMessageHolder holder = new PaperSingleMessageHolder();
		holder.key = key;

		holder.asStringList = ImmutableList.of(text);
		holder.asComponentList = ImmutableList.of(ComponentSupport.miniMessageSupport().deserialize(text, randomColor));
		
		return holder;
	}
}
