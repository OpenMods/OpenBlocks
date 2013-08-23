package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntitySprinkler;

public class BlockSprinkler extends OpenBlock {

	public BlockSprinkler() {
		super(OpenBlocks.Config.blockSprinklerId, Material.water);
		setupBlock(this, "sprinkler", TileEntitySprinkler.class);
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
	public int getRenderType() {
		return OpenBlocks.renderId;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		this.setBlockBoundsBasedOnState(world, x, y, z);
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		this.setBlockBoundsBasedOnState(world, x, y, z);
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
		return super.canPlaceBlockOnSide(world, x, y, z, ForgeDirection.DOWN);
	}
}
