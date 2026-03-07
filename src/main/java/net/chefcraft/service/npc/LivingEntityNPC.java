package net.chefcraft.service.npc;

import net.chefcraft.world.hologram.NpcHologramManager;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public interface LivingEntityNPC {
	
	NpcHologramManager getHologramManager();
	
	Location getLocation();
	
	JavaPlugin getPlugin();
	
	LivingEntity getEntity();

	NonPlayerCharTypes getType();
	
	int getUpdateTicks();
	
	float getLookCloseSize();

	boolean isLookingClose();
	
	boolean isTracking();
	
	boolean hasHologramManager();
	
	void spawn();
	
	void update();
	
	void remove();
	
	void setLocation(Location location);

	void setLookClose(boolean lookClose);
	
	void setLookCloseSize(float lookCloseSize);
	
	void setUpdateTicks(int updateTicks);

	void startLookTask();
	
	void stopLookTask();
	
	void onPlayerJoin(Player player);
	
	void onPlayerQuit(Player player);
	
	void onPlayerRespawn(Player player);
}
