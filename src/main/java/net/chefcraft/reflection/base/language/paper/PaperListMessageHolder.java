package net.chefcraft.reflection.base.language.paper;

import net.chefcraft.core.component.ComponentSupport;
import net.chefcraft.core.component.RandomColor;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.reflection.base.language.MessageHolderUtil;
import net.chefcraft.world.util.BukkitUtils;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PaperListMessageHolder implements MessageHolder {

	String key;
	String asString;
	Component asComponent;
	List<String> asStringList;
	List<Component> asComponentList;

    PaperListMessageHolder() { }

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public @NotNull String asString(boolean deserializeFormats) {
		return deserializeFormats ? MessageHolderUtil.DESERIALIZE_TEXT.apply(asString) : asString;
	}

	@Override
	public @NotNull List<String> asStringList(boolean deserializeFormats) {
		return deserializeFormats ? MessageHolderUtil.DESERIALIZE_TEXT_LIST.apply(asStringList) : asStringList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public @NotNull Component asComponent() {
		return asComponent;
	}

	@SuppressWarnings("unchecked")
	@Override
	public @NotNull List<Component> asComponentList() {
		return asComponentList;
	}
	
	@Override
	public String toString() {
		return asString;
	}
	
	public static PaperListMessageHolder hold(@NotNull String key, @NotNull List<String> texts, @Nullable RandomColor randomColor) {
		PaperListMessageHolder holder = new PaperListMessageHolder();
		holder.key = key;
		
		holder.asString = BukkitUtils.parseList(texts, "<newline>");
		holder.asComponentList = new ArrayList<>(texts.size());
		
		net.kyori.adventure.text.TextComponent.Builder builder = Component.text();
		int tail = texts.size() - 1;
		
		for (int i = 0; i < tail; i++) {
			Component c = ComponentSupport.miniMessageSupport().deserialize(texts.get(i), randomColor);
			builder.append(c);
			builder.appendNewline();
			holder.asComponentList.add(i, c);
		}
		
		Component c = ComponentSupport.miniMessageSupport().deserialize(texts.get(tail), randomColor);
		builder.append(c);
		holder.asComponentList.add(tail, c);
		
		holder.asStringList = texts;
		holder.asComponent = builder.build();
		
		return holder;
	}
}
