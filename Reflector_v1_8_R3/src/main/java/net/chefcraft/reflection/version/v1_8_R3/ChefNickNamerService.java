package net.chefcraft.reflection.version.v1_8_R3.nicknamer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.chefcraft.core.util.Pair;
import net.chefcraft.service.nick.GameProfilePool;
import net.chefcraft.service.nick.NickNamerService;
import net.chefcraft.service.npc.LivingEntityNPC;
import net.minecraft.server.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChefNickNamerService implements NickNamerService {

	private static final String MOJANG_API_URL = "https://api.mojang.com/users/profiles/minecraft/%s";
	private static final String MOJANG_SERVER_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";
	
	private static final Pattern USERNAME_PATTERN = Pattern.compile("[a-zA-Z0-9_]{3,16}");
	
	private final EntityPlayer player;
	private final CraftPlayer craftPlayer;
	
	private final Property originalProperty;
	private final String originalName;
	
	public ChefNickNamerService(Player bukkitPlayer) {
		CraftPlayer craftPlayer = ((CraftPlayer) bukkitPlayer);
		this.craftPlayer = craftPlayer;
		this.player = craftPlayer.getHandle();
		this.originalName = craftPlayer.getName();
		
		Property perty = null;
		for (Property property : craftPlayer.getProfile().getProperties().get("textures")) {
			perty = property;
			break;
		}
		this.originalProperty = perty;
	}
	
	@Override
    public ActionStatus changeSkin(String username) {
		ActionStatus status = this.prepareUserProperty(username);
		PropertyMap pm = craftPlayer.getProfile().getProperties();
		if (status == ActionStatus.EXECUTED) {
			try {
				CompletableFuture<Property> future = CompletableFuture.supplyAsync(() -> getTexturePropertyFromMojang(username));
				pm.put("textures", future.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		} else {
			Pair<String, String> propertyPair = GameProfilePool.getRandomTextureProprety();
			pm.put("textures", new Property("textures", propertyPair.getFirst(), propertyPair.getSecond()));
		}
        return status;
    }
	
	private ActionStatus prepareUserProperty(String username) {
		ActionStatus status = this.checkUsernameFormat(username);
		if (status == ActionStatus.EXECUTED) {
			PropertyMap pm = craftPlayer.getProfile().getProperties();
			for (Property pp : List.copyOf(pm.get("textures"))) {
				pm.remove("textures", pp);
			}
		}
		return status;
	}
 
    @Override
    public ActionStatus changeUsername(String username) {
    	ActionStatus status = this.checkUsernameFormat(username);
        try {
    		if (status == ActionStatus.EXECUTED) {
    			CompletableFuture<Property> future = CompletableFuture.supplyAsync(() -> getTexturePropertyFromMojang(username));
    			if (future.get().getValue().isEmpty()) {
    				Field profileField = craftPlayer.getProfile().getClass().getDeclaredField("name");
                    profileField.setAccessible(true);
                    profileField.set(craftPlayer.getProfile(), username);
    			} else {
    				return ActionStatus.USERNAME_CONFLICT;
    			}
    		}
        } catch (Exception x) {
        	logException("An error occurred setting name! (Username: " + this.originalName + ", Input: " + username + ")", x);
        	status = ActionStatus.INVALID_USERNAME;
        }
        return status;
    }
    
    @Override
	public boolean changeBothAndUpdate(String username) {
    	boolean flag = !this.changeUsername(username).isError();
    	if (flag) {
    		this.changeSkin(username);
        	this.updateProfile();
    	}
    	return flag;
	}
 
    @Override
    public void updateProfile() {
    	
        PacketPlayOutPlayerInfo removeInfo = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, this.player);
        PacketPlayOutEntityDestroy destroyEntity = new PacketPlayOutEntityDestroy(player.getId());

        PacketPlayOutNamedEntitySpawn addNamed = new PacketPlayOutNamedEntitySpawn(this.player);
        PacketPlayOutPlayerInfo addInfo = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, this.player);


        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        
        for (Player online : onlinePlayers) {
            if (online.getUniqueId().equals(player.getUniqueID())) continue;

        	PlayerConnection connection = ((CraftPlayer) online).getHandle().playerConnection;
        	connection.sendPacket(removeInfo);	
        	connection.sendPacket(destroyEntity);
            connection.sendPacket(addInfo);
            connection.sendPacket(addNamed);
        }

        player.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, this.player));
        player.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, this.player));
        player.playerConnection.sendPacket(new PacketPlayOutRespawn(this.player.dimension,
                this.player.getWorld().getDifficulty(), this.player.getWorld().getWorldData().getType(), this.player.playerInteractManager.getGameMode()));


    }
    
    @Override
    public void restoreProfile() {
    	try {
    		
    		Field profileField = craftPlayer.getProfile().getClass().getDeclaredField("name");
            profileField.setAccessible(true);
            profileField.set(craftPlayer.getProfile(), this.originalName);
    		
    		this.prepareUserProperty(this.originalName);
    		craftPlayer.getProfile().getProperties().put("textures", this.originalProperty);
        	
    		this.updateProfile();
    	} catch (Exception x) {
    		this.player.playerConnection.disconnect(
    				logException("An error occurred restoring your profile! (Username: " + this.originalName + ")", x));
    	}
    }
    
    @Override
    public void randomProfile() {
    	
    	String name = GameProfilePool.getRandomUsername();
    	this.changeUsername(name);
    	
    	this.prepareUserProperty(name);
		Pair<String, String> propertyPair = GameProfilePool.getRandomTextureProprety();
		craftPlayer.getProfile().getProperties().put("textures", new Property("textures", propertyPair.getFirst(), propertyPair.getSecond()));
		this.updateProfile();
		
    }
    
    private ActionStatus checkUsernameFormat(String input) {
    	if (input.length() > 16 || input.length() < 3) {
    		return ActionStatus.USERNAME_OUT_OF_BOUNDS;
    	}
    	Matcher matcher = USERNAME_PATTERN.matcher(input);
    	if (!matcher.matches()) {
    		return ActionStatus.INVALID_USERNAME;
    	}
    	return ActionStatus.EXECUTED;
    }
    
    public static Property getTexturePropertyFromMojang(String username) {
        try {
        	URL urlAPI = new URI(String.format(MOJANG_API_URL, username)).toURL();
        	HttpURLConnection con = openConnection(urlAPI);
	        
            InputStreamReader input = new InputStreamReader(con.getInputStream());
            JsonObject jsonObj = new JsonParser().parse(input).getAsJsonObject();
            if (!jsonObj.has("id")) {
            	input.close();
            	con.disconnect();
            	return new Property("textures", "", ""); 
            }
            
            String uuid = jsonObj.get("id").getAsString();
            
            URL urlServer = new URI(String.format(MOJANG_SERVER_URL, uuid)).toURL();
            HttpURLConnection conServer = openConnection(urlServer);
            
            InputStreamReader inputFromServer = new InputStreamReader(conServer.getInputStream());
            JsonObject server = new JsonParser().parse(inputFromServer).getAsJsonObject();
            if (!server.has("properties")) {
            	con.disconnect();
            	conServer.disconnect();
            	input.close();
            	inputFromServer.close();
            	return new Property("textures", "", ""); 
            }
            JsonObject textureProperty = server.get("properties").getAsJsonArray().get(0).getAsJsonObject();;
            String texture = textureProperty.get("value").getAsString();
            String signature = textureProperty.get("signature").getAsString();
            
            con.disconnect();
            conServer.disconnect();
            input.close();
            inputFromServer.close();
            
            return new Property("textures", texture, signature);
        } catch (IOException | URISyntaxException x) {
        	if (x.getCause() != null) {
        		logException("Could not get data from mojang's session servers! (For: " + username + ")", x);
        	}
            return new Property("textures", "", "");
        }
    }
    
    private static String logException(String message, Exception x) {
    	if (x != null) {
    		message = message + " Exception: " + x.getMessage() + " | " + x.getCause();
    	}
    	System.err.println(message);
    	return message;
    }
    
    private static HttpURLConnection openConnection(URL url) throws IOException {
    	HttpURLConnection con = (HttpURLConnection) url.openConnection();
    	con.setRequestMethod("GET");
    	con.setRequestProperty("Content-Type", "application/json");
    	return con;
    }
}
