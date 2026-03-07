package net.chefcraft.world.player;

import com.google.gson.JsonObject;
import net.chefcraft.core.ChefCore;
import net.chefcraft.core.PluginInstance;
import net.chefcraft.core.component.CoreTextColor;
import net.chefcraft.core.component.RandomColor;
import net.chefcraft.core.database.AbstractPlayerDatabase;
import net.chefcraft.core.event.PlayerLanguageChangeEvent;
import net.chefcraft.core.language.Language;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.party.CorePartyManager;
import net.chefcraft.core.server.Minigame;
import net.chefcraft.core.sound.AbstractSoundManager;
import net.chefcraft.core.util.Cooldown;
import net.chefcraft.core.util.JHelper;
import net.chefcraft.core.util.Pair;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.reflection.AbstractReflections;
import net.chefcraft.service.luckperms.LuckPermsUtils;
import net.chefcraft.service.luckperms.PlayerMetaDataStorage;
import net.chefcraft.service.nick.NickNamerService;
import net.chefcraft.service.tag.NameTagFormat;
import net.chefcraft.service.tag.NameTagService;
import net.chefcraft.world.cage.Cage;
import net.chefcraft.world.cage.CageUtils;
import net.chefcraft.world.cage.PlaceableCage;
import net.chefcraft.world.chat.ChatStatus;
import net.chefcraft.world.databridge.ServerDataBridge;
import net.chefcraft.world.handler.AbstractGameHandler;
import net.chefcraft.world.handler.NameTagHandler;
import net.chefcraft.world.level.CoreLevelManager;
import net.chefcraft.world.loot.KillMessages;
import net.chefcraft.world.scoreboard.custom.CoreTeamHolder;
import net.chefcraft.world.util.MethodProvider;
import net.chefcraft.world.util.ProxyUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.logging.Level;

public class CorePlayer extends AbstractPlayerCore {

	private static final AbstractSoundManager SOUND_MANAGER = ChefCore.getSoundManager();
	private static final AbstractReflections REFLECTOR = ChefCore.getReflector();
	private static final AbstractPlayerDatabase PLAYER_DB = ChefCore.getPlayerDatabase();
	private static final ChefCore PLUGIN = ChefCore.getInstance();

	private final Player bukkitPlayer;
	private NameTagService nameTagService;
	private final NickNamerService nickNamerService;
	private MessageHolder displayName;
	private Language language = ChefCore.getInstance().getDefaultLanguage();
	private KillMessages killMessages = KillMessages.getDefaultKillMessages();
	private PlaceableCage placeableCage = new PlaceableCage();
	private PrivateGameManager privateGameManager = new PrivateGameManager(this);
	private CoreLevelManager levelManager;
	private int networkCoins = 0;
	private Minigame currentMinigame = null;
	private ChatStatus chatStatus = ChatStatus.DEFAULT;
	private PlayerMetaDataStorage metaDataStorage;
	private Cooldown userDataRecalculateCooldown = new Cooldown(60);
	private InventoryData inventoryData = new InventoryData(this);
	private boolean hasServerRequest = false;
	private Pair<Minigame, String> lastGameID = Pair.empty();
	private final RandomColor randomColor = new RandomColor();
	private final CorePartyManager partyManager;
	
	private final Runnable safelyCloseExistingMenuRunnable = () -> this.getPlayer().closeInventory();
	private final Runnable levelBarUpdateRunnable = ()-> this.getLevelManager().updateLevelBar();

	public CorePlayer(Player bukkitPlayer) {
		this.bukkitPlayer = bukkitPlayer;
		
		AbstractReflections reflections = ChefCore.getReflector();
		
		this.nickNamerService = reflections.newNickNamerService(bukkitPlayer);
		this.levelManager = new CoreLevelManager(this);
		this.placeableCage.setCage(CageUtils.getDefaultCage());		
		LuckPermsUtils.loadForCorePlayer(this);
		this.nameTagService = reflections.newNameTagTeamService(this, this.metaDataStorage.getTabHeight());
		this.partyManager = new CorePartyManager(this);
		this.displayName = MessageHolder.text(bukkitPlayer.getName());
		
		ChefCore.registerCorePlayer(this);
	}
	
