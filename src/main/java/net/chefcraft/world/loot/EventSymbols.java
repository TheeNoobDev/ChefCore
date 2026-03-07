package net.chefcraft.world.loot;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.annotation.InitializeBefore;
import net.chefcraft.core.configuration.YamlFile;
import net.chefcraft.core.util.Reloadable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Objects;

public class EventSymbols implements Reloadable {
	
	private static EventSymbols instance = null;
	
	private final YamlFile configurationFile;
	private final EnumMap<DamageCause, String> damageSymbolsByCause = new EnumMap<>(DamageCause.class);
	private final EnumMap<Health, String> healthSymbolsByName = new EnumMap<>(Health.class);
	
	public EventSymbols(@NotNull YamlFile configurationFile) {
		this.configurationFile = Objects.requireNonNull(configurationFile);
		this.load();
	}
	
	private void load() {
		FileConfiguration config = configurationFile.getConfig();
		ConfigurationSection section = config.getConfigurationSection("health_symbols");
		for (String key : section.getKeys(false)) {
			try {
				healthSymbolsByName.put(Health.valueOf(key), section.getString(key));
			} catch (Exception x) {
				ChefCore.getInstance().sendPlainMessage("An error occurred while register Health Symbol: " + key + " is not valid enum value", x);
			}
		}
		section = config.getConfigurationSection("kill_symbols");
		for (String key : section.getKeys(false)) {
			try {
				damageSymbolsByCause.put(DamageCause.valueOf(key), section.getString(key));
			} catch (Exception x) {
				ChefCore.getInstance().sendPlainMessage("An error occurred while register Damage Cause: " + key + " is not valid enum value", x);
			}
		}
	}
	
	@Override
	public void reload() {
		this.configurationFile.reload();
		this.load();
	}
	
	public String getDamageSymbolByCause(DamageCause cause) {
		String cuz = damageSymbolsByCause.get(cause);
		return cuz != null ? cuz : "⚔";
	}
	
	public String getHealthSymbolByType(Health healthType) {
		String cuz = healthSymbolsByName.get(healthType);
		return cuz != null ? cuz : "❤";
	}
	
	@Nullable
	@InitializeBefore
	public static EventSymbols getInstance() {
		return instance;
	}
	
	public static void setInstance(@NotNull EventSymbols instance) {
		EventSymbols.instance = Objects.requireNonNull(instance);
	}
	
	public static enum Health {
		EMPTY, HALF, WHOLE, ABSORPTION;
	}
}
