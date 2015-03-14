package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import openmods.block.BlockRotationMode;

public class BlockTrophy extends OpenBlock {

	public BlockTrophy() {
		super(Material.rock);
		setBlockBounds(0.2f, 0, 0.2f, 0.8f, 0.2f, 0.8f);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
		setRenderMode(RenderMode.BOTH);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return AxisAlignedBB.getBoundingBox(x + 0.2, y + 0.0, z + 0.2, x + 0.8, y + 0.8, z + 0.8);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		return AxisAlignedBB.getBoundingBox(x + 0.2, y + 0.0, z + 0.2, x + 0.8, y + 1.0, z + 0.8);
	}

}
