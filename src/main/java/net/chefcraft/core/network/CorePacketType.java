package net.chefcraft.core.network;

import io.netty.buffer.ByteBuf;
import net.chefcraft.core.network.packet.LobbyBoundHandshakePacket;
import net.chefcraft.core.network.packet.LobbyBoundMinigameArenaDataPacket;
import net.chefcraft.core.registry.CoreRegistry;
import net.chefcraft.core.util.Numbers;
import net.chefcraft.core.util.ObjectKey;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;

public class CorePacketType implements ObjectKey {
	
	public static final CorePacketType MINIGAME_ARENA_DATA = create(0, PacketFlow.LOBBY, "minigame_arena_data", LobbyBoundMinigameArenaDataPacket::new);
	public static final CorePacketType SERVER_HANDSHAKE = create(1, PacketFlow.LOBBY, "server_handshake", LobbyBoundHandshakePacket::new);

	private static final Function<CorePacketType, Integer> GET_PACKET_ID = CorePacketType::getId;
	private static final CoreRegistry<CorePacketType> REGISTRY = new CoreRegistry<>(CorePacketType.class);
	
	private final int id;
	private final PacketFlow flow;
	private final String name;
	private final Function<ByteBuf, ? extends CorePacket> handler;
	
	private CorePacketType(int id, PacketFlow flow, String name, Function<ByteBuf, ? extends CorePacket> handler) {
		this.id = id;
		this.flow = flow;
		this.name = name;
		this.handler = handler;
	}

	public int getId() {
		return id;
	}
	
	@Nonnull
	public PacketFlow getFlow() {
		return flow;
	}

	@Nonnull
	public String getKey() {
		return name;
	}
	
	@Nonnull
	public CorePacket handle(@Nonnull ByteBuf buf) {
		return handler.apply(buf);
	}
	
	@Nonnull
	public Function<ByteBuf, ? extends CorePacket> handler() {
		return handler;
	}
	
	@Override
	public String toString() {
        return this.flow.getName() + "/" + this.name;
    }
	
	@Nonnull
	public static CorePacketType create(int id, @Nonnull PacketFlow flow, @Nonnull String name, @Nonnull Function<ByteBuf, ? extends CorePacket> handler) {
		return new CorePacketType(Numbers.min(id, 0), 
				Objects.requireNonNull(flow, "PacketFlow cannot be null!"), 
				Objects.requireNonNull(name, "ObjectKey cannot be null!"),
				Objects.requireNonNull(handler, "handler cannot be null!"));
	}
	
	@Nullable
	public static CorePacketType getById(int id) {
		return REGISTRY.getByFunction(GET_PACKET_ID, id);
	}
	
	@Nullable
	public static CorePacketType getByName(@NotNull String name) {
		return REGISTRY.getByKey(name);
	}
}
