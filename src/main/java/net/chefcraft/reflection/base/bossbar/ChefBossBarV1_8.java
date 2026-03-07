package net.chefcraft.reflection.base.bossbar.v1_8;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslatablePlayer;
import net.chefcraft.reflection.SharedVariables;
import net.chefcraft.reflection.base.PacketBuilder;
import net.chefcraft.reflection.world.CoreEntityType;
import net.chefcraft.world.boss.CoreBarColor;
import net.chefcraft.world.boss.CoreBarStyle;
import net.chefcraft.world.boss.CoreBossBar;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ChefBossBarV1_8 implements CoreBossBar {
	
	private final TranslatablePlayer owner;
	
	private MessageHolder title;
	private boolean visible = false;
	private float progress = 1.0F;
	private Wither wither = null;
	
	public ChefBossBarV1_8(TranslatablePlayer owner, MessageHolder title) {
    	this.title = title;
        this.owner = owner;
        this.wither = (Wither) CoreEntityType.WITHER.createInstance(owner.getPlayer().getWorld());
        SharedVariables.GAME_REFLECTIONS.setLivingEntityAI(this.wither, false);
        SharedVariables.GAME_REFLECTIONS.setEntityGravity(this.wither, false);
        SharedVariables.GAME_REFLECTIONS.setEntitySilent(this.wither, true);
        SharedVariables.GAME_REFLECTIONS.setEntityInvulnerable(this.wither, true);
        SharedVariables.GAME_REFLECTIONS.setNoClip(this.wither, true);
        this.wither.setInvisible(true);
        this.wither.setCustomNameVisible(true);
        this.wither.setCustomName(this.title.asString(true));
    }
    
    public void updateWitherLocation() {
    	if (this.visible) {

    		if (this.wither.getWorld() != this.owner.getPlayer().getWorld()) {
    			this.wither.teleport(this.owner.getPlayer().getLocation());

                this.owner.sendPacket(PacketBuilder.entity().removeEntities(this.wither));
                this.owner.sendPacket(PacketBuilder.entity().addEntity(this.wither));

        	}

    		Location pos = this.owner.getPlayer().getLocation();
            pos.add(pos.getDirection().multiply(100.0D));
            SharedVariables.GAME_REFLECTIONS.setEntityPos(this.wither, pos.getX(), pos.getY(), pos.getZ(), (pos.getYaw() + 180.0F) % 360.0F, 0);
    		this.owner.sendPacket(PacketBuilder.entity().teleportEntity(this.wither));
    	}
    }

    @Override
    public UUID getId() {
        return this.wither.getUniqueId();
    }

    @Override
    public MessageHolder getTitle() {
        return this.title;
    }

    @Override
    public void setTitle(@NotNull MessageHolder title) {
        if (!this.title.equals(title)) {
        	this.title = title;
        	this.wither.setCustomName(title.asString(true));
        	if (this.visible) {
                this.owner.sendPacket(PacketBuilder.entity().setEntityData(this.wither));
        	}
        }
    }

    @Override
    public float getBarProgress() {
        return progress;
    }

    @Override
    public void setBarProgress(float progress) {
		if (progress < 0.0F || progress > 1.0F) {
			throw new IllegalArgumentException("progress must be between either 0.0 and 1.0");
		}
		if (progress != this.progress) {
			this.progress = progress;
			this.wither.setHealth(progress * 300.0F);

			if (this.visible) {
                this.owner.sendPacket(PacketBuilder.entity().setEntityData(this.wither));
			}
		}
	}

    @Override
    public void setVisible(boolean flag) {
    	if (flag != this.visible) {
    		this.visible = flag;

    		if (flag) {
                this.owner.sendPacket(PacketBuilder.entity().addEntity(this.wither));
                this.updateWitherLocation();

    			new BukkitRunnable() {

                    final Player player = owner.getPlayer();
    				double x = SharedVariables.GAME_REFLECTIONS.getEntityPosX(player);
    				double y = SharedVariables.GAME_REFLECTIONS.getEntityPosY(player);
    				double z = SharedVariables.GAME_REFLECTIONS.getEntityPosZ(player);
                    float yaw = SharedVariables.GAME_REFLECTIONS.getEntityYaw(player);
                    float pitch = SharedVariables.GAME_REFLECTIONS.getEntityPitch(player);

    				public void run() {
    					if (ChefBossBarV1_8.this.visible && player.isOnline()) {

    						if (SharedVariables.GAME_REFLECTIONS.getEntityPosX(player) != x
                                    || SharedVariables.GAME_REFLECTIONS.getEntityPosY(player) != y
                                    || SharedVariables.GAME_REFLECTIONS.getEntityPosZ(player) != z
                                    || SharedVariables.GAME_REFLECTIONS.getEntityYaw(player) != yaw
                                    || SharedVariables.GAME_REFLECTIONS.getEntityPitch(player) != pitch) {
    							ChefBossBarV1_8.this.updateWitherLocation();
    						}
    						
    						x = SharedVariables.GAME_REFLECTIONS.getEntityPosX(player);
    	    				y = SharedVariables.GAME_REFLECTIONS.getEntityPosY(player);
    	    				z = SharedVariables.GAME_REFLECTIONS.getEntityPosZ(player);
                            yaw = SharedVariables.GAME_REFLECTIONS.getEntityYaw(player);
                            pitch = SharedVariables.GAME_REFLECTIONS.getEntityPitch(player);
    						
    					} else {
    						this.cancel();
    					}
    				}
    			}.runTaskTimer(ChefCore.getInstance(), 0, 5L);
    		} else {
                this.owner.sendPacket(PacketBuilder.entity().removeEntities(this.wither));
    		}
    	}
    }
    
    @Override
    public boolean isVisible() {
    	return this.visible;
    }

    @Override
    public CoreBarColor getBarColor() {
        return CoreBarColor.PINK;
    }
    
    @Override
    public CoreBarStyle getBarStyle() {
        return CoreBarStyle.SOLID;
    }

    @Override
    public void setBarColor(@NotNull CoreBarColor color) { }

    @Override
    public void setBarStyle(@NotNull CoreBarStyle style) { }
    
    @Override
    public boolean barShouldCreateWorldFog() {
        return false;
    }

    @Override
    public boolean barShouldPlayBossMusic() {
        return false;
    }
    
    @Override
    public boolean barShouldDarkenScreen() {
        return false;
    }

    @Override
    public ChefBossBarV1_8 setBarPlayBossMusic(boolean flag) {
        return this;
    }

    @Override
    public ChefBossBarV1_8 setBarCreateWorldFog(boolean flag) {
        return this;
    }
    
    @Override
    public ChefBossBarV1_8 setBarDarkenScreen(boolean flag) {
        return this;
    }
}
