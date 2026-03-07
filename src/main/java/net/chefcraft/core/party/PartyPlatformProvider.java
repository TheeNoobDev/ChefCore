package net.chefcraft.core.party;

import net.chefcraft.core.PlatformProvider;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslationSource;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface PartyPlatformProvider {
	
	public static final BiConsumer<TranslationSource, String> PLAY_KEYED_SOUND = PlatformProvider.party().playKeyedSound();
	public static final Function<TranslationSource, MessageHolder> GET_DISPLAY_NAME = PlatformProvider.party().getDisplayName();
	public static final Function<TranslationSource, String> GET_USERNAME = PlatformProvider.party().getUsername();
	public static final BiConsumer<TranslationSource, TranslationSource> SEND_INVITE_MESSAGE = PlatformProvider.party().sendInviteMessage();
	
	Function<TranslationSource, String> getUsername();
	
	Function<TranslationSource, MessageHolder> getDisplayName();
	
	BiConsumer<TranslationSource, String> playKeyedSound();
	
	BiConsumer<TranslationSource, TranslationSource> sendInviteMessage();
}
