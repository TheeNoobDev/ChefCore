package net.chefcraft.core.network.packet;

import io.netty.buffer.ByteBuf;
import net.chefcraft.core.network.CorePacket;
import net.chefcraft.core.network.CorePacketType;
import net.chefcraft.core.server.Minigame;
import net.chefcraft.reflection.world.CoreMaterial;
import net.chefcraft.world.databridge.CoreArena;
import net.chefcraft.world.databridge.GameStatus;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Objects;

public class LobbyBoundMinigameArenaDataPacket implements CorePacket {
	
	private Minigame minigame;
	private String namespaceId;
	private GameStatus gameStatus;
	private int playersSize;
	private int maxPlayersSize;
	private int playersPerTeam;
	private boolean privateGame;
	private String serverName;
	private String mapDisplayName;
	private String gameId;
	
	private CoreMaterial menuItem;
	private boolean displayEnchant;
	
	public LobbyBoundMinigameArenaDataPacket(@Nonnull ByteBuf buffer) {
		Objects.requireNonNull(buffer, "buffer cannot be null!");
		this.read(buffer);
	}
	
	public LobbyBoundMinigameArenaDataPacket(@Nonnull Minigame minigame, @Nonnull CoreArena arena) {
		Objects.requireNonNull(minigame, "minigame cannot be null!");
		Objects.requireNonNull(arena, "arena cannot be null!");
		this.minigame = minigame;
		
		this.namespaceId = arena.getNamespaceID();
		this.gameStatus = arena.getGameStatus();
		this.playersSize = arena.getPlayersSize();
		this.maxPlayersSize = arena.getMaxPlayersSize();
		this.playersPerTeam = arena.getPlayersPerTeam();
		this.privateGame = arena.isPrivate();
		this.serverName = arena.getServerName();
		this.mapDisplayName = arena.getMapDisplayName();
		this.gameId = arena.getGameID();
		
		this.menuItem = arena.getMenuItem().getType();
		this.displayEnchant = arena.getMenuItem().hasDisplayEnchant();
		
	}

	@Override
	public @NotNull CorePacketType type() {
		return CorePacketType.MINIGAME_ARENA_DATA;
	}

	@Override
	public void write(ByteBuf buffer) {
		PacketHandler.writeEnum(buffer, this.minigame);
		
		PacketHandler.writeEnum(buffer, this.gameStatus);
		buffer.writeInt(this.playersSize);
		buffer.writeInt(this.maxPlayersSize);
		buffer.writeInt(this.playersPerTeam);
		buffer.writeBoolean(this.privateGame);
		PacketHandler.writeUTF(buffer, this.namespaceId);
		PacketHandler.writeUTF(buffer, this.serverName);
		PacketHandler.writeUTF(buffer, this.mapDisplayName);
		PacketHandler.writeUTF(buffer, this.gameId);
		
		PacketHandler.writeEnum(buffer, this.menuItem);
		buffer.writeBoolean(this.displayEnchant);
	}

	@Override
	public void read(ByteBuf buffer) {
		this.minigame = PacketHandler.readEnum(buffer, Minigame.asArray());
		
		this.gameStatus = PacketHandler.readEnum(buffer, GameStatus.asArray());
		this.playersSize = buffer.readInt();
		this.maxPlayersSize = buffer.readInt();
		this.playersPerTeam = buffer.readInt();
		this.privateGame = buffer.readBoolean();
		this.namespaceId = PacketHandler.readUTF(buffer);
		this.serverName = PacketHandler.readUTF(buffer);
		this.mapDisplayName = PacketHandler.readUTF(buffer);
		this.gameId = PacketHandler.readUTF(buffer);
		
		this.menuItem = PacketHandler.readEnum(buffer, CoreMaterial.asArray());
		this.displayEnchant = buffer.readBoolean();
	}
	
	@Override
	public String toString() {
		return this.type().toString() + " -> " + this.namespaceId;
	}
}
