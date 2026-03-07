package net.chefcraft.core.network;

import net.chefcraft.core.network.game.GameServer;
import net.chefcraft.core.network.lobby.LobbyServer;
import org.jetbrains.annotations.NotNull;

public final class ConnectionManager implements PacketListener {

	private final PacketFlow flow;
	private final String host;
	private final int port;
	private LobbyServer lobby = null;
	private GameServer game = null;
	
	public ConnectionManager(@NotNull PacketFlow flow, @NotNull String host, int port) {
		this.flow = flow;
		this.host = host;
		this.port = port;
	}

	@Override
	public PacketFlow flow() {
		return flow;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
	
	public void disconnect() {
		if (this.lobby != null) {
			this.lobby.disable();
			this.lobby = null;
		}
		
		if (this.game != null) {
			this.game.disable();
			this.game = null;
		}
	}
	
	public void connect() {
		this.disconnect();
		
		if (this.flow == PacketFlow.LOBBY) {
			this.lobby = new LobbyServer(this.port);
		} else {
			this.game = new GameServer(this.host, this.port);
		}
	}

	@Override
	public void sendPacket(CorePacket packet) {
		if (this.lobby != null) {
			this.lobby.sendPacket(packet);
		} else if (this.game != null) {
			this.game.sendPacket(packet);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
