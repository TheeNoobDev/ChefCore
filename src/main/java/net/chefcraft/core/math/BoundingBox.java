package net.chefcraft.core.math;

public class BoundingBox {
	
	protected static final java.util.Random RANDOM = new java.util.Random();

	public final double minX, minY, minZ, maxX, maxY, maxZ;
	
	private BoundingBox(BoundingBox box) {
		this.minX = box.minX;
        this.minY = box.minY;
        this.minZ = box.minZ;
        this.maxX = box.maxX;
        this.maxY = box.maxY;
        this.maxZ = box.maxZ;
	}
    
    public BoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.minZ = Math.min(minZ, maxZ);
        this.maxX = Math.max(minX, maxX);
        this.maxY = Math.max(minY, maxY);
        this.maxZ = Math.max(minZ, maxZ);
    }
    
    public BoundingBox(org.bukkit.Location first, org.bukkit.Location second) {
    	this(first.getX(), first.getY(), first.getZ(), second.getX(), second.getY(), second.getZ());
    }
    
    public BoundingBox(org.bukkit.Location center, double offsetX, double offsetY, double offsetZ) {
    	this.minX = center.getX() - offsetX;
        this.minY = center.getY() - offsetY;
        this.minZ = center.getZ() - offsetZ;
        this.maxX = center.getX() + offsetX;
        this.maxY = center.getY() + offsetY;
        this.maxZ = center.getZ() + offsetZ;
    }
    
    @javax.annotation.Nonnull
    public BoundingBox grow(double offsetX, double offsetY, double offsetZ) {
    	return new BoundingBox(minX - offsetX, minY - offsetY, offsetZ - minZ, maxX + offsetX, maxY + offsetY, maxZ + offsetZ);
    }
    
    @javax.annotation.Nonnull
    public BoundingBox grow(double offset) {
    	return this.grow(offset, offset, offset);
    }
    
    @javax.annotation.Nonnull
    public BoundingBox reduce(double offsetX, double offsetY, double offsetZ) {
    	return new BoundingBox(
    			minX < 0.0D ? minX + offsetX : minX - offsetX,
    			minY < 0.0D ? minY + offsetY : minY - offsetY,
    			minZ < 0.0D ? minZ + offsetZ : minZ - offsetZ,
    			maxX < 0.0D ? maxX + offsetX : maxX - offsetX,
    			maxY < 0.0D ? maxY + offsetY : maxY - offsetY,
    			maxZ < 0.0D ? maxZ + offsetZ : maxZ - offsetZ);
    }
    
    @javax.annotation.Nonnull
    public BoundingBox reduce(double offset) {
    	return this.reduce(offset, offset, offset);
    }
    
    public double getLengthX() {
    	return maxX - minX;
    }
    
    public double getLengthY() {
    	return maxY - minY;
    }

	public double getLengthZ() {
		return maxZ - minZ;
	}
	
	public double getSize() {
		return (maxX - minX) * (maxY - minY) * (maxZ - minZ);
	}
    
    public double getCenterX() {
    	return (maxX - minX) / 2 + minX;
    }
    
    public double getCenterY() {
    	return (maxY - minY) / 2 + minY;
    }

	public double getCenterZ() {
		return (maxZ - minZ) / 2 + minZ;
	}
	
	@javax.annotation.Nonnull
	public org.bukkit.Location toCenterLocation(@javax.annotation.Nonnull org.bukkit.World world) {
		return new org.bukkit.Location(world, this.getCenterX(), this.getCenterY(), this.getCenterZ());
	}
	
	@javax.annotation.Nonnull
	public org.bukkit.Location toMinLocation(@javax.annotation.Nonnull org.bukkit.World world) {
		return new org.bukkit.Location(world, minX, minY, minZ);
	}
	
	@javax.annotation.Nonnull
	public org.bukkit.Location toMaxLocation(@javax.annotation.Nonnull org.bukkit.World world) {
		return new org.bukkit.Location(world, maxX, maxY, maxZ);
	}
	
	public double getRandomX() {
		return RANDOM.nextInt(Math.max(Math.abs((int) this.getLengthX()) + 1, 0)) + minX;
	}
	
	public double getRandomY() {
		return RANDOM.nextInt(Math.max(Math.abs((int) this.getLengthY()) + 1, 0)) + minY;
	}
	
	public double getRandomZ() {
		return RANDOM.nextInt(Math.max(Math.abs((int) this.getLengthZ()) + 1, 0)) + minZ;
	}
	
	@javax.annotation.Nonnull
	public org.bukkit.Location getRandomLocation(@javax.annotation.Nonnull org.bukkit.World world) {
		return new org.bukkit.Location(world, this.getRandomX(), this.getRandomY(), this.getRandomZ());
	}
	
	public double distanceSquaredToCenter(double x, double y, double z) {
		return square(x - this.getCenterX()) + square(x - this.getCenterY()) + square(x - this.getCenterZ());
	}
	
	public double distanceSquaredToMin(double x, double y, double z) {
		return square(x - minX) + square(x - minY) + square(x - minZ);
	}
	
	public double distanceSquaredToMax(double x, double y, double z) {
		return square(x - maxX) + square(x - maxY) + square(x - maxZ);
	}
	
	public double distanceToCenter(double x, double y, double z) {
		return Math.sqrt(this.distanceSquaredToCenter(x, y, z));
	}
	
	public double distanceToMin(double x, double y, double z) {
		return Math.sqrt(this.distanceSquaredToMin(x, y, z));
	}
	
	public double distanceToMax(double x, double y, double z) {
		return Math.sqrt(this.distanceSquaredToMax(x, y, z));
	}
	
	public double distanceSquaredToCenter(org.bukkit.Location pos) {
		return square(pos.getX() - this.getCenterX()) + square(pos.getY() - this.getCenterY()) + square(pos.getZ() - this.getCenterZ());
	}
	
	public double distanceSquaredToMin(org.bukkit.Location pos) {
		return square(pos.getX() - minX) + square(pos.getY() - minY) + square(pos.getZ() - minZ);
	}
	
	public double distanceSquaredToMax(org.bukkit.Location pos) {
		return square(pos.getX() - maxX) + square(pos.getY() - maxY) + square(pos.getZ() - maxZ);
	}
	
	public double distanceToCenter(org.bukkit.Location pos) {
		return Math.sqrt(this.distanceSquaredToCenter(pos));
	}
	
	public double distanceToMin(org.bukkit.Location pos) {
		return Math.sqrt(this.distanceSquaredToMin(pos));
	}
	
	public double distanceToMax(org.bukkit.Location pos) {
		return Math.sqrt(this.distanceSquaredToMax(pos));
	}
	
	public boolean isIn(double x, double y, double z) {
        return x >= minX && x <= maxX && z >= minZ && z <= maxZ && y >= minY && y <= maxY;
    }
    
    public boolean isIn(org.bukkit.Location pos) {
        return this.isIn(pos.getX(), pos.getY(), pos.getZ());
    }
	
	public boolean isInWithIgnorePosY(double x, double z) {
        return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
    }
    
    public boolean isInWithIgnorePosY(org.bukkit.Location pos) {
    	return this.isInWithIgnorePosY(pos.getX(), pos.getZ());
    }
    
    public boolean isInWithMarge(double x, double y, double z, double marge) {
    	return x >= minX - marge && x <= maxX + marge && z >= minZ - marge && z <= maxZ + marge && y >= minY - marge && y <= maxY + marge;
    }
    
    public boolean isInWithMarge(org.bukkit.Location pos, double marge) {
    	return this.isInWithMarge(pos.getX(), pos.getY(), pos.getZ(), marge);
    }
    
    public boolean isInWithMargeIgnorePosY(double x, double z, double marge) {
    	return x >= minX - marge && x <= maxX + marge && z >= minZ - marge && z <= maxZ + marge;
    }
    
    public boolean isInWithMargeIgnorePosY(org.bukkit.Location pos, double marge) {
    	return this.isInWithMargeIgnorePosY(pos.getX(), pos.getZ(), marge);
    }
    
    @javax.annotation.Nonnull
    public java.util.List<org.bukkit.block.Block> listBlocks(@javax.annotation.Nonnull org.bukkit.World world, int marge, @javax.annotation.Nullable java.util.function.Predicate<org.bukkit.block.Block> predicate) {
    	
    	int minx = floor(minX) - marge, miny = floor(minY) - marge, minz = floor(minZ) - marge;
    	int maxx = floor(maxX) + marge, maxy = floor(maxY) + marge, maxz = floor(maxZ) + marge;
    	
    	java.util.List<org.bukkit.block.Block> list = new java.util.ArrayList<>();
    	
    	if (predicate == null) {
    		for (int x = minx; x <= maxx; x++) {
        		for (int y = miny; y <= maxy; y++) {
        			for (int z = minz; z <= maxz; z++) {
        				list.add(world.getBlockAt(x, y, z));
        	    	}
            	}
        	}
    	} else {
    		for (int x = minx; x <= maxx; x++) {
        		for (int y = miny; y <= maxy; y++) {
        			for (int z = minz; z <= maxz; z++) {
        				org.bukkit.block.Block block = world.getBlockAt(x, y, z);
        				if (predicate.test(block)) {
        					list.add(block);
        				}
        	    	}
            	}
        	}
    	}
    	return list;
    }
    
    @javax.annotation.Nonnull
    public java.util.List<org.bukkit.block.Block> listBlocks(@javax.annotation.Nonnull org.bukkit.World world, int marge) {
    	return this.listBlocks(world, marge, null);
    }
    
    @javax.annotation.Nonnull
    public java.util.List<org.bukkit.block.Block> listBlocks(@javax.annotation.Nonnull org.bukkit.World world, 
    		@javax.annotation.Nullable java.util.function.Predicate<org.bukkit.block.Block> predicate) {
    	return this.listBlocks(world, 0, predicate);
    }
    
    @javax.annotation.Nonnull
    public java.util.List<org.bukkit.block.Block> listBlocks(@javax.annotation.Nonnull org.bukkit.World world) {
    	return this.listBlocks(world, 0, null);
    }
    
    public void applyToInside(@javax.annotation.Nonnull java.util.function.Consumer<org.bukkit.util.Vector> function, double nextPositionOffset) {
    	
    	for (double x = minX; x <= maxX; x+= nextPositionOffset) {
            for (double y = minY; y <= maxY; y+= nextPositionOffset) {
                for (double z = minZ; z <= maxZ; z+= nextPositionOffset) {
                	function.accept(new org.bukkit.util.Vector(x, y, z));
                }
            }
        }
    }   
    
    public void applyToOutline(@javax.annotation.Nonnull java.util.function.Consumer<org.bukkit.util.Vector> function, double nextPositionOffset) {
    	
    	for (double x = minX; x <= maxX; x += nextPositionOffset) {
    		function.accept(new org.bukkit.util.Vector(x, minY, minZ));
    		function.accept(new org.bukkit.util.Vector(x, maxY, maxZ));
    		
    		function.accept(new org.bukkit.util.Vector(x, maxY, minZ));
    		function.accept(new org.bukkit.util.Vector(x, minY, maxZ));
    	}
    	
    	for (double y = minY; y <= maxY; y += nextPositionOffset) {
    		function.accept(new org.bukkit.util.Vector(minX, y, minZ));
    		function.accept(new org.bukkit.util.Vector(maxX, y, maxZ));
    		
    		function.accept(new org.bukkit.util.Vector(minX, y, maxZ));
    		function.accept(new org.bukkit.util.Vector(maxX, y, minZ));
    	}
    	
    	for (double z = minZ; z <= maxZ; z += nextPositionOffset) {
    		function.accept(new org.bukkit.util.Vector(minX, minY, z));
    		function.accept(new org.bukkit.util.Vector(maxX, maxY, z));
    		
    		function.accept(new org.bukkit.util.Vector(maxX, minY, z));
    		function.accept(new org.bukkit.util.Vector(minX, maxY, z));
    	}
    }
    
	
	@Override
	public BoundingBox clone() {
		return new BoundingBox(this);
	}
	
	@Override
	public String toString() {
		return "{minX=" + minX
				+ ", minY=" + minY
				+ ", minZ=" + minZ
				+ ", maxX=" + maxX
				+ ", maxY=" + maxY
				+ ", maxZ=" + maxZ
				+ "}";
	}
	
	protected static double square(double value) {
		return value * value;
	}
	
	protected static int floor(double num) {
        final int floor = (int) num;
        return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }
}
