package net.chefcraft.service.tag;

import net.chefcraft.core.exception.NumberOutOfBoundsException;
import net.chefcraft.core.math.SimpleMath;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TagPriorityTracker {
	
	/**
	 * Firstly, don't let them change this. We need to use final modifier key.
	 * I decide to max team tab-list sort size 1000. I think so that's enough.
	 */
	public static final int MAX_PRIORITY = 999;
	public static final int MIN_PRIORITY = 0;
	
	private static final Map<UUID, String> TEAMS_ID_TRACKER = new HashMap<>();
	
	public static String createEntry(Player player, int priority) {
		if (priority > MAX_PRIORITY || priority < MIN_PRIORITY) {
			throw new NumberOutOfBoundsException("number must be between " + MIN_PRIORITY + " and " + MAX_PRIORITY);
		} else {
			String id = String.format("%03d", priority) + player.getName().substring(0, 3) + SimpleMath.createRandomID(10);
			if (TEAMS_ID_TRACKER.containsValue(id)) {
				return createEntry(player, priority);
			} else {
				TEAMS_ID_TRACKER.put(player.getUniqueId(), id);
				return id;
			}
		}
	}
	
	public static void removeEntry(Player player) {
		TEAMS_ID_TRACKER.remove(player.getUniqueId());
	}
	
	public static int checkPriority(int priority) {
		if (priority > MAX_PRIORITY || priority < MIN_PRIORITY)
			throw new NumberOutOfBoundsException("number must be between " + MIN_PRIORITY + " and " + MAX_PRIORITY);
		return priority;
	}
}
