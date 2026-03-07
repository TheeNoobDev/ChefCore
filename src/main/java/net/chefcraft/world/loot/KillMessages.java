package net.chefcraft.world.loot;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.configuration.CoreConfigKey;
import net.chefcraft.core.configuration.YamlFile;
import net.chefcraft.core.language.MessageCompiler;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslatablePlayer;
import net.chefcraft.core.language.TranslationSource;
import net.chefcraft.core.math.SimpleMath;
import net.chefcraft.core.util.JHelper;
import net.chefcraft.core.util.Pair;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.reflection.world.CoreMaterial;
import net.chefcraft.world.inventory.MultiInventory;
import net.chefcraft.world.inventory.MultiInventorySize;
import net.chefcraft.world.player.CorePlayer;
import net.chefcraft.world.rarity.CoreRarity;
import net.chefcraft.world.rarity.Evaluable;
import net.chefcraft.world.translatable.TranslatableItemStack;
import net.chefcraft.world.util.DamageTracker;
import net.chefcraft.world.util.MethodProvider;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

public class KillMessages implements Evaluable {
	
	public static File killMessagesDirectory;
	private static Map<String, KillMessages> killMessagesMap = new HashMap<>();
	
	public static void bootstrap() {
		killMessagesDirectory = ChefCore.getGlobalConfigHandler().createFileDirectory(CoreConfigKey.KILL_MESSAGES, "kill_messages");
		
		new KillMessages(ChefCore.getGlobalConfigHandler().copyResource(CoreConfigKey.KILL_MESSAGES, "kill_messages/default.yml"));
		new KillMessages(ChefCore.getGlobalConfigHandler().copyResource(CoreConfigKey.KILL_MESSAGES, "kill_messages/rainbow.yml"));
		
		for (String name : killMessagesDirectory.list()) {
			if (name.contains(".yml") && !name.equalsIgnoreCase("default.yml") && !name.equalsIgnoreCase("rainbow.yml")) {
				try {
					new KillMessages(YamlFile.create(killMessagesDirectory.getPath() + File.separator + name));
				} catch (IOException e) {
					e.printStackTrace();
					ChefCore.log(Level.SEVERE, "Failed to load '" + name + "' kill message!"); 
				}
			}
		}
	}
	
	@Nullable
	public static KillMessages getKillMessageByName(String name) {
		return killMessagesMap.get(name);
	}
	
	public static KillMessages getDefaultKillMessages() {
		return killMessagesMap.get("default"); 
	}
	
