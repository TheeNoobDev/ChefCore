package net.chefcraft.world.player;

public enum PrivateGameStatus {
	
	PENDING("pending"),
	READY("ready"),
	INGAME("ingame"),
	DENIED("denied");
	
	
	private final String stringValue;
	
	PrivateGameStatus(String stringValue) {
		this.stringValue = stringValue;
	}

	public String getStringValue() {
		return stringValue;
	}
	
	private static final PrivateGameStatus[] VALUES = PrivateGameStatus.values();
	
	public static PrivateGameStatus matchGameStatus(String value) {
		for (PrivateGameStatus status : VALUES) {
			if (status.getStringValue().equalsIgnoreCase(value)) {
				return status;
			}
		}
		return PrivateGameStatus.PENDING;
	}
}
