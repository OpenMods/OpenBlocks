package openblocks.utils;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class PaintUtils {
	public static boolean isAllowedToPaint(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		return id == Block.stone.blockID || id == Block.cobblestone.blockID || id == Block.cobblestoneMossy.blockID || 
				id == Block.sandStone.blockID || id == Block.blockIron.blockID || id == Block.stoneBrick.blockID || 
				id == Block.glass.blockID || id == Block.wood.blockID;
	}
}
