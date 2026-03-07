package net.chefcraft.core.party;

import com.google.common.collect.ImmutableList;
import net.chefcraft.core.language.TranslationSource;
import net.chefcraft.core.util.Placeholder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CorePartyManager {
	
	public static final CorePartyManager EMPTY = new CorePartyManager();
	
	private final TranslationSource source;
	private final List<? super TranslationSource> pendingPartyInvites;
	private AbstractParty party = null;
	private int maxPartySize = -1;
	
	private CorePartyManager() {
		this.source = null;
		this.pendingPartyInvites = ImmutableList.of();
	}
	
	public CorePartyManager(@NotNull TranslationSource source) {
		Objects.requireNonNull(source);
		this.source = source;
		this.pendingPartyInvites = new ArrayList<>();
	}
	
	public void inviteToYourParty(TranslationSource translationSource) {
		if (translationSource.equals(this.source)) return;

		if (this.party != null && !this.party.isOwner(this.source)) {
			translationSource.sendMessage("party.mustBeOwner");
			return;
		}
		
		if (this.party != null && this.party.hasPlayer(translationSource)) {
			this.source.sendMessage("party.partOf", Placeholder.of("{PLAYER}", PartyPlatformProvider.GET_DISPLAY_NAME.apply(translationSource)));
			return;
		}
		
		if (this.party != null && !this.party.checkPartySizeWithNotify(1)) return;
		
		this.pendingPartyInvites.add(translationSource);
		translationSource.sendMessage("party.invite", Placeholder.of("{PLAYER}", PartyPlatformProvider.GET_DISPLAY_NAME.apply(this.source)));
		
		//member, owner
		PartyPlatformProvider.SEND_INVITE_MESSAGE.accept(translationSource, this.source);
	}
	
	public void onPartyInviteAccepted(TranslationSource member) {
		if (member.getPartyManager().isInParty()) {
			member.sendMessage("party.alreadyHave");
			PartyPlatformProvider.PLAY_KEYED_SOUND.accept(member, "party.error");
			return;
		}
		
		
		if (this.party == null && !this.pendingPartyInvites.isEmpty()) {
			this.party = AbstractParty.create(this.source);
		}
		
		if (this.party != null && this.party.isOwner(this.source)) {
			member.sendMessage("party.youJoined", Placeholder.of("{PLAYER}", PartyPlatformProvider.GET_DISPLAY_NAME.apply(this.source)));
			this.party.addPlayer(member);
			this.party.onInviteAccepted(member);
		} else {
			member.sendMessage("party.notAvailable");
			PartyPlatformProvider.PLAY_KEYED_SOUND.accept(member, "party.error");
		}
		
		this.pendingPartyInvites.remove(member);
	}
	
	public void onPartyInviteDenied(TranslationSource member) {
		if (this.pendingPartyInvites.contains(member)) {
			this.source.sendMessage("party.inviteDenied", Placeholder.of("{PLAYER}", PartyPlatformProvider.GET_DISPLAY_NAME.apply(member)));
			this.pendingPartyInvites.remove(member);
			member.sendMessage("party.deniedSelf", Placeholder.of("{PLAYER}", PartyPlatformProvider.GET_DISPLAY_NAME.apply(this.source)));
		} else {
			if (this.isInParty() && this.party.hasPlayer(member)) {
				member.sendMessage("party.alreadyInThis");
			} else {
				member.sendMessage("party.cannotDeny");
			}
		}
	}
	
	public boolean isPartyMember(boolean sendErrorMessage) {
		boolean flag = this.isPartyMember();
		
		if (flag && sendErrorMessage) {
			this.source.sendMessage("party.mustBeOwner");
		}
		
		return flag;
	}
	
	public boolean isPartyMember() {
		return this.party != null && !this.party.isOwner(this.source);
	}
	
	public boolean isPartyOwner() {
		return this.party != null && this.party.isOwner(this.source);
	}
	
	public boolean isInParty() {
		return this.party != null;
	}
	
	@NotNull
	public AbstractParty getOrCreateParty() {
		return this.party == null ? AbstractParty.create(this.source) : this.party;
	}
	
	@Nullable
	public AbstractParty getParty() {
		return this.party;
	}
	
	public void setParty(AbstractParty party) {
		this.party = party;
	}
	
	public void clearPendingPartyInvites() {
		this.pendingPartyInvites.clear();
	}
	
	public int getMaxPartySize() {
		return this.maxPartySize;
	}
	
	public void setMaxPartySize(int size) {
		this.maxPartySize = size;
	}
	
	public String getFormattedMaxPartySize() {
		return this.maxPartySize > 0 ? this.maxPartySize + "" : "∞";	
	}
}
