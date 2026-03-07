package net.chefcraft.core.collect;

import javax.annotation.Nullable;

public class PreparedConditions {

    public static <T> T notNull(@Nullable T obj, @Nullable String param) {
        if (obj == null) throw new NullPointerException(param != null ? param + " cannot be null!" : "Cannot be null!");
        return obj;
    }

    public static <T> T notNull(@Nullable T obj, @Nullable String param, @Nullable String cause) {
        if (obj == null) throw new NullPointerException((param != null ? param + " cannot be null: " : "Cannot be null: ") + cause);
        return obj;
    }
}
