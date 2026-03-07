package net.chefcraft.world.util;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.PluginInstance;
import net.chefcraft.reflection.world.CoreMaterial;
import net.chefcraft.reflection.world.GameReflections;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {

	private static final GameReflections GAME_REFLECTOR = ChefCore.getReflector().getGameReflections();
	
	public static boolean isEmpty(ItemStack itemStack) {
		return GAME_REFLECTOR.isItemStackEmpty(itemStack);
	}

	public static PotionEffect deserializePotionEffectFromString(String s) {
		String[] arr = s.split(";");
		//int duration, int amplifier, boolean ambient, boolean particles, boolean icon
		PotionEffectType type = GAME_REFLECTOR.getPotionEffectTypeByVanillaName(arr[0]);
		PotionEffect effect = GAME_REFLECTOR.createPotionEffect(type, Integer.parseInt(arr[1]), Integer.parseInt(arr[2]),
				Boolean.parseBoolean(arr[3]), Boolean.parseBoolean(arr[4]), null);
		return effect;
	}

	public static List<PotionEffect> deserializePotionEffectFromList(List<String> dataList) {
		List<PotionEffect> pEffect = new ArrayList<>();
		for (String s : dataList) {
			pEffect.add(deserializePotionEffectFromString(s));
		}
		return pEffect;
	}

	public static Color[] deserializeColorFromListString(List<String> codes) throws NumberFormatException, IllegalArgumentException {
		Color[] result = new Color[codes.size()];

		for (int i = 0; i < codes.size(); i++) {
			result[i] = Color.fromRGB(Integer.decode(codes.get(i)));
		}

		return result;
	}

	public static ItemStack deserializeItemStackFromConfigurationSection(PluginInstance plugin, ConfigurationSection section) {
		try {
			ItemStack item = CoreMaterial.matchByName(section.getString("material")).toItemStack();

			if (section.isSet("amount")) {
				item.setAmount(Math.max(section.getInt("amount"), 1));
			}
			
			if (section.isSet("durability") && section.getInt("durability") > -1) {
				item.setDurability((short) section.getInt("durability"));
			}
			
			ItemMeta meta = item.getItemMeta();
			List<String> itemFlags = section.getStringList("itemFlags");
			
			if (itemFlags != null && !itemFlags.isEmpty()) {
				if (itemFlags.contains("ALL") || itemFlags.contains("all")) {
					meta.addItemFlags(ItemFlag.values());
				} else {
					for (String flag : itemFlags) {
						try {
							meta.addItemFlags(ItemFlag.valueOf(flag));
						} catch (Exception x) {
							plugin.sendPlainMessage("&cAn error occured parsing the item's flag: &e" + section + " &c(You can ignore or fix this)", x);
						}
					}
				}
			}
			
			List<String> enchants = section.getStringList("enchants");
			if (enchants != null && !enchants.isEmpty()) {
				for (String ench : enchants) {
					String[] e = ench.split(";");
					Enchantment enchValue = GAME_REFLECTOR.getEnchantmentByVanillaName(e[0]);
					meta.addEnchant(enchValue == null ? Enchantment.LURE : enchValue, Integer.valueOf(e[1]), true);
				}
			}

			if (section.isSet("unbreakable")) {
				ChefCore.getReflector().setItemUnbreakable(meta, true);
			}

			if (section.isSet("leatherColor") && GAME_REFLECTOR.isLeatherArmor(item.getType())) {
				LeatherArmorMeta leatherMeta = (LeatherArmorMeta) meta;
				try {
					leatherMeta.setColor(Color.fromRGB(Integer.decode(section.getString("leatherColor"))));
				} catch (Exception x) {
					plugin.sendPlainMessage("&eLeather Armor Color parse error", x);
				}
			}

			if (section.isSet("fireworkMeta") && meta != null && FireworkMeta.class.isAssignableFrom(meta.getClass())) {
				FireworkMeta fireworkMeta = (FireworkMeta) meta;
				FireworkEffect.Builder builder = FireworkEffect.builder();
				ConfigurationSection fwMeta = section.getConfigurationSection("fireworkMeta");
				if (fwMeta.isSet("type")) {
					try {
						builder.with(FireworkEffect.Type.valueOf(fwMeta.getString("type")));
					} catch (Exception x) {
						plugin.sendPlainMessage("&eFirework Effect type parse error", x);
					}
				}

				if (fwMeta.isSet("flicker")) {
					try {
						builder.flicker(fwMeta.getBoolean("flicker"));
					} catch (Exception x) {
						plugin.sendPlainMessage("&eFirework Effect flicker parse error", x);
					}
				}

				if (fwMeta.isSet("trail")) {
					try {
						builder.trail(fwMeta.getBoolean("trail"));
					} catch (Exception x) {
						plugin.sendPlainMessage("&eFirework Effect trail parse error", x);
					}
				}

				if (fwMeta.isSet("colors")) {
					try {
						builder.withColor(deserializeColorFromListString(fwMeta.getStringList("colors")));
					} catch (Exception x) {
						plugin.sendPlainMessage("&eFirework Effect colors parse error", x);
					}
				}

				if (fwMeta.isSet("fades")) {
					try {
						builder.withFade(deserializeColorFromListString(fwMeta.getStringList("fades")));
					} catch (Exception x) {
						plugin.sendPlainMessage("&eFirework Effect fades parse error", x);
					}
				}

				fireworkMeta.addEffect(builder.build());

				item.setItemMeta(fireworkMeta);
			}
			
			if (section.isSet("skullValue") && SkullMeta.class.isAssignableFrom(meta.getClass())) {
				
				String value = section.getString("skullValue");
				if (value != null && !value.isEmpty()) {
					item.setItemMeta(ChefCore.getReflector().applyCustomValueToSkullMeta(((SkullMeta) meta), value));
				}
				
			}

			if (meta != null && PotionMeta.class.isAssignableFrom(meta.getClass())) {
				
				if (section.isSet("potionMeta")) {
					PotionMeta potionMeta = (PotionMeta) meta;
					String[] dat = section.getString("potionMeta").split(";");
					potionMeta.clearCustomEffects();

					PotionEffectType effType = GAME_REFLECTOR.getPotionEffectTypeByVanillaName(dat[0]);

					GAME_REFLECTOR.addCustomPotionEffect(potionMeta, effType, Integer.valueOf(dat[1]), Integer.valueOf(dat[2]), Boolean.valueOf(dat[3]),
							Boolean.valueOf(dat[4]), null);

					GAME_REFLECTOR.potionMetaSetColorAndMainEffectLegacy(potionMeta, Color.fromRGB(Integer.decode(dat[5])), effType);
					item.setItemMeta(potionMeta);
				}

				if (section.isSet("potionType")) {
					PotionMeta potionMeta = (PotionMeta) meta;
					PotionEffectType effType = GAME_REFLECTOR.getPotionEffectTypeByVanillaName(section.getString("potionType"));
					GAME_REFLECTOR.setBasePotionType(potionMeta, PotionType.getByEffect(effType));
					item.setItemMeta(potionMeta);
				}
			} else {
				item.setItemMeta(meta);
			}
			
			return item;
		} catch (Exception x) {
			plugin.sendPlainMessage("&cAn error occured parsing the item: &e" + section, x);
			return new ItemStack(Material.STONE);
		}
	}
}
