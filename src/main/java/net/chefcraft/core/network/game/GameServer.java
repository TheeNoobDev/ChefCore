package net.chefcraft.core.network.game;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.chefcraft.core.ChefCore;
import net.chefcraft.core.network.CorePacket;
import net.chefcraft.core.network.PacketFlow;
import net.chefcraft.core.network.PacketListener;
import net.chefcraft.core.network.packet.PacketHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class GameServer implements PacketListener {

	private final String host;
	private final int port;
	private Channel channel;
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private Thread shutdownhook;
	private int timeout;
	
	public GameServer(String host, int port) {
		this.host = host;
		this.port = port;
		
		executor.submit(() -> this.run());
	}
	
	public void run() {
		final EventLoopGroup group = new NioEventLoopGroup();
		
		try {
			Bootstrap bootstrap = new Bootstrap()
				.group(group)
				.channel(NioSocketChannel.class)
				.handler(new GameServerInitializer());
			
			this.channel = bootstrap.connect(host, port).sync().channel();
			
			if (shutdownhook != null) {
				Runtime.getRuntime().removeShutdownHook(shutdownhook);
			}
			
			shutdownhook = new Thread(() -> {
				group.shutdownGracefully();
			});
			
			Runtime.getRuntime().addShutdownHook(shutdownhook);
		} catch (Exception x) {
			ChefCore.getInstance().getLogger()
			 .log(Level.WARNING, "Game Flow -> a connection error occurred: " + x.getMessage());
		}
	}
	
	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public void disable() {
		if (shutdownhook != null) {
			this.shutdownhook.run();
		}
	}

	@Override
	public PacketFlow flow() {
		return PacketFlow.GAME;
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
