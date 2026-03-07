package net.chefcraft.core.component;

import net.chefcraft.core.util.TextUtil;
import net.chefcraft.core.util.TextUtil.AlignFrom;
import net.chefcraft.core.util.TextUtil.TextDetails;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Objects;
import java.util.regex.Matcher;

/**@since 1.3.0*/
public interface ComponentSupport {
	
	/**
	 *<pre>
  	 * If Kyori Adventure is supported, it returns Kyori support,
  	 * if not, it returns legacy support.
	 */
	@NotNull
	static ComponentSupport support() {
		return ComponentProvider.COMPONENT_SUPPORT_PLATFORM;
	}
	
	/**
	 *<pre>
  	 * Components Utils supported with classic Bukkit's chat color formats.
  	 * if you are using velocity this will be null
	 */
	@Nullable
	static LegacyComponentSupport legacySupport() {
		return ComponentProvider.COMPONENT_SUPPORT_PLATFORM_BUKKIT;
	}
	
	/**
	 *<pre>
  	 * If your Minecraft server version does not support Kyori Adventure,
  	 * it will return null.
	 */
	@Nullable
	static MiniMessageComponentSupport miniMessageSupport() {
		return ComponentProvider.COMPONENT_SUPPORT_PLATFORM_KYORI;
	}
	
	/** 
	 * @param input text
	 * @param randomColor randomColor
	 * @return deserialized text
	 */
	@NotNull Object deserialize(@NotNull String input, @Nullable RandomColor randomColor);
	
	/**
	 * @param input text to strip
	 * @returns plain text separated from its colors and all its components
	 */
	@NotNull String strip(@NotNull String input);
	
	/** 
	 * @param side the side you want to align
	 * @param input text
	 * @param width width of the area
	 * @return centered text
	 */
	default @NotNull String alignToCenter(@NotNull TextUtil.AlignFrom side, @Nullable String input, int width) {
		return ComponentProvider.alignToCenter(side, input, width);
	}
	
	/**
	 * @param side the side you want to align
	 * @param input text
	 * @return centered text
	 * <pre>
	 * Examples (ignore '.'):
	 * <.center>Hello  <./center>
	 * <.center:left>  Hello from Left<./center>
	 * <.center:both> Hello from Left <./center>
	 * <.center:right>Hello from Left  <./center>
	 * </pre>
	 */
	default @NotNull String parseTagsAndAlignToCenter(@NotNull String input, int width) {
		if (input == null || input.isEmpty()) return "";
		 
		 TextDetails details = TextUtil.textDetails(TextUtil.aligmentPattern(), this.strip(input), 2);
		 if (details.matchesCount() == 0) return input;
		 
		 float rebalancedWidth = (float)(width - ComponentProvider.getCharLength(details.unmatches().toString())) / (float) Math.max(details.matchesCount(), 1);
		 StringBuilder builder = new StringBuilder();
		 
		 Matcher matcher = TextUtil.aligmentPattern().matcher(input);
		 int unmatchedStart = 0, unmatchedEnd = 0;
		 while (matcher.find()) {
			 
			 unmatchedEnd = matcher.start();
			 
			 builder.append(input.substring(unmatchedStart, unmatchedEnd));
			 builder.append(this.alignToCenter(AlignFrom.matchOrDefault(matcher.group(1)), matcher.group(2), (int) rebalancedWidth));
			 
			 unmatchedStart = matcher.end();
		 }
		 
		 if (input.length() > unmatchedStart) {
			 builder.append(input.substring(unmatchedStart, input.length()));
		 }
		 
		 return builder.toString();
	}
	
	@NotNull
	default String deserializeHexColors(@NotNull String input) {
		Matcher matcher = ComponentProvider.HEX_PATTERN.matcher(input);
		while (matcher.find()) {
			String color = matcher.group(1);
			Color deco = Color.decode(color);
			input = input.replace("<" + color + ">", ComponentProvider.deserializeHexToMinecraft(deco.getRed(), deco.getGreen(), deco.getBlue()));
		}
		return input;
	}
	
	@NotNull
	default String deserializeTextColors(@NotNull String input) {
		Matcher matcher = ComponentProvider.TEXT_COLOR_PATTERN.matcher(input);
		while (matcher.find()) {
			String color = matcher.group(1);
			input = input.replace("<" + color + ">", CoreTextColor.getByName(color).toString());
		}
		return input;
	}
	
	@NotNull
	default String deserializeTextStyles(@NotNull String input) {
		Matcher matcher = ComponentProvider.TEXT_STYLE_PATTERN.matcher(input);
		while (matcher.find()) {
			String style = matcher.group(1);
			input = input.replace("<" + style + ">", CoreTextStyle.getByName(style).toString());
		}
		return input;
	}
	
