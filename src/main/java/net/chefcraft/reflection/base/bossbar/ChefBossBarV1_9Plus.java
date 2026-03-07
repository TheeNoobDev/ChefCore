package net.chefcraft.reflection.base.bossbar;

import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslatablePlayer;
import net.chefcraft.core.util.PreparedConditions;
import net.chefcraft.reflection.base.PacketBuilder;
import net.chefcraft.reflection.version.v1_21_R3.ChefMessageHolder;
import net.chefcraft.world.boss.CoreBarColor;
import net.chefcraft.world.boss.CoreBarStyle;
import net.chefcraft.world.boss.CoreBossBar;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.UUID;

public class ChefBossBar1_9Plus implements CoreBossBar {

	private final TranslatablePlayer translatablePlayer;
	private MessageHolder title;
    private float progress = 1.0F;
    private CoreBarColor color;
    private CoreBarStyle style;
	private boolean visible = false;

	public ChefBossBar1_9Plus(TranslatablePlayer translatablePlayer, MessageHolder title, CoreBarColor color, CoreBarStyle style) {
    	this.translatablePlayer = PreparedConditions.notNull(translatablePlayer, "translatablePlayer");
        this.title = PreparedConditions.notNull(title, "title");
        this.color = PreparedConditions.notNull(color, "color");
        this.style = PreparedConditions.notNull(style, "style");
    }

    @Override
    public @NotNull UUID getId() {
        return this.translatablePlayer.getPlayer().getUniqueId();
    }

    @Override
    public MessageHolder getTitle() {
        return this.title;
    }

    @Override
    public void setTitle(@Nonnull MessageHolder title) {
        if (!this.title.equals(title)) {
        	this.title = title;
            this.handleChanges(PacketBuilder.bossEvent().updateName(this));
        }
    }

    @Override
    public float getBarProgress() {
        return super.progress;
    }

    @Override
    public void setBarProgress(float progress) {
		if (progress < 0.0F || progress > 1.0F) {
			throw new IllegalArgumentException("progress must be between either 0.0 and 1.0");
		}
		if (progress != super.progress) {
			super.progress = progress;
			this.handleChanges(ClientboundBossEventPacket.createUpdateProgressPacket(this)); //createUpdateProgressPacket
		}
	}

    @Override
    public CoreBarColor getBarColor() {
        return mojangBarColorToCore(super.color);
    }

    @Override
    public void setBarColor(@Nonnull CoreBarColor color) {
    	BossBarColor x = coreBarColorToMojang(color);
        if (super.color != x) {
        	super.color = x;
        	this.handleChanges(ClientboundBossEventPacket.createUpdateStylePacket(this)); //createUpdateStylePacket
        }
    }

    @Override
    public CoreBarStyle getBarStyle() {
        return mojangBarStyleToCore(super.overlay);
    }

    @Override
    public void setBarStyle(@Nonnull CoreBarStyle style) {
        BossBarOverlay x = coreBarStyleToMojang(style);
        if (super.overlay != x) {
        	super.overlay = x;
        	this.handleChanges(ClientboundBossEventPacket.createUpdateStylePacket(this)); //createUpdateStylePacket
        }
    }
    
    @Override
    public boolean barShouldCreateWorldFog() {
        return super.createWorldFog;
    }

    @Override
    public boolean barShouldPlayBossMusic() {
        return super.playBossMusic;
    }
    
    @Override
    public boolean barShouldDarkenScreen() {
        return super.darkenScreen;
    }

    @Override
    public ChefBossBar setBarPlayBossMusic(boolean flag) {
    	if (super.playBossMusic != flag) {
        	super.playBossMusic = flag;
        	this.handleChanges(ClientboundBossEventPacket.createUpdatePropertiesPacket(this)); //createUpdatePropertiesPacket
        }
        return this;
    }

    @Override
    public ChefBossBar setBarCreateWorldFog(boolean flag) {
    	if (super.createWorldFog != flag) {
        	super.createWorldFog = flag;
        	this.handleChanges(ClientboundBossEventPacket.createUpdatePropertiesPacket(this)); //createUpdatePropertiesPacket
        }
        return this;
    }
    
    @Override
    public ChefBossBar setBarDarkenScreen(boolean flag) {
    	if (super.darkenScreen != flag) {
        	super.darkenScreen = flag;
        	this.handleChanges(ClientboundBossEventPacket.createUpdatePropertiesPacket(this)); //createUpdatePropertiesPacket
        }
        return this;
    }
    
    @Override
    public boolean isVisible() {
    	return this.visible;
    }
    
    @Override
    public void setVisible(boolean flag) {
    	if (this.visible != flag) {
    		this.visible = flag;
    		if (this.visible) {
    			 this.translatablePlayer.sendPacket(ClientboundBossEventPacket.createAddPacket(this)); //createAddPacket
    		} else {
    			this.translatablePlayer.sendPacket(ClientboundBossEventPacket.createRemovePacket(this.getId())); //createRemovePacket 
    		}
    	}
    }
    
    private void handleChanges(Object packet) {
		if (this.visible) {
            this.translatablePlayer.sendPacket(packet);
        }
	}
}
