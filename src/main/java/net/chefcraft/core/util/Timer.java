package net.chefcraft.core.util;

/** @since 1.0*/
public class Timer {

	private int currentTime;
	private final int expireTime;
	
	public Timer() {
		this(0, Integer.MAX_VALUE);
	}
	
	public Timer(int currentTime) {
		this(currentTime, Integer.MAX_VALUE);
	}
	
	public Timer(int currentTime, int expireTime) {
		this.currentTime = currentTime;
		this.expireTime = expireTime;
	}

	public int getCurrentTime() {
		return this.currentTime;
	}

	public void setCurrentTime(int time) {
		this.currentTime = time;
	}
	
	public void increase() {
		this.currentTime++;
	}
	
	public void decrease() {
		this.currentTime--;
	}
	
	public void increase(int amount) {
		this.currentTime += amount;
	}
	
	public void decrease(int amount) {
		this.currentTime -= amount;
	}

	public int getExpireTime() {
		return expireTime;
	}
	
	public boolean isExpired() {
		return currentTime < 1 || currentTime >= expireTime;
	}
}
