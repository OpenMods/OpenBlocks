package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.common.tileentity.TileEntitySprinkler;
import openmods.common.block.OpenBlock;

public class BlockSprinkler extends OpenBlock {

	public BlockSprinkler() {
		super(Config.blockSprinklerId, Material.water);
		setupBlock(this, "sprinkler", TileEntitySprinkler.class);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean shouldRenderBlock() {
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
}
