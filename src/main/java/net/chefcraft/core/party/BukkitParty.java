package net.chefcraft.core.party;

import net.chefcraft.core.language.TranslationSource;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.world.player.CorePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class BukkitParty extends AbstractParty {
	
	private static final Comparator<? super TranslationSource> FALLBACK_OWNER_SORT = (first, second) -> {
		if (first instanceof CorePlayer f && second instanceof CorePlayer s) {
			return f.getNameTagService().getTabListPriority() - s.getNameTagService().getTabListPriority();
		}
		return 99;
	};

	protected BukkitParty(CorePlayer owner) {
		super(owner);
	}
	
	@Override
	public Placeholder addChatPlaceholders(@NotNull TranslationSource source, @NotNull Placeholder current) {
		return ((CorePlayer) source).getPlayerMetaDataStorage().addToPlaceholder(current);
	}

	@Override
	public TranslationSource getFallbackOwner() {
		if (this.members.isEmpty()) return null;
		this.members.sort(FALLBACK_OWNER_SORT);
		return this.members.get(0);
	}

	@Override
	public void onInviteAccepted(TranslationSource whoAcceptted) { }

	@Override
	public void onPlayerLeft(TranslationSource whoLeft, PartyLeaveCause cause) { }

	@Override
	public void onOwnerChanged(TranslationSource newOwner) { }

	@Override
	public void onDisbanded() { }

	@Override //for proxy
	public void onLeaderSwitchServer(Object server) { }
}
