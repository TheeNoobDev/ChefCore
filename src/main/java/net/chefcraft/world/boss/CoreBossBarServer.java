package net.chefcraft.world.boss;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.language.TranslatablePlayer;
import net.chefcraft.core.util.Numbers;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.world.player.PlayerEvents;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoreBossBarServer implements PlayerEvents {

	private final Map<TranslatablePlayer, CoreBossBar> playersMap = new HashMap<>();
	private String titleKey;
	private CoreBarColor color;
	private CoreBarStyle style;
	private float progress = 1.0F;
	private boolean playBossMusic = false;
	private boolean createWorldFog = false; 
	private boolean darkenScreen = false;
	private boolean visible = false;
	
	public CoreBossBarServer(String titleKey, CoreBarColor color, CoreBarStyle style, @Nullable Placeholder placeholder, List<? extends TranslatablePlayer> players) {
		this.titleKey = titleKey;
		this.color = color;
		this.style = style;
		
		for (TranslatablePlayer player : players) {
			this.playersMap.put(player, ChefCore.getReflector().newBossBarForPlayer(player, player.getMessage(titleKey, placeholder), color, style));
		}
	}

	@Override
	public void onPlayerJoin(TranslatablePlayer translatablePlayer) {
		this.playersMap.put(translatablePlayer, ChefCore.getReflector().newBossBarForPlayer(translatablePlayer, 
				translatablePlayer.getMessage(this.titleKey), this.color, this.style))
				.setBarCreateWorldFog(this.createWorldFog)
				.setBarDarkenScreen(this.darkenScreen)
				.setBarPlayBossMusic(this.playBossMusic)
				.setVisible(this.visible);
	}

	@Override
	public void onPlayerQuit(TranslatablePlayer translatablePlayer) {
		CoreBossBar boss = this.playersMap.remove(translatablePlayer);
		
		if (boss != null) {
			boss.setVisible(false);
		}
	}

	@Override
	public void onPlayerRespawn(TranslatablePlayer translatablePlayer) { }
	
	@Nullable
	public CoreBossBar getBossBarByPlayer(TranslatablePlayer translatablePlayer) {
		return this.playersMap.get(translatablePlayer);
	}

	public String getTitleKey() {
		return this.titleKey;
	}

	public void setTitleKey(String titleKey, @Nullable Placeholder placeholder) {
		if (this.titleKey.equals(titleKey)) return;
		this.titleKey = titleKey;
		
		this.playersMap.forEach((player, boss) -> boss.setTitle(player.getMessage(titleKey, placeholder)));
		
	}
	
	public void increaseProgress(float amount) {
		this.setBarProgress(Numbers.checkRangeOrElseMax(this.progress + amount, 0.0F, 1.0F));
	}
	
	public void decreaseProgress(float amount) {
		this.setBarProgress(Numbers.checkRangeOrElseMin(this.progress - amount, 0.0F, 1.0F));
	}

	public float getBarProgress() {
		return this.progress;
	}

	public void setBarProgress(float progress) {
		if (this.progress == progress) return;
		
		this.progress = Numbers.checkRange(progress, 0.0F, 1.0F);
		
		this.playersMap.values().forEach(boss -> boss.setBarProgress(progress));
	}

	public CoreBarColor getBarColor() {
		return this.color;
	}

	public void setBarColor(CoreBarColor color) {
		if (this.color == color) return;
		this.color = color;
		
		this.playersMap.values().forEach(boss -> boss.setBarColor(color));
	}

	public CoreBarStyle getBarStyle() {
		return this.style;
	}

	public void setBarStyle(CoreBarStyle style) {
		if (this.style == style) return;
		this.style = style;
		
		this.playersMap.values().forEach(boss -> boss.setBarStyle(style));
	}

	public boolean barShouldCreateWorldFog() {
		return this.createWorldFog;
	}

	public boolean barShouldPlayBossMusic() {
		return this.playBossMusic;
	}

	public boolean barShouldDarkenScreen() {
		return this.darkenScreen;
	}
	
	public boolean isVisible() {
		return this.visible;
	}

	public CoreBossBarServer setBarPlayBossMusic(boolean flag) {
		if (this.playBossMusic != flag) {
			this.playBossMusic = flag;
			
			this.playersMap.values().forEach(boss -> boss.setBarPlayBossMusic(flag));
		}
		return this;
	}

	public CoreBossBarServer setBarCreateWorldFog(boolean flag) {
		if (this.createWorldFog != flag) {
			this.createWorldFog = flag;
			
			this.playersMap.values().forEach(boss -> boss.setBarCreateWorldFog(flag));
		}
		return this;
	}

	public CoreBossBarServer setBarDarkenScreen(boolean flag) {
		if (this.darkenScreen != flag) {
			this.darkenScreen = flag;
			
			this.playersMap.values().forEach(boss -> boss.setBarDarkenScreen(flag));
		}
		return this;
	}

	public CoreBossBarServer setVisible(boolean flag) {
		if (this.visible != flag) {
			this.visible = flag;
			
			this.playersMap.values().forEach(boss -> boss.setVisible(flag));
		}
		return this;
	}
	
}
