package net.chefcraft.world.rarity;

import net.chefcraft.core.collect.ImmutableList;
import net.chefcraft.core.component.CoreTextColor;
import net.chefcraft.core.language.TranslationSource;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public enum CoreRarity {

	COMMON(CoreTextColor.WHITE, "common", 1),
	UNCOMMON(CoreTextColor.YELLOW, "uncommon", 2),
	RARE(CoreTextColor.AQUA, "rare", 3),
	EPIC(CoreTextColor.LIGHT_PURPLE, "epic", 4),
	LEGENDARY(CoreTextColor.GOLD, "legendary", 5),
	MYTHICAL(CoreTextColor.DARK_RED, "mythical", 6);
	
	private final CoreTextColor textColor;
	private final String name;
	private final int id;

	private CoreRarity(CoreTextColor textColor, String name, int id) {
		this.textColor = textColor;
		this.name = name;
		this.id = id;
	}
	
	public CoreTextColor getTextColor() {
		return this.textColor;
	}
	
	public int getId() {
		return this.id;
	}

	public String getName() {
		return name;
	}
	
	public String getTranslationKey() {
		return "rarities." + this.name;
	}
	
	public String getTranslatedName(TranslationSource source, boolean colorize) {
		return colorize ? this.textColor + source.getPlainMessage(this.getTranslationKey()) : source.getPlainMessage(this.getTranslationKey());
	}
	
	public static final Comparator<? super Evaluable> VALUE_COMPARATOR = (x , y) -> x.getRarity().id - y.getRarity().id;
	
	public static void sortValues(List<? extends Evaluable> list) {
		list.sort(VALUE_COMPARATOR);
	}
	
	public static <K> List<? extends Evaluable>  getSortedListFromMap(Map<K, ? extends Evaluable> map) {
		return ImmutableList.sortedCopyOf(map.values(), VALUE_COMPARATOR);
	}
}