	public static void openKillMessagesMenu(final CorePlayer corePlayer) {
		List<? extends Evaluable> list = CoreRarity.getSortedListFromMap(killMessagesMap);
		int j = list.size();
		
		MultiInventory multiInv = MultiInventory.createWithDefaultUtilityItems(corePlayer, MultiInventorySize.MEDIUM, "kill_messages.menu.title");
		Player player = corePlayer.getPlayer();
		
		KillMessages current = corePlayer.getKillMessages();
		
		for (int i = 0; i < j; i++) {
			KillMessages msg = (KillMessages) list.get(i);
			
			boolean flag = current.equals(msg);
			
			final Placeholder placeholder = Placeholder.of("{NAME}", msg.getTranslatedName(corePlayer))
					.add("{RARITY}", msg.rarity.getTranslatedName(corePlayer, true))
					.add("{RARITY_COLOR}", msg.rarity.getTextColor());
			
			
			if (msg.permission.isEmpty() || player.hasPermission(msg.permission) || flag) {
				
				TranslatableItemStack stack = TranslatableItemStack.from(msg.menuItem, null, null);
				
				stack.updateDisplayName(corePlayer.getMessage("kill_messages.menu." + (flag ? "selected" : "unlocked") + ".name", placeholder));
				
				MessageHolder info = MessageHolder.merge(corePlayer.getMessage("kill_messages.menu." + (flag ? "selected" : "unlocked") + ".lore", placeholder),
						MessageHolder.text(msg.getTranslatedLore(corePlayer)), "{MESSAGE_LORE}");
				
				stack.updateLore(MessageHolder.merge(info, msg.compileExampleDeathMessagesForMenu(corePlayer, 5), "{EXAMPLE_MESSAGE}"));
				
				multiInv.setItem(i, stack.asBukkit());
			} else {
				
				TranslatableItemStack stack = TranslatableItemStack.from(msg.menuItem, null, null);
				
				stack.updateDisplayName(corePlayer.getMessage("kill_messages.menu.locked.name", placeholder));
				
				MessageHolder info = MessageHolder.merge(corePlayer.getMessage("kill_messages.menu.locked.lore", placeholder),
						MessageHolder.text(msg.getTranslatedLore(corePlayer)), "{MESSAGE_LORE}");
				
				stack.updateLore(MessageHolder.merge(info, msg.compileExampleDeathMessagesForMenu(corePlayer, 5), "{EXAMPLE_MESSAGE}"));
				
				multiInv.setItem(i, stack.asBukkit());
			}
			
			multiInv.setClickAction(i, () -> {
				final KillMessages km = msg;
				
				if (corePlayer.getKillMessages().equals(km)) {
					corePlayer.sendMessage("kill_messages.menu.alreadySelected");
					corePlayer.playSound("error");
				} else if (km.permission.isEmpty() || corePlayer.getPlayer().hasPermission(km.permission)) {
					corePlayer.setKillMessages(km);
					corePlayer.sendMessage("kill_messages.menu.youSelected", placeholder);
					corePlayer.playSound("successful");
					corePlayer.safelyCloseExistingMenu();
				} else {
					corePlayer.sendMessage("kill_messages.menu.cantSelect");
					corePlayer.playSound("error");
				}
			});
		}
		
		corePlayer.getInventoryData().setMultiInventory(multiInv);
		multiInv.openFirstPage(corePlayer);
	}
	
	private final YamlFile yamlFile;
	
	private String namespaceID;
	private String prefixKey;
	private String permission;
	private ItemStack menuItem;
	private String nameKey;
	private String loreKey;
	private CoreRarity rarity;
	
	private String selfMessageNode;
	private String killerMessageNode;
	
	public KillMessages(YamlFile yamlFile) {
		this.yamlFile = yamlFile;
		FileConfiguration config = yamlFile.getConfig();
		this.namespaceID = config.getString("namespaceID");
		this.prefixKey = config.getString("prefixKey");
		this.menuItem = CoreMaterial.matchByName(config.getString("menuItem")).toItemStack();
		this.nameKey = config.getString("nameKey").replace("<KEY>", this.prefixKey);;
		this.loreKey = config.getString("loreKey").replace("<KEY>", this.prefixKey);;
		this.rarity = CoreRarity.valueOf(config.getString("rarity").toUpperCase(Locale.ENGLISH));
		this.permission = config.getString("permission");
		
		this.selfMessageNode = config.getString("messageNodes.self").replace("<KEY>", this.prefixKey);
		this.killerMessageNode = config.getString("messageNodes.killer").replace("<KEY>", this.prefixKey);
		
		killMessagesMap.put(namespaceID, this);
	}
	
	private @NotNull MessageHolder compileExampleDeathMessagesForMenu(CorePlayer translatable, int count) {
		MessageHolder list = MessageHolder.text("");
		List<Integer> initList = new ArrayList<>();
		DamageCause[] values = DamageCause.values();
		
		for (int i = 0; i < count; i++) {
			list = MessageHolder.merge(list, this.compileDeathMessageForMenu(translatable, JHelper.getRandomElementFromArrayWithDuplicateCheck(values, initList), false));
		}
		
		initList.clear();
		initList = null;
		values = null;
		
		return list;
	}
	
