package net.chefcraft.reflection.base.language.paper;

import net.chefcraft.core.component.RandomColor;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.util.JHelper;
import net.chefcraft.reflection.base.language.MessageHolderUtil;
import net.chefcraft.reflection.base.language.paper.PaperListMessageHolder;
import net.chefcraft.reflection.base.language.paper.PaperSingleMessageHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PaperMessageHolderUtil implements MessageHolderUtil {

    @Override
    public MessageHolder mergeMessageHolders(@NotNull MessageHolder @NotNull ... messageHolders) {
        switch (messageHolders.length) {
            case 0:
                return MessageHolder.empty();
            case 1:
                return messageHolders[0];
            case 2:
                return PaperListMessageHolder.hold("", JHelper.addAllToNewList(messageHolders[0].asStringList(false), messageHolders[1].asStringList(false)), null);
            default:
                List<String> texts = new ArrayList<>();
                for (MessageHolder parts : messageHolders) {
                    if (!parts.asStringList(false).isEmpty()) {
                        texts.addAll(parts.asStringList(false));
                    }
                }
                PaperListMessageHolder result = PaperListMessageHolder.hold("", texts, null);
                texts.clear();
                texts = null;
                return result;
        }
    }

    @Override
    public MessageHolder mergeMessageHoldersByPlaceholder(@NotNull String placeholder, @NotNull MessageHolder main, @NotNull MessageHolder replacements) {
        return PaperListMessageHolder.hold("", JHelper.replaceIndexToListElements(main.asStringList(false), replacements.asStringList(false), placeholder), null);
    }

    @Override
    public MessageHolder stringToMessageHolder(@NotNull String key, @NotNull String text, @Nullable RandomColor randomColor) {
        return PaperSingleMessageHolder.hold(key, text, randomColor);
    }

    @Override
    public MessageHolder listStringToMessageHolder(@NotNull String key, @NotNull List<String> texts, @Nullable RandomColor randomColor) {
        return PaperListMessageHolder.hold(key, texts, randomColor);
    }

    @Override
    public List<MessageHolder> listStringToListMessageHolder(@NotNull String key, @NotNull List<String> texts, @Nullable RandomColor randomColor) {
        List<MessageHolder> result = new ArrayList<>(texts.size());
        for (int i = 0; i < texts.size(); i++) {
            result.add(i, PaperSingleMessageHolder.hold(key, texts.get(i), randomColor));
        }
        return result;
    }
}
