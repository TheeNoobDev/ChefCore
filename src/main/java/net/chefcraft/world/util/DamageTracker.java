package net.chefcraft.world.util;

import net.chefcraft.core.language.TranslatablePlayer;

import java.util.*;
import java.util.Map.Entry;

public class DamageTracker {
	
	public static final int DEFAULT_DAMAGE_TICKS = 300; 
	
	private final TranslatablePlayer player;
	private final Map<TranslatablePlayer, DamageTimer> assistsMap = new WeakHashMap<>();
	
	public DamageTracker(TranslatablePlayer player) {
		this.player = player;
	}

	public TranslatablePlayer getPlayer() {
		return player;
	}
	
	public void doAssist(TranslatablePlayer player, double damage) {
		this.doAssist(player, DEFAULT_DAMAGE_TICKS, damage);
	}
	
	public void doAssist(TranslatablePlayer player, int ticks, double damage) {
		if (player.equals(this.player)) return;
		if (assistsMap.containsKey(player)) {
			assistsMap.get(player).setDuration(ticks).increaseDamage(damage);
		} else {
			assistsMap.put(player, new DamageTimer(ticks).increaseDamage(damage));
		}
	}
	
	public void removeAssist(TranslatablePlayer player) {
		if (assistsMap.containsKey(player)) {
			assistsMap.remove(player);
		}
	}
	
	public void checkExpiredAssists() {
		if (assistsMap.isEmpty()) return;
		List<Entry<TranslatablePlayer, DamageTimer>> players = new ArrayList<>(assistsMap.entrySet());
		for (int i = 0; i < players.size(); i++) {
			Entry<TranslatablePlayer, DamageTimer> entry = players.get(i);
			if (!entry.getValue().hasDuration()) {
				assistsMap.remove(entry.getKey());
			}
		}
		players.clear();
		players = null;
	}
	
	public Map<TranslatablePlayer, Float> getAssistedPlayersDamagePercent() {
		Map<TranslatablePlayer, Float> result = new HashMap<>(this.assistsMap.size());
		
		List<Entry<TranslatablePlayer, DamageTimer>> players = new ArrayList<>(assistsMap.entrySet());

		double totalDamage = 0;
		
		for (int i = 0; i < players.size(); i++) {
			totalDamage += players.get(i).getValue().damage;
		}
		
		//proportionality constant
		double k = 100.0D / (double) Math.max(totalDamage, 1); //infinite check with Math.max()
		
		for (int i = 0; i < players.size(); i++) {
			Entry<TranslatablePlayer, DamageTimer> entry = players.get(i);
			result.put(entry.getKey(), (float) ((float) entry.getValue().damage * k));
		}
		
		players.clear();
		players = null;
		return result;
	}
	
	public Set<TranslatablePlayer> getAssists() {
		return this.assistsMap.keySet();
	}
	
	public void clear() {
		this.assistsMap.clear();
	}
	
	public static class DamageTimer {
		
		private double duration = 0.0D;
		private double damage = 0.0D;
		
		public DamageTimer() {}
		
		public DamageTimer(int ticks) {
			this.setDuration(ticks);
		}
		
		public DamageTimer setDuration(int ticks) {
			this.duration = System.currentTimeMillis() + ((ticks * 0.05D) * 1000.0D);
			return DamageTimer.this;
		}
		
		public double getDuration() {
			return this.hasDuration() ? Math.abs((this.duration - System.currentTimeMillis()) / 1000.0D) : 0.0D;
		}
		
		public boolean hasDuration() {
			return this.duration >= System.currentTimeMillis();
		}
		
		public DamageTimer increaseDamage(double damage) {
			this.damage += damage;
			return DamageTimer.this;
		}

		public double getDamage() {
			return this.damage;
		}

		public void setDamage(double damage) {
			this.damage = damage;
		}
	}
}
