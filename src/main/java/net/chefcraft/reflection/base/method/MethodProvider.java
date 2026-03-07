package net.chefcraft.reflection.base;

import net.chefcraft.core.collect.PreparedConditions;
import net.chefcraft.core.component.ComponentSupport;
import net.chefcraft.core.component.CoreTextBase;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslatablePlayer;
import net.chefcraft.core.server.ServerVersion;
import net.chefcraft.world.scoreboard.CoreScoreboardTeam;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.function.BiConsumer;

public interface MethodProvider {

    void clearPlayerInventory(@NotNull PlayerInventory playerInventory);

    void setPlayerListName(@NotNull Player player, @Nullable MessageHolder playerListName);

    void setPlayerListHeaderFooter(@NotNull Player player, @Nullable MessageHolder header, @Nullable MessageHolder footer);

    void setPlayerDisplayName(@NotNull Player player, @Nullable MessageHolder displayName);

    void setItemMetaDisplayName(@NotNull ItemMeta itemMeta, @NotNull MessageHolder displayName);

    void setItemMetaLore(@NotNull ItemMeta itemMeta, @NotNull MessageHolder lore);

    void addExtraLoreToItemMeta(@NotNull ItemMeta itemMeta, @NotNull MessageHolder lore, boolean fromFirstLine);

    void kickPlayer(@NotNull Player player, @Nullable MessageHolder reason);

    void sendMessageToConsole(@NotNull ConsoleCommandSender consoleCommandSender, @NotNull MessageHolder message);

    void createInventory(@NotNull InventoryHolder inventoryHolder, int size, @NotNull MessageHolder title);

    void createInventoryByType(@NotNull InventoryHolder inventoryHolder, @NotNull InventoryType type, @NotNull MessageHolder title);

    void sendMessageToPlayer(@NotNull Player player, @NotNull MessageHolder message);

    void sendActionBarMessageToPlayer(@NotNull Player player, @NotNull MessageHolder actionBarMessage);

    default void updateScoreNameForScoreboardTeam(@NotNull CoreScoreboardTeam team, @NotNull MessageHolder scoreText) {
        ScoreboardUtil.SCOREBOARD_TEAM_UPDATE_SCORE_NAME.accept(team, scoreText);
    }

    interface HoverBox {

        net.chefcraft.reflection.base.MethodProvider.HoverBox appendNewLine();

        net.chefcraft.reflection.base.MethodProvider.HoverBox appendMessage(@NotNull MessageHolder message);

        net.chefcraft.reflection.base.MethodProvider.HoverBox appendHoveredPart(@NotNull MessageHolder message, @NotNull MessageHolder hoverBox);

        void send(@NotNull Player player);

        default void send(@NotNull TranslatablePlayer translatablePlayer) {
            PreparedConditions.notNull(translatablePlayer, "translatablePlayer");
            this.send(translatablePlayer.getPlayer());
        }

        default net.chefcraft.reflection.base.MethodProvider.HoverBox builder() {
            return this;
        }
    }

    interface Title {

        void show(@NotNull Player player, @Nullable MessageHolder title, @Nullable MessageHolder subtitle, @NotNull Duration fadeIn, @NotNull Duration stay, @NotNull Duration fadeOut);

        void clear(@NotNull Player player);

        default void show(@NotNull TranslatablePlayer translatablePlayer, @Nullable MessageHolder title, @Nullable MessageHolder subtitle, @NotNull Duration fadeIn, @NotNull Duration stay, @NotNull Duration fadeOut) {
            PreparedConditions.notNull(translatablePlayer, "translatablePlayer");
            this.show(translatablePlayer.getPlayer(), title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    class ScoreboardUtil {

        static final BiConsumer<CoreScoreboardTeam, MessageHolder> SCOREBOARD_TEAM_UPDATE_SCORE_NAME =
                ServerVersion.isLegacy() ? ScoreboardUtil::applyLegacyScoreNameForTeam1_8_8To1_12_2 :
                ServerVersion.current().isLowerThan(ServerVersion.v1_18) ? ScoreboardUtil::applyLegacyScoreNameForTeam1_13To1_17_1 :
                CoreScoreboardTeam::setPrefix;

        static void applyLegacyScoreNameForTeam1_13To1_17_1(CoreScoreboardTeam team, MessageHolder scoreName) {
            String score = scoreName.asString(true);
            int len = score.length();
            if (len > 32)  {
                String prefix = score.substring(0, 32);
                String suffix = score.substring(32, Math.min(len, 64));

                CoreTextBase lastColors = CoreTextBase.getByChar(suffix.charAt(0));

                if (prefix.charAt(31) == CoreTextBase.LEGACY_COLOR_CHAR && lastColors != null) {
                    prefix = score.substring(0, 31);
                    suffix = score.substring(31, Math.min(len, 63));
                }

                team.setPrefix(MessageHolder.text(prefix));
                team.setSuffix(MessageHolder.text(ComponentSupport.support().getLastTextModifiers(prefix) + suffix));
            } else {
                team.setPrefix(scoreName);
                team.setSuffix(MessageHolder.empty());
            }
        }

        static void applyLegacyScoreNameForTeam1_8_8To1_12_2(CoreScoreboardTeam team, MessageHolder scoreName) {
            String score = scoreName.asString(true);
            int len = score.length();
            if (len > 16) {
                String prefix = score.substring(0, 16);
                String suffix = score.substring(16, Math.min(len, 32));

                CoreTextBase lastColors = CoreTextBase.getByChar(suffix.charAt(0));

                if (prefix.charAt(15) == CoreTextBase.LEGACY_COLOR_CHAR && lastColors != null) {
                    prefix = score.substring(0, 15);
                    suffix = score.substring(15, Math.min(len, 31));
                }

                team.setPrefix(MessageHolder.text(prefix));
                team.setSuffix(MessageHolder.text(ComponentSupport.support().getLastTextModifiers(prefix) + suffix));
            } else {
                team.setPrefix(scoreName);
                team.setSuffix(MessageHolder.empty());
            }
        }
    }
}
