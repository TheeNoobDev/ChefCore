package net.chefcraft.world.cage.animation;

import net.chefcraft.core.math.BoundingBox;
import net.chefcraft.world.cage.CageAnimation;
import org.bukkit.World;

public abstract class AbstractCageAnimation {
	
	protected static final double PIx2 = Math.PI * 2;
	
	protected int interval = 20;
	protected CageAnimation animationType = CageAnimation.NONE;
	protected BoundingBox boundingBox;
	protected double radius = 0.2D;
	
	public AbstractCageAnimation(CageAnimation animationType) {
		this.animationType = animationType;
	}
	
	public abstract void tick(World world);
	
	public abstract void prepare();
	
	public abstract AbstractCageAnimation clone();

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public CageAnimation getAnimationType() {
		return animationType;
	}

	public void setAnimationType(CageAnimation animationType) {
		this.animationType = animationType;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public BoundingBox getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
	}
	
	public static AbstractCageAnimation getAnimationByType(CageAnimation type) {
		if (type == CageAnimation.CONFETTI) {
			return new ConfettiCageAnimation();
		} else if (type == CageAnimation.TOTEM) {
			return new TotemCageAnimation();
		} else {
			return null;
		}
	}
}