	public void sendDeathMessage(PlayerDeathEvent event, DamageTracker damageTracker, GameReward killerGameReward, GameReward assitGameReward, boolean withHealthComponent, TranslatablePlayer victim, @Nullable TranslatablePlayer killer, Iterable<? extends TranslatablePlayer> list) {
		Set<TranslatablePlayer> assistorsSet = damageTracker.getAssists();
		int assistsSize = assistorsSet.size();
		boolean hasKiller = killer != null;
		MessageHolder victimDisplayName = victim.getDisplayName();
		
		DamageCause damageCause = victim.getPlayer().getLastDamageCause() != null ? victim.getPlayer().getLastDamageCause().getCause() : DamageCause.SUICIDE;
		Placeholder placeholder = Placeholder.of("{VICTIM}", victimDisplayName.asString(false))
				.add("{KILLER}", hasKiller ? killer.getDisplayName().asString(false) : "<dark_red><bold>Herobrine" );
		
		String messageKey = hasKiller ? killerMessageNode.replace("<TYPE>", damageCause.name().toLowerCase(Locale.ENGLISH)) : selfMessageNode.replace("<TYPE>", damageCause.name().toLowerCase(Locale.ENGLISH));

		if (assistsSize <= 1) {
			if (withHealthComponent && hasKiller) {
				for (TranslatablePlayer source : list) {
					MethodProvider.HoverBox hover = MethodProvider.SEND_HOVER_MESSAGE.get();
					hover.appendMessage(MessageCompiler.getMessage(ChefCore.getInstance(), source, messageKey, placeholder));
					Pair<MessageHolder, MessageHolder> component =  this.buildKillerHealthComponent(source, killer);
					hover.appendHoveredPart(component.getFirst(), component.getSecond());
					hover.send(source);
				}
			} else {
				for (TranslatablePlayer source : list) {
					source.sendMessage(MessageCompiler.getMessage(ChefCore.getInstance(), source, messageKey, placeholder));
				}
			}
		} else {
			
			Placeholder assitAmountPlaceholder = Placeholder.of("{AMOUNT}", assistsSize - 1);
			String assistFormatKey = "kill_messages.assistInfo.format" + (assistsSize - 1 <= 1 ? "Single" : "Multiple");
			
			if (withHealthComponent && hasKiller) {
				for (TranslatablePlayer source : list) {
					MessageHolder holder = MessageCompiler.getMessage(ChefCore.getInstance(), source, messageKey, placeholder);
					MessageHolder assists = MessageCompiler.getMessage(ChefCore.getInstance(), source, assistFormatKey, assitAmountPlaceholder);
					
					MethodProvider.HoverBox hover = MethodProvider.SEND_HOVER_MESSAGE.get();
					hover.appendMessage(holder);
					hover.appendHoveredPart(assists, this.createAssistHoverBox(damageTracker, source, killer));
					Pair<MessageHolder, MessageHolder> component =  this.buildKillerHealthComponent(source, killer);
					hover.appendHoveredPart(component.getFirst(), component.getSecond());
					hover.send(source);
				}
			} else {
				for (TranslatablePlayer source : list) {
					MessageHolder holder = MessageCompiler.getMessage(ChefCore.getInstance(), source, messageKey, placeholder);
					MessageHolder assists = MessageCompiler.getMessage(ChefCore.getInstance(), source, assistFormatKey, assitAmountPlaceholder);
					
					MethodProvider.HoverBox hover = MethodProvider.SEND_HOVER_MESSAGE.get();
					hover.appendMessage(holder);
					hover.appendHoveredPart(assists, this.createAssistHoverBox(damageTracker, source, killer));
					hover.send(source);
				}
			}
		}
		
		if (killer != null) {
			this.sendActionBarKillMessage(killerGameReward, killer, victimDisplayName, damageCause, false);
			killerGameReward.callActionIfPresent(killer);
		}
		
		for (TranslatablePlayer assistor : damageTracker.getAssists()) {
			if (killer != assistor) {
				this.sendActionBarKillMessage(assitGameReward, assistor, victimDisplayName, damageCause, true);
				assitGameReward.callActionIfPresent(assistor);
			}
		}
	}
	
