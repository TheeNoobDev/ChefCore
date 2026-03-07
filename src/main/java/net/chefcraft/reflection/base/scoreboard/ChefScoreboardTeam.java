package net.chefcraft.reflection.base.scoreboard;

import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Sets;

import net.chefcraft.core.component.CoreTextBase;
import net.chefcraft.core.component.CoreTextStyle;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.world.scoreboard.CoreScoreboardOption;
import net.chefcraft.world.scoreboard.CoreScoreboardTeam;

public class ChefScoreboardTeam implements CoreScoreboardTeam {

	private final AbstractChefScoreboard scoreboard;
	private final String name;
	private final Set<String> entries = Sets.newHashSet();
	private MessageHolder displayName;
	private MessageHolder prefix = MessageHolder.empty();
	private MessageHolder suffix = MessageHolder.empty();
	private boolean allowFriendlyFire = true;
	private boolean seeFriendlyInvisibles = true;
	private CoreScoreboardOption nameTagVisibility = CoreScoreboardOption.ALWAYS;
	private CoreScoreboardOption deathMessageVisibility = CoreScoreboardOption.ALWAYS;
	private CoreScoreboardOption collisionRule = CoreScoreboardOption.FOR_OTHER_TEAMS;
	private CoreTextBase textModifier = CoreTextStyle.RESET;
	
	public ChefScoreboardTeam(AbstractChefScoreboard scoreboard, String name) {
		Objects.requireNonNull(name, "Scoreboard team name cannot be null!");
		this.scoreboard = scoreboard;
		this.name = name;
		this.displayName = MessageHolder.text(name);
	}
	
	public AbstractChefScoreboard getScoreboard() {
		return scoreboard;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public boolean hasEntry(String entry) {
		return this.entries.contains(entry);
	}

	@Override
	public Set<String> getEntries() {
		return entries;
	}
	
	@Override
	public MessageHolder getDisplayName() {
		return displayName;
	}

	@Override
	public void setDisplayName(MessageHolder displayName) {
		Objects.requireNonNull(displayName, "Team display name cannot be null!");
		
		this.displayName = displayName;
		this.scoreboard.onTeamChanged(this);
	}

	@Override
	public MessageHolder getPrefix() {
		return prefix;
	}

	@Override
	public void setPrefix(MessageHolder prefix) {
		this.prefix = prefix == null ? MessageHolder.empty() : prefix;
		this.scoreboard.onTeamChanged(this);
	}

	@Override
	public MessageHolder getSuffix() {
		return suffix;
	}

	@Override
	public void setSuffix(MessageHolder suffix) {
		this.suffix = suffix == null ? MessageHolder.empty() : suffix;
		this.scoreboard.onTeamChanged(this);
	}

	@Override
	public boolean isAllowFriendlyFire() {
		return allowFriendlyFire;
	}

	@Override
	public void setAllowFriendlyFire(boolean allowFriendlyFire) {
		this.allowFriendlyFire = allowFriendlyFire;
		this.scoreboard.onTeamChanged(this);
	}

	@Override
	public boolean canSeeFriendlyInvisibles() {
		return seeFriendlyInvisibles;
	}

	@Override
	public void setSeeFriendlyInvisibles(boolean seeFriendlyInvisibles) {
		this.seeFriendlyInvisibles = seeFriendlyInvisibles;
		this.scoreboard.onTeamChanged(this);
	}

	@Override
	public CoreScoreboardOption getNameTagVisibility() {
		return nameTagVisibility;
	}

	@Override
	public void setNameTagVisibility(CoreScoreboardOption nameTagVisibility) {
		this.nameTagVisibility = nameTagVisibility;
		this.scoreboard.onTeamChanged(this);
	}

	public CoreScoreboardOption getDeathMessageVisibility() {
		return deathMessageVisibility;
	}

	public void setDeathMessageVisibility(CoreScoreboardOption deathMessageVisibility) {
		this.deathMessageVisibility = deathMessageVisibility;
		this.scoreboard.onTeamChanged(this);
	}

	@Override
	public CoreScoreboardOption getCollisionRule() {
		return collisionRule;
	}

	@Override
	public void setCollisionRule(CoreScoreboardOption collisionRule) {
		this.collisionRule = collisionRule;
		this.scoreboard.onTeamChanged(this);
	}

	@Override
	public CoreTextBase getTextModifier() {
		return textModifier;
	}

	@Override
	public void setTextModifier(CoreTextBase textModifier) {
		this.textModifier = textModifier;
		this.scoreboard.onTeamChanged(this);
	}

	public int packOptions() {
        int i = allowFriendlyFire ? (0 | 1) : 0;
        return seeFriendlyInvisibles ? (i | 2) : i;
    }
	
	public void unpackOptions(int i) {
		this.allowFriendlyFire =  (i & 1) > 0;
		this.seeFriendlyInvisibles = (i & 2) > 0;
		this.scoreboard.onTeamChanged(this);
	}
}
