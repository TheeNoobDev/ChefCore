package net.chefcraft.core.util;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.math.SimpleMath;
import net.chefcraft.world.player.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

/** @since 1.0*/
public class ServerTickClock {

	private static BukkitTask tickClock;
	private static long uptime = 0;
	private static DateTimeFormatter dateTimeFormatter;
	private static String shutdownClock;
	
	public static void start(boolean force) {
		FileConfiguration config = ChefCore.getInstance().getConfig();
		if (!force && !config.getBoolean("server_restarter.enabled")) return;
		
		dateTimeFormatter = parseFormatter(config);
		shutdownClock = config.getString("server_restarter.clock");
		
		final LocalDateTime schedule = LocalDateTime.parse(shutdownClock, dateTimeFormatter);
		final LocalDateTime now = LocalDateTime.now();
		
		tickClock = new BukkitRunnable() {
			public void run() {
				if (schedule.getHour() == now.getHour() && schedule.getSecond() == now.getSecond()) {
					this.cancel();
					doRestartWithWarnings();
				}
				uptime++;
			}
			
		}.runTaskTimer(ChefCore.getInstance(), 0L, 20L);
	}
	
	private static DateTimeFormatter parseFormatter(FileConfiguration config) {
		try {
			return DateTimeFormatter.ofPattern(config.getString("server_restarter.clock_format"));
		} catch (Exception x) {
			x.printStackTrace();
			Bukkit.getLogger().log(Level.SEVERE, "The time format was set to 24 hours because an error occurred while loading the time format!");
			return DateTimeFormatter.ofPattern("HH:mm");
		}
	}
	
	public static void doRestartWithWarnings() {
		new BukkitRunnable() {
			
			private int time = 900; 
			
			public void run() {
				switch (time) {
				case 900:
				case 600:
				case 300:
				case 240:
				case 180:
				case 120:
				case 60:
				case 30:
				case 15:
				case 5:
				case 4:
				case 3:
				case 2:
				case 1:
					
					for (CorePlayer corePlayer : ChefCore.getCorePlayers()) {
						corePlayer.sendMessage("serverClock.restarting", Placeholder.of("{TIME}", SimpleMath.getTranslatedTimeFormat(time, corePlayer)));
					}
					Bukkit.getLogger().log(Level.WARNING, "ServerClock -> Server restarting in " + time + " second(s)!");
					break;
				case 0:
					this.cancel();
					Bukkit.getLogger().log(Level.SEVERE, "ServerClock -> Shutting down see you later!");
					Bukkit.getServer().shutdown();
					break;
				}
				time--;
			}
			
		}.runTaskTimer(ChefCore.getInstance(), 0L, 20L);
	}
	
	public static long getUptime() {
		return uptime;
	}
	
	public static void end() {
		if (tickClock != null) {
			tickClock.cancel();
		}
	}
}
