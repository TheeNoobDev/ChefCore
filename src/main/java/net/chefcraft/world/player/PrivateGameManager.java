package net.chefcraft.world.player;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.component.ComponentSupport;
import net.chefcraft.core.math.SimpleMath;
import net.chefcraft.core.server.Minigame;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.reflection.world.GameReflections;
import net.chefcraft.world.databridge.ServerDataBridge;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class PrivateGameManager {

	private static final GameReflections GAME_REFLECTIONS = ChefCore.getReflector().getGameReflections();
	
	private final CorePlayer owner;
	private BukkitTask statusListenerTask = null;
	
	private boolean gameReady = false;
	private String gameCode = "?";
	private String server = "?";
	private String mapName = "?";
	private PrivateGameStatus privateGameStatus = PrivateGameStatus.PENDING; 
	
	public PrivateGameManager(CorePlayer owner) {
		this.owner = owner;
	}
	
	public CorePlayer getOwner() {
		return this.owner;
	}
	
	public boolean isGameReady() {
		return this.gameReady;
	}
	
	public String getGameCode() {
		return this.gameCode;
	}
	
	public boolean isCodeReady() {
		return this.gameCode.length() > 3;
	}
	
	public String getArenaNamespaceID() {
		return this.mapName + "-" + this.gameCode;
	}
	
	public void updateGameData(String map, String status, String code, String server) {
		this.mapName = map;
		this.privateGameStatus = PrivateGameStatus.matchGameStatus(status);
		this.gameCode = code;
		this.server = server;
		if (map.length() > 0 && this.privateGameStatus != PrivateGameStatus.PENDING && code.length() > 0 && server.length() > 0) {
			this.gameReady = true;
		}
	}
	
	public void stopStatusListener() {
		if (this.statusListenerTask != null) {
			this.statusListenerTask.cancel();
		}
	}
	
	public void startStatusListener(final Minigame type, final String map) {
		this.stopStatusListener();
		
		this.gameCode = "?";
		this.gameReady = false;
		this.mapName = map;
		
		final Player player = owner.getPlayer();
		final String ownerName = player.getName();
		final ChefCore plugin = ChefCore.getInstance();
		final Placeholder placeholder = Placeholder.of("{VALUE}", "").add("{GAME}", type.getStringValue());
		
		this.owner.sendMessage("privateGames.queue");
		this.owner.playSound("privateGame.queue");
		
		this.statusListenerTask = new BukkitRunnable() {

			int time = 0;
			boolean first = true;
			
			@Override
			public void run() {
				if (time % 2 == 0 && owner != null) {
					ServerDataBridge.updatePrivateGameStatus(owner);
				}
				
				if (gameReady) {
					placeholder.setValue(0, gameCode);
					owner.sendActionBar("privateGames.readyActionBar", placeholder);
					
					if (first) {
						first = false;
						TextComponent comp = new TextComponent(ComponentSupport.legacySupport().deserialize(owner.getPlainMessage("privateGames.ready", placeholder), null));
						TextComponent click = new TextComponent(ComponentSupport.legacySupport().deserialize(owner.getPlainMessage("privateGames.clickToJoin"), null));
						
						click.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/pg join " + gameCode));
						
						comp.addExtra(click);
						
						GAME_REFLECTIONS.sendComponents(player, comp);
						ChefCore.getSoundManager().playSound(player, "privateGame.ready");
					}
				} else {
					placeholder.setValue(0, SimpleMath.secondToDigital(time));
					owner.sendActionBar("privateGames.pendingActionBar", placeholder);
					
				}
				
				if (time > 120) {
					this.cancel();
					owner.sendMessage("privateGames.timeout");
					owner.playSound("privateGame.denied");
					if (!gameReady) {
						ServerDataBridge.clearPrivateGameRequest(ownerName);	
					}
					resetData();
				}
				
				if (PrivateGameManager.this.privateGameStatus == PrivateGameStatus.READY && owner.getCurrentMinigame() != null) {
					this.cancel();
					resetData();
				}
				
				if (owner == null || PrivateGameManager.this.privateGameStatus == PrivateGameStatus.DENIED) {
					this.cancel();
					ServerDataBridge.clearPrivateGameRequest(ownerName);	
					owner.sendMessage("privateGames.denied");
					owner.playSound("privateGame.denied");
					resetData();
				}
				time++;
			}
			
		}.runTaskTimerAsynchronously(plugin, 20L, 20L);
	}
	
	protected void resetData() {
		gameCode = "?";
		gameReady = false;
		mapName = "?";
		server = "?";
	}

	public PrivateGameStatus getPrivateGameStatus() {
		return privateGameStatus;
	}

	public String getServerName() {
		return server;
	}

	public boolean hasGameRequest() {
		return ServerDataBridge.hasPrivateGameRequest(owner);
	}
}