	public void saveServerRequest() {
		JsonObject json = new JsonObject();
		json.addProperty("minigame", JHelper.nullOrEmptyEnum(this.lastGameID.getFirst()));
		json.addProperty("id", JHelper.nullOrEmptyString(this.lastGameID.getSecond()));
		PLAYER_DB.setData("LastGame", this.bukkitPlayer.getUniqueId(), json.toString());
	}
	
	public boolean saveData() {
		try {
			
			// Main data
			PLAYER_DB.setData("Language", this.bukkitPlayer.getUniqueId(), language.getLocale());
			
			// Level System and Coins data
			JsonObject json = new JsonObject();
			json.addProperty("coins", this.networkCoins);
			json.addProperty("level", this.levelManager.getCurrentLevel());
			json.addProperty("exp", this.levelManager.getCurrentExp());
			
			PLAYER_DB.setData("PlayerData", this.bukkitPlayer.getUniqueId(), json.toString());
			
			// Game utils data
			json = new JsonObject();
			json.addProperty("cage", this.placeableCage.getCage().getNamespaceID());
			json.addProperty("killMessages", this.killMessages.getNamespaceID());
			
			PLAYER_DB.setData("GameUtilsData", this.bukkitPlayer.getUniqueId(), json.toString());
			
		} catch (Exception x) {
			ChefCore.getInstance().sendPlainMessage("&cAn error occured the saving &e" + this.bukkitPlayer.getName() + "'s&c data", x);
			return false;
		}
		return true;
	}

	public void setLanguage(Language language, boolean callEvent, PlayerLanguageChangeEvent.Cause cause) {
		this.language = language;
		if (callEvent) {
			Bukkit.getPluginManager().callEvent(new PlayerLanguageChangeEvent<>(cause, this, language));
		}
		
		if (this.inventoryData.hasTranslatableMenu()) {
			this.inventoryData.getTranslatableMenu().updateMenu(this, null, Placeholder.of("<PLAYER>", bukkitPlayer.getName()));
		}
	}

	public void setLanguage(Language language) {
		this.setLanguage(language, false, PlayerLanguageChangeEvent.Cause.UNKNOWN);
	}
	
	public void loadPlayerDataFromJson(JsonObject object) {
		this.setNetworkCoins(object.get("coins").getAsInt());
		this.levelManager.setCurrentExp(object.get("exp").getAsFloat());
		this.levelManager.setCurrentLevel(object.get("level").getAsInt());
		Bukkit.getScheduler().runTaskLater(PLUGIN, levelBarUpdateRunnable, 5L);
	}
	
	public void loadGameUtilsDataFromJson(JsonObject object) {
		Cage cage =  CageUtils.getCageByNamespaceID(object.get("cage").getAsString());
		if (cage != null) {
			this.getPlaceableCage().setCage(cage);
		} else {
			ChefCore.log(Level.SEVERE, "An error occurred loading player CAGE for: " + this.bukkitPlayer.getName());
		}
		
		KillMessages messages = KillMessages.getKillMessageByName(object.get("killMessages").getAsString());
		if (messages != null) {
			this.setKillMessages(messages);
		} else {
			ChefCore.log(Level.SEVERE, "An error occurred loading player KILL MESSAGES for: " + this.bukkitPlayer.getName());
		}
	}
	
