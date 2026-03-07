package net.chefcraft.world.player;

import net.chefcraft.core.util.PreparedConditions;
import org.jetbrains.annotations.NotNull;

public abstract class Preferences<T> {

    private final T holder;

    protected Preferences(@NotNull T holder) {
        PreparedConditions.notNull(holder, "holder");
        this.holder = holder;
    }

    @NotNull
    public T getHolder() {
        return this.holder;
    }

    public enum Visibility {
        ALL, FRIENDS, FRIENDS_AND_PARTY, NONE;
    }
}
