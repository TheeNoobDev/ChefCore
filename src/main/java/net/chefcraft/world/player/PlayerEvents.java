package net.chefcraft.world.player;

import net.chefcraft.core.language.TranslatablePlayer;

public interface PlayerEvents {

	void onPlayerJoin(TranslatablePlayer translatablePlayer);
	
	void onPlayerQuit(TranslatablePlayer translatablePlayer);
	
	void onPlayerRespawn(TranslatablePlayer translatablePlayer);
}
