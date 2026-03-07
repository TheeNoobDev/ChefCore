package net.chefcraft.core.util;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** @since 1.3.0*/
public class TextUtil {
	
	 private static final Pattern ALIGNMENT_PATTERN = Pattern.compile("<center(?::(left|right|both))?>(.*?)</center>");
	 
	 @NotNull
	 public static Pattern aligmentPattern() {
		 return ALIGNMENT_PATTERN;
	 }
	 
	 /** 
	  * @param side the side you want to align
	  * @param input text
	  * @param width width of the area
	  * @return centered text
	  */
	 @NotNull
	 public static String alignToCenter(@NotNull AlignFrom side, @Nullable String input, int width) {
		 Objects.requireNonNull(side, "side cannot be null!");
		 if (input == null || input.isEmpty()) return "";
		 
		 boolean bothSide = side == AlignFrom.BOTH_SIDES;
		 int padding = bothSide ? (width - input.length()) / 2 : (width - input.length());
		 StringBuilder builder = new StringBuilder();
		 
		 if (bothSide || side == AlignFrom.LEFT_SIDE) {
			 appendPadding(builder, padding, " ");
			 if (bothSide) {
				 appendPadding(builder.append(input), padding, " ");
			 } else {
				 builder.append(input);
			 }
		 } else {
			 appendPadding(builder.append(input), padding, " ");
		 }
		 return builder.toString();
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
	 @NotNull
	 public static String parseTagsAndAlignToCenter(@NotNull String input, int width) {
		 if (input == null || input.isEmpty()) return "";
		 
		 TextDetails details = textDetails(ALIGNMENT_PATTERN, input, 2);
		 if (details.matchesCount == 0) return input;
		 
		 int rebalancedWidth = (width - details.unmatches.length()) / Math.max(details.matchesCount, 1);
		 StringBuilder builder = new StringBuilder();
		 
		 Matcher matcher = ALIGNMENT_PATTERN.matcher(input);
		 int unmatchedStart = 0, unmatchedEnd = 0;
		 while (matcher.find()) {
			 
			 unmatchedEnd = matcher.start();
			 
			 builder.append(input.substring(unmatchedStart, unmatchedEnd));
			 builder.append(alignToCenter(AlignFrom.matchOrDefault(matcher.group(1)), matcher.group(2), rebalancedWidth));
			 
			 unmatchedStart = matcher.end();
		 }
		 
		 if (input.length() > unmatchedStart) {
			 builder.append(input.substring(unmatchedStart, input.length()));
		 }
		 
		 return builder.toString();
	 }
	 
	 @NotNull
	 public static StringBuilder appendPadding(@NotNull StringBuilder builder, int length, String pad) {
		 Objects.requireNonNull(builder, "builder cannot be null");
		 for (int i = 0; i < length; i++) {
			 builder.append(pad);
		 }
		 return builder;
	 }
	
	 public static int countMatches(@NotNull String regex, CharSequence sequence) {
		 Objects.requireNonNull(regex, "regex cannot be null!");
		 return countMatches(Pattern.compile(regex), sequence);
	 }
	 
	 public static int countMatches(@NotNull Pattern pattern, CharSequence sequence) {
		 Objects.requireNonNull(pattern, "pattern cannot be null!");
		 Matcher match = pattern.matcher(sequence);
		 int matches = 0;
		 while (match.find()) matches++;
		 return matches;
	 }
	 
	 @NotNull
	 public static TextDetails textDetails(@NotNull String regex, CharSequence sequence, int group) {
		 Objects.requireNonNull(regex, "regex cannot be null!");
		 return textDetails(Pattern.compile(regex), sequence, group);
	 }
	 
	 @NotNull
	 public static TextDetails textDetails(@NotNull Pattern pattern, CharSequence sequence, int group) {
		 Objects.requireNonNull(pattern, "pattern cannot be null!");
		 Matcher match = pattern.matcher(sequence);
		 String input = sequence.toString();
		 
		 final TextDetails details = new TextDetails();
		 int unmatchedStart = 0;
		 int unmatchedEnd = 0;
		 
		 while (match.find()) {
			 details.matchesCount++;
			 details.matches.append(match.group(group));
			 
			 unmatchedEnd = match.start();
			 details.unmatches.append(input.substring(unmatchedStart, unmatchedEnd));
			 unmatchedStart = match.end();
		 }
		 
		 if (input.length() > unmatchedStart) {
			 details.unmatches.append(input.substring(unmatchedStart, input.length()));
		 }
		 
		 return details;
	 }
	 
	 public static final class TextDetails {
		 
		 StringBuilder matches;
		 StringBuilder unmatches;
		 int matchesCount;
		 
		 TextDetails() { 
			 this(0, new StringBuilder(), new StringBuilder());
		 }
		 
		 TextDetails(int matchesCount, StringBuilder matches) {
			 this(matchesCount, matches, new StringBuilder());
		 }
		 
		 public TextDetails(int matchesCount, StringBuilder matches, StringBuilder unmatches) {
			 this.matchesCount = matchesCount;
			 this.matches = matches;
			 this.unmatches = unmatches;
		 }
		 
		 public StringBuilder matches() {
			 return this.matches;
		 }
		 
		 public StringBuilder unmatches() {
			 return this.unmatches;
		 }
		 
		 public int matchesCount() {
			 return this.matchesCount;
		 }
	 }
	 
	 public static enum AlignFrom {
		 
		 LEFT_SIDE,
		 BOTH_SIDES,
		 RIGHT_SIDE;
		 
		 public static AlignFrom matchOrDefault(String key, @NotNull AlignFrom defaultt) {
			 Objects.requireNonNull(defaultt, "defaultt cannot be null!");
			 return key == null ? BOTH_SIDES :
					key.equalsIgnoreCase("left") ? LEFT_SIDE : 
				    key.equalsIgnoreCase("both") ? BOTH_SIDES : 
				    key.equalsIgnoreCase("right") ? RIGHT_SIDE : defaultt;
		 }
		 
		 public static AlignFrom matchOrDefault(String key) {
			 return key == null ? BOTH_SIDES : 
				    key.equalsIgnoreCase("left") ? LEFT_SIDE : 
				    key.equalsIgnoreCase("both") ? BOTH_SIDES : 
				    key.equalsIgnoreCase("right") ? RIGHT_SIDE : BOTH_SIDES;
		 }
	 }
}
