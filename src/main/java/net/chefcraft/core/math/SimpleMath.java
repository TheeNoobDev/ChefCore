package net.chefcraft.core.math;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.language.TranslationSource;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.text.DecimalFormat;
import java.util.Random;

public class SimpleMath {
	
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
	private static final DecimalFormat DECIMAL_FORMAT_2 = new DecimalFormat("0.0");
	
	public static void bounceEntity(Entity entity, double multiplier, double y) {
		Location pos = entity.getLocation();
		pos.setY(pos.getY() - 1.0D);
		entity.setVelocity(pos.getDirection().multiply(multiplier).setY(y));
	}

	public static Location projectileSpawnLocationFix(LivingEntity shooter, double offset) {
		Location shooterLoc = shooter.getLocation();
		return shooter.getEyeLocation().toVector().add(shooterLoc.getDirection().multiply(offset))
				.toLocation(shooterLoc.getWorld(), shooterLoc.getYaw(), shooterLoc.getPitch());
	}

	public static double percentToBossBarValue(double percentage) {
		double result = percentage / 100;
		if (percentage > 100 || result > 100) {
			return 1.0D;
		}
		if (percentage < 0 || result < 0) {
			return 0.0D;
		}
		return result;
	}

	public static double percentToBossBarValue(double value, int ticksPerSecond) {
		double result = (100.0D / value / ticksPerSecond) / 100.0D;
		if (result > 1.0D) {
			return 1.0D;
		}
		if (result < 0.0D) {
			return 0.0D;
		}
		return result;
	}
	
	public static double distanceWithoutWorld(Location from, Location to) {
		return distance(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ()); 
	}
	
	public static double distanceSquaredWithoutWorld(Location from, Location to) {
		return distanceSquared(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ()); 
	}
	
	public static double square(double value) {
		return value * value;
	}
	
	public static double percent(double value, double percent) {
		return (value * percent) / 100.0F;
	}
	
	public static double distance(double x, double y, double z, double dx, double dy, double dz) {
		return Math.sqrt(distanceSquared(x, y, z, dx, dy, dz)); 
	}
	
	public static double distanceSquared(double x, double y, double z, double dx, double dy, double dz) {
		return square(dx - x) + square(dy - y) + square(dz - z); 
	}
	
	public static String createRandomID(int length) {
		return A.a(length);
	}
	
	public static double roundToHalfNumber(double value) {
		return Math.round(value * 2) / 2.0D;
	}

	public static float roundToHalfNumber(float value) {
		return Math.round(value * 2) / 2.0F;
	}
	
	public static String calculateRatio(double value1, double value2) {
		if(value1 == 0 && value2 == 0) {
			return "0.00";
		} else if(value2 == 0) {
			return DECIMAL_FORMAT.format((double) value1).replace(',', '.');
		} else {
			return DECIMAL_FORMAT.format((double) (value1 / value2)).replace(',', '.');
		}
	}
	
	public static String formatNumber(double value) {
		return DECIMAL_FORMAT_2.format(value).replace(',', '.');
	}
	
	/**
	 * @returns H:MM:SS as String
	 */
	public static String secondToDigital(long seconds) {
		if (3600 <= seconds) {
			long h = seconds / 3600;
			long m = (seconds % 3600) / 60;
			long s = seconds % 60;
			
			return h + ":" + (m < 10 ? ("0" + m) : m) + ":" + (s < 10 ? ("0" + s) : s);
		} else {
			long m = (seconds % 3600) / 60;
			long s = seconds % 60;
			
			return m + ":" + (s < 10 ? ("0" + s) : s);
		}
	}
	
	/**
	 * @returns days, hours, minutes, seconds as String value
	 */
	public static String secondToText(long seconds, String[] timeVals) {
		final long[] time = new long[4];
		time[3] =  (seconds / 3600) / 24;
		time[2] = (seconds / 3600) % 24;
		time[1] = (seconds % 3600) / 60;
		time[0] = seconds % 60;
		return (time[3] == 0 ? "" : time[3] == 1 ? time[3] + timeVals[4] : time[3] + timeVals[8])
				+ (time[2] == 0 ? "" : time[2] == 1 ? time[2] + timeVals[3] : time[2] + timeVals[7])
				+ (time[1] == 0 ? "" : time[1] == 1 ? time[1] + timeVals[2] + (time[0] == 0 ? "" : timeVals[0]) : time[1] + timeVals[6] + (time[0] == 0 ? "" : timeVals[0]))
				+ (time[0] == 0 ? (seconds < 60 ? "0" : "") : time[0] == 1 ? time[0] + timeVals[1] : time[0] + timeVals[5]);
	}
	
	public static String secondToText(long sec0nds, String and, String second, String minute, String hour, String day, String seconds, String minutes, String hours, String days) {
		String[] vals = new String[9];
		vals[0] = and; vals[1] = second; vals[2] = minute; vals[3] = hour; vals[4] = day;
		vals[5] = seconds; vals[6] = minutes; vals[7] = hours; vals[8] = days;
		return secondToText(sec0nds, vals);
	}
	
	public static String getTranslatedTimeFormat(long seconds, TranslationSource source) {
		return secondToText(seconds, ChefCore.getTimeUnitValueByLanguage(source.getLanguage()));
	}
	
	public static String getTranslatedTickFormat(int ticks, TranslationSource source) {
		//1 s 5nds
		float val = ticks / 20;
		int flag = (val > 1) ? 5 : 1;
		return ((float)ticks / 20) + ChefCore.getTimeUnitValueByLanguage(source.getLanguage())[flag];
	}
	
	static class A {
		// A //
		static final char[] Aa = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
		static final byte[] aA = {1-1,1,1+1,1+1+1,1+1+1+1,1+1+1+1+1,1+1+1+1+1+1,1+1+1+1+1+1+1,1+1+1+1+1+1+1+1,1+1+1+1+1+1+1+1+1};
		static final Random aa = new Random();

		static String a(int a) {
			String AA = "";
			for (int i = aA[0]; a > i; i++) {
				if (aa.nextBoolean()) {
					AA = AA+Aa[aa.nextInt(Aa.length)];
				} else {
					AA = AA+aA[aa.nextInt(aA.length)];
				}
			}
			return AA;
		}
	}
}