	public MessageHolder createAssistHoverBox(DamageTracker damageTracker, TranslatablePlayer source, TranslatablePlayer killer) {
		Map<TranslatablePlayer, Float> map = damageTracker.getAssistedPlayersDamagePercent();
		float killerPercent = map.containsKey(killer) ? map.get(killer) : 0.0F;
		
		List<String> lines = MessageCompiler.getPlainMessageAsList(ChefCore.getInstance(), source, "kill_messages.assistInfo.hoverBox",
				Placeholder.of("{KILLER}", killer != null ? killer.getDisplayName().asString(false) : "<dark_red><bold>Herobrine").add("{KILLER_PERCENT}", SimpleMath.formatNumber(killerPercent)));

		List<String> newLines = new ArrayList<>();
		int index = lines.indexOf("{ASSISTS}");
		
		if (index != -1) {
			for (int j = 0; j < index; j++) {
				newLines.add(lines.get(j));
			}
			for (Entry<TranslatablePlayer, Float> entry : map.entrySet()) {
				TranslatablePlayer assistor = entry.getKey();

				if (assistor.equals(killer)) {
					continue;
				}

				newLines.add(MessageCompiler.getPlainMessage(ChefCore.getInstance(), source, "kill_messages.assistInfo.boxFormat")
						.replace("{PLAYER}", assistor.getDisplayName().asString(false))
						.replace("{PERCENT}", SimpleMath.formatNumber(entry.getValue())));
			}
			for (int k = index + 1; k < lines.size(); k++) {
				newLines.add(lines.get(k));
			}
			
			lines = newLines;
		}
		
		lines.replaceAll(text ->
			text.replace("{KILLER_FORMAT}",  MessageCompiler.getPlainMessage(ChefCore.getInstance(), source, "kill_messages.killer"))
			.replace("{ASSIST_FORMAT}",  MessageCompiler.getPlainMessage(ChefCore.getInstance(), source, "kill_messages.assist" + (map.size() - 1 <= 1 ? "" : "s"))));
		
		return MessageHolder.texts(lines);
	}
	
	public void sendActionBarKillMessage(GameReward gameReward, TranslatablePlayer source, MessageHolder victim, DamageCause cause, boolean assist) {
		ChefCore core = ChefCore.getInstance();

		Placeholder placeholder = Placeholder.of("{KILL_SYMBOL}", EventSymbols.getInstance().getDamageSymbolByCause(cause));
		placeholder.add("{VICTIM}", victim.asString(false));
		placeholder.add("{COINS}", MessageCompiler.getPlainMessage(core, source, "rewardFormats." + (gameReward.getCoins() > 1 ? "coins" : "coin"), Placeholder.of("{AMOUNT}", gameReward.getCoins()).add("{TYPE}", "")));
		placeholder.add("{EXP}", MessageCompiler.getPlainMessage(core, source, "rewardFormats." + (gameReward.getExp() > 1 ? "exps" : "exp"), Placeholder.of("{AMOUNT}", gameReward.getExp()).add("{TYPE}", "")));

		source.sendActionBar(MessageCompiler.getMessage(core, source, "kill_messages.actionBarMessage." + (assist ? "assisted" : "killed"), placeholder));
	}
	
