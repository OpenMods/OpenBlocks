package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.common.tileentity.TileEntitySprinkler;
import openmods.block.BlockRotationMode;
import openmods.geometry.BlockSpaceTransform;
import openmods.geometry.Orientation;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockSprinkler extends OpenBlock {

	public BlockSprinkler() {
		super(Material.water);
		setRotationMode(BlockRotationMode.TWO_DIRECTIONS);
		setRenderMode(RenderMode.TESR_ONLY);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		TileEntitySprinkler sprinkler = getTileEntity(world, x, y, z, TileEntitySprinkler.class);
		if (sprinkler != null) {
			final Orientation orientation = sprinkler.getOrientation();
			final AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(0.3, 0.0, 0.0, 0.7, 0.3, 1.0);
			final AxisAlignedBB rotatedAabb = BlockSpaceTransform.instance.mapBlockToWorld(orientation, aabb);
			setBlockBounds(rotatedAabb);
		}
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return isOnTopOfSolidBlock(world, x, y, z, side);
	}

	@Override
	public boolean isReplaceable(IBlockAccess arg0, int arg1, int arg2, int arg3) {
		return false;
	}
}
