package net.chefcraft.world.menu.game;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.language.TranslatablePlayer;
import net.chefcraft.core.server.Minigame;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.reflection.world.CoreMaterial;
import net.chefcraft.reflection.world.GameReflections;
import net.chefcraft.world.databridge.CoreArena;
import net.chefcraft.world.menu.AbstractTranslatableContent;
import net.chefcraft.world.player.CorePlayer;
import net.chefcraft.world.translatable.TranslatableItemStack;
import net.chefcraft.world.util.ProxyUtils;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class GameMapSelectorItem extends AbstractTranslatableContent {

	private static final GameReflections GAME_REFLECTOR = ChefCore.getReflector().getGameReflections();
	
	private final GameMapSelector selector;
	private CoreArena arenaData;
	private Placeholder placeholder = Placeholder.of("{MAP_NAME}", "?").add("{PLAYERS_PER_TEAM}", 0)
			.add("{STATUS}", "?").add("{CURRENT_PLAYERS}", 0).add("{MAX_PLAYERS}", 0).add("{GAME_ID}", "?");
	
	public GameMapSelectorItem(String rawName, GameMapSelector selector) {
		super(ChefCore.getInstance(), rawName);
		this.selector = selector;
	}
	
	private void setPlaceholders(TranslatablePlayer player) {
		placeholder.setValue(0, arenaData.getMapDisplayName());
		placeholder.setValue(1, arenaData.getPlayersPerTeam());
		placeholder.setValue(2, arenaData.getGameStatus().getTranslatedName(player));
		placeholder.setValue(3, arenaData.getPlayersSize());
		placeholder.setValue(4, arenaData.getMaxPlayersSize());
		placeholder.setValue(5, arenaData.getGameID());
		
	}
	
	@Override
	public void onClick(TranslatablePlayer player) {
		if (arenaData.isJoineable()) {
			if (player instanceof CorePlayer corePlayer) {
				boolean flag = corePlayer.connectToLocalGameWorld(Minigame.EGGWARS, arenaData.getNamespaceID(), false);
				if (!flag && ProxyUtils.isBungeeModeEnabled()) {
					corePlayer.connectToGameServer(Minigame.EGGWARS, arenaData.getServerName(), arenaData.getNamespaceID(), false);
				}
			}
		} else {
			player.sendMessage("bungee.notNow");
			player.playSound("privateGame.denied");
		}
	}

	@Override
	public ItemStack getTranslatedItem(TranslatablePlayer player, int amount, @Nullable Placeholder nullHolder, @Nullable String itemState) {
		ItemStack item = this.itemStack.clone();
		int i = amount < 1 ? 1 : amount;
		i = i > 64 ? 64 : i;
		item.setAmount(i);
		if (this.isPlayerHead) {
			item = CoreMaterial.PLAYER_HEAD.toItemStack();
			GAME_REFLECTOR.setSkullOwner(player.getPlayer(), item);
		}
		this.setPlaceholders(player);
		if (!itemStates.isEmpty() && itemState != null) {
			AbstractTranslatableContent content = itemStates.get(itemState);
			if (content != null) {
				return content.getTranslatedItem(player, content.getItemStack().getAmount(), placeholder, null);
			}
		}
		
		boolean flag = arenaData.isInWaitingSlot();
		return TranslatableItemStack.from(item, 
				flag ? selector.getDynamicItemWaitingNameKey() : selector.getDynamicItemIngameNameKey(),
				flag ? selector.getDynamicItemWaitingLoreKey() : selector.getDynamicItemIngameLoreKey()).localTranslation(player, placeholder).asBukkit();
	}

	public CoreArena getArenaData() {
		return arenaData;
	}

	public GameMapSelectorItem setArenaData(CoreArena arenaData) {
		this.arenaData = arenaData;
		super.itemStack = arenaData.getMenuItem().toItemStack().asBukkit();
		return this;
	}

}
