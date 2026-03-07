package net.chefcraft.proxy.player;

import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.Player;
import net.chefcraft.core.PluginInstance;
import net.chefcraft.core.component.RandomColor;
import net.chefcraft.core.language.Language;
import net.chefcraft.core.language.TranslationSource;
import net.chefcraft.core.party.CorePartyManager;
import net.chefcraft.core.server.Minigame;
import net.chefcraft.core.util.JHelper;
import net.chefcraft.core.util.Pair;
import net.chefcraft.core.util.ValueTranslation;
import net.chefcraft.proxy.ChefProxyCore;
import net.chefcraft.service.luckperms.PlayerMetaDataStorage;
import net.chefcraft.world.chat.ChatStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class CoreProxyPlayer implements TranslationSource {
	
	private final Player velocityPlayer;
	private final RandomColor randomColor = new RandomColor();
	private final CorePartyManager partyManager;
	private PlayerMetaDataStorage playerMetaDataStorage;
	private ChatStatus chatStatus = ChatStatus.DEFAULT;
	private Pair<Minigame, String> lastGameID = Pair.empty();
	
	public CoreProxyPlayer(Player velocityPlayer) {
		this.velocityPlayer = velocityPlayer;
		this.partyManager = new CorePartyManager(this);
		this.playerMetaDataStorage = new PlayerMetaDataStorage(this);
		/*if (PlatformProvider.hasLuckPerms()) {
			this.playerMetaDataStorage = LuckPermsUtils.loadForTranlationSource(this, LuckPermsProvider.get().getPlayerAdapter(Player.class).getMetaData(velocityPlayer));
		} else {
			this.playerMetaDataStorage = new PlayerMetaDataStorage(this);
		}*/
	}
	
	public Player getVelocityPlayer() {
		return this.velocityPlayer;
	}

	@Override
	public Language getLanguage() {
		return ChefProxyCore.getInstance().getDefaultLanguage();
	}

	@Override
	public PluginInstance getPlugin() {
		return ChefProxyCore.getInstance();
	}

	@Override
	public RandomColor getRandomColor() {
		return randomColor;
	}

	@Override
	public boolean isConsole() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Player getAudience() {
		return velocityPlayer;
	}

	@Override
	public @NotNull CorePartyManager getPartyManager() {
		return this.partyManager;
	}
	
	public boolean isInParty() {
		return this.partyManager.isInParty();
	}
	
	public boolean isPartyMember() {
		return this.partyManager.isInParty() && !this.partyManager.isPartyOwner();
	}
	
	public boolean isPartyOwner() {
		return this.partyManager.isPartyOwner();
	}
	
	public ChatStatus getChatStatus() {
		return this.chatStatus;
	}
	
	public void setChatStatus(ChatStatus chatStatus, boolean notify) {
		if (chatStatus == ChatStatus.PARTY && this.isInParty()) {
			this.chatStatus = ChatStatus.PARTY;
			if (notify) {
				this.sendMessage("party.chatEnabled");
			}
		} else {
			this.chatStatus = ChatStatus.DEFAULT;
			if (notify) {
				this.sendMessage("party.chatDisabled");
			}
		}
	}

	public PlayerMetaDataStorage getPlayerMetaDataStorage() {
		return this.playerMetaDataStorage;
	}
	
	@NotNull
	public Pair<Minigame, String> getLastGameID() {
		return this.lastGameID;
	}

	public void setLastGameID(@Nullable Minigame game, @Nullable String id) {
		this.lastGameID = Pair.of(game, id);
	}
	
	public CompletableFuture<Void> updateLastGameID(boolean read) {
		if (read) {
			return CompletableFuture.runAsync(() -> {
				String data = ChefProxyCore.getPlayerDatabase().getData("LastGame", this.velocityPlayer.getUniqueId());
				JsonObject json = ValueTranslation.getAsJsonObject(data);
				this.setLastGameID(Minigame.matchMinigame(json.get("minigame").getAsString()), 
						json.get("id").getAsString());
			});
		} else {
			return CompletableFuture.runAsync(() -> {
				JsonObject json = new JsonObject();
				json.addProperty("minigame", JHelper.nullOrEmptyEnum(this.lastGameID.getFirst()));
				json.addProperty("id", JHelper.nullOrEmptyString(this.lastGameID.getSecond()));
				ChefProxyCore.getPlayerDatabase().setData("LastGame", this.velocityPlayer.getUniqueId(), json.toString());
			});
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
