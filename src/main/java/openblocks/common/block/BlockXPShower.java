package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import openmods.block.OpenBlock;
import openmods.geometry.BlockSpaceTransform;
import openmods.geometry.Orientation;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockXPShower extends OpenBlock.FourDirections {

	public BlockXPShower() {
		super(Material.rock);
		setPlacementMode(BlockPlacementMode.SURFACE);
	}

	// TODO 1.8.9 and you too...
	@Override
	public int getRenderType() {
		return 2; // TESR only
	}

	private void setBoundsBasedOnOrientation(Orientation orientation) {
		final AxisAlignedBB aabb = AxisAlignedBB.fromBounds(7.0 / 16.0, 7.0 / 16.0, 0.0 / 16.0, 9.0 / 16.0, 9.0 / 16.0, 9.0 / 16.0);
		final AxisAlignedBB rotatedAabb = BlockSpaceTransform.instance.mapBlockToWorld(orientation, aabb);
		setBlockBounds(rotatedAabb);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
		Orientation orientation = getOrientation(world, pos);
		setBoundsBasedOnOrientation(orientation);
	}
}
