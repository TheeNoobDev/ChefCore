package net.chefcraft.core.component;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RandomColor {

	private final List<String> bukkitHex = new ArrayList<>();
	private final List<String> kyoriHex = new ArrayList<>();
	private int nextBukkit = -1;
	private int nextKyori = -1;

	public RandomColor() {
		int len = 16;
		
		this.setupGradients(CoreTextColor.DARK_PURPLE.color(), CoreTextColor.DARK_BLUE.color(), len);
		this.setupGradients(CoreTextColor.DARK_BLUE.color(), CoreTextColor.AQUA.color(), len);
		this.setupGradients(CoreTextColor.AQUA.color(), CoreTextColor.GREEN.color(), len);
		this.setupGradients(CoreTextColor.GREEN.color(), CoreTextColor.YELLOW.color(), len);
		this.setupGradients(CoreTextColor.YELLOW.color(), CoreTextColor.GOLD.color(), len);
		this.setupGradients(CoreTextColor.GOLD.color(), CoreTextColor.RED.color(), len);
		this.setupGradients(CoreTextColor.RED.color(), CoreTextColor.DARK_PURPLE.color(), len);
		
	}
	
	private void setupGradients(Color start, Color end, int length) {
        for (int i = 0; i < length; i++) {
        	
            int r = ComponentProvider.calculateInterpolate(start.getRed(), end.getRed(), i, length);
            int g = ComponentProvider.calculateInterpolate(start.getGreen(), end.getGreen(), i, length);
            int b = ComponentProvider.calculateInterpolate(start.getBlue(), end.getBlue(), i, length);
            
            this.bukkitHex.add(ComponentProvider.deserializeHexToMinecraft(r, g, b));
            this.kyoriHex.add(ComponentProvider.rgbToHex(r, g, b));
        }
    }
	
	public String nextBukkit() {
		nextBukkit++;
		int i = nextBukkit == bukkitHex.size() ? nextBukkit = 0 : nextBukkit;
		return bukkitHex.get(i);
	}
	
	public String nextKyori() {
		nextKyori++;
		int i = nextKyori == kyoriHex.size() ? nextKyori = 0 : nextKyori;
		return kyoriHex.get(i);
	}
}
