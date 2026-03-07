package net.chefcraft.world.handler;

import net.chefcraft.core.server.Minigame;
import net.chefcraft.world.player.CorePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractGameHandler {
	
	private static final Map<Minigame, AbstractGameHandler> GAME_HANDLER_REGISTRY = new HashMap<>();
	
	@Nullable
	public static AbstractGameHandler getGameHandlerByMinigame(Minigame minigame) {
		return GAME_HANDLER_REGISTRY.get(minigame);
	}
	
	public static boolean hasGameHandler(Minigame minigame) {
		return GAME_HANDLER_REGISTRY.containsKey(minigame);
	}
	
	@Nonnull
	private final Minigame minigame;
	
	protected AbstractGameHandler(@Nonnull Minigame minigame) { 
		this.minigame = minigame;
		GAME_HANDLER_REGISTRY.put(minigame, this);
	}

	@Nonnull
	public Minigame getMinigame() {
		return minigame;
	}
	
	public abstract StateResult join(String gameId, CorePlayer corePlayer);
	
	public abstract StateResult rejoin(CorePlayer corePlayer);
	
	public abstract StateResult leave(CorePlayer corePlayer);
	
	public static enum StateResult {
		
		JOINED,
		JOINED_AS_SPECTATOR,
		LEFT,
		LEFT_BY_PARTY_OWNER,
		REJOINED, 
		REJOIN_DISABLED, 
		GAME_FULL, 
		ILLEGAL_STATE,
		DENIED,
		GAME_NOT_FOUND;
		
		public boolean successful() {
			return this == JOINED || this == REJOINED || this == LEFT || this == JOINED_AS_SPECTATOR || this == LEFT_BY_PARTY_OWNER;
		}
		
		public boolean joined() {
			return this == JOINED;
		}
		
		public boolean joinedAsSpectator() {
			return this == JOINED_AS_SPECTATOR;
		}
		
		public boolean left() {
			return this == LEFT;
		}
		
		public boolean leftByPartyOwner() {
			return this == LEFT;
		}
		
		public boolean denied() {
			return this == DENIED;
		}
		
		public boolean gameNotFound() {
			return this == GAME_NOT_FOUND;
		}
		
		public boolean illegalState() {
			return this == ILLEGAL_STATE;
		}
		
		public boolean gameFull() {
			return this == GAME_FULL;
		}
		
		public boolean rejoined() {
			return this == REJOINED;
		}
		
		public boolean rejoinDisabled() {
			return this == REJOIN_DISABLED;
		}
	}
}
