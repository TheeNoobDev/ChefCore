package net.chefcraft.core.network.game;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.chefcraft.core.event.CorePacketReadEvent;
import net.chefcraft.core.network.CorePacket;
import net.chefcraft.core.network.PacketFlow;
import net.chefcraft.core.network.packet.PacketHandler;
import org.bukkit.Bukkit;

public class GameServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
		CorePacket packet = PacketHandler.handleIncomingPacket(buffer);
		if (packet != null) {
			Bukkit.getPluginManager().callEvent(new CorePacketReadEvent(packet, PacketFlow.GAME));
		}
	}

}
