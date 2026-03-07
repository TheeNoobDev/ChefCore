package net.chefcraft.world.loot;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.PluginInstance;
import net.chefcraft.core.language.MessageCompiler;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslatablePlayer;
import net.chefcraft.core.registry.CoreRegistry;
import net.chefcraft.core.util.ObjectKey;
import net.chefcraft.core.util.Placeholder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;

public class GameReward {
	
	private final Type type;
	private final float exp;
	private final int coins;
	private final BiConsumer<GameReward, TranslatablePlayer> action;
	
	public GameReward(@NotNull Type type, float exp, int coins, @Nullable BiConsumer<GameReward, TranslatablePlayer> action) {
		Objects.requireNonNull(type, "reward type cannot be null!");
		this.type = type;
		this.exp = exp;
		this.coins = coins;
		this.action = action;
	}
	
	public GameReward(@NotNull Type type, float exp, int coins) {
		this(type, exp, coins, null);
	}
	
	public Type getType() {
		return this.type;
	}
	
	public float getExp() {
		return this.exp;
	}
	
	public int getCoins() {
		return this.coins;
	}
	
	/**
	 * @return reward action
	 */
	@Nullable
	public BiConsumer<GameReward, TranslatablePlayer> getAction() {
		return this.action;
	}

	public void callActionIfPresent(@NotNull TranslatablePlayer translatablePlayer) {
		if (this.action != null) {
			Objects.requireNonNull(translatablePlayer, "cannot call action for null translatable player!");
			this.action.accept(this, translatablePlayer);
		}
	}

	public static interface Type extends ObjectKey {
		
		public static final Type KILL = create(ChefCore.getInstance(), "kill", "rewardFormats.type.kill");
		public static final Type ASSIST = create(ChefCore.getInstance(), "assist", "rewardFormats.type.assist");
		
		public static final CoreRegistry<Type> REGISTRY = new CoreRegistry<>(Type.class);
		
		@NotNull String getTranslationKey();
		
		@NotNull PluginInstance getPluginInstance();
		
		@NotNull MessageHolder translate(@NotNull TranslatablePlayer player, @Nullable Placeholder placeholder);
		
		static Type create(@NotNull PluginInstance instance, @NotNull String key, @NotNull String translationKey) {
			return new TypeImpl(instance, key, translationKey);
		}
		
	}
	
	final static class TypeImpl implements Type {
		
		private final PluginInstance instance;
		private final String key;
		private final String translationKey;
		
		TypeImpl(@NotNull PluginInstance instance, @NotNull String key, @NotNull String translationKey) {
			this.instance = Objects.requireNonNull(instance, "instance cannot be null!");
			this.key = Objects.requireNonNull(key, "key cannot be null!");
			this.translationKey = Objects.requireNonNull(translationKey, "translation key cannot be null!");
		}

		@Override
		public @NotNull String getKey() {
			return this.key;
		}
		
		@Override
		public @NotNull String getTranslationKey() {
			return this.translationKey;
		}
		
		@Override
		public @NotNull PluginInstance getPluginInstance() {
			return this.instance;
		}

		@Override
		public MessageHolder translate(@NotNull TranslatablePlayer player, @Nullable Placeholder placeholder) {
			return MessageCompiler.getMessage(instance, player, this.translationKey, placeholder);
		}
	}
}
