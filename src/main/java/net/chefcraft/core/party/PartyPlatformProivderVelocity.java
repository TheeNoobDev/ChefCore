package net.chefcraft.core.party;

import net.chefcraft.core.component.ComponentSupport;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslationSource;
import net.chefcraft.core.util.Placeholder;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class PartyPlatformProivderVelocity implements PartyPlatformProvider {

	@Override
	public Function<TranslationSource, String> getUsername() {
		return (source) -> source.getAudience() instanceof com.velocitypowered.api.proxy.Player player ? player.getUsername() : "?";
	}
	
	@Override
	public Function<TranslationSource, MessageHolder> getDisplayName() {
		return (source) -> source.getAudience() instanceof com.velocitypowered.api.proxy.Player player ? MessageHolder.text(player.getUsername()) : MessageHolder.empty();
	}
	
	@Override
	public BiConsumer<TranslationSource, String> playKeyedSound() {
		return (source, key) -> {};
	}
	
	//source, who
	@Override
	public BiConsumer<TranslationSource, TranslationSource> sendInviteMessage() {
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
	}

}
