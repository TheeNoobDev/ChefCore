package net.chefcraft.core.party;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.PlatformProvider;
import net.chefcraft.core.component.ComponentSupport;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslatablePlayer;
import net.chefcraft.core.language.TranslationSource;
import net.chefcraft.core.util.Placeholder;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class PartyPlatformProivderBukkit implements PartyPlatformProvider {

	public Function<TranslationSource, String> getUsername() {
		return (source) -> source instanceof TranslatablePlayer trans ? trans.getPlayer().getName() : "?";
	}
	
	public Function<TranslationSource, MessageHolder> getDisplayName() {
		return (source) -> source instanceof TranslatablePlayer trans ? trans.getDisplayName() : MessageHolder.empty();
	}
	
	public BiConsumer<TranslationSource, String> playKeyedSound() {
		return (source, key) -> ChefCore.getSoundManager().playSound(((org.bukkit.entity.Player) source.getAudience()), key);
	}
	
	//source, who
	public BiConsumer<TranslationSource, TranslationSource> sendInviteMessage() {
		if (PlatformProvider.hasKyoriAdventure()) {
			
			return (translationSource, thisPlayer) -> {
				
				net.kyori.adventure.text.TextComponent.Builder comp = Component.text();
				
				comp.append(ComponentSupport.miniMessageSupport().deserialize(translationSource.getPlainMessage("party.acceptFormat"), null)
						.hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(ComponentSupport.miniMessageSupport().deserialize(translationSource.getPlainMessage("party.acceptHover"), null)))
						.clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/party accept " + (thisPlayer.getAudience() instanceof com.velocitypowered.api.proxy.Player player ? player.getUsername() : "?"))));
				
				comp.append(ComponentSupport.miniMessageSupport().deserialize(translationSource.getPlainMessage("party.inviteSeperator"), null));
				
				comp.append(ComponentSupport.miniMessageSupport().deserialize(translationSource.getPlainMessage("party.denyFormat"), null)
						.hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(ComponentSupport.miniMessageSupport().deserialize(translationSource.getPlainMessage("party.denyHover"), null)))
						.clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/party deny " + (thisPlayer.getAudience() instanceof com.velocitypowered.api.proxy.Player player ? player.getUsername() : "?"))));
				
				thisPlayer.sendMessage("party.inviteSent", Placeholder.of("{PLAYER}", GET_DISPLAY_NAME.apply(translationSource)));
				
				if (translationSource.getAudience() instanceof Audience audience) {
					audience.sendMessage(comp.build());
				}
			};
			
		} else {
			return (translationSource, thisPlayer) -> {
				net.md_5.bungee.api.chat.TextComponent formatComp = new net.md_5.bungee.api.chat.TextComponent(ComponentSupport.legacySupport().deserialize(translationSource.getPlainMessage("party.acceptFormat"), null));
				formatComp.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/party accept " + (thisPlayer.getAudience() instanceof org.bukkit.entity.Player player ? player.getName() : "?")));
				formatComp.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.hover.content.Text(ComponentSupport.legacySupport().deserialize(translationSource.getPlainMessage("party.acceptHover"), null))));
				
				formatComp.addExtra(new net.md_5.bungee.api.chat.TextComponent(ComponentSupport.legacySupport().deserialize(translationSource.getPlainMessage("party.inviteSeperator"), null)));
				
				net.md_5.bungee.api.chat.TextComponent withDeny = new net.md_5.bungee.api.chat.TextComponent(ComponentSupport.legacySupport().deserialize(translationSource.getPlainMessage("party.denyFormat"), null));
				withDeny.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/party deny " + (thisPlayer.getAudience() instanceof org.bukkit.entity.Player player ? player.getName() : "?")));
				withDeny.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.hover.content.Text(ComponentSupport.legacySupport().deserialize(translationSource.getPlainMessage("party.denyHover"), null))));
				formatComp.addExtra(withDeny);
				
				thisPlayer.sendMessage("party.inviteSent", Placeholder.of("{PLAYER}", GET_DISPLAY_NAME.apply(translationSource)));
				
				if (translationSource instanceof TranslatablePlayer translatablePlayer) {
					ChefCore.getReflector().getGameReflections().sendComponents(translatablePlayer.getPlayer(), formatComp);
				}
			};
		}
	}
}
