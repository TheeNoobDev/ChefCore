package net.chefcraft.core.database;

import net.chefcraft.core.PlatformProvider;
import net.chefcraft.core.PluginInstance;

import java.io.File;
import java.sql.*;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractDatabase {
	
	private static int db_ids = 1;
	
	protected abstract String getUsername();
	
	protected abstract String getPassword();
	
	protected abstract String getDriverUrl();
	
	protected abstract PluginInstance getPlugin();
	
	private ScheduledExecutorService connectionAliveTask = Executors.newScheduledThreadPool(1);
	private Connection connection = null;
	private final int id;
	
	protected AbstractDatabase() {
		this.id = db_ids++;
		this.getPlugin().sendPlainMessage("<white>Database <green>" + this.id + "<white> initializing...");
		this.initConnection(false);
		
		this.connectionAliveTask.scheduleAtFixedRate(() -> {
			if (AbstractDatabase.this.isConnected()) {
				try {
					PreparedStatement ps = AbstractDatabase.this.getConnection().prepareStatement("SELECT 1");
					ResultSet results = ps.executeQuery();
					
					AbstractDatabase.this.close(ps, results);
					
				} catch (SQLException e) {
					e.printStackTrace();
					AbstractDatabase.this.getPlugin().sendPlainMessage("<red>Connection alive test FAILED! (DB_ID: " + id + ")");
				}
			}
		}, 10L, 3600L, TimeUnit.SECONDS);
	}
	
	protected void initConnection(boolean reconnect) {
		this.initConnection(reconnect, this.getDriverUrl().contains("sqlite"));
	}
	
	protected void initConnection(boolean reconnect, boolean sqlite) {
		this.driverRegistry();
		try {
			if (sqlite) {
				String url = this.getDriverUrl().replace("{FILE_DIRECTORY}", this.getPlugin().getDataFolder().getAbsolutePath() + File.separator + this.getPlugin().getName());
				
				this.setConnection(DriverManager.getConnection(url));
			} else {
				this.setConnection(DriverManager.getConnection(this.getDriverUrl(), this.getUsername(), this.getPassword()));
			}
			if (reconnect) {
				this.getPlugin().sendPlainMessage("<white>Database <green>" + this.id + "<white> successfully reconnected! <gray>[DB Type: <green>"+ (sqlite ? "SQLite" : "MySQL") + "<gray>]");
			} else {
				this.getPlugin().sendPlainMessage("<white>Database <green>" + this.id + "<white> successfully loaded! <gray>[DB Type: <green>"+ (sqlite ? "SQLite" : "MySQL") + "<gray>]");
			}
		} catch (Exception e) {
			this.getPlugin().sendPlainMessage("<red>Database " + this.id + " couldn't load!", e);
			
			this.getPlugin().sendPlainMessage("<yellow>Trying to load local database...");
			
			try {
				//Class.forName("org.sqlite.JDBC");
				
				String url = "jdbc:sqlite:{FILE_DIRECTORY}.db";
				url = url.replace("{FILE_DIRECTORY}", this.getPlugin().getDataFolder().getAbsolutePath() + File.separator + this.getPlugin().getName());
				
				this.setConnection(DriverManager.getConnection(url));
				
				this.getPlugin().sendPlainMessage("<white>Local database successfully loaded! <gray>[DB Type: <green>SQLite" + "<gray>] [DB_ID: " + this.id + "]");
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	// for velocity load from external source
	private void driverRegistry() {
		if (!PlatformProvider.usingVelocity()) {
			try {
				Class.forName("org.sqlite.JDBC");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isConnected() {
		return this.connection != null;
	}
	
	public Connection getConnection() {
		return this.connection;
	}
	
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	public boolean disconnect() {
		try {
			if (this.connectionAliveTask != null) {
				this.connectionAliveTask.shutdown();
			}
			if (this.isConnected()) {
				this.connection.close();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void createTable(String tableValues, String tableName) {
		try {
			Statement statement = this.connection.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + tableName + "` " + tableValues);
	    	this.close(statement);
		} catch (SQLException e) {
			e.printStackTrace();
			this.getPlugin().sendPlainMessage("<red>An error occured database table creating!", e);
		}
	}
	
	public boolean exists(String key, String where, String tablename) {
		try {
			boolean flag = false;
			PreparedStatement ps = this.connection.prepareStatement("SELECT * FROM " + tablename + " WHERE "+ where +"=?");
			ps.setString(1, key);
			ResultSet results = ps.executeQuery();
			if (results.next()) {
				flag = true;
			}

			this.close(ps, results);
			
			return flag;
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void createEntry(String key, String where, Map<String, Object> datas, String tableName) {
		try {
			if (!this.exists(key, where, tableName)) {
				String values = "";
				String qMarks = "";
				for (String dataType : datas.keySet()) {
					values = values + dataType + ",";
					qMarks = qMarks + "?,";
				}
				values = values.substring(0, values.length() - 1);
				qMarks = qMarks.substring(0, qMarks.length() - 1);
				String state = "INSERT INTO " + tableName + " (" + values + ") VALUES (" + qMarks + ")";
				PreparedStatement ps = this.connection.prepareStatement(state);
				int i = 1;
				for (Object data : datas.values()) {
					ps.setObject(i, data);
					i++;
				}
				ps.executeUpdate();
				this.close(ps);
				datas = null;
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteData(String key, String where, String tableName) {
		try {
			PreparedStatement ps = this.connection.prepareStatement("DELETE FROM " + tableName + " WHERE " + where + "=?");
			ps.setString(1, key);
			ps.executeUpdate();

			this.close(ps);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getData(String type, String key, String where, String tableName) {
		try {
			T data = null;
			PreparedStatement ps = this.connection.prepareStatement("SELECT " + type + " FROM " + tableName + " WHERE " + where + "=?");
			ps.setString(1, key);
			ResultSet results = ps.executeQuery();
			if (results.next()) {
				data = (T) results.getObject(type);
			}
			this.close(ps, results);
			return data;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public <T> void setData(String type, String key, String where, String tableName, T object) {
		try {
			PreparedStatement ps = this.connection.prepareStatement("UPDATE " + tableName + " SET " + type + "=? WHERE " + where + "=?");
			ps.setObject(1, object);
			ps.setString(2, key);
			ps.executeUpdate();
			this.close(ps);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void close(PreparedStatement statement, ResultSet resultSet) {
		try {
			if (statement != null) {
				statement.close();
			}
			
			if (resultSet != null) {
				resultSet.close();
			}
		} catch (SQLException x) {
			x.printStackTrace();
		}
	}
	
	private void close(Statement statement) {
		try {
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException x) {
			x.printStackTrace();
		}
	}

	public int getID() {
		return id;
	}
}
