package net.chefcraft.reflection.version.v1_8_R3;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import net.chefcraft.core.ChefCore;
import net.chefcraft.core.component.CoreTextBase;
import net.chefcraft.core.component.CoreTextColor;
import net.chefcraft.core.component.CoreTextStyle;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.util.JHelper;
import net.chefcraft.core.util.ObjectHelper;
import net.chefcraft.reflection.base.PacketBuilder;
import net.chefcraft.world.scoreboard.CoreNumberFormat;
import net.chefcraft.world.scoreboard.CoreScoreRenderType;
import net.chefcraft.world.scoreboard.CoreScoreboardOption;
import net.chefcraft.world.scoreboard.CoreTeamData;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.scoreboard.DisplaySlot;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.logging.Level;

interface ChefPacketBuilder {

    ImmutableMap<Class<?>, PacketBuilder> PACKET_BUILDERS_BY_CLASS = ImmutableMap.<Class<?>, PacketBuilder>builder()
            .put(PacketBuilder.Scoreboard.class, new Scoreboard())
            .put(PacketBuilder.Scoreboard.SetPlayerTeam.class, new ScoreboardTeam())
            .build();

    @Nullable
    static PacketBuilder getByClass(Class<?> clazz) {
        return PACKET_BUILDERS_BY_CLASS.get(clazz);
    }

    class Scoreboard implements PacketBuilder.Scoreboard {

        static final ScoreboardObjective EMPTY_OBJECTIVE = new ScoreboardObjective(null, null, IScoreboardCriteria.b);

        @Override
        public Packet<?> resetScore(String objectiveName, String entry) {
            PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore(entry);
            ObjectHelper.setField(packet, "b", JHelper.substringIfNeeded(objectiveName, 0, 16));
            return packet;
        }

        @Override
        public Packet<?> setDisplayObjective(DisplaySlot displaySlot, String objectiveName) {
            PacketPlayOutScoreboardDisplayObjective packet = new PacketPlayOutScoreboardDisplayObjective(
                    (displaySlot == DisplaySlot.PLAYER_LIST ? 0 : displaySlot == DisplaySlot.BELOW_NAME ? 2 : 1),
                    null
            );

            ObjectHelper.setField(packet, "b", JHelper.substringIfNeeded(objectiveName, 0, 16));
            return packet;
        }

        @Override
        public Packet<?> setObjective(ObjectiveMethod method, String objectiveName, MessageHolder displayName, CoreScoreRenderType renderType, @Nullable CoreNumberFormat numberFormat) {
            PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective(EMPTY_OBJECTIVE, method.status());

            ObjectHelper.setField(packet, "a", JHelper.substringIfNeeded(objectiveName, 0, 16));

            if (method != ObjectiveMethod.REMOVE) {
                ObjectHelper.setField(packet, "b", displayName.asString(true));
                ObjectHelper.setField(packet, "c", renderType == CoreScoreRenderType.INTEGER ?
                        IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER :
                        IScoreboardCriteria.EnumScoreboardHealthDisplay.HEARTS
                );
            }
            return packet;
        }

        @Override
        public Packet<?> setScore(String objectiveName, String owner, int score, MessageHolder scoreText, @Nullable CoreNumberFormat numberFormat) {
            PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore(owner);
            ObjectHelper.setField(packet, "b", JHelper.substringIfNeeded(objectiveName, 0, 16));
            ObjectHelper.setField(packet, "c", score);
            ObjectHelper.setField(packet, "d", PacketPlayOutScoreboardScore.EnumScoreboardAction.CHANGE);
            return packet;
        }
    }

    class ScoreboardTeam implements PacketBuilder.Scoreboard.SetPlayerTeam {

