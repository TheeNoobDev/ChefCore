package net.chefcraft.world.scoreboard;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.component.CoreTextBase;
import net.chefcraft.core.language.MessageHolder;
import org.bukkit.Color;

public interface CoreNumberFormat {
	
	static final CoreNumberFormat BLANK = ChefCore.getReflector().newNumberFormatByName("blank");
	static final CoreNumberFormat FIXED = ChefCore.getReflector().newNumberFormatByName("fixed");
	static final CoreNumberFormat STYLED = ChefCore.getReflector().newNumberFormatByName("styled");
	
	CoreNumberFormat withTextBase(CoreTextBase textBase);
	
	CoreNumberFormat withColor(Color color);
	
	CoreNumberFormat withFixed(MessageHolder fix);
	
	/**
	 * 
	 * @return Object is NMS NumberFormat
	 */
	
	default Object toMojang() {
		return null;
	}
}
