package net.chefcraft.world.boss;

import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.util.Numbers;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @since 1.2.2
 */
public interface CoreBossBar {
	
	UUID getId();

    MessageHolder getTitle();
    
    CoreBarColor getBarColor();
    
    CoreBarStyle getBarStyle();

    CoreBossBar setBarPlayBossMusic(boolean flag);

    CoreBossBar setBarCreateWorldFog(boolean flag);
    
    CoreBossBar setBarDarkenScreen(boolean flag);
    
    float getBarProgress();
    
    boolean barShouldCreateWorldFog();

    boolean barShouldPlayBossMusic();
    
    boolean barShouldDarkenScreen();
    
    boolean isVisible();
    
    void setTitle(@NotNull MessageHolder title);

    void setBarProgress(float progress);

    void setBarColor(@NotNull CoreBarColor color);

    void setBarStyle(@NotNull CoreBarStyle style);
    
    void setVisible(boolean flag);
    
    public default void increaseProgress(float amount) {
		this.setBarProgress(Numbers.checkRangeOrElseMax(this.getBarProgress() + amount, 0.0F, 1.0F));
	}
	
	public default void decreaseProgress(float amount) {
		this.setBarProgress(Numbers.checkRangeOrElseMin(this.getBarProgress() - amount, 0.0F, 1.0F));
	}
}
