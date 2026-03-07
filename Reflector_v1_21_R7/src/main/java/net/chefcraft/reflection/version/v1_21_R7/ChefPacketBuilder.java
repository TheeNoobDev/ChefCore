package net.chefcraft.reflection.v1_21_R7;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.papermc.paper.adventure.PaperAdventure;
import net.chefcraft.core.component.CoreTextBase;
import net.chefcraft.core.component.CoreTextColor;
import net.chefcraft.core.component.CoreTextStyle;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.util.JHelper;
import net.chefcraft.core.util.ObjectHelper;
import net.chefcraft.reflection.base.PacketBuilder;
import net.chefcraft.world.boss.CoreBarColor;
import net.chefcraft.world.boss.CoreBarOverlay;
import net.chefcraft.world.boss.CoreBossBar;
import net.chefcraft.world.itemframe.CoreMapPatch;
import net.chefcraft.world.scoreboard.CoreNumberFormat;
import net.chefcraft.world.scoreboard.CoreScoreRenderType;
import net.chefcraft.world.scoreboard.CoreScoreboardOption;
import net.chefcraft.world.scoreboard.CoreTeamData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.decoration.BlockAttachedEntity;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.map.CraftMapCursor;
import org.bukkit.map.MapCursor;
import org.bukkit.scoreboard.DisplaySlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

interface ChefPacketBuilder {

    ImmutableMap<Class<?>, PacketBuilder> PACKET_BUILDERS_BY_CLASS = ImmutableMap.<Class<?>, PacketBuilder>builder()
            .put(PacketBuilder.Scoreboard.class, new Scoreboard())
            .put(PacketBuilder.Scoreboard.SetPlayerTeam.class, new ScoreboardTeam())
            .put(PacketBuilder.Entity.class, new Entity())
            .put(PacketBuilder.MapItem.class, new MapItem())
            .put(PacketBuilder.BossEvent.class, new BossEvent())
            .build();

    @Nullable
    static PacketBuilder getByClass(Class<?> clazz) {
        return PACKET_BUILDERS_BY_CLASS.get(clazz);
    }

    class Scoreboard implements PacketBuilder.Scoreboard {

        static final Objective EMPTY_OBJECTIVE = new Objective(null, "", null, Component.literal(""), null, false, StyledFormat.NO_STYLE);

        static final ImmutableBiMap<DisplaySlot, net.minecraft.world.scores.DisplaySlot> DISPLAY_SLOT_MAPPING = ImmutableBiMap.<DisplaySlot, net.minecraft.world.scores.DisplaySlot>builder()
                .put(DisplaySlot.PLAYER_LIST, net.minecraft.world.scores.DisplaySlot.LIST)
                .put(DisplaySlot.BELOW_NAME, net.minecraft.world.scores.DisplaySlot.BELOW_NAME)
                .put(DisplaySlot.SIDEBAR, net.minecraft.world.scores.DisplaySlot.SIDEBAR)
                .put(DisplaySlot.SIDEBAR_TEAM_BLACK, net.minecraft.world.scores.DisplaySlot.TEAM_BLACK)
                .put(DisplaySlot.SIDEBAR_TEAM_DARK_BLUE, net.minecraft.world.scores.DisplaySlot.TEAM_DARK_BLUE)
                .put(DisplaySlot.SIDEBAR_TEAM_DARK_GREEN, net.minecraft.world.scores.DisplaySlot.TEAM_DARK_GREEN)
                .put(DisplaySlot.SIDEBAR_TEAM_DARK_AQUA, net.minecraft.world.scores.DisplaySlot.TEAM_DARK_AQUA)
                .put(DisplaySlot.SIDEBAR_TEAM_DARK_RED, net.minecraft.world.scores.DisplaySlot.TEAM_DARK_RED)
                .put(DisplaySlot.SIDEBAR_TEAM_DARK_PURPLE, net.minecraft.world.scores.DisplaySlot.TEAM_DARK_PURPLE)
                .put(DisplaySlot.SIDEBAR_TEAM_GOLD, net.minecraft.world.scores.DisplaySlot.TEAM_GOLD)
                .put(DisplaySlot.SIDEBAR_TEAM_GRAY, net.minecraft.world.scores.DisplaySlot.TEAM_GRAY)
                .put(DisplaySlot.SIDEBAR_TEAM_DARK_GRAY, net.minecraft.world.scores.DisplaySlot.TEAM_DARK_GRAY)
                .put(DisplaySlot.SIDEBAR_TEAM_BLUE, net.minecraft.world.scores.DisplaySlot.TEAM_BLUE)
                .put(DisplaySlot.SIDEBAR_TEAM_GREEN, net.minecraft.world.scores.DisplaySlot.TEAM_GREEN)
                .put(DisplaySlot.SIDEBAR_TEAM_AQUA, net.minecraft.world.scores.DisplaySlot.TEAM_AQUA)
                .put(DisplaySlot.SIDEBAR_TEAM_RED, net.minecraft.world.scores.DisplaySlot.TEAM_RED)
                .put(DisplaySlot.SIDEBAR_TEAM_LIGHT_PURPLE, net.minecraft.world.scores.DisplaySlot.TEAM_LIGHT_PURPLE)
                .put(DisplaySlot.SIDEBAR_TEAM_YELLOW, net.minecraft.world.scores.DisplaySlot.TEAM_YELLOW)
                .put(DisplaySlot.SIDEBAR_TEAM_WHITE, net.minecraft.world.scores.DisplaySlot.TEAM_WHITE)
                .build();

