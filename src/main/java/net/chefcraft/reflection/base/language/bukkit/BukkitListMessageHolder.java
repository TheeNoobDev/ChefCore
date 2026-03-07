package net.chefcraft.reflection.base.language.bukkit;

import net.chefcraft.core.component.ComponentSupport;
import net.chefcraft.core.component.RandomColor;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.reflection.base.language.MessageHolderUtil;
import net.chefcraft.world.util.BukkitUtils;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @param <C> type of component
 */
public class BukkitListMessageHolder<C> implements MessageHolder {

	String key;
	String asString;
	C asComponent;
	List<String> asStringList;
	List<C> asComponentList;

    BukkitListMessageHolder() { }

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
	public @NotNull C asComponent() {
		return asComponent;
	}

	@SuppressWarnings("unchecked")
	@Override
	public @NotNull List<C> asComponentList() {
		return asComponentList;
	}
	
	@Override
	public String toString() {
		return asString;
	}
	
	public static BukkitListMessageHolder<BaseComponent> holdAsBaseComponent(@NotNull String key, @NotNull List<String> texts, @Nullable RandomColor randomColor) {
        BukkitListMessageHolder<BaseComponent> holder = new BukkitListMessageHolder<>();
		holder.key = key;
		
		holder.asString = BukkitUtils.parseList(texts, "\\n");
		holder.asComponent = new TextComponent();
		holder.asComponentList = new ArrayList<>(texts.size());
		
		for (int i = 0; i < texts.size(); i++) {
			String deserialized = ComponentSupport.legacySupport().deserialize(texts.get(i), randomColor); 
			texts.set(i, deserialized);
			BaseComponent c = TextComponent.fromLegacy(deserialized);
			holder.asComponent.addExtra(c);
			holder.asComponentList.add(i, c);
		}
		
		return holder;
	}
}
