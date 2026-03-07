package net.chefcraft.service.nick;

/**
 * @since 1.1.5
 */
public interface NickNamerService {

	ActionStatus changeSkin(String username);
	
	ActionStatus changeUsername(String username);
	
	boolean changeBothAndUpdate(String username);
	
	void updateProfile();
	
	void restoreProfile();
	
	void randomProfile();
	
	public static enum ActionStatus {
		
		INVALID_USERNAME(true),
		USERNAME_OUT_OF_BOUNDS(true),
		USERNAME_CONFLICT(true),
		EXECUTED(false);
		
		final boolean error;
		
		ActionStatus(boolean error) {
			this.error = error;
		}
		
		public boolean isError() {
			return this.error;
		}
	}
}