	@NotNull
	default String deserializeRandomColor(@NotNull String input, @NotNull RandomColor randomColor, boolean asBukkit) {
		Matcher matcher = ComponentProvider.RANDOM_COLOR_PATTERN.matcher(input);
	    StringBuffer buffer = new StringBuffer();
	    while (matcher.find()) {
	        matcher.appendReplacement(buffer, asBukkit ? randomColor.nextBukkit() : "<" + randomColor.nextKyori() + ">");
	    }
	    matcher.appendTail(buffer);
	    return buffer.toString();
	}
	
	@NotNull
	default String deserializeGradient(@NotNull String input) {
		Matcher matcher = ComponentProvider.GRADIENT_PATTERN.matcher(input);
		StringBuffer buffer = new StringBuffer();
		
		int outStart = 0;
		int outEnd = 0;
		
	    while (matcher.find()) {
	        String colors = matcher.group(1);
	        String content = matcher.group(2);
	        
	        outEnd = matcher.start();
	        String back = input.substring(outStart, outEnd);
	        String lastStyles = getLastTextStyles(back);
	        outStart = matcher.end();
	        
	        if (colors != null) {
	        	
	        	String[] colorArray = colors.split(":");
	        	Color[] cls = new Color[colorArray.length];
	        	
	        	for (int i = 0; i < cls.length; i++) {
	        		CoreTextColor textColor = CoreTextColor.getByName(colorArray[i]);
	        		if (textColor != null) {
	        			cls[i] = textColor.color();
	        			continue;
	        		}
	        		
	        		cls[i] = Color.decode(colorArray[i]);
	        	}
	        	
	        	matcher.appendReplacement(buffer, Matcher.quoteReplacement(ComponentProvider.applyGradient(content, lastStyles.isEmpty() ? null : lastStyles,  cls)));
	        } else {
	        	matcher.appendReplacement(buffer, Matcher.quoteReplacement(content));
	        }
	        
	        
	    }
	    
	    matcher.appendTail(buffer);
	    return buffer.toString();
	}
	
	@NotNull //fromBukkit
    default String getLastTextStyles(@NotNull String input) {
        Objects.requireNonNull(input, "input cannot be null!");

        String result = "";
        int length = input.length();

        for (int index = length - 1; index > -1; index--) {
            char section = input.charAt(index);
            if (section == ComponentProvider.COLOR_CHAR && index < length - 1) {
                char c = input.charAt(index + 1);
                
                if (CoreTextColor.getByChar(c) != null) break;
                
                CoreTextStyle style = CoreTextStyle.getByChar(c);
                
                if (style != null) {
                    result = style.toString() + result;
                    if (style.equals(CoreTextStyle.RESET)) {
                    	break;
                    }
                
                }
            }
        }

        return result;
    }
	
	@NotNull //fromBukkit
    default String getLastTextModifiers(@NotNull String input) {
		Objects.requireNonNull(input, "input cannot be null!");

        String result = "";
        int length = input.length();

        for (int index = length - 1; index > -1; index--) {
            char section = input.charAt(index);
            if (section == ComponentProvider.COLOR_CHAR && index < length - 1) {

                String hexColor = ComponentProvider.getHexColor(input, index);
                if (hexColor != null) {
                    result = hexColor + result;
                    break;
                }

                char c = input.charAt(index + 1);
                CoreTextColor color = CoreTextColor.getByChar(c);
                CoreTextStyle style = CoreTextStyle.getByChar(c);

                if (color != null) {
                    result = color.toString() + result;
                    break;
                }
                
                if (style != null) {
                    result = style.toString() + result;
                    if (style.equals(CoreTextStyle.RESET)) {
                    	break;
                    }
                
                }
            }
        }

        return result;
    }

    default CoreTextColor getClosestTextColor(String hexadecimal) {
    	return getClosestTextColor(Color.decode(hexadecimal));
    }

    default CoreTextColor getClosestTextColor(Color color) {
    	CoreTextColor closest = null;
        int mark = 0;
        
        for (CoreTextColor textColor : CoreTextColor.registry().values()) {
        	int diff = getColorDiff(textColor.color(), color);
        	if (closest == null || diff < mark) {
        		closest = textColor;
                mark = diff;
        	}
        }
        return closest;
    }

    default int getColorDiff(Color color, Color compare) {
        int a = color.getAlpha() - compare.getAlpha(),
                r = color.getRed() - compare.getRed(),
                g = color.getGreen() - compare.getGreen(),
                b = color.getBlue() - compare.getBlue();
        return a * a + r * r + g * g + b * b;
    }
}
