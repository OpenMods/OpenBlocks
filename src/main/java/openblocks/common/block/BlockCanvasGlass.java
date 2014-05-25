package openblocks.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;

public class BlockCanvasGlass extends BlockCanvas {

	public BlockCanvasGlass() {
		super(Material.glass);
		setStepSound(soundTypeGlass);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		Block block = world.getBlock(x, y, z);
		if (block == this || block == Blocks.glass) { return false; }
		return super.shouldSideBeRendered(world, x, y, z, side);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void getSubBlocks(Item item, CreativeTabs creativeTabs, List list) {}
}
