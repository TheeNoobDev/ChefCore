package net.chefcraft.world.listener;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.server.ReflectorVersion;
import net.chefcraft.core.server.ServerVersion;
import net.chefcraft.reflection.base.itemframe.ChefItemFrame;
import net.chefcraft.reflection.base.util.PacketInjector;
import net.chefcraft.world.event.PlayerPunchToEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

import static net.chefcraft.core.util.ObjectHelper.*;

public final class ClientSidePlayerPunchToEntityEventListener {

    static final String PACKET_NAME;
    static final boolean PACKET_STATUS;
    static final String ENTITY_ID_FIELD;
    static final String ACTION_FIELD;

    //defineVariables
    static {
        Class<?> clazz = getClassOrNull("net.minecraft.network.protocol.game.ServerboundInteractPacket");
        PACKET_NAME = clazz != null ? "ServerboundInteractPacket" : "PacketPlayInUseEntity";

        clazz = clazz != null ? clazz : getClassOrNull("net.minecraft.server." + ReflectorVersion.current().getReflectorVersion() + ".PacketPlayInUseEntity");
        PACKET_STATUS = clazz != null;

        if (clazz == null) {
            throw new RuntimeException("ServerboundInteractPacket couldn't found :(");
        }
        ENTITY_ID_FIELD = hasField(clazz, "entityId") ? "entityId" : "a";
        ACTION_FIELD = hasField(clazz, "action") ? "action" : "b";
    }

	public static void onPlayerJoined(final @NotNull Player player) {
        ChefCore.log(Level.WARNING, "log on");
        if (!PACKET_STATUS) return;
        ChefCore.log(Level.WARNING, "loc acc");
		
		PacketInjector.injectPlayer(player, "chefcore-frame-click-listener",
                PacketInjector.createInboundChannelHandler(packet -> PACKET_NAME.equals(packet.getClass().getSimpleName()), packet -> {

                    int entityId = getFieldOrDefault(packet, ENTITY_ID_FIELD, -1);
                    boolean isAttack = ((Enum<?>) (getFieldOrDefault(packet, ACTION_FIELD, null))).ordinal() == 1;

                    ChefCore.log(Level.WARNING, entityId + " = isAtt: " + isAttack);
                    ChefItemFrame frame = ChefItemFrame.ITEM_FRAME_TRACKER.get(entityId);

                    if (frame != null) {
                        //Call sync because a-sync not allowed!
                        Bukkit.getScheduler().runTask(ChefCore.getInstance(), () -> callEvent(player, frame.getBukkitHandle(), isAttack));

                    } else {
                        Bukkit.getScheduler().runTask(ChefCore.getInstance(), () -> {
                            for (Entity entity : player.getWorld().getEntities()) {
                                if (entity.getEntityId() == entityId) {
                                    callEvent(player, entity, isAttack);
                                    break;
                                }
                            }
                        });
                    }
		}));
	}
	
	private static void callEvent(Player whoClicked, Entity entity, boolean attack) {
        Bukkit.getServer().getPluginManager().callEvent(new PlayerPunchToEntityEvent(
				whoClicked, 
				entity,
				whoClicked.getLocation().toVector(),
				attack));
	}
}
