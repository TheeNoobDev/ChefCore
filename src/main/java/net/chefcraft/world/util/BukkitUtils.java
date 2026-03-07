package net.chefcraft.world.util;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.util.JHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.util.StringUtil;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;

public class BukkitUtils {
	
	public static final Map<String, Material> MATERIAL_MAPPINGS = JHelper.createImmutableMapFromArray(false, Material.values());
	public static final CommandMap COMMAND_MAP;
	
	static {
		CommandMap map = null;
		try {
		    Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			bukkitCommandMap.setAccessible(true);
			map = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
		} catch (Exception x) {
			ChefCore.log(Level.SEVERE, "Command map registry error!", x);
		}
		
		if (map == null) {
			ChefCore.log(Level.SEVERE, "Command map registry error! Cause: command map null");
		}
		
		COMMAND_MAP = map;
	}
	
	public static String parseList(List<String> list, String spliterator) {
		StringBuilder builder = new StringBuilder();
		int tail = list.size() - 1;
		
		for (int i = 0; i < tail; i++) {
			builder.append(list.get(i));
			builder.append(spliterator);
		}
		
		builder.append(list.get(tail));
		
		return builder.toString();
	}
	
	public static String mergeArgs(String...args) {
		return mergeArgs(0, args);
	}
	
	public static String mergeArgs(int fromIndex, String...args) {
		StringBuilder builder = new StringBuilder();
		int tail = args.length - 1;
		
		for (int i = fromIndex; i < tail; i++) {
			builder.append(args[i]);
		}
		
		builder.append(args[tail]);
		
		return builder.toString();
	}
	
	public static List<String> sortTabCompleterResults(String arg, String... results) {
        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(arg, Arrays.asList(results), completions);
        Collections.sort(completions);
        return completions;
    }
	
	public static List<String> sortTabCompleterResults(String arg, Iterable<String> results) {
        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(arg, results, completions);
        Collections.sort(completions);
        return completions;
    }
	
	public static void registerCommand(String label, Command command) {
		try {
			COMMAND_MAP.register(label, command);
		} catch(Exception x) {
			ChefCore.getInstance().sendPlainMessage("&cAn error occured Registering the command: &e'" + label + ":" + command.getName() + "'", x);
		}
	}
	
	public static void unregisterCommand(String command) {
		try {
			COMMAND_MAP.getCommand(command).unregister(COMMAND_MAP);
		} catch(Exception x) {
			ChefCore.getInstance().sendPlainMessage("&cAn error occured Unregistering the command: &e'" + command + "'", x);
		}
    }
}
