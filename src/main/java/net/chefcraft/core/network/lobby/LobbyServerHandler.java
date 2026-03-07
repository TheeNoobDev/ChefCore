package net.chefcraft.core.network.lobby;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import net.chefcraft.core.event.CorePacketReadEvent;
import net.chefcraft.core.network.CorePacket;
import net.chefcraft.core.network.PacketFlow;
import net.chefcraft.core.network.packet.LobbyBoundHandshakePacket;
import net.chefcraft.core.network.packet.PacketHandler;
import org.bukkit.Bukkit;

public class LobbyServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

	private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		 Channel incoming = ctx.channel();
		 
		 for (Channel channel : channels) {
			 channel.write(new LobbyBoundHandshakePacket("[SERVER] +++ " + incoming.remoteAddress() + " has connected!"));
		 }
		 
		 channels.add(ctx.channel());
	}
	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();
		 
		 for (Channel channel : channels) {
			 channel.write(new LobbyBoundHandshakePacket("[SERVER] --- " + incoming.remoteAddress() + " has disconnected!"));
		 }
		 
		 channels.remove(ctx.channel());
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
		Channel incoming = ctx.channel();
		
		for (Channel channel : channels) {
			if (channel != incoming) {
				 
			}
		}
		
		CorePacket packet = PacketHandler.handleIncomingPacket(buffer);
		if (packet != null) {
			Bukkit.getPluginManager().callEvent(new CorePacketReadEvent(packet, PacketFlow.LOBBY));
		}
	}
	
}
