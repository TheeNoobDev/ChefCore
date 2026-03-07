package net.chefcraft.world.command;

import net.chefcraft.core.server.Minigame;
import net.chefcraft.world.player.CorePlayer;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PrivateGameCommandManager {
	
	private static final Map<Minigame, IPrivateGameCommandHandler> COMMAND_REGISTRY_MAP = new HashMap<>();
	
	public static void registerHandler(IPrivateGameCommandHandler handler) {
		Objects.requireNonNull(handler, "PrivateGameCommandHandler cannot be null!");
		Objects.requireNonNull(handler.getMinigame(), "Minigame cannot be null!");
		COMMAND_REGISTRY_MAP.put(handler.getMinigame(), handler);
	}
	
	public static void unregisterHandler(IPrivateGameCommandHandler handler) {
		Objects.requireNonNull(handler, "PrivateGameCommandHandler cannot be null!");
		if (COMMAND_REGISTRY_MAP.containsValue(handler)) {
			COMMAND_REGISTRY_MAP.remove(handler.getMinigame());
		}
	}
	
	public static void unregisterHandler(Minigame minigame) {
		Objects.requireNonNull(minigame, "Minigame cannot be null!");
		if (COMMAND_REGISTRY_MAP.containsKey(minigame)) {
			COMMAND_REGISTRY_MAP.remove(minigame);
		}
	}
	
	public static IPrivateGameCommandHandler getHandler(Minigame minigame) {
		return COMMAND_REGISTRY_MAP.get(minigame);
	}
	
	public interface IPrivateGameCommandHandler {
		
		@Nonnull Minigame getMinigame();
		
		void cancel(CorePlayer sender);
		
		void start(CorePlayer sender);
		
		void stop(CorePlayer sender);
		
		void pause(CorePlayer sender);
		
		void resume(CorePlayer sender);
		
		void settings(CorePlayer sender);
		
		void end(CorePlayer sender);
	}

}
