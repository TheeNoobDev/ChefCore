package net.chefcraft.world.cage.animation;

import net.chefcraft.world.cage.CageAnimation;
import net.chefcraft.world.particle.CoreParticle;
import org.bukkit.Location;
import org.bukkit.World;

public class TotemCageAnimation extends AbstractCageAnimation {

	public TotemCageAnimation() {
		super(CageAnimation.TOTEM);
	}
	
	@Override
	public void prepare() {  }

	@Override
	public void tick(World world) {
		
        for (int i = 0; i < 10; i++) {
        	Location loc = super.boundingBox.getRandomLocation(world);
        	CoreParticle.TOTEM_OF_UNDYING.spawnParticle(loc.getWorld(), loc, 2);
        }
	}
	
	public TotemCageAnimation clone() {
		TotemCageAnimation anim = new TotemCageAnimation();
		anim.interval = super.interval;
		anim.animationType = super.animationType;
		anim.radius = super.radius;
		return anim;
	}
}
