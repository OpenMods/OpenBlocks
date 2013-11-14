package openblocks.utils;

import java.util.HashSet;
import java.util.Set;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.registry.GameRegistry;

import openblocks.Mods;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class PaintUtils {
	
	private Set<Integer> allowed;
	
	private static PaintUtils _instance;
	
	protected PaintUtils() {
		allowed = new HashSet<Integer>();
		allowed.add(Block.stone.blockID);
		allowed.add(Block.cobblestone.blockID);
		allowed.add(Block.cobblestoneMossy.blockID);
		allowed.add(Block.sandStone.blockID);
		allowed.add(Block.blockIron.blockID);
		allowed.add(Block.stoneBrick.blockID);
		allowed.add(Block.glass.blockID);
		allowed.add(Block.planks.blockID);
		if (Loader.isModLoaded(Mods.TCONSTRUCT)) {
			addBlocksForMod(Mods.TCONSTRUCT, new String[] {
					"GlassBlock",
					"decoration.multibrick",
					"decoration.multibrickfancy"
			});
		}
		if (Loader.isModLoaded(Mods.EXTRAUTILITIES)) {
			addBlocksForMod(Mods.EXTRAUTILITIES, new String[] {
					"greenScreen",
					"extrautils:decor"
			});
		}
	}
	
	protected void addBlocksForMod(String modId, String[] blocks) {
		for (String blockName : blocks) {
			Block block = GameRegistry.findBlock(modId, blockName);
			if (block != null) {
				allowed.add(block.blockID);
			}
		}
	}
	
	public static PaintUtils instance() {
		if (_instance == null) {
			_instance = new PaintUtils();
		}
		return _instance;
	}
	
	public boolean isAllowedToPaint(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		return allowed.contains(id);
	}
}
