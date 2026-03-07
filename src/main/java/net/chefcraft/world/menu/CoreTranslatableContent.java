package net.chefcraft.world.menu;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.PluginInstance;
import net.chefcraft.core.language.TranslatablePlayer;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.reflection.world.CoreMaterial;
import net.chefcraft.reflection.world.GameReflections;
import net.chefcraft.world.translatable.TranslatableItemStack;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class CoreTranslatableContent extends AbstractTranslatableContent {

	private static final GameReflections GAME_REFLECTOR = ChefCore.getReflector().getGameReflections();
	
	public CoreTranslatableContent(PluginInstance plugin, String rawName) {
		super(plugin, rawName);
	}
	
	public ItemStack getTranslatedItem(TranslatablePlayer player, int amount, @Nullable Placeholder placeholders, @Nullable String itemState) {
		ItemStack item = this.itemStack.clone();
		if (this.isPlayerHead) {
			item = CoreMaterial.PLAYER_HEAD.toItemStack();
			GAME_REFLECTOR.setSkullOwner(player.getPlayer(), item);
		}
		if (!itemStates.isEmpty() && itemState != null) {
			AbstractTranslatableContent content = itemStates.get(itemState);
			if (content != null) {
				return content.getTranslatedItem(player, content.itemStack.getAmount(), placeholders, null);
			}
		}
		return TranslatableItemStack.from(item, nameKey.replace("<KEY>", contentsPrefixKey), loreKey.replace("<KEY>", contentsPrefixKey))
				.translate(player, placeholders).asBukkit();
	}

	@Override
	public void onClick(TranslatablePlayer corePlayer) { }

}
