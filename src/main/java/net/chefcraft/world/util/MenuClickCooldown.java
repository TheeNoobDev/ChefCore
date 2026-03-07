package net.chefcraft.world.util;

import net.chefcraft.core.util.Cooldown;

public class MenuClickCooldown extends Cooldown {
	
	private final int maxClickPerSecond;
	private int clickPerSecond = 0;
	
	public MenuClickCooldown(int maxClickPerSecond) {
		this.maxClickPerSecond = maxClickPerSecond;
	}
	
	public int getMaxClickPerSecond() {
		return maxClickPerSecond;
	}
	
	public void onClickDefault() {
		this.onClick(40);
	}

	public void onClick(int ticks) {
		if (super.hasDuration()) {
			this.clickPerSecond++;
		} else {
			super.setDuration(ticks);
			this.clickPerSecond = 0;
		}
	}
	
	public boolean canClick() {
		return this.clickPerSecond <= this.maxClickPerSecond;
	}
	
}
