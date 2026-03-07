package net.chefcraft.reflection.base.scoreboard.custom;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.chefcraft.core.component.CoreTextBase;
import net.chefcraft.core.component.CoreTextStyle;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.world.scoreboard.CoreScoreboardOption;
import net.chefcraft.world.scoreboard.custom.CoreTeamHolder;

public class ChefTeamHolder implements CoreTeamHolder {
	
	private CoreTextBase textModifier = CoreTextStyle.RESET;
	private CoreScoreboardOption collisionRule = CoreScoreboardOption.FOR_OWN_TEAM;
	private CoreScoreboardOption nameTagVisibility = CoreScoreboardOption.ALWAYS;
	private CoreScoreboardOption deathMessageVisibility = CoreScoreboardOption.ALWAYS;
	private final String name;
	private MessageHolder displayName;
	private MessageHolder prefix = MessageHolder.empty();
	private MessageHolder suffix = MessageHolder.empty();
	private boolean allowFriendlyFire = false;
	private boolean canSeeFriendlyInvisibles = true;
	private Collection<String> entryCollection;
	
	public ChefTeamHolder(String name) {
		this.name = name;
		this.displayName = MessageHolder.text(name);
		this.entryCollection = Lists.newArrayList();
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public MessageHolder getDisplayName() {
		return this.displayName;
	}

	@Override
	public CoreTextBase getTextModifier() {
		return this.textModifier;
	}

	@Override
	public CoreScoreboardOption getCollisionRule() {
		return this.collisionRule;
	}

	@Override
	public CoreScoreboardOption getNameTagVisibility() {
		return this.nameTagVisibility;
	}

	@Override
	public MessageHolder getPrefix() {
		return prefix;
	}

	@Override
	public MessageHolder getSuffix() {
		return suffix;
	}
	
	@Override
	public void setDisplayName(MessageHolder displayName) {
		this.displayName = displayName;
	}

	@Override
	public void setTextModifier(CoreTextBase textModifier) {
		this.textModifier = textModifier;
	}

	@Override
	public void setCollisionRule(CoreScoreboardOption collisionRule) {
		this.collisionRule = collisionRule;
	}

	@Override
	public void setNameTagVisibility(CoreScoreboardOption nameTagVisibility) {
		this.nameTagVisibility = nameTagVisibility;
	}

	@Override
	public void setPrefix(MessageHolder prefix) {
		this.prefix = prefix;
	}

	@Override
	public void setSuffix(MessageHolder suffix) {
		this.suffix = suffix;
	}
	
	@Override
	public boolean isAllowFriendlyFire() {
		return allowFriendlyFire;
	}

	@Override
	public boolean canSeeFriendlyInvisibles() {
		return canSeeFriendlyInvisibles;
	}
	
	@Override
	public void setAllowFriendlyFire(boolean allowFriendlyFire) {
		this.allowFriendlyFire = allowFriendlyFire;
	}

	@Override
	public void setSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
		this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
	}
	
	@Override
	public CoreScoreboardOption getDeathMessageVisibility() {
		return deathMessageVisibility;
	}

	@Override
	public void setDeathMessageVisibility(CoreScoreboardOption deathMessageVisibility) {
		this.deathMessageVisibility = deathMessageVisibility;
	}
	
	@Override
	public void addEntry(String... entries) { 
		for (int i = 0; i < entries.length; i++) {
			if (entryCollection.contains(entries[i])) continue;
			entryCollection.add(entries[i]);
		}
	}
	
	@Override
	public void addEntry(List<String> entryList) {
		for (int i = 0; i < entryList.size(); i++) {
			String name = entryList.get(i);
			if (entryCollection.contains(name)) continue;
			entryCollection.add(name);
		}
	}
	
	@Override
	public void removeEntry(String... entries) {
		for (int i = 0; i < entries.length; i++) {
			if (entryCollection.contains(entries[i])) continue;
			entryCollection.remove(entries[i]);
		}
	}
	
	@Override
	public void removeEntry(List<String> entryList) {
		for (int i = 0; i < entryList.size(); i++) {
			String name = entryList.get(i);
			if (entryCollection.contains(name)) continue;
			entryCollection.remove(name);
		}
	}
	
	@Override
	public Collection<String> getEntries() {
		return ImmutableList.copyOf(entryCollection);
	}
	
	@Override
	public boolean hasEntry(String entry) {
		return entryCollection.contains(entry);
	}
	
	public ChefTeamHolder cloneWithNewID(String tagID) {
		ChefTeamHolder newHolder = new ChefTeamHolder(tagID);
		newHolder.allowFriendlyFire = this.allowFriendlyFire;
		newHolder.textModifier = this.textModifier;
		newHolder.canSeeFriendlyInvisibles = this.canSeeFriendlyInvisibles;
		newHolder.collisionRule = this.collisionRule;
		newHolder.deathMessageVisibility = this.deathMessageVisibility;
		newHolder.displayName = this.displayName;
		newHolder.entryCollection.addAll(this.entryCollection);
		newHolder.nameTagVisibility = this.nameTagVisibility;
		newHolder.prefix = this.prefix;
		newHolder.suffix = this.suffix;
		return newHolder;
	}
}
