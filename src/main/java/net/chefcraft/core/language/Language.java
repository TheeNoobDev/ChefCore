package net.chefcraft.core.language;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.chefcraft.core.PluginInstance;
import net.chefcraft.core.collect.ImmutableList;
import net.chefcraft.core.component.RandomColor;
import net.chefcraft.core.configuration.BaseConfigurationFile;
import net.chefcraft.core.party.CorePartyManager;
import net.chefcraft.core.util.JHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Language implements TranslationSource {

	private final String name;
	private final String locale;
	private final String region;
	private final Map<PluginInstance, BaseConfigurationFile> languageFileMap = new HashMap<>();
	private final RandomColor randomColor = new RandomColor();
	
	public Language(String name, String locale, String region) {
		this.name = name;
		this.locale = locale;
		this.region = region;
	}
	
	@Override
	@NullMarked
	public <A> A getAudience() {
		return null;
	}
	
	@Override
	public boolean isConsole() {
		return false;
	}
	
	@Override
	public Language getLanguage() {
		return this;
	}

	@Override
	public PluginInstance getPlugin() {
		return JHelper.getFirst(languageFileMap.keySet());
	}
	
	public String getName() {
		return this.name;
	}

	public String getLocale() {
		return this.locale;
	}

	public String getRegion() {
		return this.region;
	}
	
	public void addLanguageFile(PluginInstance plugin, BaseConfigurationFile configuration) {
		languageFileMap.put(plugin, configuration);
	}

	public BaseConfigurationFile getLanguageFile(PluginInstance plugin) {
		return languageFileMap.get(plugin);
	}
	
	public boolean hasLanguageFile(PluginInstance plugin) {
		return languageFileMap.containsKey(plugin);
	}
	
	public void removeYamlFile(PluginInstance plugin) {
		languageFileMap.remove(plugin);
	}
	
	@Unmodifiable
	public List<BaseConfigurationFile> getYamlFiles() {
		return ImmutableList.copyOf(languageFileMap.values());
	}
	
	@Override
	public RandomColor getRandomColor() {
		return this.randomColor;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		} else if (object instanceof Language && ((Language) object).locale.equalsIgnoreCase(this.locale) && ((Language) object).name.equalsIgnoreCase(this.name) && ((Language) object).region.equalsIgnoreCase(this.region)) {
			return true;
		} else if (object instanceof TranslationSource) {
			Language lang = ((TranslationSource) object).getLanguage();
			if (lang.locale.equalsIgnoreCase(this.locale) && lang.name.equalsIgnoreCase(this.name) && lang.region.equalsIgnoreCase(this.region)) {
				return true;
			}
			return false;
		}
		return false;
	}

	@Override
	@CanIgnoreReturnValue
	public @NotNull CorePartyManager getPartyManager() {
		return CorePartyManager.EMPTY;
	}
}