	public void displayTag(boolean update) {
		
		MessageHolder playerListPrefix = this.metaDataStorage.replaceAllFormatsAndGetAsMessageHolder((String) NameTagHandler.findFormatFromConfigByType(NameTagFormat.PLAYER_LIST_PREFIX, this));
		MessageHolder playerListSuffix = this.metaDataStorage.replaceAllFormatsAndGetAsMessageHolder((String) NameTagHandler.findFormatFromConfigByType(NameTagFormat.PLAYER_LIST_SUFFIX, this));
		MessageHolder nameTagPrefix = this.metaDataStorage.replaceAllFormatsAndGetAsMessageHolder((String) NameTagHandler.findFormatFromConfigByType(NameTagFormat.NAME_TAG_PREFIX, this));
		MessageHolder nameTagSuffix = this.metaDataStorage.replaceAllFormatsAndGetAsMessageHolder((String) NameTagHandler.findFormatFromConfigByType(NameTagFormat.NAME_TAG_SUFFIX, this));
		
		this.nameTagService.setPrefix(nameTagPrefix);
		this.nameTagService.setSuffix(nameTagSuffix);
		CoreTextColor textColor = CoreTextColor.getLastColor(nameTagPrefix.asString(true));
		this.nameTagService.setTextModifier(textColor != null ? textColor : CoreTextColor.WHITE); 
		
		if (!update) {
			this.nameTagService.display();
		} else {
			this.nameTagService.setTabListPriority(this.metaDataStorage.getTabHeight());
			this.nameTagService.update();
		}
		
		MethodProvider.SET_PLAYER_PLAYER_LIST_NAME.accept(this.bukkitPlayer, MessageHolder.text(playerListPrefix.asString(false) + playerListSuffix.asString(false)));
		this.modifyDisplayName("");
	}
	
	public void modifyDisplayName(@Nonnull String extra) {
		String displayName = this.metaDataStorage.replaceAllFormats((String) NameTagHandler.findFormatFromConfigByType(NameTagFormat.DISPLAY_NAME, this));
		String modified = displayName.replace("{EXTRA}", extra);
		
		if (displayName.trim().length() != 0) {
			this.setDisplayName(MessageHolder.text("", modified, this.randomColor));
		}
	}
	
