package net.chefcraft.world.cage;

import net.chefcraft.core.configuration.YamlFile;
import net.chefcraft.core.language.TranslationSource;
import net.chefcraft.core.util.JHelper;
import net.chefcraft.reflection.world.CoreMaterial;
import net.chefcraft.world.cage.CageLayer.CageBlock;
import net.chefcraft.world.cage.animation.AbstractCageAnimation;
import net.chefcraft.world.rarity.CoreRarity;
import net.chefcraft.world.rarity.Evaluable;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public class Cage implements Evaluable {
	
	private static final Function<CageBlock, String> FUNC_CAGE_BLOCK = (cg) -> cg.toString();

	private Map<Integer, CageLayer> cageLayerMap = new HashMap<>();
	private final String namespaceID;
	private String permission = "";
	private String prefixKey = null;
	private CoreRarity rarity = CoreRarity.COMMON;
	private int posModX = 0, posModY = 0, posModZ = 0;
	private ItemStack menuItem = null;
	private String menuItemNameNode = null;
	private String menuItemLoreNode = null;
	private AbstractCageAnimation animation = null;
	private boolean animated = false;
	
	public Cage(String namespaceID) {
		this.namespaceID = namespaceID;
	}
	
	public @NotNull String getTranslatedName(TranslationSource source) {
		return source.getPlainMessage(this.menuItemNameNode);
	}
	
	public @NotNull String getTranslatedLore(TranslationSource source) {
		return source.getPlainMessage(this.menuItemLoreNode);
	}

	public Map<Integer, CageLayer> getCageLayerMap() {
		return cageLayerMap;
	}

	public String getPrefixKey() {
		return prefixKey;
	}

	public void setPrefixKey(String prefixKey) {
		this.prefixKey = prefixKey;
	}

	public String getNamespaceID() {
		return namespaceID;
	}

	@Override
	public CoreRarity getRarity() {
		return rarity;
	}

	public void setRarity(CoreRarity rarity) {
		this.rarity = rarity;
	}
	
	public Location fixLocation(Location loc) {
		return loc.add(posModX, posModY, posModZ);
	}
	
	public void setPositionModifier(int x, int y, int z) {
		this.posModX = x;
		this.posModY = y;
		this.posModZ = z;
	}

	public ItemStack getMenuItem() {
		return menuItem;
	}

	public void setMenuItem(ItemStack menuItem) {
		this.menuItem = menuItem;
	}

	public AbstractCageAnimation getAnimation() {
		return animation;
	}

	public void setAnimation(AbstractCageAnimation animation) {
		this.animation = animation;
	}

	public String getMenuItemNameNode() {
		return menuItemNameNode;
	}

	public void setMenuItemNameNode(String menuItemNameNode) {
		this.menuItemNameNode = menuItemNameNode;
	}

	public String getMenuItemLoreNode() {
		return menuItemLoreNode;
	}

	public void setMenuItemLoreNode(String menuItemLoreNode) {
		this.menuItemLoreNode = menuItemLoreNode;
	}
	
	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}
	
	public static Cage loadFromConfig(YamlFile file) {
		FileConfiguration config = file.getConfig();
		Cage cage = new Cage(config.getString("namespaceID"));
		cage.setPrefixKey(config.getString("prefixKey"));
		cage.setRarity(CoreRarity.valueOf(config.getString("rarity").toUpperCase(Locale.ENGLISH)));
		String[] pos = config.getString("positionModifier").split(";");
		cage.setPositionModifier(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]), Integer.parseInt(pos[2]));
		cage.setMenuItem(CoreMaterial.matchByName(config.getString("menuItem.material")).toItemStack());
		cage.setMenuItemNameNode(config.getString("menuItem.nameKey").replace("<KEY>", cage.prefixKey));
		cage.setMenuItemLoreNode(config.getString("menuItem.loreKey").replace("<KEY>", cage.prefixKey));
		cage.setAnimated(config.getBoolean("animated"));
		cage.setPermission(config.getString("permission"));
		
		if (cage.isAnimated()) {
			AbstractCageAnimation anim = AbstractCageAnimation.getAnimationByType(CageAnimation.valueOf(config.getString("animation.type").toUpperCase(Locale.ENGLISH)));
			if (anim != null) {
				anim.setInterval(config.getInt("animation.interval"));
				anim.setRadius(config.getDouble("animation.radius"));
				cage.setAnimation(anim);
			}
		}
		
		ConfigurationSection section = config.getConfigurationSection("layers");
		
		for (String str : section.getKeys(false)) {
			int layer = Integer.parseInt(str);
			CageLayer cl = new CageLayer(layer);

			for (String data : section.getStringList(str)) {
				cl.addCageBlock(CageLayer.stringToCageBlock(data));
			}
			
			cage.getCageLayerMap().put(layer, cl);
			
		}
		
		CageUtils.registerCage(cage);
		return cage;
	}
	
	public void saveToConfig(YamlFile file) {
		FileConfiguration config = file.getConfig();
		config.set("namespaceID", this.namespaceID);
		config.set("prefixKey", this.prefixKey);
		config.set("rarity", this.rarity.name());
		config.set("positionModifier", posModX + ";" + posModY + ";" + posModZ);
		CoreMaterial mat = menuItem == null ? null : CoreMaterial.toCore(menuItem.getType());
		config.set("menuItem.material", mat == null ? "NETHER_STAR" : mat);
		config.set("menuItem.nameKey", this.menuItemNameNode);
		config.set("menuItem.loreKey", this.menuItemLoreNode);
		config.set("animated", this.animated);
		config.set("permission", this.permission);
		
		if (this.animated && this.animation != null) {
			config.set("animation.radius", this.animation.getRadius());
			config.set("animation.interval", this.animation.getInterval());
			config.set("animation.type", this.animation.getAnimationType().name());
		} else {
			config.set("animation.radius", 0.2D);
			config.set("animation.interval", 10);
			config.set("animation.type", "NONE");
		}
		
		for (CageLayer cageLayer : cageLayerMap.values()) {
			
			config.set("layers." + cageLayer.getLayer(), JHelper.listToListString(cageLayer.getCageBlockList(), FUNC_CAGE_BLOCK));
		}
		
		file.save();
	}

	public boolean isAnimated() {
		return animated;
	}

	public void setAnimated(boolean animated) {
		this.animated = animated;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof Cage) {
			return ((Cage) o).namespaceID.equalsIgnoreCase(this.namespaceID);
		} else {
			return false;
		}
	}
}
