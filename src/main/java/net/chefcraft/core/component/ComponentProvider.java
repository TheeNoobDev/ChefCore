package net.chefcraft.core.component;

import com.google.common.collect.ImmutableMap;
import net.chefcraft.core.PlatformProvider;
import net.chefcraft.core.server.ServerVersion;
import net.chefcraft.core.util.TextUtil;
import net.chefcraft.core.util.TextUtil.AlignFrom;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** @since 1.3.0*/
public class ComponentProvider {
	
	static final ComponentSupport COMPONENT_SUPPORT_PLATFORM = PlatformProvider.hasKyoriAdventure() ? new MiniMessageComponentSupport() : new LegacyComponentSupport();
	static final @Nullable MiniMessageComponentSupport COMPONENT_SUPPORT_PLATFORM_KYORI = PlatformProvider.hasKyoriAdventure() ? new MiniMessageComponentSupport() : null;
	static final @Nullable LegacyComponentSupport COMPONENT_SUPPORT_PLATFORM_BUKKIT = new LegacyComponentSupport();
	
	private static final Map<Character, Integer> CHARSETS = ImmutableMap.<Character, Integer>builder()
			.put('A', 5).put('a', 5).put('B', 5).put('b', 5).put('C', 5).put('c', 5)
			.put('Ç', 5).put('ç', 5).put('D', 5).put('d', 5).put('E', 5).put('e', 5)
			.put('F', 5).put('f', 5).put('G', 5).put('g', 5).put('Ğ', 5).put('ğ', 5)
			.put('H', 5).put('h', 5).put('ı', 1).put('I', 3).put('i', 1).put('İ', 3)
			.put('J', 5).put('j', 5).put('K', 5).put('k', 5).put('L', 5).put('l', 5)
			.put('M', 5).put('m', 5).put('N', 5).put('n', 5).put('O', 5).put('o', 5)
			.put('Ö', 5).put('ö', 5).put('P', 5).put('p', 5).put('Q', 5).put('q', 5)
			.put('R', 5).put('r', 5).put('S', 5).put('s', 5).put('Ş', 5).put('ş', 5)
			.put('T', 5).put('t', 4).put('U', 5).put('u', 5).put('Ü', 5).put('ü', 5)
			.put('V', 5).put('v', 5).put('W', 5).put('w', 5).put('X', 5).put('x', 5)
			.put('Y', 5).put('y', 5).put('Z', 5).put('z', 5).put('1', 5).put('2', 5)
			.put('3', 5).put('4', 5).put('5', 5).put('6', 5).put('7', 5).put('8', 5)
			.put('9', 5).put('0', 5).put('!', 1).put('@', 6).put('#', 5).put('$', 5)
			.put('%', 5).put('^', 5).put('&', 5).put('*', 5).put('(', 4).put(')', 4)
			.put('-', 5).put('_', 5).put('+', 5).put('=', 5).put('{', 4).put('}', 4)
			.put('[', 3).put(']', 3).put(':', 1).put(';', 1).put('"', 3).put(' ', 3)
			.put('<', 4).put('>', 4).put('?', 5).put('/', 5).put(',', 1).put('|', 1)
			.put('~', 5).put('`', 2).put('.', 1).put('\\', 5).put('\'', 1).build();
	
	public static final int UNKNOWN_CHARSET_LENGHT = 4;
	
	public static int getCharLength(char c) {
		return getCharLength(c, UNKNOWN_CHARSET_LENGHT);
	}
	 
	public static int getCharLength(char c, int unknownCharsetLenght) {
		Integer i = CHARSETS.get(c);
		return i != null && i != 0 ? i : unknownCharsetLenght;
	}
	
	public static int getCharLength(String text) {
		if (text == null || text.isEmpty()) return 0;
		int i = 0;
		for (char c : text.toCharArray()) {
			i += getCharLength(c);
		}
		return i;
	}

