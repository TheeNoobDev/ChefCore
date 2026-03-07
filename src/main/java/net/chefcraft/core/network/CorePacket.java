package net.chefcraft.core.network;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public interface CorePacket {

	@NotNull
	CorePacketType type();
	
	void write(ByteBuf buff);
	
	void read(ByteBuf buff);
}
