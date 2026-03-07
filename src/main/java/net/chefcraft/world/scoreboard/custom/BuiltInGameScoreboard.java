package net.chefcraft.world.scoreboard.custom;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.component.CoreTextColor;
import net.chefcraft.core.language.MessageCompiler;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslatablePlayer;
import net.chefcraft.core.math.SimpleMath;
import net.chefcraft.core.server.ServerVersion;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.reflection.world.GameReflections;
import net.chefcraft.world.scoreboard.CoreCriteria;
import net.chefcraft.world.scoreboard.CoreNumberFormat;
import net.chefcraft.world.scoreboard.CoreScoreRenderType;
import net.chefcraft.world.scoreboard.CoreScoreboardScore;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class BuiltInGameScoreboard extends AbstractGameScoreboard {

	private static final GameReflections GAME_REFLECTOR = ChefCore.getReflector().getGameReflections();
	private static final boolean NUMBER_FORMATTED_BAR = ServerVersion.current().isHigherThan(ServerVersion.v1_20_2);

	private final TranslatablePlayer translatablePlayer;
	private final Player bukkitPlayer;
	private final boolean useStyledHealthBar;
	private final Consumer<? super TranslatablePlayer> updateHealthBarConsumer;

	public double lastHealth = 0.0D;
	public double lastAbsorption = 0.0D;

	public BuiltInGameScoreboard(@NotNull TranslatablePlayer translatablePlayer, @NotNull Consumer<? super TranslatablePlayer> updateHealthBarConsumer) {
		super (translatablePlayer);
		this.bukkitPlayer = translatablePlayer.getPlayer();
		this.translatablePlayer = translatablePlayer;
		this.useStyledHealthBar = NUMBER_FORMATTED_BAR;
		this.updateHealthBarConsumer = updateHealthBarConsumer;
	}
	
	@NotNull
	public TranslatablePlayer getPlayer() {
		return this.translatablePlayer;
	}

	public void displayKillCounter() {
		super.createPlayerListObjective(MessageHolder.text("kills"), CoreCriteria.DUMMY, CoreScoreRenderType.INTEGER, CoreNumberFormat.STYLED.withTextBase(CoreTextColor.WHITE));
	}

	public void displayHealthBar(Iterable<? extends TranslatablePlayer> toPlayers) {
		
		if (useStyledHealthBar) {
			super.createBelowNameObjective(MessageHolder.empty(), CoreCriteria.DUMMY, CoreScoreRenderType.INTEGER, CoreNumberFormat.BLANK);
		} else {
			super.createBelowNameObjective(MessageCompiler.getMessage(ChefCore.getInstance(), translatablePlayer, "scoreboards.healthBars.classic"), CoreCriteria.DUMMY, CoreScoreRenderType.INTEGER, null);
		}
		
		toPlayers.forEach(inArena -> this.updateHealthBar(inArena));
	}

	public void updateHealthBarRaw(String scoreEntry, double health, double absorption) {
		if (super.belowName == null) return;

		CoreScoreboardScore score = this.belowName.getOrCreateScore(scoreEntry, true);
		score.setValue(Math.max((int) (health + absorption), 0));
		if (useStyledHealthBar) {

			CoreNumberFormat format = absorption > 0 ?

					CoreNumberFormat.STYLED.withFixed(MessageCompiler.getMessage(ChefCore.getInstance(), translatablePlayer, "scoreboards.healthBars.withAbsorption", Placeholder.of("{ABSORPTION}", absorption).add("{HEALTH}", health))) :

					CoreNumberFormat.STYLED.withFixed(MessageCompiler.getMessage(ChefCore.getInstance(), translatablePlayer, "scoreboards.healthBars.withoutAbsorption", Placeholder.of("{HEALTH}", health)));

			score.setNumberFormat(format);
		}
	}

	public void updateHealthBar(TranslatablePlayer translatablePlayer) {
		Player player = translatablePlayer.getPlayer();
		updateHealthBarRaw(player.getName(), SimpleMath.roundToHalfNumber(player.getHealth()), SimpleMath.roundToHalfNumber(GAME_REFLECTOR.getAbsorptionAmount(player)));
	}

	public void updateKillCount(TranslatablePlayer translatablePlayer, int kills) {
		this.playerList.getOrCreateScore(translatablePlayer.getPlayer().getName(), true).setValue(kills);
	}

	public void tickHealthBar(Iterable<? extends TranslatablePlayer> toPlayers) {
		double j = bukkitPlayer.getHealth();
		double k = GAME_REFLECTOR.getAbsorptionAmount(bukkitPlayer);

		if (this.lastHealth != j || this.lastAbsorption != k) {
			this.lastHealth = j;
			this.lastAbsorption = k;

			toPlayers.forEach(this.updateHealthBarConsumer);
		}
	}
}
