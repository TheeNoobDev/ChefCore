package net.chefcraft.core.network.packet;

import io.netty.buffer.ByteBuf;
import net.chefcraft.core.network.CorePacket;
import net.chefcraft.core.network.CorePacketType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Objects;

public class LobbyBoundHandshakePacket implements CorePacket {
	
	private String message;
	
	public LobbyBoundHandshakePacket(@Nonnull ByteBuf buffer) {
		Objects.requireNonNull(buffer, "buffer cannot be null!");
		this.read(buffer);
	}
	
	public LobbyBoundHandshakePacket(@Nonnull String message) {
		this.message = Objects.requireNonNull(message, "msg cannot be null!");
	}
	
	@Nonnull
	public String getMessage() {
		return message;
	}

	@Override
	public @NotNull CorePacketType type() {
		return CorePacketType.SERVER_HANDSHAKE;
	}
	
	@Override
	public String toString() {
		return this.type().toString() + " -> " + this.message;
	}

	@Override
	public void write(ByteBuf buffer) {
		PacketHandler.writeUTF(buffer, this.message);
	}

	@Override
	public void read(ByteBuf buffer) {
		this.message = PacketHandler.readUTF(buffer);
	}

}
