package net.chefcraft.world.menu;

import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslatablePlayer;
import net.chefcraft.world.inventory.InventoryDataHolder;
import net.chefcraft.world.inventory.UtilityMenuItem;
import net.chefcraft.world.util.MethodProvider;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class ConfirmMenu {
	
	@Nonnull
	private final Runnable action;
	private boolean cancel = false;
	
	public ConfirmMenu(@Nonnull final Runnable action) {
		this.action = action;
	}
	
	public void open(@Nonnull TranslatablePlayer transPlayer, @Nonnull MessageHolder title, @Nonnull ItemStack confirm, @Nonnull ItemStack cancel) {
		Inventory hopperInv = MethodProvider.CREATE_INVENTORY_BY_TYPE.apply(InventoryDataHolder.of(this), InventoryType.HOPPER, title);
		hopperInv.setItem(1, confirm);
		hopperInv.setItem(3, cancel);
		transPlayer.getPlayer().openInventory(hopperInv);
	}
	
	public void open(@Nonnull TranslatablePlayer transPlayer) {
		this.open(transPlayer, transPlayer.getMessage("menuUtils.confirmMenuTitle"), 
				MenuController.getTranslatedMenuItem(transPlayer, UtilityMenuItem.CONFIRM).asBukkit(),
				MenuController.getTranslatedMenuItem(transPlayer, UtilityMenuItem.CANCEL).asBukkit());
	}
	
	public void onConfirmed(@Nonnull TranslatablePlayer transPlayer) {
		this.cancel = false;
		transPlayer.playSound("confirm");
		transPlayer.getPlayer().closeInventory();
		this.action.run();
	}
	
	public void onCancelled(@Nonnull TranslatablePlayer transPlayer) {
		this.cancel = true;
		transPlayer.playSound("cancel");
		transPlayer.getPlayer().closeInventory();
		this.action.run();
	}
	
	public boolean isCancelled() {
		return this.cancel;
	}
}