        @Override
        public Packet<?> resetScore(String objectiveName, String entry) {
            return new ClientboundResetScorePacket(entry, objectiveName);
        }

        @Override
        public Packet<?> setDisplayObjective(DisplaySlot displaySlot, String objectiveName) {
            ClientboundSetDisplayObjectivePacket packet = new ClientboundSetDisplayObjectivePacket(DISPLAY_SLOT_MAPPING.get(displaySlot), null);
            ObjectHelper.setField(packet, "objectiveName", objectiveName);
            return packet;
        }

        @Override
        public Packet<?> setObjective(ObjectiveMethod method, String objectiveName, MessageHolder displayName, CoreScoreRenderType renderType, @Nullable CoreNumberFormat numberFormat) {
            ClientboundSetObjectivePacket packet = new ClientboundSetObjectivePacket(EMPTY_OBJECTIVE, method.status());

            ObjectHelper.setField(packet, "objectiveName", objectiveName);

            if (method != ObjectiveMethod.REMOVE) {
                ObjectHelper.setField(packet, "renderType", renderType == CoreScoreRenderType.INTEGER ? ObjectiveCriteria.RenderType.INTEGER : ObjectiveCriteria.RenderType.HEARTS);
                ObjectHelper.setField(packet, "displayName", MainReflector.toVanilla(displayName));
                ObjectHelper.setField(packet, "numberFormat", ChefNumberFormat.nullableFormatCheck(numberFormat));

            }
            return packet;
        }

        @Override
        public Packet<?> setScore(String objectiveName, String owner, int score, MessageHolder scoreText, @Nullable CoreNumberFormat numberFormat) {
            return new ClientboundSetScorePacket(owner, objectiveName, score,
                    Optional.ofNullable(MainReflector.toVanilla(scoreText)),
                    ChefNumberFormat.nullableFormatCheck(numberFormat));
        }
    }

    class ScoreboardTeam implements PacketBuilder.Scoreboard.SetPlayerTeam {

        static final ImmutableBiMap<CoreTextBase, ChatFormatting> TEXT_BASE_MAPPING = ImmutableBiMap.<CoreTextBase, ChatFormatting>builder()
                .put(CoreTextColor.AQUA, ChatFormatting.AQUA)
                .put(CoreTextColor.BLACK, ChatFormatting.BLACK)
                .put(CoreTextColor.BLUE, ChatFormatting.BLUE)
                .put(CoreTextColor.DARK_AQUA, ChatFormatting.DARK_AQUA)
                .put(CoreTextColor.DARK_BLUE, ChatFormatting.DARK_BLUE)
                .put(CoreTextColor.DARK_GRAY, ChatFormatting.DARK_GRAY)
                .put(CoreTextColor.DARK_GREEN, ChatFormatting.DARK_GREEN)
                .put(CoreTextColor.DARK_PURPLE, ChatFormatting.DARK_PURPLE)
                .put(CoreTextColor.DARK_RED, ChatFormatting.DARK_RED)
                .put(CoreTextColor.GOLD, ChatFormatting.GOLD)
                .put(CoreTextColor.GRAY, ChatFormatting.GRAY)
                .put(CoreTextColor.GREEN, ChatFormatting.GREEN)
                .put(CoreTextColor.LIGHT_PURPLE, ChatFormatting.LIGHT_PURPLE)
                .put(CoreTextColor.RED, ChatFormatting.RED)
                .put(CoreTextColor.WHITE, ChatFormatting.WHITE)
                .put(CoreTextColor.YELLOW, ChatFormatting.YELLOW)

