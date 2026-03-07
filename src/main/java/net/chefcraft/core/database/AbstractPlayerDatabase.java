package net.chefcraft.core.database;

import net.chefcraft.core.PluginInstance;

import java.sql.Connection;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractPlayerDatabase {

	protected abstract String getUsername();
	
	protected abstract String getPassword();
	
	protected abstract String getTableName();
	
	protected abstract String getDriverUrl();
	
	protected abstract PluginInstance getPlugin();
	
	private AbstractDatabase database;
	
	protected AbstractPlayerDatabase() {
		this.database = new AbstractDatabase() {

			@Override
			protected String getUsername() {
				return AbstractPlayerDatabase.this.getUsername();
			}

			@Override
			protected String getPassword() {
				return AbstractPlayerDatabase.this.getPassword();
			}

			@Override
			protected String getDriverUrl() {
				return AbstractPlayerDatabase.this.getDriverUrl();
			}

			@Override
			protected PluginInstance getPlugin() {
				return AbstractPlayerDatabase.this.getPlugin();
			}
			
		};
	}
	
	protected Connection getConnection() {
		return this.database.getConnection();
	}
	
	protected void setConnection(Connection connection) {
		this.database.setConnection(connection);
	}
	
	public boolean isConnected() {
		return this.database.isConnected();
	}
	
	public boolean disconnect() {
		return this.database.disconnect();
	}
	
	public void createTable(String tableValues) {
	    this.database.createTable(tableValues, this.getTableName());
	}
	
	public void createPlayer(UUID uuid, Map<String, Object> datas) {
		this.database.createEntry(uuid.toString(), "UUID", datas, this.getTableName());
	}
	
	public boolean exists(UUID uuid) {
		return this.database.exists(uuid.toString(), "UUID", this.getTableName());
	}
	
	public <T> T getData(String type, UUID uuid) {
		return this.database.getData(type, uuid.toString(), "UUID", this.getTableName());
	}
	
	public <T> void setData(String type, UUID uuid, T object) {
		this.database.setData(type, uuid.toString(), "UUID", this.getTableName(), object);
	}
}
