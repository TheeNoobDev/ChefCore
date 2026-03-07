package net.chefcraft.reflection.v1_21_R7;

import io.papermc.paper.dialog.Dialog;
import net.chefcraft.core.component.ComponentBuilder;
import net.chefcraft.core.component.CoreTextColor;
import net.chefcraft.core.component.CoreTextStyle;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslationSource;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public class ComponentBuilderImpl implements ComponentBuilder {

    net.kyori.adventure.text.TextComponent.Builder builder = Component.text();

    @Override
    public ComponentBuilder appendNewLine() {
        builder.appendNewline();
        return this;
    }

    @Override
    public ComponentBuilder append(@NotNull MessageHolder text) {
        builder.append(((Component) text.asComponent()));
        return this;
    }

    @Override
    public ComponentBuilder hoverEvent(@NotNull HoverEvent hoverEvent) {
        if (hoverEvent instanceof HoverEvent.ShowText showText) {
            builder.hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(showText.getText().asComponent()));
        } else if (hoverEvent instanceof HoverEvent.ShowItem showItem) {
            builder.hoverEvent(net.kyori.adventure.text.event.HoverEvent.showItem(Key.key(showItem.getKey()), showItem.getCount(), BinaryTagHolder.binaryTagHolder(showItem.getNbt())));
        } else if (hoverEvent instanceof HoverEvent.ShowEntity showEntity) {
            builder.hoverEvent(net.kyori.adventure.text.event.HoverEvent.showEntity(Key.key(showEntity.getKey()), showEntity.getUniqueID(), showEntity.getName().asComponent()));
        } else {
            throw new IllegalStateException("undefined hover event type");
        }
        return this;
    }

    @Override
    public ComponentBuilder clickEvent(@NotNull ClickEvent clickEvent) {
        if (clickEvent instanceof ClickEvent.OpenURL event) {
            builder.clickEvent(net.kyori.adventure.text.event.ClickEvent.openUrl(event.url()));
        } else if (clickEvent instanceof ClickEvent.OpenFile event) {
            builder.clickEvent(net.kyori.adventure.text.event.ClickEvent.openFile(event.file()));
        } else if (clickEvent instanceof ClickEvent.RunCommand event) {
            builder.clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand(event.command()));
        } else if (clickEvent instanceof ClickEvent.SuggestCommand event) {
            builder.clickEvent(net.kyori.adventure.text.event.ClickEvent.suggestCommand(event.command()));
        } else if (clickEvent instanceof ClickEvent.ChangePage event) {
            builder.clickEvent(net.kyori.adventure.text.event.ClickEvent.changePage(event.page()));
        } else if (clickEvent instanceof ClickEvent.CopyToClipboard event) {
            builder.clickEvent(net.kyori.adventure.text.event.ClickEvent.copyToClipboard(event.text()));
        } else if (clickEvent instanceof ClickEvent.Dialog event) {
            if (event.dialog() instanceof Dialog dialog) {
                builder.clickEvent(net.kyori.adventure.text.event.ClickEvent.showDialog(dialog));
            } else {
                throw new IllegalStateException("event.dialog() must be io.papermc.paper.dialog.Dialog");
            }
        } else if (clickEvent instanceof ClickEvent.Custom event) {
            if (event.nbt() instanceof BinaryTagHolder tagHolder) {
                builder.clickEvent(net.kyori.adventure.text.event.ClickEvent.custom(Key.key(event.key()), tagHolder));
            } else {
                throw new IllegalStateException("event.nbt() must be net.kyori.adventure.nbt.api.BinaryTagHolder");
            }
        } else {
            throw new IllegalStateException("undefined click event type");
        }
        return this;
    }

    @Override
    public ComponentBuilder color(@NotNull CoreTextColor color) {
        builder.color(NamedTextColor.NAMES.value(color.name()));
        return this;
    }

    @Override
    public ComponentBuilder style(@NotNull CoreTextStyle style) {
        builder.decorate(TextDecoration.NAMES.value(style.name()));
        return this;
    }

    @Override
    public ComponentBuilder append(@NotNull Object object) {
        builder.append(Component.text(object.toString()));
        return this;
    }

    @Override
    public Object build() {
        return builder.build();
    }

    @Override
    public void send(@NotNull TranslationSource source) {
        Audience audience = source.getAudience();
        audience.sendMessage(builder.build());
    }
}
