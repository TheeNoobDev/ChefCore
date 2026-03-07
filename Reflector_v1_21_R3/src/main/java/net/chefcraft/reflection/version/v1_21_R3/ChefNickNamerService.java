package net.chefcraft.reflection.version.v1_21_R3.nicknamer;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.chefcraft.service.nick.NickNamerService;

public class ChefNickNamerService implements NickNamerService {

	//private static final String MOJANG_API_URL = "https://api.mojang.com/users/profiles/minecraft/%s";
	//private static final String MOJANG_SERVER_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";
	
	//private static final Pattern USERNAME_PATTERN = Pattern.compile("[a-zA-Z0-9_]{3,16}");
	
	public ChefNickNamerService(Player player) {

	}

	@Override
	public ActionStatus changeSkin(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActionStatus changeUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean changeBothAndUpdate(String username) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateProfile() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void restoreProfile() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void randomProfile() {
		// TODO Auto-generated method stub
		
	}
	

}
