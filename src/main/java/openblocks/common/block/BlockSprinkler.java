package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.common.tileentity.TileEntitySprinkler;

public class BlockSprinkler extends OpenBlock {

	public BlockSprinkler() {
		super(Material.water);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
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
			if (sprinkler.getRotation() == ForgeDirection.EAST
					|| sprinkler.getRotation() == ForgeDirection.WEST) {
				setBlockBounds(0, 0, 0.3f, 1f, 0.3f, 0.7f);
			} else {
				setBlockBounds(0.3f, 0, 0, 0.7f, 0.3f, 1f);
			}
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
