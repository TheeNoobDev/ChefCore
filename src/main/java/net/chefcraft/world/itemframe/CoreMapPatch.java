package net.chefcraft.world.itemframe;

import org.bukkit.map.MapPalette;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Arrays;

public class CoreMapPatch {

	public final int minX = 0;
	public final int minY = 0;
	public final int maxX = 128;
	public final int maxY = 128;
	public final byte[] colors;
	
	private CoreMapPatch() {
		this.colors = new byte[16384];
		Arrays.fill(this.colors, (byte) -1);
	}
	
	public static CoreMapPatch empty() {
		return new CoreMapPatch();
	}
	
	public static CoreMapPatch fromImage(@Nonnull Image image) {
		CoreMapPatch patch = new CoreMapPatch();

		byte[] bytes = MapPalette.imageToBytes(image);
        
        for (int x = 0; x < image.getWidth(null); ++x) {
            for (int y = 0; y < image.getHeight(null); ++y) {
            	patch.colors[x + y * 128] = bytes[y * image.getWidth(null) + x];
            }
        }
        
        return patch;
	}
}
