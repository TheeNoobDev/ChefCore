package net.chefcraft.service.luckperms.bukkit;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.language.TranslationSource;
import net.chefcraft.core.util.JHelper;
import net.chefcraft.core.util.PreparedConditions;
import net.chefcraft.service.luckperms.CoreLuckPermsProvider;
import net.chefcraft.service.luckperms.PlayerMetaDataStorage;
import net.chefcraft.world.handler.NameTagHandler;
import net.chefcraft.world.player.CorePlayer;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class BukkitLuckPermsProvider implements CoreLuckPermsProvider {

    @Override
    public void subscribeListeners() {
        EventBus bus = LuckPermsProvider.get().getEventBus();

        bus.subscribe(ChefCore.getInstance(), UserDataRecalculateEvent.class, (event) -> {
            User user = event.getUser();
            CorePlayer corePlayer = ChefCore.getCorePlayerByUniqueId(user.getUniqueId());

            if (corePlayer != null && NameTagHandler.isNameTagFormatsEnabled() && !corePlayer.getUserDataRecalculateCooldown().hasDuration() && !corePlayer.isInGame()) {
                corePlayer.getUserDataRecalculateCooldown().setDuration(20);

                Bukkit.getScheduler().runTaskLater(ChefCore.getInstance(), ()-> {
                    PlayerMetaDataStorage storage = this.createMetaDataStorage(corePlayer, event.getData().getMetaData());
                    corePlayer.setPlayerMetaDataStorage(storage);
                    corePlayer.displayTag(true);

                }, 10L);
            }
        });
    }

    @Override
    public @NotNull PlayerMetaDataStorage createMetaDataStorage(@NotNull TranslationSource translationSource) {
        PreparedConditions.notNull(translationSource, "translationSource");
        if (translationSource instanceof CorePlayer corePlayer) {
            CachedMetaData metadata = getPlayerCachedMetaData(corePlayer.getPlayer());
            PlayerMetaDataStorage metadataStore = this.createMetaDataStorage(corePlayer, metadata);
            corePlayer.setPlayerMetaDataStorage(metadataStore);
            return metadataStore;
        }
        throw new IllegalStateException("param translationSource must be CorePlayer!");
    }

    @Override
    public String getPlayerMetaDataByName(@NotNull Object player, @NotNull String metaDataName) {
        try {
            CachedMetaData metadata = getPlayerCachedMetaData(player);
            String data = metadata.getMetaValue(metaDataName);
            return data != null && !data.isEmpty() ? data : "";
        } catch (Exception x) {
            ChefCore.getInstance().sendPlainMessage("<red>An error occurred while getting player data for -> User: " + player + ", Metadata: " + metaDataName, x);
            return "";
        }
    }

    @Override
    public int getPlayerTabHeight(@NotNull Object player) {
        int parsed = JHelper.nullOrEmptyInt(this.getPlayerMetaDataByName(player, GET_TAB_HEIGHT_KEY), -1);
        if (parsed == -1) {
            ChefCore.log(Level.WARNING, "An error occurred parsing 'tab-height'!");
            return 99;
        }
        return parsed;
    }

    @Override
    public CachedMetaData getPlayerCachedMetaData(@NotNull Object player) {
        OfflinePlayer offlinePlayer = objectAsOfflinePlayer(player);
        return offlinePlayer.isOnline() ? LuckPermsProvider.get().getPlayerAdapter(Player.class).getMetaData(((Player) offlinePlayer))
                : LuckPermsProvider.get().getPlayerAdapter(OfflinePlayer.class).getMetaData(offlinePlayer);
    }

    private OfflinePlayer objectAsOfflinePlayer(Object player) {
        PreparedConditions.notNull(player, "player");
        if (player instanceof OfflinePlayer offlinePlayer) {
            return offlinePlayer;
        } else if (player instanceof TranslationSource source && source.getAudience() instanceof Player bukkitPlayer) {
            return bukkitPlayer;
        } else {
            throw new IllegalStateException("param player must be: OfflinePlayer, Player or TranslationSource)");
        }
    }
}
