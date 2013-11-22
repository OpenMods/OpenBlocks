package openblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openmods.block.OpenBlock;

public class BlockPath extends OpenBlock {

	public BlockPath() {
		super(Config.blockPathId, Material.plants);
		setupBlock(this, "path");
		setBlockBounds(0, 0, 0, 1f, 0.1f, 1f);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return isValidLocation(world, x, y, z)
				&& super.canPlaceBlockAt(world, x, y, z);
	}

	protected boolean isValidLocation(World world, int x, int y, int z) {
		int bId = world.getBlockId(x, y - 1, z);
		Block below = Block.blocksList[bId];
		if (below != null) { return below.isBlockSolidOnSide(world, x, y - 1, z, ForgeDirection.UP); }
		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int par5) {
		if (!world.isRemote && !isValidLocation(world, x, y, z)) {
			dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlockToAir(x, y, z);
		}
	}

}
