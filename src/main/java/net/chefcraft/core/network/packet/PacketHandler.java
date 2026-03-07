package net.chefcraft.core.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.CharsetUtil;
import net.chefcraft.core.ChefCore;
import net.chefcraft.core.collect.FunctionalEnums;
import net.chefcraft.core.network.CorePacket;
import net.chefcraft.core.network.CorePacketType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public class PacketHandler {

	@Nullable
	public static CorePacket handleIncomingPacket(@NotNull ByteBuf buffer) {
		int id = buffer.readInt();
		CorePacketType type = CorePacketType.getById(id);
		
		if (type == null) {
			ChefCore.log(Level.WARNING, "An error occurred handling incoming packet! Illegal packet id: " + id + ", skipping...");
			return null;
		}
		
		return type.handle(buffer);
	}
	
	public static void writeEnum(@NotNull ByteBuf buffer, @NotNull Enum<?> enumm) {
		buffer.writeInt(enumm.ordinal());
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> T readEnum(@NotNull ByteBuf buffer, Enum<T>[] enumm) {
		return (T) FunctionalEnums.getByOrdinal(buffer.readInt(), enumm);
	}
	
	public static synchronized void handleOutgoingPacket(@NotNull Channel channel, @NotNull CorePacket packet) {
		ByteBuf buf = Unpooled.buffer();
		buf.writeInt(packet.type().getId());
		packet.write(buf);
		channel.writeAndFlush(buf);
	}
	
	public static void writeUTF(@NotNull ByteBuf buffer, @NotNull String string) {
		buffer.writeInt(string.length());
		buffer.writeCharSequence(string, CharsetUtil.UTF_8);
	}
	
	public static String readUTF(@NotNull ByteBuf buffer) {
		int len = buffer.readInt();
		
		if (buffer.readableBytes() >= len) {
			return buffer.readCharSequence(len, CharsetUtil.UTF_8).toString();
		}
		
		throw new IndexOutOfBoundsException("The buffer capacity (" + buffer.readableBytes() + ") is greater than expected (" + len + ")!");
		
	}
}