        static final ImmutableBiMap<CoreTextBase, EnumChatFormat> TEXT_BASE_MAPPING = ImmutableBiMap.<CoreTextBase, EnumChatFormat>builder()
                .put(CoreTextColor.AQUA, EnumChatFormat.AQUA)
                .put(CoreTextColor.BLACK, EnumChatFormat.BLACK)
                .put(CoreTextColor.BLUE, EnumChatFormat.BLUE)
                .put(CoreTextColor.DARK_AQUA, EnumChatFormat.DARK_AQUA)
                .put(CoreTextColor.DARK_BLUE, EnumChatFormat.DARK_BLUE)
                .put(CoreTextColor.DARK_GRAY, EnumChatFormat.DARK_GRAY)
                .put(CoreTextColor.DARK_GREEN, EnumChatFormat.DARK_GREEN)
                .put(CoreTextColor.DARK_PURPLE, EnumChatFormat.DARK_PURPLE)
                .put(CoreTextColor.DARK_RED, EnumChatFormat.DARK_RED)
                .put(CoreTextColor.GOLD, EnumChatFormat.GOLD)
                .put(CoreTextColor.GRAY, EnumChatFormat.GRAY)
                .put(CoreTextColor.GREEN, EnumChatFormat.GREEN)
                .put(CoreTextColor.LIGHT_PURPLE, EnumChatFormat.LIGHT_PURPLE)
                .put(CoreTextColor.RED, EnumChatFormat.RED)
                .put(CoreTextColor.WHITE, EnumChatFormat.WHITE)
                .put(CoreTextColor.YELLOW, EnumChatFormat.YELLOW)

                .put(CoreTextStyle.BOLD, EnumChatFormat.BOLD)
                .put(CoreTextStyle.ITALIC, EnumChatFormat.ITALIC)
                .put(CoreTextStyle.OBFUSCATED, EnumChatFormat.OBFUSCATED)
                .put(CoreTextStyle.RESET, EnumChatFormat.RESET)
                .put(CoreTextStyle.STRIKETHROUGH, EnumChatFormat.STRIKETHROUGH)
                .put(CoreTextStyle.UNDERLINED, EnumChatFormat.UNDERLINE)
                .build();

        static final ImmutableBiMap<CoreScoreboardOption, ScoreboardTeamBase.EnumNameTagVisibility> OPTION_MAPPING = ImmutableBiMap.<CoreScoreboardOption, ScoreboardTeamBase.EnumNameTagVisibility>builder()
                .put(CoreScoreboardOption.ALWAYS, ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS)
                .put(CoreScoreboardOption.FOR_OTHER_TEAMS, ScoreboardTeamBase.EnumNameTagVisibility.HIDE_FOR_OTHER_TEAMS)
                .put(CoreScoreboardOption.FOR_OWN_TEAM, ScoreboardTeamBase.EnumNameTagVisibility.HIDE_FOR_OWN_TEAM)
                .put(CoreScoreboardOption.NEVER, ScoreboardTeamBase.EnumNameTagVisibility.NEVER)
                .build();

        @Override
        public Packet<?> build(String name, Action action, Collection<String> players, CoreTeamData team) {
            PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
            ObjectHelper.setField(packet, "h", action.status());
            ObjectHelper.setField(packet, "a", JHelper.substringIfNeeded(name, 0, 16));

            if (action == Action.REMOVE) return packet;

            if (action != Action.UPDATE) {
                if (action != Action.CREATE && (players == null || players.isEmpty())) {
                    throw new IllegalArgumentException("Players cannot be " + (players == null ? "null" : "empty") + "!");
                }
                ((Collection<String>) ObjectHelper.getField(packet, "g")).addAll(players);
            }

            if (action == Action.CREATE || action == Action.UPDATE) {
                ObjectHelper.setField(packet, "b", team.getDisplayName().asString(true));
                ObjectHelper.setField(packet, "c", JHelper.substringIfNeeded(team.getPrefix().asString(true), 0, 16));
                ObjectHelper.setField(packet, "d", JHelper.substringIfNeeded(team.getSuffix().asString(true), 0, 16));
                ObjectHelper.setField(packet, "e", OPTION_MAPPING.getOrDefault(team.getNameTagVisibility(), ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS).e);
                ObjectHelper.setField(packet, "f", TEXT_BASE_MAPPING.getOrDefault(team.getTextModifier(), EnumChatFormat.RESET).b());
                ObjectHelper.setField(packet, "i", team.packOptionsData());
            }
            return packet;
        }
    }
}
