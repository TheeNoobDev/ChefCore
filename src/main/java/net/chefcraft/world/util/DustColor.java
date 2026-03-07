package net.chefcraft.world.util;

import net.chefcraft.world.particle.CoreParticleData;
import org.bukkit.Color;

public class DustColor {
	
	private static final CoreParticleData[] DUST_COLORS = new CoreParticleData[] {
			CoreParticleData.fromDustOptions(Color.RED, 1.0F),
			CoreParticleData.fromDustOptions(Color.AQUA, 1.0F),
			CoreParticleData.fromDustOptions(Color.YELLOW, 1.0F),
			CoreParticleData.fromDustOptions(Color.GREEN, 1.0F),
			CoreParticleData.fromDustOptions(Color.FUCHSIA, 1.0F),
			CoreParticleData.fromDustOptions(Color.LIME, 1.0F),
			CoreParticleData.fromDustOptions(Color.OLIVE, 1.0F),
			CoreParticleData.fromDustOptions(Color.ORANGE, 1.0F),
			CoreParticleData.fromDustOptions(Color.PURPLE, 1.0F),
			CoreParticleData.fromDustOptions(Color.WHITE, 1.0F),
			CoreParticleData.fromDustOptions(Color.TEAL, 1.0F),
			CoreParticleData.fromDustOptions(Color.SILVER, 1.0F)	
	};
	
	private int nextColor = -1;
	
	public CoreParticleData nextColor() {
		nextColor++;
		int i = nextColor == 12 ? nextColor = 0 : nextColor; 
		return DUST_COLORS[i];
	}
	
	public static DustColor random() {
		return new DustColor();
	}
}
