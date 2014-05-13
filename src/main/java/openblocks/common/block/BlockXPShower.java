package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityXPShower;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockXPShower extends OpenBlock {

	public BlockXPShower() {
		super(Config.blockXPShowerId, Material.rock);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
		setPlacementMode(BlockPlacementMode.SURFACE);
	}

	@Override
	public boolean shouldRenderBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		return super.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		return super.getSelectedBoundingBoxFromPool(par1World, par2, par3, par4);
	}

	@Override
	public void setBoundsBasedOnRotation(ForgeDirection direction) {
		float min = 0.4375f;
		float max = 0.5625f;
		switch (direction) {
			case EAST:
				setBlockBounds(min, min, min, 1f, max, max);
				break;
			case WEST:
				setBlockBounds(0f, min, min, max, max, max);
				break;
			case NORTH:
				setBlockBounds(min, min, 0f, max, max, max);
				break;
			default:
			case SOUTH:
				setBlockBounds(min, min, min, max, max, 1f);
				break;
		}
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {

		TileEntityXPShower tile = getTileEntity(world, x, y, z, TileEntityXPShower.class);
		if (tile != null) {
			ForgeDirection direction = tile.getRotation();
			setBoundsBasedOnRotation(direction);
		}

	}
}