                .put(CoreTextStyle.BOLD, ChatFormatting.BOLD)
                .put(CoreTextStyle.ITALIC, ChatFormatting.ITALIC)
                .put(CoreTextStyle.OBFUSCATED, ChatFormatting.OBFUSCATED)
                .put(CoreTextStyle.RESET, ChatFormatting.RESET)
                .put(CoreTextStyle.STRIKETHROUGH, ChatFormatting.STRIKETHROUGH)
                .put(CoreTextStyle.UNDERLINED, ChatFormatting.UNDERLINE)
                .build();

        static final ImmutableBiMap<CoreScoreboardOption, Team.Visibility> VISIBILITY_OPTION_MAPPING = ImmutableBiMap.<CoreScoreboardOption, Team.Visibility>builder()
                .put(CoreScoreboardOption.ALWAYS, Team.Visibility.ALWAYS)
                .put(CoreScoreboardOption.FOR_OTHER_TEAMS, Team.Visibility.HIDE_FOR_OTHER_TEAMS)
                .put(CoreScoreboardOption.FOR_OWN_TEAM, Team.Visibility.HIDE_FOR_OWN_TEAM)
                .put(CoreScoreboardOption.NEVER, Team.Visibility.NEVER)
                .build();

        static final ImmutableBiMap<CoreScoreboardOption, Team.CollisionRule> COLLISION_OPTION_MAPPING = ImmutableBiMap.<CoreScoreboardOption, Team.CollisionRule>builder()
                .put(CoreScoreboardOption.ALWAYS, Team.CollisionRule.ALWAYS)
                .put(CoreScoreboardOption.FOR_OTHER_TEAMS, Team.CollisionRule.PUSH_OTHER_TEAMS)
                .put(CoreScoreboardOption.FOR_OWN_TEAM, Team.CollisionRule.PUSH_OWN_TEAM)
                .put(CoreScoreboardOption.NEVER, Team.CollisionRule.NEVER)
                .build();

        static final PlayerTeam EMPTY_TEAM = new PlayerTeam(null, "Empty");


        @Override
        public Packet<?> build(String name, Action action, Collection<String> players, CoreTeamData team) {
            ClientboundSetPlayerTeamPacket packet = ClientboundSetPlayerTeamPacket.createRemovePacket(EMPTY_TEAM);
            ObjectHelper.setField(packet, "name", name);
            if (action == Action.REMOVE) return packet;

            ObjectHelper.setField(packet, "method", action.status());

            if (action != Action.UPDATE) {
                if (action != Action.CREATE && (players == null || players.isEmpty())) {
                    throw new IllegalArgumentException("Players cannot be " + (players == null ? "null" : "empty") + "!");
                }
                ObjectHelper.setField(packet, "players", ImmutableList.copyOf(players));
            }

            if (action == Action.CREATE || action == Action.UPDATE) {
                ClientboundSetPlayerTeamPacket.Parameters params = new ClientboundSetPlayerTeamPacket.Parameters(EMPTY_TEAM);

                ObjectHelper.setField(params, "displayName", MainReflector.toVanilla(team.getDisplayName()));
                ObjectHelper.setField(params, "playerPrefix", MainReflector.toVanilla(team.getPrefix()));
                ObjectHelper.setField(params, "playerSuffix", MainReflector.toVanilla(team.getSuffix()));
                ObjectHelper.setField(params, "nametagVisibility", VISIBILITY_OPTION_MAPPING.get(team.getNameTagVisibility()).name);
                ObjectHelper.setField(params, "collisionRule", COLLISION_OPTION_MAPPING.get(team.getCollisionRule()).name);
                ObjectHelper.setField(params, "color", TEXT_BASE_MAPPING.getOrDefault(team.getTextModifier(), ChatFormatting.RESET));
                ObjectHelper.setField(params, "options", team.packOptionsData());

                ObjectHelper.setField(packet, "parameters", Optional.of(params));
            }
            return packet;
        }
    }

