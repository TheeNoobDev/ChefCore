package net.chefcraft.service.npc;

import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

public class NPCTracker {
	
	private static final Set<LivingEntityNPC> TRACKER_SET = new HashSet<>();
	
	public static boolean addEntry(LivingEntityNPC npc) {
		if (!npc.isTracking() || TRACKER_SET.contains(npc)) return false;
		return TRACKER_SET.add(npc);
	}
	
	public static boolean removeEntry(LivingEntityNPC npc) {
		return TRACKER_SET.remove(npc);
	}
	
	public static Set<LivingEntityNPC> getEntries() {
		return ImmutableSet.copyOf(TRACKER_SET);
	}
	
	public static void removeEntries() {
		for (LivingEntityNPC npc : TRACKER_SET) {
			npc.remove();
		}
		TRACKER_SET.clear();
	}
}
