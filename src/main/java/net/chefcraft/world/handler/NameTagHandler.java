package net.chefcraft.world.handler;

import net.chefcraft.core.ChefCore;
import net.chefcraft.service.tag.NameTagFormat;
import net.chefcraft.world.player.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NameTagHandler {
	
	public static boolean isNameTagFormatsEnabled() {
		return ChefCore.getInstance().getConfig().getBoolean("name_tag_service.enabled");
	}

	public static void sendPlayerNameTag(CorePlayer corePlayer) {
		ChefCore plugin = ChefCore.getInstance();
		
		if (plugin.getConfig().getBoolean("name_tag_service.enabled")) {
			
			int delay = plugin.getConfig().getInt("name_tag_service.display_delay");
			
			if (delay <= 0) {
				corePlayer.displayTag(false);
			} else {
				Bukkit.getScheduler().runTaskLater(plugin, ()-> {
					corePlayer.displayTag(false);
				}, delay);
			}
		}
	}
	
	public static void removePlayerNameTag(CorePlayer corePlayer) {
		if (isNameTagFormatsEnabled()) {
			corePlayer.getNameTagService().remove();
		}
	}
	
	/**
	 * 
	 * @param formatType format type
	 * @param corePlayer core player
	 * @returns If the player's name is in the group, it returns it, if not, it looks for the player's
	 *   luckperms group and if that is not found, it returns the default format, if it cannot find it either,
	 *   it returns an empty string or the number 99 (if you have selected the "name tag format tab height").
	 */
	@Nonnull
	public static Object findFormatFromConfigByType(NameTagFormat formatType, CorePlayer corePlayer) {
		 FileConfiguration config = ChefCore.getNameTagsYamlFile().getConfig();
		 
		 String path = "formats." + corePlayer.getPlayer().getName() + "." + formatType.getConfigPath();
		 
		 if (config.isSet(path)) {
			 return config.get(path);
		 }
		 
		 path = "formats." + corePlayer.getPlayerMetaDataStorage().getGroupName() + "." + formatType.getConfigPath();
		 
		 if (config.isSet(path)) {
			 return config.get(path);
		 }
		 
		 path = "formats.default." + formatType.getConfigPath();
		 
		 if (config.isSet(path)) {
			 return config.get(path);
		 }
		 
		 return formatType == NameTagFormat.TAB_HEIGHT ? 99 : "";
	}
	
	@Nullable
	public static Object getFormatFromConfigByType(NameTagFormat formatType, String format) {
		return ChefCore.getNameTagsYamlFile().getConfig().get("formats." + format + "." + formatType.getConfigPath());
	}
}
