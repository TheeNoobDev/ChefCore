package net.chefcraft.reflection.version.v1_21_R3;

import net.chefcraft.core.ChefCore;
import net.chefcraft.reflection.base.itemframe.ChefItemFrame;
import net.chefcraft.reflection.base.util.PacketInjector;
import net.chefcraft.world.event.PlayerPunchToEntityEvent;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public final class ClientSidePlayerPunchToEntityEventListener {

    public static void onPlayerJoined(final ServerPlayer serverPlayer) {

        PacketInjector.injectPlayer(serverPlayer.getBukkitEntity(), "chefcore-frame-click-listener",
                PacketInjector.createInboundChannelHandler(message -> message instanceof ServerboundInteractPacket, action -> {

            ServerboundInteractPacket packet = (ServerboundInteractPacket) action;
            ChefItemFrame frame = ChefItemFrame.ITEM_FRAME_TRACKER.get(packet.getEntityId());

            if (frame != null) {
                //Call sync because a-sync not allowed!
                Bukkit.getScheduler().runTask(ChefCore.getInstance(), () -> callEvent(serverPlayer.getBukkitEntity(), frame.getBukkitHandle(), packet.isAttack()));

            } else Bukkit.getScheduler().runTask(ChefCore.getInstance(), () -> {
                net.minecraft.world.entity.Entity entity = serverPlayer.level().getEntity(packet.getEntityId());
                if (entity != null) {
                    callEvent(serverPlayer.getBukkitEntity(), entity.getBukkitEntity(), packet.isAttack());
                }
            });
        }));
    }

    public static void callEvent(Player whoClicked, Entity entity, boolean attack) {
        Bukkit.getServer().getPluginManager().callEvent(new PlayerPunchToEntityEvent(
                whoClicked,
                entity,
                whoClicked.getLocation().toVector(),
                attack));
    }
}