	public static final char COLOR_CHAR = '\u00A7';
	public static final boolean HEX_SUPPORT = ServerVersion.current().isHigherThan(ServerVersion.v1_15_2);
	static final String LEGACY_COLOR_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx";
	static final Pattern LEGACY_NAMED_COLOR_CODES_PATTERN = Pattern.compile("(?i)[&§][0-9a-fk-or]");
	static final Pattern GRADIENT_PATTERN = Pattern.compile("<gradient(?::((?:#?[a-zA-Z0-9_]{3,}:?)+))?>(.*?)</gradient>");
	static final Pattern HEX_PATTERN = Pattern.compile("<(#[A-Fa-f0-9]{6})>");
	static final Pattern RANDOM_COLOR_PATTERN = Pattern.compile("<randomcolor>");
	static final Pattern TEXT_COLOR_PATTERN = Pattern.compile("<(black|dark_blue|dark_green|dark_aqua|dark_red|dark_purple|gold|gray|dark_gray|blue|green|aqua|red|light_purple|yellow|white)>");
	static final Pattern TEXT_STYLE_PATTERN = Pattern.compile("<(obfuscated|bold|strikethrough|underline|italic|reset)>");
	static final RandomColor RANDOM_COLOR = new RandomColor();
	
	@Nullable //fromBukkit
	protected static String getHexColor(@NotNull String input, int index) {
        if (index < 12) return null;

        if (input.charAt(index - 11) != 'x' || input.charAt(index - 12) != COLOR_CHAR) return null;

        for (int i = index - 10; i <= index; i += 2) {
            if (input.charAt(i) != COLOR_CHAR) {
                return null;
            }
        }

        for (int i = index - 9; i <= (index + 1); i += 2) {
            char toCheck = input.charAt(i);
            if (toCheck < '0' || toCheck > 'f') {
                return null;
            }

            if (toCheck > '9' && toCheck < 'A') {
                return null;
            }

            if (toCheck > 'F' && toCheck < 'a') {
                return null;
            }
        }

        return input.substring(index - 12, index + 2);
    }
	
	@NotNull
	public static String alignToCenter(@NotNull TextUtil.AlignFrom side, String input, final int width) {
		Objects.requireNonNull(side, "side cannot be null!");
		 if (input == null || input.isEmpty()) return input;
		 
		 String stripped = input.replaceAll("<bold>", "Ø");
		 stripped = stripped.replaceAll("[&§][lL]", "Ø");
		 stripped = stripped.replaceAll("<reset>", "Ʒ");
		 stripped = stripped.replaceAll("[&§][rR]", "Ʒ");
		 
		 stripped = stripped.replaceAll(TEXT_COLOR_PATTERN.pattern(), "");
		 stripped = stripped.replaceAll(TEXT_STYLE_PATTERN.pattern(), "");
		 stripped = stripped.replaceAll("<gradient(?::((?:#?[a-zA-Z0-9_]{3,}:?)+))?>|</gradient>", "");
		 stripped = stripped.replaceAll(HEX_PATTERN.pattern(), "");
		 stripped = COMPONENT_SUPPORT_PLATFORM.strip(stripped);
		 
		 int paddingLen = 0;
		 boolean bold = false;
		 
		 char[] c = stripped.toCharArray();
		 for (int i = 0; i < c.length; i++) {
			 
			 if (c[i] == ' ') {
				 paddingLen += 3;
				 continue;
			 }
			 
			 if (c[i] == 'Ʒ') {
				 bold = false;
				 continue;
				 
			 } else if (c[i] == 'Ø') {
				 bold = true;
				 continue;
			 }

			 paddingLen += ComponentProvider.getCharLength(c[i]) + (bold ? 1 : 0);
			 
		 }
		 
		 boolean bothSide = side == AlignFrom.BOTH_SIDES;
		 int remain = width - paddingLen;
		 int padding = (bothSide ? remain / 2 : remain) / 3;
		 StringBuilder builder = new StringBuilder();
		 
		 if (bothSide || side == AlignFrom.LEFT_SIDE) {
			 TextUtil.appendPadding(builder, padding, " ");
			 if (bothSide) {
				 TextUtil.appendPadding(builder.append(input), padding, " ");
			 } else {
				 builder.append(input);
			 }
		 } else {
			 TextUtil.appendPadding(builder.append(input), padding, " ");
		 }
		 return builder.toString();
	}
	
