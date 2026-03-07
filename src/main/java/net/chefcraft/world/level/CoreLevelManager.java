package net.chefcraft.world.level;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.language.TranslatablePlayer;
import net.chefcraft.core.util.Placeholder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class CoreLevelManager extends AbstractLevelManager {
	
	private ChatColor progress;
	private ChatColor required;
	private String decoration;

	public CoreLevelManager(TranslatablePlayer player) {
		super(player);
		
		FileConfiguration config = ChefCore.getLevelManagerYamlFile().getConfig();
		
		try {
			this.progress = ChatColor.getByChar(config.getString("progress_color").replace("&", "").toCharArray()[0]);
			this.required = ChatColor.getByChar(config.getString("required_color").replace("&", "").toCharArray()[0]);
		} catch (Exception x) {
			this.progress = ChatColor.GREEN;
			this.required = ChatColor.GRAY;
			ChefCore.getInstance().sendPlainMessage("&cLevel Manager chatcolor parse error! Char is unknown check config", x);
		}
		
		String deco = config.getString("progress_bar_format");
		if (deco == null) {
			ChefCore.getInstance().sendPlainMessage("&cLevel Manager level bar progress format cannot be null! check config");
		} else {
			this.decoration = deco;
		}
	}

	@Override
	public float getLevelBarMultiplier() {
		return (float) ChefCore.getLevelManagerYamlFile().getConfig().getDouble("level_bar_multiplier");
	}

	@Override
	public void onLevelUp() {
		super.player.sendMessage("network.levelSystem.levelUp", Placeholder.of("{LEVEL}", this.getCurrentLevel()));
		super.player.playSound("levelUp");
	}

	@Override
	public ChatColor getProgressChatColor() {
		return this.progress;
	}

	@Override
	public ChatColor getRequiredChatColor() {
		return this.required;
	}

	@Override
	public String getLevelBarDecoration() {
		return this.decoration;
	}

}
