package net.chefcraft.core.language;

import net.chefcraft.core.component.RandomColor;
import net.chefcraft.core.util.JHelper;
import net.chefcraft.core.util.ObjectKey;
import net.chefcraft.reflection.base.language.MessageHolderUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**@since 1.3.0*/
public interface MessageHolder extends ObjectKey {
	
	@NotNull String asString(boolean deserializeFormats);
	
	@NotNull List<String> asStringList(boolean deserializeFormats);
	
	@NotNull <C> C asComponent();
	
	@NotNull <C> List<C> asComponentList();
	
	default List<MessageHolder> parts() {
		return JHelper.mapList(this.asStringList(false), text -> MessageHolder.text(text));
	}
	
	default MessageHolder reverse() {
		List<String> texts = this.asStringList(false);
		Collections.reverse(texts);
		return texts(texts);
	}
	
	default MessageHolder merge(@NotNull MessageHolder with) {
		return merge(this, with);
	}
	
	default MessageHolder merge(@NotNull MessageHolder with, @NotNull String placeholder) {
		return merge(this, with, placeholder);
	}
	
	default MessageHolder merge(@NotNull String text) {
		return merge(this, MessageHolder.text(text));
	}
	
	default MessageHolder merge(@NotNull String text, @NotNull String placeholder) {
		return merge(this, MessageHolder.text(text), placeholder);
	}
	
	static @NotNull MessageHolder empty() {
		return MessageHolderUtil.EMPTY_MESSAGE_HOLDER;
	}
	
	static @NotNull MessageHolder text(@NotNull String text) {
		return text("*undefined", text, null);
	}
	
	static @NotNull MessageHolder text(@NotNull String key, @NotNull String text) {
		return text(key, text, null);
	}
	
	static @NotNull MessageHolder text(@NotNull String key, @NotNull String text, @Nullable RandomColor randomColor) {
		Objects.requireNonNull(key, "cannot create message holder from null key!");
		Objects.requireNonNull(text, "cannot create message holder from null text!");
		return MessageHolderUtil.STRING_TO_MESSAGE_HOLDER.apply(key, text, randomColor);
	}
	
	static @NotNull MessageHolder texts(@NotNull List<String> texts) {
		return texts("*undefined", texts, null);
	}
	
	static @NotNull MessageHolder texts(@NotNull String key, @NotNull List<String> texts) {
		return texts(key, texts, null);
	}
	
	static @NotNull MessageHolder texts(@NotNull String key, @NotNull List<String> texts, @Nullable RandomColor randomColor) {
		Objects.requireNonNull(key, "cannot create message holder from null key!");
		Objects.requireNonNull(texts, "cannot create message holder from null text!");
		return MessageHolderUtil.LIST_STRING_TO_MESSAGE_HOLDER.apply(key, texts, randomColor);
	}
	
	static @NotNull List<MessageHolder> textsAsList(@NotNull List<String> texts) {
		return textsAsList("*undefined", texts, null);
	}
	
	static @NotNull List<MessageHolder> textsAsList(@NotNull String key, @NotNull List<String> texts) {
		return textsAsList(key, texts, null);
	}
	
	static @NotNull List<MessageHolder> textsAsList(@NotNull String key, @NotNull List<String> texts, @Nullable RandomColor randomColor) {
		Objects.requireNonNull(key, "cannot create message holder from null key!");
		Objects.requireNonNull(texts, "cannot create message holder from null text!");
		return MessageHolderUtil.LIST_STRING_TO_LIST_MESSAGE_HOLDER.apply(key, texts, randomColor);
	}
	
	static @NotNull MessageHolder merge(@NotNull MessageHolder first, @NotNull MessageHolder second) {
		Objects.requireNonNull(first, "cannot merge null message holder!");
		Objects.requireNonNull(second, "cannot merge null message holder!");
		return MessageHolderUtil.MERGE_MESSAGE_HOLDERS.apply(first, second);
	}
	
	static @NotNull MessageHolder merge(@NotNull MessageHolder first, @NotNull MessageHolder second, @NotNull String placeholder) {
		Objects.requireNonNull(first, "cannot merge null message holder!");
		Objects.requireNonNull(second, "cannot merge null message holder!");
		Objects.requireNonNull(placeholder, "cannot replace placeholder from null text!");
		return MessageHolderUtil.MERGE_MESSAGE_HOLDERS_BY_PLACEHOLDER.apply(first, second, placeholder);
	}
}
