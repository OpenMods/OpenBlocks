package openblocks.common.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityRopeLadder;
import openblocks.utils.BlockUtils;
import openmods.common.block.OpenBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRopeLadder extends OpenBlock {

	public BlockRopeLadder() {
		super(Config.blockRopeLadderId, Material.circuits);
		setHardness(0.4F);
		setStepSound(soundLadderFootstep);
		setupBlock(this, "ropeladder", TileEntityRopeLadder.class);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
		setPlacementMode(BlockPlacementMode.SURFACE);
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return side != ForgeDirection.UP && side != ForgeDirection.DOWN;// &&
																		// isNeighborBlockSolid(world,
																		// x, y,
																		// z,
																		// side);
	}

	@Override
	public boolean isLadder(World world, int x, int y, int z, EntityLivingBase entity) {
		return true;
	}

	@Override
	public boolean shouldRenderBlock() {
		return false;
	}

	@Override
	public int idDropped(int par1, Random par2Random, int par3) {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB bb, List list, Entity entity) {
		if (entity instanceof EntityLivingBase) {
			int meta = world.getBlockMetadata(x, y, z);
			ForgeDirection rotation = ForgeDirection.getOrientation(meta);
			ForgeDirection playerRotation = BlockUtils.get2dOrientation((EntityLivingBase)entity);
			if (rotation == playerRotation) {
				super.addCollisionBoxesToList(world, x, y, z, bb, list, entity);
			}
		} else {
			super.addCollisionBoxesToList(world, x, y, z, bb, list, entity);
		}
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
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {

		TileEntityRopeLadder tile = getTileEntity(world, x, y, z, TileEntityRopeLadder.class);
		if (tile != null) {
			ForgeDirection direction = tile.getRotation();

			switch (direction) {
				case EAST:
					setBlockBounds(0.9375f, 0, 0, 1f, 1f, 1f);
					break;
				case WEST:
					setBlockBounds(0, 0, 0, 0.0625f, 1f, 1f);
					break;
				case NORTH:
					setBlockBounds(0, 0, 0, 1f, 1f, 0.0625f);
					break;
				case SOUTH:
					setBlockBounds(0, 0, 0.9375f, 1f, 1f, 1f);
					break;
				default:
					setBlockBounds(0, 0, 0.9375f, 1f, 1f, 1f);
					break;
			}
		}

	}

}