    class Entity implements PacketBuilder.Entity {

        @Override
        public Object addEntity(org.bukkit.entity.Entity entity) {
            net.minecraft.world.entity.Entity nms = ((CraftEntity) entity).getHandle();
            int data = nms instanceof BlockAttachedEntity ? nms.getDirection().get3DDataValue() : 0;
            return new ClientboundAddEntityPacket(nms.getId(), nms.getUUID(), nms.getX(), nms.getY(), nms.getZ(), nms.getXRot(), nms.getYRot(),
                    nms.getType(), data, nms.getDeltaMovement(), nms.getYHeadRot());
        }

        @Override
        public Object setEntityData(org.bukkit.entity.Entity entity) {
            net.minecraft.world.entity.Entity nms = ((CraftEntity) entity).getHandle();
            return new ClientboundSetEntityDataPacket(nms.getId(), nms.getEntityData().packAll());
        }

        @Override
        public Object teleportEntity(org.bukkit.entity.Entity entity) {
            net.minecraft.world.entity.Entity nms = ((CraftEntity) entity).getHandle();
            return new ClientboundTeleportEntityPacket(nms.getId(),
                    PositionMoveRotation.of(nms),
                    Collections.emptySet(),
                    nms.onGround);
        }

        @Override
        public Object removeEntities(org.bukkit.entity.Entity... entities) {
            int[] ids = new int[entities.length];
            for (int i = 0; i < ids.length; i++) {
                ids[i] = entities[i].getEntityId();
            }
            return new ClientboundRemoveEntitiesPacket(ids);
        }
    }

    class MapItem implements PacketBuilder.MapItem {

        static final Function<MapCursor, MapDecoration> MAP_CURSOR_TO_MAP_ICON_MAPPER = mapCursor ->
                new MapDecoration(Holder.direct(CraftMapCursor.CraftType.bukkitToMinecraft(mapCursor.getType())),
                        mapCursor.getX(), mapCursor.getY(), mapCursor.getDirection(), Optional.ofNullable(PaperAdventure.asVanilla(mapCursor.caption())));

        @Override
        public Object mapItemData(int mapId, byte scale, boolean locked, @Nullable Collection<MapCursor> decorations, @Nullable CoreMapPatch colorPatch) {
            return new ClientboundMapItemDataPacket(
                    new MapId(mapId),
                    scale,
                    locked,
                    Optional.ofNullable(decorations != null ? JHelper.mapCollectionToList(decorations, MAP_CURSOR_TO_MAP_ICON_MAPPER) : null),
                    Optional.ofNullable(colorPatch != null ? new MapItemSavedData.MapPatch(colorPatch.minX, colorPatch.minY, colorPatch.maxX, colorPatch.maxY, colorPatch.colors) : null));
        }
    }

    class BossEvent implements PacketBuilder.BossEvent {

        static final ImmutableBiMap<CoreBarColor, net.minecraft.world.BossEvent.BossBarColor> BAR_COLOR_MAPPING = ImmutableBiMap.<CoreBarColor, net.minecraft.world.BossEvent.BossBarColor>builder()
                .put(CoreBarColor.PINK, net.minecraft.world.BossEvent.BossBarColor.PINK)
                .put(CoreBarColor.BLUE, net.minecraft.world.BossEvent.BossBarColor.BLUE)
                .put(CoreBarColor.RED, net.minecraft.world.BossEvent.BossBarColor.RED)
                .put(CoreBarColor.GREEN, net.minecraft.world.BossEvent.BossBarColor.GREEN)
                .put(CoreBarColor.PURPLE, net.minecraft.world.BossEvent.BossBarColor.PURPLE)
                .put(CoreBarColor.WHITE, net.minecraft.world.BossEvent.BossBarColor.WHITE)
                .put(CoreBarColor.YELLOW, net.minecraft.world.BossEvent.BossBarColor.YELLOW)
                .build();