	@NotNull
	protected static String applyGradient(@NotNull String input, @Nullable String style, @NotNull Color... colors) {
		int segments = colors.length;
	    if (segments < 1) return input;
	    char[] chars = input.toCharArray();
	    int length = chars.length;
	    segments--;
	    
	    if (segments <= 0) {
	    	return deserializeHexToMinecraft(colors[0].getRed(), colors[0].getGreen(), colors[0].getBlue()) + (style != null ? style : "") + input;
	    }

	    StringBuilder builder = new StringBuilder();
	    int segmentLength = length / segments;
	    int remainder = length % segments;

	    int charIndex = 0;
	    for (int i = 0; i < segments; i++) {
	        Color start = colors[i];
	        Color end = colors[i + 1];
	        int currentSegmentLength = segmentLength + (i < remainder ? 1 : 0);

	        for (int j = 0; j < currentSegmentLength && charIndex < length; j++, charIndex++) {
	            builder.append(interpolateToMinecraftFormat(start.getRed(), end.getRed(), start.getGreen(), end.getGreen(), start.getBlue(), end.getBlue(), j, currentSegmentLength));
	            if (style != null) builder.append(style);
	            builder.append(chars[charIndex]);
	        }
	    }

	    return builder.toString();
	}
	
	@NotNull
    public static String translateAlternateColorCodes(char altColorChar, @NotNull String textToTranslate) {
        Objects.requireNonNull(textToTranslate, "Cannot translate null text");

        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && LEGACY_COLOR_CODES.indexOf(b[i + 1]) > -1) {
                b[i] = COLOR_CHAR;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }
	
	@NotNull
    public static String legacyColorCodesToMiniMessageCodes(@NotNull String input) {
        Objects.requireNonNull(input, "input cannot be null!");
        
        Matcher matcher = LEGACY_NAMED_COLOR_CODES_PATTERN.matcher(input);
	    StringBuffer buffer = new StringBuffer();
	    while (matcher.find()) {
	    	char code = matcher.group().charAt(1);
	    	CoreTextBase base = CoreTextColor.getByChar(code);
	    	base = base != null ? base : CoreTextStyle.getByChar(code);
	    	
	        matcher.appendReplacement(buffer, base != null ? "<" + base.name() + ">" : matcher.group());
	    }
	    matcher.appendTail(buffer);
	    return buffer.toString();
    }
	
	@NotNull
	public static String interpolateToHex(int rs, int re, int gs, int ge, int bs, int be, int currentStep, int totalSteps) {
        return String.format("#%02X%02X%02X",
        		calculateInterpolate(rs, re, currentStep, totalSteps),
        		calculateInterpolate(gs, ge, currentStep, totalSteps),
        		calculateInterpolate(bs, be, currentStep, totalSteps));
    }
	
	@NotNull
	public static String interpolateToMinecraftFormat(int rs, int re, int gs, int ge, int bs, int be, int currentStep, int totalSteps) {
		int r = calculateInterpolate(rs, re, currentStep, totalSteps);
		int g = calculateInterpolate(gs, ge, currentStep, totalSteps);
		int b = calculateInterpolate(bs, be, currentStep, totalSteps);
		return deserializeHexToMinecraft(r, g, b);
    }
	
	@NotNull
	public static String rgbToHex(int r, int g, int b) {
	    if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
	        throw new IllegalArgumentException("RGB values must be between 0 and 255.");
	    }
	    return String.format("#%02X%02X%02X", r, g, b);
	}
	
	public static int calculateInterpolate(int start, int end, int currentStep, int totalSteps) {
        float ratio = (float) currentStep / totalSteps;
        int range = end - start;
        return Math.round(start + ratio * range);
    }
	
	@NotNull
	public static String deserializeHexToMinecraft(int r, int g, int b) {
		return COLOR_CHAR + "x" + COLOR_CHAR + String.join(String.valueOf(COLOR_CHAR), String.format("%06X", (r << 16) | (g << 8) | b).split(""));
    }
	
}
