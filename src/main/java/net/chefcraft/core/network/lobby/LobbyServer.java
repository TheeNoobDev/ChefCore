package net.chefcraft.core.network.lobby;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.chefcraft.core.ChefCore;
import net.chefcraft.core.network.CorePacket;
import net.chefcraft.core.network.PacketFlow;
import net.chefcraft.core.network.PacketListener;
import net.chefcraft.core.network.packet.PacketHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class LobbyServer implements PacketListener {

	private final int port;
	private Channel channel;
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private Thread shutdownhook;
	
	public LobbyServer(int port) {
		this.port = port;
		
		this.executor.submit(() -> this.run());
	}
	
	public void run() {
		final EventLoopGroup bossGroup = new NioEventLoopGroup();
		final EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap bootstrap = new ServerBootstrap()
				.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new LobbyServerInitializer());
			
			this.channel = bootstrap.bind(port).sync().channel();
			this.channel.closeFuture().sync();
			
			if (shutdownhook != null) {
				Runtime.getRuntime().removeShutdownHook(shutdownhook);
			}
			
			shutdownhook = new Thread(() -> {
				bossGroup.shutdownGracefully();
				workerGroup.shutdownGracefully();
			});
			
			Runtime.getRuntime().addShutdownHook(shutdownhook);
		} catch (Exception x) {
			ChefCore.getInstance().getLogger()
			 .log(Level.WARNING, "Lobby Flow -> a connection error occurred: " + x.getMessage());
		}
	}
	
	public void disable() {
		if (shutdownhook != null) {
			this.shutdownhook.run();
		}
	}

	@Override
	public PacketFlow flow() {
		return PacketFlow.LOBBY;
	}

	@Override
	public void sendPacket(CorePacket packet) {
		executor.submit(() -> {
			
			synchronized (packet) {
				if (channel == null || !channel.isActive() || !channel.isOpen()) {
					this.run();
				} else {
					PacketHandler.handleOutgoingPacket(channel, packet);
				}
			}
			
		});
	}
}
