package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntitySprinkler;
import openmods.block.OpenBlock;
import openmods.geometry.BlockSpaceTransform;
import openmods.geometry.Orientation;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockSprinkler extends OpenBlock.TwoDirections {

	public BlockSprinkler() {
		super(Material.water);
	}

	// TODO 1.8.9 room for improvments?
	@Override
	public int getRenderType() {
		return 2; // TESR only
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
		TileEntitySprinkler sprinkler = getTileEntity(world, pos, TileEntitySprinkler.class);
		if (sprinkler != null) {
			final Orientation orientation = sprinkler.getOrientation();
			final AxisAlignedBB aabb = AxisAlignedBB.fromBounds(0.3, 0.0, 0.0, 0.7, 0.3, 1.0);
			final AxisAlignedBB rotatedAabb = BlockSpaceTransform.instance.mapBlockToWorld(orientation, aabb);
			setBlockBounds(rotatedAabb);
		}
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		// TODO 1.8.9 verify
		return isOnTopOfSolidBlock(world, pos, side);
	}

	@Override
	public boolean isReplaceable(World world, BlockPos pos) {
		return false;
	}
}
