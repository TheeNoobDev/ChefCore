package net.chefcraft.reflection.version.v1_21_R3.scoreboard;

import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;

import javax.annotation.Nullable;

import net.chefcraft.reflection.version.v1_21_R3.ChefMessageHolder;
import net.minecraft.ChatFormatting;
import org.bukkit.Color;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.component.CoreTextBase;
import net.chefcraft.core.component.CoreTextStyle;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.world.scoreboard.CoreNumberFormat;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.numbers.BlankFormat;
import net.minecraft.network.chat.numbers.FixedFormat;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.StyledFormat;

public class ChefNumberFormat implements CoreNumberFormat {

	private final String type;
	private Color color = Color.YELLOW;
	private CoreTextBase textBase = CoreTextStyle.RESET;
	private MessageHolder fix = MessageHolder.empty();
	
	public ChefNumberFormat(String type) {
		if (type.equalsIgnoreCase("blank") || type.equalsIgnoreCase("styled") || type.equalsIgnoreCase("fixed")) {
			this.type = type.toLowerCase(Locale.ENGLISH);
		} else {
			throw new IllegalArgumentException("There is no such number type! Avaible types: blank, styled, fixed");
		}
	}
	
	@Override
	public CoreNumberFormat withColor(Color color) {
		ChefNumberFormat format = new ChefNumberFormat("styled");
		format.color = color;
		return format;
	}
	
	@Override
	public CoreNumberFormat withTextBase(CoreTextBase textBase) {
		ChefNumberFormat format = new ChefNumberFormat("styled");
		format.textBase = textBase;
		return format;
	}
	
	@Override
	public CoreNumberFormat withFixed(MessageHolder fix) {
		ChefNumberFormat format = new ChefNumberFormat("fixed");
		format.fix = fix;
		return format;
	}
	
	@Override
	public NumberFormat toMojang() {
		if (type.equalsIgnoreCase("blank")) {
			return BlankFormat.INSTANCE;
		} else if (type.equalsIgnoreCase("styled")) {
			return new StyledFormat(this.textBase == CoreTextStyle.RESET ? Style.EMPTY.withColor(this.color.asRGB()) : Style.EMPTY.applyFormat(ChatFormatting.getByCode(this.textBase.character())));
		} else if (type.equalsIgnoreCase("fixed")) {
			return new FixedFormat(ChefMessageHolder.toVanilla(this.fix));
		} else {
			return null;
		}
	}
	
	public static Optional<NumberFormat> nullableFormatCheck(@Nullable CoreNumberFormat numberFormat) {
		if (numberFormat != null) {
			try {
				return Optional.ofNullable((NumberFormat) numberFormat.toMojang());
			} catch (Exception x) {
				x.fillInStackTrace();
				ChefCore.log(Level.SEVERE, "Check your 'CoreNumberFormat' implementation class! toMojang() function must be return as '" + NumberFormat.class.getCanonicalName() + "'");
			}
		}
		return Optional.empty();
	}
}