        static final ImmutableBiMap<CoreBarOverlay, net.minecraft.world.BossEvent.BossBarOverlay> BAR_OVERLAY_MAPPING = ImmutableBiMap.<CoreBarOverlay, net.minecraft.world.BossEvent.BossBarOverlay>builder()
                .put(CoreBarOverlay.PROGRESS, net.minecraft.world.BossEvent.BossBarOverlay.PROGRESS)
                .put(CoreBarOverlay.NOTCHED_6, net.minecraft.world.BossEvent.BossBarOverlay.NOTCHED_6)
                .put(CoreBarOverlay.NOTCHED_10, net.minecraft.world.BossEvent.BossBarOverlay.NOTCHED_10)
                .put(CoreBarOverlay.NOTCHED_12, net.minecraft.world.BossEvent.BossBarOverlay.NOTCHED_12)
                .put(CoreBarOverlay.NOTCHED_20, net.minecraft.world.BossEvent.BossBarOverlay.NOTCHED_20)
                .build();

        static final Delegate DELEGATE = new Delegate(null);

        @Override
        public Object bossEvent(@NotNull CoreBossBar coreBossBar, @NotNull Action action) {
            if (action == Action.REMOVE) {
                return ClientboundBossEventPacket.createRemovePacket(coreBossBar.getId());
            }

            DELEGATE.updateOriginal(coreBossBar);
            return switch (action) {
                case ADD -> ClientboundBossEventPacket.createAddPacket(DELEGATE);
                case UPDATE_PROGRESS -> ClientboundBossEventPacket.createUpdateProgressPacket(DELEGATE);
                case UPDATE_NAME -> ClientboundBossEventPacket.createUpdateNamePacket(DELEGATE);
                case UPDATE_STYLE -> ClientboundBossEventPacket.createUpdateStylePacket(DELEGATE);
                case UPDATE_PROPERTIES -> ClientboundBossEventPacket.createUpdatePropertiesPacket(DELEGATE);
                default -> throw new IllegalStateException("Illegal operation type!");
            };
        }

        static final class Delegate extends net.minecraft.world.BossEvent {

            private CoreBossBar original;

            public Delegate(CoreBossBar original) {
                super(null, null, null, null);
                this.original = original;
            }

            public void updateOriginal(CoreBossBar original) {
                this.original = original;
            }

            @Override
            public @NotNull UUID getId() {
                return this.original.getId();
            }

            @Override
            public @NotNull Component getName() {
                return MainReflector.toVanilla(this.original.getTitle());
            }

            @Override
            public float getProgress() {
                return this.original.getBarProgress();
            }

            @Override
            public @NotNull BossBarColor getColor() {
                return BAR_COLOR_MAPPING.get(this.original.getBarColor());
            }

            @Override
            public @NotNull BossBarOverlay getOverlay() {
                return BAR_OVERLAY_MAPPING.get(this.original.getBarOverlay());
            }

            @Override
            public boolean shouldDarkenScreen() {
                return this.original.shouldDarkenScreen();
            }

            @Override
            public boolean shouldPlayBossMusic() {
                return this.original.shouldPlayBossMusic();
            }

            @Override
            public boolean shouldCreateWorldFog() {
                return this.original.shouldCreateWorldFog();
            }



            //UnsupportedOperations

            @Override
            public void setName(@NotNull Component name) {
                throw new UnsupportedOperationException("delegate");
            }

            @Override
            public void setProgress(float progress) {
                throw new UnsupportedOperationException("delegate");
            }

            @Override
            public void setColor(@NotNull BossBarColor color) {
                throw new UnsupportedOperationException("delegate");
            }

            @Override
            public void setOverlay(@NotNull BossBarOverlay overlay) {
                throw new UnsupportedOperationException("delegate");
            }

            @Override
            public @NotNull net.minecraft.world.BossEvent setDarkenScreen(boolean darkenSky) {
                throw new UnsupportedOperationException("delegate");
            }

            @Override
            public @NotNull net.minecraft.world.BossEvent setPlayBossMusic(boolean playEndBossMusic) {
                throw new UnsupportedOperationException("delegate");
            }

            @Override
            public @NotNull net.minecraft.world.BossEvent setCreateWorldFog(boolean createFog) {
                throw new UnsupportedOperationException("delegate");
            }
        }
    }
}
