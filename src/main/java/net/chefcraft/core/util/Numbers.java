package net.chefcraft.core.util;

/** @since 1.2*/
public class Numbers {
	
	//min range
	public static byte min(byte num, byte min) {
		if (num >= min) { return num; }
		throw illegalArgumentException(num, min, true);
	}
	
	public static short min(short num, short min) {
		if (num >= min) { return num; }
		throw illegalArgumentException(num, min, true);
	}
	
	public static int min(int num, int min) {
		if (num >= min) { return num; }
		throw illegalArgumentException(num, min, true);
	}
	
	public static long min(long num, long min) {
		if (num >= min) { return num; }
		throw illegalArgumentException(num, min, true);
	}
	
	public static float min(float num, float min) {
		if (num >= min) { return num; }
		throw illegalArgumentException(num, min, true);
	}
	
	public static double min(double num, double min) {
		if (num >= min) { return num; }
		throw illegalArgumentException(num, min, true);
	}
	
	//max range
	public static byte max(byte num, byte max) {
		if (num <= max) { return num; }
		throw illegalArgumentException(num, max, false);
	}
	
	public static short max(short num, short max) {
		if (num <= max) { return num; }
		throw illegalArgumentException(num, max, false);
	}
	
	public static int max(int num, int max) {
		if (num <= max) { return num; }
		throw illegalArgumentException(num, max, false);
	}
	
	public static long max(long num, long max) {
		if (num <= max) { return num; }
		throw illegalArgumentException(num, max, false);
	}
	
	public static float max(float num, float max) {
		if (num <= max) { return num; }
		throw illegalArgumentException(num, max, false);
	}
	
	public static double max(double num, double max) {
		if (num <= max) { return num; }
		throw illegalArgumentException(num, max, false);
	}
	
	//Check range
	public static byte checkRange(byte num, byte min, byte max) {
		if (num >= min && max >= num) { return num; }
		throw illegalArgumentException(num, min, max);
	}
	
	public static short checkRange(short num, short min, short max) {
		if (num >= min && max >= num) { return num; }
		throw illegalArgumentException(num, min, max);
	}
	
	public static int checkRange(int num, int min, int max) {
		if (num >= min && max >= num) { return num; }
		throw illegalArgumentException(num, min, max);
	}
	
	public static long checkRange(long num, long min, long max) {
		if (num >= min && max >= num) { return num; }
		throw illegalArgumentException(num, min, max);
	}
	
	public static float checkRange(float num, float min, float max) {
		if (num >= min && max >= num) { return num; }
		throw illegalArgumentException(num, min, max);
	}
	
	public static double checkRange(double num, double min, double max) {
		if (num >= min && max >= num) { return num; }
		throw illegalArgumentException(num, min, max);
	}
	
	//orElse
	public static byte checkRangeOrElse(byte num, byte min, byte max, byte defaultValue) { return (num >= min && max >= num) ? num : defaultValue; }
	
	public static short checkRangeOrElse(short num, short min, short max, short defaultValue) { return (num >= min && max >= num) ? num : defaultValue; }
	
	public static int checkRangeOrElse(int num, int min, int max, int defaultValue) { return (num >= min && max >= num) ? num : defaultValue; }
	
	public static long checkRangeOrElse(long num, long min, long max, long defaultValue) { return (num >= min && max >= num) ? num : defaultValue; }
	
	public static float checkRangeOrElse(float num, float min, float max, float defaultValue) { return (num >= min && max >= num) ? num : defaultValue; }
	
	public static double checkRangeOrElse(double num, double min, double max, double defaultValue) { return (num >= min && max >= num) ? num : defaultValue; }
	
	
	//orElseMin
	public static byte checkRangeOrElseMin(byte num, byte min, byte max) { return checkRangeOrElse(num, min, max, min); }
	
	public static short checkRangeOrElseMin(short num, short min, short max) { return checkRangeOrElse(num, min, max, min); }
	
	public static int checkRangeOrElseMin(int num, int min, int max) { return checkRangeOrElse(num, min, max, min); }
	
	public static long checkRangeOrElseMin(long num, long min, long max) { return checkRangeOrElse(num, min, max, min); }
	
	public static float checkRangeOrElseMin(float num, float min, float max) { return checkRangeOrElse(num, min, max, min); }
	
	public static double checkRangeOrElseMin(double num, double min, double max) { return checkRangeOrElse(num, min, max, min); }
	
	
	//orElseMax
	public static byte checkRangeOrElseMax(byte num, byte min, byte max) { return checkRangeOrElse(num, min, max, max); }
	
	public static short checkRangeOrElseMax(short num, short min, short max) { return checkRangeOrElse(num, min, max, max); }
	
	public static int checkRangeOrElseMax(int num, int min, int max) { return checkRangeOrElse(num, min, max, max); }
	
	public static long checkRangeOrElseMax(long num, long min, long max) { return checkRangeOrElse(num, min, max, max); }
	
	public static float checkRangeOrElseMax(float num, float min, float max) { return checkRangeOrElse(num, min, max, max); }
	
	public static double checkRangeOrElseMax(double num, double min, double max) { return checkRangeOrElse(num, min, max, max); }
	
	static IllegalArgumentException illegalArgumentException(Number num, Number min, Number max) {
		return new IllegalArgumentException("the number " + num + " must be between or equal to " + min + " and " + max);
	}
	
	static IllegalArgumentException illegalArgumentException(Number num, Number bound, boolean min) {
		return new IllegalArgumentException("the number " + num + (min ? " can be at least " : " can be maximum ") + bound);
	}
}