	private MessageHolder compileDeathMessageForMenu(CorePlayer translatable, DamageCause cause, boolean self) {
		String exampleVictim = translatable.getPlainMessage("kill_messages.menu.exampleVictim");
		String exampleKiller = translatable.getPlainMessage("kill_messages.menu.exampleKiller", Placeholder.of("{PLAYER_NAME}", translatable.getDisplayName().asString(false)));
		Placeholder placeholder = Placeholder.of("{VICTIM}", exampleVictim).add("{KILLER}", exampleKiller);
		
		MessageHolder holder = translatable.getMessage(self ? 
				selfMessageNode.replace("<TYPE>", cause.name().toLowerCase(Locale.ENGLISH)) : 
				killerMessageNode.replace("<TYPE>", cause.name().toLowerCase(Locale.ENGLISH)), placeholder);
		
		return holder;
	}
	
	public MessageHolder compileDeathMessage(TranslationSource source, DamageCause cause, boolean self, Placeholder placeholder) {
		
		MessageHolder holder = source.getMessage(self ? 
				selfMessageNode.replace("<TYPE>", cause.name().toLowerCase(Locale.ENGLISH)) : 
				killerMessageNode.replace("<TYPE>", cause.name().toLowerCase(Locale.ENGLISH)), placeholder);
		
		return holder;
	}
	
	public Pair<MessageHolder, MessageHolder> buildKillerHealthComponent(TranslationSource source, TranslatablePlayer killer) {
		
		String wholeHealth = EventSymbols.getInstance().getHealthSymbolByType(EventSymbols.Health.WHOLE);
		String halfHealth = EventSymbols.getInstance().getHealthSymbolByType(EventSymbols.Health.HALF);
		
		final double killerHealthFinal = killer.getPlayer().getHealth();
		double killerHealth = killerHealthFinal;
		String format = "";
		
		while (killerHealth > 0.0D) {
			
			if (killerHealth < 1.0D) {
				format = format + halfHealth;
			} else {
				format = format + wholeHealth;
			}
			
			killerHealth -= 2.0D;
		}
		
		Placeholder placeholder = Placeholder.of("{KILLER_HEALTH}", SimpleMath.formatNumber(killerHealthFinal)).add("{HEART_SYMBOL}", wholeHealth)
				.add("{KILLER}", killer.getDisplayName().asString(false)).add("{HEALTH_BAR}", format);
																				//Box
		return Pair.of(
				MessageCompiler.getMessage(ChefCore.getInstance(), source, "kill_messages.chatHealthFormat", placeholder), //Text	
				MessageCompiler.getMessage(ChefCore.getInstance(), source, "kill_messages.healthBoxFormat", placeholder)); //Box
	}
	
	public @NotNull String getTranslatedName(TranslationSource source) {
		return source.getPlainMessage(this.nameKey);
	}
	
	public @NotNull String getTranslatedLore(TranslationSource source) {
		return source.getPlainMessage(this.loreKey);
	}

	public YamlFile getYamlFile() {
		return yamlFile;
	}

	public String getNamespaceID() {
		return namespaceID;
	}

	public String getPrefixKey() {
		return prefixKey;
	}

	public ItemStack getMenuItem() {
		return menuItem;
	}

	public String getNameKey() {
		return nameKey;
	}

	public String getLoreKey() {
		return loreKey;
	}

	@Override
	public CoreRarity getRarity() {
		return rarity;
	}

	public void setNamespaceID(String namespaceID) {
		this.namespaceID = namespaceID;
	}

	public void setPrefixKey(String prefixKey) {
		this.prefixKey = prefixKey;
	}

	public void setMenuItem(ItemStack menuItem) {
		this.menuItem = menuItem;
	}

	public void setNameKey(String nameKey) {
		this.nameKey = nameKey;
	}

	public void setLoreKey(String loreKey) {
		this.loreKey = loreKey;
	}

	public void setRarity(CoreRarity rarity) {
		this.rarity = rarity;
	}

	public String getSelfMessageNode() {
		return selfMessageNode;
	}

	public String getKillerMessageNode() {
		return killerMessageNode;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof KillMessages) {
			return ((KillMessages) o).namespaceID.equalsIgnoreCase(this.namespaceID);
		} else {
			return false;
		}
	}
}
