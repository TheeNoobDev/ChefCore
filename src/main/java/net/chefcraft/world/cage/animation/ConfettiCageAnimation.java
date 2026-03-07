package net.chefcraft.world.cage.animation;

import net.chefcraft.world.cage.CageAnimation;
import net.chefcraft.world.particle.CoreParticle;
import net.chefcraft.world.util.DustColor;
import org.bukkit.Location;
import org.bukkit.World;

public class ConfettiCageAnimation extends AbstractCageAnimation {
	
	private double particleAngle = 0.0D;
	private double speed = Math.toRadians(20.0D);
	private double verticalOffset = 0.0D;
	private double verticalSpeed = 0.08D;
	private double maxYOffset = 1.0D;
	private DustColor randomColor = DustColor.random();

	public ConfettiCageAnimation() {
		super(CageAnimation.CONFETTI);
	}
	
	@Override
	public void prepare() {  }

	@Override
	public void tick(World world) {
		
		double x = radius * Math.cos(particleAngle);
        double z = radius * Math.sin(particleAngle);

        for (int i = 0; i < 20; i++) {
        	Location loc = super.boundingBox.getRandomLocation(world);
        	
        	CoreParticle.DUST.spawnParticle(loc.getWorld(), x + loc.getBlockX(), verticalOffset + loc.getBlockY(), z + loc.getBlockZ(), 0, randomColor.nextColor());
        }
        
        particleAngle += speed;

        if (particleAngle >= PIx2) {
        	particleAngle = 0;
        }

        verticalOffset += verticalSpeed;

        if (verticalOffset >= maxYOffset) {
        	verticalOffset = 0;
        }

	}
	
	public ConfettiCageAnimation clone() {
		ConfettiCageAnimation anim = new ConfettiCageAnimation();
		anim.interval = super.interval;
		anim.animationType = super.animationType;
		anim.radius = super.radius;
		return anim;
	}
	
}
