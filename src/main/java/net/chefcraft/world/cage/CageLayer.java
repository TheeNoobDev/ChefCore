package net.chefcraft.world.cage;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.util.JHelper;
import net.chefcraft.core.util.Pair;
import net.chefcraft.reflection.world.CoreMaterial;
import net.chefcraft.reflection.world.GameReflections;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CageLayer {
	
	private static final Function<MaterialData, String> FUNC_MATERIAL = (md) -> (CoreMaterial.toCore(md.getItemType()) + ":" + md.getData());
	private static final GameReflections REFLECTOR = ChefCore.getReflector().getGameReflections();
	
	private final int layer;
	private List<CageBlock> cageBlockList = new ArrayList<>();
	
	public CageLayer(int layer) {
		this.layer = layer;
	}

	public int getLayer() {
		return layer;
	}
	
	public void addCageBlock(CageBlock block) {
		cageBlockList.add(block);
	}
	
	public List<CageBlock> getCageBlockList() {
		return cageBlockList;
	}
	
	public void placeLayer(List<Location> effected, List<Pair<Block, CageBlock>> animatedCageBlockList,  World world, int x, int y, int z) {
		for (int i = 0; i < cageBlockList.size(); i++) {
			CageBlock cageBlock = cageBlockList.get(i);
			MaterialData data = cageBlock.nextMaterial();
			int a = cageBlock.getPlacement();
			Block block = world.getBlockAt(x + getPlacementX(a), y, z + getPlacementZ(a));
			REFLECTOR.setBlockType(block, data.getItemType(), data.getData());
			
			effected.add(block.getLocation());
			if (cageBlock.isAnimated()) {
				animatedCageBlockList.add(new Pair<>(block, cageBlock));
			}
			
		}
	}
	
	public int getPlacementX(int val) {
		return (val >= 1 && val <= 3) ? 0 : (val >= 4 && val <= 6) ? 1 : (val >= 7 && val <= 9) ? 2 : 0;
	}
	
	public int getPlacementZ(int val) {
		int result = val % 3;
		return result == 0 ? 2 : result - 1;
	}
	
	public static class CageBlock {
		
		private int placement;
		private List<MaterialData> materialList = new ArrayList<>();
		
		private int next = 0;
		
		public int getPlacement() {
			return placement;
		}
		
		public void setPlacement(int placement) {
			this.placement = placement;
		}

		public List<MaterialData> getMaterialList() {
			return materialList;
		}
		
		public MaterialData nextMaterial() {
			if (next == 0) {
				next = materialList.size();
			}
			next--;
			return materialList.get(next);
		}
		
		public boolean isAnimated() {
			return materialList.size() > 1;
		}
		
		@Override
		public String toString() {
			return placement + ";" + JHelper.parseList(materialList, ",", FUNC_MATERIAL);
		}
	}
	
	public static CageBlock stringToCageBlock(String data) {
		CageBlock cageBlock = new CageBlock();
		String[] parsed = data.split(";");
		
		cageBlock.placement = Integer.parseInt(parsed[0]);
		
		String[] array = parsed[1].split(",");
		
		for (int i = 0; i < array.length; i++) {
			String[] sp = array[i].split(":");
					
			cageBlock.materialList.add(new MaterialData(CoreMaterial.matchByName(sp[0]).toMaterial(), Byte.parseByte(sp[1])));
		}
		
		return cageBlock;
	}
}
