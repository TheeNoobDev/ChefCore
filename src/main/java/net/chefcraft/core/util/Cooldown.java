package net.chefcraft.core.util;

import java.text.DecimalFormat;

/** @since 1.0*/
public class Cooldown {

	private static final DecimalFormat FORMATTER = new DecimalFormat("0.0");
	
	private double duration = 0;
	
	public Cooldown() {}
	
	public Cooldown(int ticks) {
		this.setDuration(ticks);
	}
	
	public void setDuration(int ticks) {
		if (ticks > 0) {
			this.duration = System.currentTimeMillis() + ((ticks * 0.05D) * 1000.0D);
		} else {
			this.duration = 0;
		}
	}
	
	public double getDuration() {
		return this.hasDuration() ? Math.abs((this.duration - System.currentTimeMillis()) / 1000.0D) : 0.0D;
	}
	
	public String getFromattedDuration() {
		return this.hasDuration() ? FORMATTER.format(Math.abs((this.duration - System.currentTimeMillis()) / 1000.0D)) : "0.0";
	}
	
	public boolean hasDuration() {
		return this.duration >= System.currentTimeMillis();
	}
	
	public static Cooldown of(int ticks) {
		return new Cooldown(ticks);
	}
	
	public static Cooldown zero() {
		return new Cooldown(0);
	}
}
