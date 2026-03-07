package net.chefcraft.world.util;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.configuration.YamlFile;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.world.player.CorePlayer;
import org.bukkit.Bukkit;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;

public class ProxyUtils {
	
	private static final ChefCore PLUGIN = ChefCore.getInstance();
	private static final boolean BUNGEE_ENABLED;
	private static final String BUNGEE_CHANNEL = "BungeeCord";
	private static final String BUNGEE_CONNECT = "Connect";
	
	static {
		boolean flag = false;
		
		try {
			YamlFile yamlFile = YamlFile.create(Bukkit.getWorldContainer().getAbsolutePath() + File.separator + "spigot.yml");
			flag = yamlFile.getConfig().getBoolean("settings.bungeecord");
			
			if (!flag) {
				yamlFile = YamlFile.create(Bukkit.getWorldContainer().getAbsolutePath() + File.separator + "config" + File.separator + "paper-global.yml");
				flag = yamlFile.getConfig().getBoolean("proxies.velocity.enabled");
			}
		} catch (Exception x) {
			flag = false;
		}
		
		
		BUNGEE_ENABLED = flag;
	};

	public static boolean sendPlayerToServer(CorePlayer player, String server) {
		if (BUNGEE_ENABLED) {
			try {
				player.saveServerRequest();
				Bukkit.getScheduler().runTaskLater(PLUGIN, ()-> {
					try {
						final ByteArrayOutputStream byteArrOut = new ByteArrayOutputStream();
						final DataOutputStream out = new DataOutputStream(byteArrOut);
						out.writeUTF(BUNGEE_CONNECT);
			            out.writeUTF(server);
			            player.getPlayer().sendPluginMessage(PLUGIN, BUNGEE_CHANNEL, byteArrOut.toByteArray());
			            byteArrOut.close();
			            out.close();
					} catch (Exception x) {
						player.sendMessage("bungee.connectionFalied", Placeholder.of("{SERVER}", server));
					}
				}, 10L);
	            return true;
			} catch (Exception x) {
				player.sendMessage("bungee.connectionFalied", Placeholder.of("{SERVER}", server));
				return false;
			}
		}
		return false;
	}
	
	public static boolean isBungeeModeEnabled() {
		return BUNGEE_ENABLED;
	}
}
