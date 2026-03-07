package net.chefcraft.world.databridge;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.PluginInstance;
import net.chefcraft.core.database.AbstractDatabase;

public class DataBridgeDatabase extends AbstractDatabase {

	private static AbstractDatabase instance;
	
	@Override
	protected String getUsername() {
		return ChefCore.getInstance().getConfig().getString("games_data_bridge.username");
	}

	@Override
	protected String getPassword() {
		return ChefCore.getInstance().getConfig().getString("games_data_bridge.password");
	}
	
	@Override
	protected String getDriverUrl() {
		return ChefCore.getInstance().getConfig().getString("games_data_bridge.driver_url");
	}

	@Override
	protected PluginInstance getPlugin() {
		return ChefCore.getInstance();
	}
	
	public static AbstractDatabase getInstance() {
		return instance;
	}
	
	public static void loadDatabase() {
		instance = new DataBridgeDatabase();
	}

}
