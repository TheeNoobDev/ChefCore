package net.chefcraft.reflection.version.v1_8_R3;

import net.chefcraft.core.component.ComponentBuilder;
import net.chefcraft.core.component.CoreTextColor;
import net.chefcraft.core.component.CoreTextStyle;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslationSource;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;

public class ComponentBuilderImpl implements ComponentBuilder {
    net.md_5.bungee.api.chat.ComponentBuilder componentBuilder = new net.md_5.bungee.api.chat.ComponentBuilder("");

    @Override
    public ComponentBuilder appendNewLine() {
        componentBuilder.append("\n");
        return this;
    }

    @Override
    public ComponentBuilder append(@NotNull MessageHolder text) {
        componentBuilder.append(text.asString(true));
        return this;
    }

    @Override
    public ComponentBuilder hoverEvent(@NotNull HoverEvent hoverEvent) {
        if (hoverEvent instanceof HoverEvent.ShowText showText) {

            componentBuilder.event(new net.md_5.bungee.api.chat.HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT,
                    TextComponent.fromLegacyText(showText.getText().asString(true))));

        } else if (hoverEvent instanceof HoverEvent.ShowItem showItem) {
            //
        } else if (hoverEvent instanceof HoverEvent.ShowEntity showEntity) {
            //
        } else {
            throw new IllegalStateException("undefined hover event type");
        }
        return this;
    }

    @Override
    public ComponentBuilder clickEvent(@NotNull ClickEvent clickEvent) {
        if (clickEvent instanceof ClickEvent.OpenURL event) {
            componentBuilder.event(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.OPEN_URL, event.url()));
        } else if (clickEvent instanceof ClickEvent.OpenFile event) {
            componentBuilder.event(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.OPEN_FILE, event.file()));
        } else if (clickEvent instanceof ClickEvent.RunCommand event) {
            componentBuilder.event(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, event.command()));
        } else if (clickEvent instanceof ClickEvent.SuggestCommand event) {
            componentBuilder.event(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND, event.command()));
        } else if (clickEvent instanceof ClickEvent.ChangePage event) {
            componentBuilder.event(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.CHANGE_PAGE, String.valueOf(event.page())));
        } else if (clickEvent instanceof ClickEvent.CopyToClipboard event) {
            //unsupported for 1.8.8
        } else if (clickEvent instanceof ClickEvent.Dialog event) {
            //unsupported for 1.8.8 (supported in 1.21.6)
        } else if (clickEvent instanceof ClickEvent.Custom event) {
            //unsupported for 1.8.8 (supported in 1.21.6)
        } else {
            throw new IllegalStateException("undefined click event type");
        }
        return this;
    }

    @Override
    public ComponentBuilder color(@NotNull CoreTextColor color) {
        componentBuilder.color(ChatColor.getByChar(color.character()));
        return this;
    }

    @Override
    public ComponentBuilder style(@NotNull CoreTextStyle style) {
        componentBuilder.color(ChatColor.getByChar(style.character()));
        return this;
    }

    @Override
    public ComponentBuilder append(@NotNull Object object) {
        componentBuilder.append(object.toString());
        return this;
    }

    @Override
    public Object build() {
        return componentBuilder.create();
    }

    @Override
    public void send(@NotNull TranslationSource source) {
        CraftPlayer craftPlayer = source.getAudience();
        craftPlayer.spigot().sendMessage(componentBuilder.create());
    }
}
