package net.chefcraft.core.util;

/** @since 1.2.2*/
public interface ActivationRange {
	
	static final ActivationRange INFINITE = fromDistanceSquared(-1.0D);

	double getDistanceSquared();
	
	default double getDistance() {
		return Math.sqrt(this.getDistanceSquared());
	}
	
	default boolean isInfinite() {
		return this.getDistanceSquared() <= 0.0D;
	}
	
	static ActivationRange fromDistance(final double distance) {
		return new ActivationRangeImpl(distance * distance);
	}
	
	static ActivationRange fromDistanceSquared(final double distanceSquared) {
		return new ActivationRangeImpl(distanceSquared);
	}
	
	class ActivationRangeImpl implements ActivationRange {
		
		final double distanceSquared;
		
		ActivationRangeImpl(double distanceSquared) {
			this.distanceSquared = distanceSquared;
		}

		@Override
		public double getDistanceSquared() {
			return this.distanceSquared;
		}
	}
}