	public CoreTeamHolder createSpectatorTeamForMinigames(@Nonnull String teamName) {
		CoreTeamHolder specTeam = REFLECTOR.newTeamHolder(teamName);
		specTeam.setDisplayName(MessageHolder.text(this.bukkitPlayer.getName()));
		specTeam.setTextModifier(CoreTextColor.GRAY);
		specTeam.setPrefix(this.metaDataStorage.replaceAllFormatsAndGetAsMessageHolder((String) NameTagHandler.findFormatFromConfigByType(NameTagFormat.NAME_TAG_PREFIX, this)));
		specTeam.setSuffix(this.metaDataStorage.replaceAllFormatsAndGetAsMessageHolder((String) NameTagHandler.findFormatFromConfigByType(NameTagFormat.NAME_TAG_SUFFIX, this)));
		return specTeam;
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
	
	public boolean connectToPrivateGameServer(@Nonnull String code, boolean overrideParty) {
		if (!overrideParty && this.isInParty() && !this.isPartyOwner()) {
			this.sendMessage("party.mustBeOwner");
			return false;
		}
		
		if (ServerDataBridge.hasPrivateGame(code)) {
			Minigame minigame = ServerDataBridge.getMinigameByCode(code);
			if (AbstractGameHandler.hasGameHandler(minigame)) {
				String map = ServerDataBridge.getMapNameByCode(code);
				return this.connectToLocalGameWorld(minigame, map + "-" + code, overrideParty);
			} else {
				String server = ServerDataBridge.getServerNameByCode(code);
				String map = ServerDataBridge.getMapNameByCode(code);
				return this.connectToGameServer(minigame, server, map + "-" + code, overrideParty);
			}
		} 
		return false;
	}
	
	public boolean connectToGameServer(@Nonnull Minigame minigame, @Nonnull String server, @Nonnull String namespaceID, boolean overrideParty) {
		if (!overrideParty && this.isInParty() && !this.isPartyOwner()) {
			this.sendMessage("party.mustBeOwner");
			return false;
		}
		
		this.setLastGameID(minigame, namespaceID); 
		return ProxyUtils.sendPlayerToServer(this, server);
	}
	
	public boolean connectToLocalGameWorld(@Nonnull Minigame minigame, @Nonnull String namespaceID, boolean overrideParty) {
		if (!overrideParty && !this.isPartyOwner()) {
			this.sendMessage("party.mustBeOwner");
			return false;
		}
		
		this.setLastGameID(minigame, namespaceID);
		AbstractGameHandler handler = AbstractGameHandler.getGameHandlerByMinigame(minigame);
		if (handler != null) {
			handler.join(namespaceID, this);
			return true;
		}
		
		return false;
	}
	
	@Override
	public AbstractSoundManager getSoundManager() {
		return SOUND_MANAGER;
	}

	@Override
	public PluginInstance getPlugin() {
		return PLUGIN;
	}
	
	@Override
	public RandomColor getRandomColor() {
		return this.randomColor;
	}
	
	@Override
	public CorePlayer getCorePlayer() {
		return this;
	}
	
	@Override
	public void safelyCloseExistingMenu() {
		Bukkit.getScheduler().runTaskLater(PLUGIN, this.safelyCloseExistingMenuRunnable, 1L);
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
	
	@Override
	public Player getPlayer() {
		return this.bukkitPlayer;
	}
	
	@SuppressWarnings("unchecked")//nah checked
	@Override
	public Player getAudience() {
		return this.bukkitPlayer;
	}
	
	@Override
	public boolean isConsole() {
		return false;
	}

	@Override
	public Language getLanguage() {
		return this.language;
	}
	
	@Override
	public @NotNull CorePartyManager getPartyManager() {
		return this.partyManager;
	}
	
	@Override
	public @NotNull MessageHolder getDisplayName() {
		return this.displayName;
	}

	@Override
	public void setDisplayName(@Nullable MessageHolder displayName) {
		this.displayName = displayName;
		MethodProvider.SET_PLAYER_DISPLAY_NAME.accept(this.bukkitPlayer, displayName);
	}
	
	public void handlePlayerQuit() {
		if (this.isInParty()) {
			this.partyManager.getParty().leave(this, true, false);
		}
		
		UUID id = this.bukkitPlayer.getUniqueId();
		this.partyManager.clearPendingPartyInvites();
		
		Bukkit.getScheduler().runTaskLater(PLUGIN, () -> {
			ChefCore.unregisterCorePlayer(id);
		}, 20L);
	}

	public NameTagService getNameTagService() {
		return nameTagService;
	}

	public NickNamerService getNickNamerService() {
		return nickNamerService;
	}

	public KillMessages getKillMessages() {
		if (this.killMessages == null) {
			this.setKillMessages(KillMessages.getDefaultKillMessages());
		}
		return killMessages;
	}

	public void setKillMessages(KillMessages killMessages) {
		this.killMessages = killMessages;
	}

	public PlaceableCage getPlaceableCage() {
		return this.placeableCage;
	}

	public void setPlaceableCage(PlaceableCage cage) {
		this.placeableCage = cage;
	}

	public int getNetworkCoins() {
		return this.networkCoins;
	}

	public void setNetworkCoins(int coins) {
		this.networkCoins = coins;
	}

	public void addNetworkCoins(int coins) {
		this.networkCoins += coins;
	}

	public CoreLevelManager getLevelManager() {
		return this.levelManager;
	}

	public PrivateGameManager getPrivateGameManager() {
		return this.privateGameManager;
	}

	@NotNull
	public Pair<Minigame, String> getLastGameID() {
		return this.lastGameID;
	}

	public void setLastGameID(@Nullable Minigame game, @Nullable String id) {
		this.lastGameID = Pair.of(game, id);
	}
	
	public boolean isInGame() {
		return this.currentMinigame != null;
	}

	public Minigame getCurrentMinigame() {
		return this.currentMinigame;
	}

	public void setCurrentMinigame(Minigame currentMinigame) {
		this.currentMinigame = currentMinigame;
	}

	public PlayerMetaDataStorage getPlayerMetaDataStorage() {
		return this.metaDataStorage;
	}

	public void setPlayerMetaDataStorage(PlayerMetaDataStorage storage) {
		this.metaDataStorage = storage;
	}

	public Cooldown getUserDataRecalculateCooldown() {
		return userDataRecalculateCooldown;
	}

	public InventoryData getInventoryData() {
		return inventoryData;
	}

	public void setInventoryData(InventoryData inventoryData) {
		this.inventoryData = inventoryData;
	}

	public boolean hasServerRequest() {
		return hasServerRequest;
	}

	public void setHasServerRequest(boolean hasServerRequest) {
		this.hasServerRequest = hasServerRequest;
	}
	
	public void sendComponent(BaseComponent... baseComponents) {
		REFLECTOR.getGameReflections().sendComponents(this.bukkitPlayer, baseComponents);
	}
}
