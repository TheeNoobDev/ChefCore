package net.chefcraft.world.databridge;

import com.google.gson.JsonObject;
import net.chefcraft.reflection.world.CoreMaterial;
import net.chefcraft.world.databridge.CoreArena.MenuItem.MenuItemImpl;
import net.chefcraft.world.translatable.TranslatableItemStack;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public interface CoreArena {

	String getNamespaceID();
	
	MenuItem getMenuItem();
	
	GameStatus getGameStatus();
	
	int getPlayersSize();
	
	int getMaxPlayersSize();
	
	int getPlayersPerTeam();
	
	boolean isPrivate();
	
	String getServerName();
	
	String getMapDisplayName();
	
	String getGameID();
	
	default boolean isSolo() {
		return this.getPlayersPerTeam() == 1;
	}
	
	default boolean isFull() {
		return this.getPlayersSize() == this.getMaxPlayersSize();
	}
	
	default boolean isInWaitingSlot() {
		return (this.getGameStatus() == GameStatus.WAITING || this.getGameStatus() == GameStatus.STARTING) && !this.isFull();
	}
	
	default boolean isJoineable() {
		return this.isInWaitingSlot() || this.getGameStatus() == GameStatus.PLAYING;
	}
	
	default JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("id", this.getNamespaceID());
		json.addProperty("status", this.getGameStatus().toString());
		json.addProperty("current", this.getPlayersSize());
		json.addProperty("max", this.getMaxPlayersSize());
		json.addProperty("perTeam", this.getPlayersPerTeam());
		json.addProperty("private", this.isPrivate());
		json.addProperty("server", this.getServerName());
		json.addProperty("displayName", this.getMapDisplayName());
		json.addProperty("gameId", this.getGameID());
		json.add("item", this.getMenuItem().toJson());
		return json;
	}
	
	static interface MenuItem {
		
		@Nonnull
		CoreMaterial getType();
		
		boolean hasDisplayEnchant();
		
		default TranslatableItemStack toItemStack() {
			return TranslatableItemStack.from(this.getType().toItemStack(), null, null).setEnchantmentGlint(this.hasDisplayEnchant());
		}
		
		default TranslatableItemStack toItemStack(String nameKey, String loreKey) {
			return TranslatableItemStack.from(this.getType().toItemStack(), nameKey, loreKey).setEnchantmentGlint(this.hasDisplayEnchant());
		}
		
		default String toStringData() {
			return this.getType().name() + ";" + this.hasDisplayEnchant();
		}
		
		default JsonObject toJson() {
			JsonObject json = new JsonObject();
			json.addProperty("type", this.getType().toString());
			json.addProperty("enchant", this.hasDisplayEnchant());
			return json;
		}
		
		static MenuItem fromJson(@Nonnull JsonObject jsonObject) {
			return new MenuItemImpl(jsonObject);
		}
		
		static MenuItem fromMaterial(@Nonnull CoreMaterial material, boolean displayEnchant) {
			return new MenuItemImpl(material, displayEnchant);
		}
		
		static MenuItem fromData(@Nonnull String[] data) {
			return new MenuItemImpl(data);
		}
		
		static class MenuItemImpl implements MenuItem {
			
			private final CoreMaterial material;
			private final boolean displayEnchant;
			
			private MenuItemImpl(@Nonnull JsonObject jsonObject) {
				Objects.requireNonNull(jsonObject, "jsonObject cannot be null!");
				
				this.material = CoreMaterial.getByName(jsonObject.get("type").getAsString());
				this.displayEnchant = jsonObject.get("enchant").getAsBoolean();
			}
			
			private MenuItemImpl(@Nonnull CoreMaterial material, boolean displayEnchant) {
				Objects.requireNonNull(material, "material cannot be null!");
				
				this.material = material;
				this.displayEnchant = displayEnchant;
			}
			
			private MenuItemImpl(@Nonnull String[] data) {
				Objects.requireNonNull(data, "data cannot be null!");
				if (data.length != 2) {
					throw new IllegalArgumentException("Data length must be equal 2! An example: 'NNMaterial<splitor>Boolean'");
				}
				
				this.material = CoreMaterial.getByName(data[0]);
				this.displayEnchant = Boolean.valueOf(data[1]);
			}

			@Override
			public CoreMaterial getType() {
				return material;
			}

			@Override
			public boolean hasDisplayEnchant() {
				return this.displayEnchant;
			}
		}
	}
	
	static CoreArena fromJson(@Nonnull JsonObject jsonObject) {
		return new CoreArenaImpl(jsonObject);
	}
	
	static CoreArena fromResultSet(@Nonnull ResultSet resultSet) throws SQLException {
		return new CoreArenaImpl(resultSet);
	}
	
	static class CoreArenaImpl implements CoreArena {
		
		private final String namespaceID;
		private final String serverName;
		private final String mapDisplayName;
		private final String gameID;
		private final MenuItem menuItem;
		private final GameStatus gameStatus;
		private final int playersSize;
		private final int maxPlayersSize;
		private final int playersPerTeam;
		private final boolean isPrivate;
		
		private CoreArenaImpl(@Nonnull JsonObject jsonObject) {
			Objects.requireNonNull(jsonObject, "jsonObject cannot be null!");
			
			this.namespaceID = jsonObject.get("id").getAsString();
			this.serverName = jsonObject.get("server").getAsString();
			this.mapDisplayName = jsonObject.get("displayName").getAsString();
			this.gameID = jsonObject.get("gameId").getAsString();
			this.menuItem = new MenuItemImpl(jsonObject.get("item").getAsJsonObject());
			this.gameStatus = GameStatus.getByName(jsonObject.get("status").getAsString());
			this.playersSize = jsonObject.get("current").getAsInt();
			this.maxPlayersSize = jsonObject.get("max").getAsInt();
			this.playersPerTeam = jsonObject.get("perTeam").getAsInt();
			this.isPrivate = jsonObject.get("private").getAsBoolean();
		}
		
		private CoreArenaImpl(@Nonnull ResultSet resultSet) throws SQLException {
			Objects.requireNonNull(resultSet, "result set cannot be null!");
			
			this.namespaceID = resultSet.getString(1);
			this.menuItem = new MenuItemImpl(resultSet.getString(2).split(";"));
			this.gameStatus = GameStatus.getByName(resultSet.getString(3));
			this.playersSize = resultSet.getInt(4);
			this.maxPlayersSize = resultSet.getInt(5);
			this.playersPerTeam = resultSet.getInt(6);
			this.isPrivate = resultSet.getBoolean(7);
			this.serverName = resultSet.getString(8);
			this.mapDisplayName = resultSet.getString(9);
			this.gameID = resultSet.getString(10);
		}

		@Override
		public String getNamespaceID() {
			return this.namespaceID;
		}

		@Override
		public MenuItem getMenuItem() {
			return this.menuItem;
		}

		@Override
		public GameStatus getGameStatus() {
			return this.gameStatus;
		}

		@Override
		public int getPlayersSize() {
			return this.playersSize;
		}

		@Override
		public int getMaxPlayersSize() {
			return this.maxPlayersSize;
		}

		@Override
		public int getPlayersPerTeam() {
			return this.playersPerTeam;
		}

		@Override
		public boolean isPrivate() {
			return this.isPrivate;
		}

		@Override
		public String getServerName() {
			return this.serverName;
		}

		@Override
		public String getMapDisplayName() {
			return this.mapDisplayName;
		}

		@Override
		public String getGameID() {
			return this.gameID;
		}
	}
}
