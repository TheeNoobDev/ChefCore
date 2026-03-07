package net.chefcraft.reflection.base.utils;

import io.netty.channel.*;
import net.chefcraft.core.ChefCore;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class PacketInjector {
	
	private static final Map<UUID, List<String>> INJECTOR_TRACKER = new ConcurrentHashMap<>();
	
	public static boolean injectPlayer(Player player, String handlerName, ChannelDuplexHandler channelDuplexHandler) {
		ChannelPipeline pipeline = ChefCore.getReflector().getPlayerChannel(player).pipeline();
		
		List<String> handlerList;
		
		if (INJECTOR_TRACKER.containsKey(player.getUniqueId())) {
			handlerList = INJECTOR_TRACKER.get(player.getUniqueId());
		} else {
			handlerList = new ArrayList<>();
			INJECTOR_TRACKER.put(player.getUniqueId(), handlerList);
		}
		
		ChannelHandler channelHandler = pipeline.get(handlerName);
		
		if (channelHandler == null && !handlerList.contains(handlerName)) {
			handlerList.add(handlerName);
			pipeline.addBefore("packet_handler", handlerName, channelDuplexHandler);
			return true;
		}
		
		return false;
	}
	
	public static boolean removeInjection(Player player, String handlerName) {
		ChannelPipeline pipeline = ChefCore.getReflector().getPlayerChannel(player).pipeline();
		
		if (pipeline.get(handlerName) != null) {
			pipeline.remove(handlerName);
				
			List<String> handlerList = INJECTOR_TRACKER.get(player.getUniqueId());
			
			if (handlerList != null) {
				handlerList.removeIf(name -> name.equalsIgnoreCase(handlerName));
			}
			return true;
		}
		return false;
	}
	
	public static void removeAllInjections(Player player) {
		ChannelPipeline pipeline = ChefCore.getReflector().getPlayerChannel(player).pipeline();
		
		List<String> handlerList = INJECTOR_TRACKER.get(player.getUniqueId());
		
		if (handlerList == null) return;
		
		for (String handlerName : handlerList) {
			
			if (pipeline.get(handlerName) != null) {
				pipeline.remove(handlerName);
			}
		}
		
		handlerList.clear();
		
		INJECTOR_TRACKER.remove(player.getUniqueId());
	}
	
	public static ChannelDuplexHandler createInboundChannelHandler(Predicate<Object> messageFilter, Consumer<Object> action) {
		return new ChannelDuplexHandler() {
			
			@Override
            public void channelRead(ChannelHandlerContext context, Object message) throws Exception {
                if (messageFilter.test(message)) {
                	action.accept(message);
                }

                super.channelRead(context, message);
            }
		};
	}
	
	public static ChannelDuplexHandler createOutboundChannelHandler(Predicate<Object> messageFilter, Consumer<Object> action) {
		return new ChannelDuplexHandler() {
			
			@Override
            public void write(ChannelHandlerContext context, Object message, ChannelPromise promise) throws Exception {
                if (messageFilter.test(message)) {
                	action.accept(message);
                }

                super.write(context, message, promise);
            }
		};
	}
}
