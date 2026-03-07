package net.chefcraft.service.npc;

import net.chefcraft.core.util.ObjectKey;
import net.chefcraft.world.rarity.CoreRarity;
import net.chefcraft.world.rarity.Evaluable;
import org.jetbrains.annotations.NotNull;

public interface NonPlayerChar {

    NonPlayerCharType VILLAGER 			    = new NonPlayerCharTypeImpl(CoreRarity.COMMON, "villager");
    NonPlayerCharType WANDERING_TRADER 	    = new NonPlayerCharTypeImpl(CoreRarity.RARE, "wandering_trader");
    NonPlayerCharType WARDEN 				= new NonPlayerCharTypeImpl(CoreRarity.EPIC, "warden");
    NonPlayerCharType ZOMBIE 				= new NonPlayerCharTypeImpl(CoreRarity.UNCOMMON, "zombie");
    NonPlayerCharType SKELETON 			    = new NonPlayerCharTypeImpl(CoreRarity.UNCOMMON, "skeleton");
    NonPlayerCharType ENDERMAN 			    = new NonPlayerCharTypeImpl(CoreRarity.EPIC, "enderman");
    NonPlayerCharType BLAZE 				= new NonPlayerCharTypeImpl(CoreRarity.RARE, "blaze");
    NonPlayerCharType LLAMA 				= new NonPlayerCharTypeImpl(CoreRarity.UNCOMMON, "llama");
    NonPlayerCharType PLAYER_MIRROR 		= new NonPlayerCharTypeImpl(CoreRarity.LEGENDARY, "player_mirror");
}
