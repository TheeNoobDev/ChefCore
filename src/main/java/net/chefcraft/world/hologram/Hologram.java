package net.chefcraft.world.hologram;

import net.chefcraft.core.language.MessageHolder;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface Hologram {

	Entity getBukkitHandle();

	Location getLocation();
	
	void reload();
	
    void remove();
	
	void setCustomNameVisible(boolean customNameVisible);
	
	void setNoClip(boolean noClip);
	
	void setText(MessageHolder text);
	
	void updateLocation(Location location);
	
	void updateMetadata();
}
