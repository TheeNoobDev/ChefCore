package net.chefcraft.world.level;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.language.TranslatablePlayer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class AbstractLevelManager {
	
	public static final float DEFAULT_LEVEL_BAR_MULTIPLIER = 500.0F;

	protected final TranslatablePlayer player;
	
	protected float currentExp = 0.0F;
	protected int currentLevel = 1;
	protected String levelBarFormat = "";
	
	public AbstractLevelManager(TranslatablePlayer player) {
		this.player = player;
	}

	public TranslatablePlayer getPlayer() {
		return player;
	}

	public float getCurrentExp() {
		return currentExp;
	}

	public void setCurrentExp(float currentExp) {
		this.currentExp = currentExp;
	}

	public int getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(int currentLevel) {
		this.currentLevel = currentLevel;
	}
	
	public void addExp(float amount) {
		float total = this.currentExp + Math.max(0, amount);
		float bar = this.currentLevelBarSize();
		if (total >= bar) {
			this.currentLevel++;
			this.currentExp = (total - bar);
			this.updateLevelBar();
			this.onLevelUp();
			
			if (this.canLevelUp()) {
				
				new BukkitRunnable() {

					@Override
					public void run() {
						if (AbstractLevelManager.this.canLevelUp()) {
							AbstractLevelManager.this.currentLevel++;
							AbstractLevelManager.this.currentExp = AbstractLevelManager.this.currentExp - AbstractLevelManager.this.currentLevelBarSize();
							AbstractLevelManager.this.updateLevelBar();
							AbstractLevelManager.this.onLevelUp();
						} else {
							this.cancel();
						}
					}
					
				}.runTaskTimer(ChefCore.getInstance(), 0L, 10L);
			}
		} else {
			this.currentExp += amount;
			this.updateLevelBar();
		}
	}
	
	public boolean canLevelUp() {
		return this.currentExp >= this.currentLevelBarSize();
	}
	
	public float getRequiredExpForNextLevel() {
		return this.currentLevelBarSize() - this.currentExp;
	}
	
	public float currentLevelBarSize() {
		return this.getLevelBarMultiplier() * this.currentLevel;
	}
	
	public String getLevelBarFormat() {
		return this.levelBarFormat;
	}
	
	public void updateLevelBar() {
		String decoration = this.getLevelBarDecoration();
		float len = decoration.replace(" ", "").length();
		len = (len < 1.0F ? 1.0F : len) / this.currentLevelBarSize();
		int prog = (int) (len * this.currentExp);
		
		StringBuilder builder = new StringBuilder();
		builder.append(this.getProgressChatColor().toString());
		
		int j = 1;
		for (char c : decoration.toCharArray()) {
			builder.append(prog >= j ? this.getProgressChatColor().toString() : this.getRequiredChatColor().toString());
			builder.append(c);
			
			if (c != ' ') {
				j++;
			}
		}
		
		this.levelBarFormat = builder.toString();
		
	}
	
	public void applyToPlayer() {
		float val = 1.0F / this.currentLevelBarSize() * this.currentExp;
		
		this.player.getPlayer().setExp(val >= 1.0F ? 1.0F : val <= 0.0F ? 0.0F : Float.isNaN(val) ? 0.0F : val);
		this.player.getPlayer().setLevel(this.currentLevel);
	}
	
	public abstract ChatColor getProgressChatColor();
	
	public abstract ChatColor getRequiredChatColor();
	
	public abstract String getLevelBarDecoration();
	
	public abstract float getLevelBarMultiplier();
	
	public abstract void onLevelUp();
}
