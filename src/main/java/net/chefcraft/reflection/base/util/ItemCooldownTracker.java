package net.chefcraft.reflection.base.utils;

import net.chefcraft.core.util.Cooldown;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class ItemCooldownTracker {

	private static final Map<UUID, Map<Material, Cooldown>> COOLDOWN_TRACKER_MAP = new WeakHashMap<>();
	
	public static boolean hasCooldown(Player player, Material material) {
		Map<Material, Cooldown> tracker = COOLDOWN_TRACKER_MAP.get(player.getUniqueId());
		if (tracker != null && tracker.containsKey(material)) {
            if (!tracker.get(material).hasDuration()) {
                tracker.remove(material);
                return false;
            }
            return true;
		}
        return false;
	}
	
	public static int getCooldown(Player player, Material material) {
		Map<Material, Cooldown> tracker = COOLDOWN_TRACKER_MAP.get(player.getUniqueId());
		if (tracker != null && tracker.containsKey(material)) {
			return (int) tracker.get(material).getDuration();
		}
		return 0;
	}
	
	public static void setCooldown(Player player, Material material, int ticks) {
		UUID uuid = player.getUniqueId();
        Map<Material, Cooldown> tracker = COOLDOWN_TRACKER_MAP.get(uuid);
        if (tracker == null) {
            tracker = new HashMap<>();
            tracker.put(material, Cooldown.of(ticks));
            COOLDOWN_TRACKER_MAP.put(uuid, tracker);
        } else {
            tracker.put(material, Cooldown.of(ticks));
        }
	}
}
