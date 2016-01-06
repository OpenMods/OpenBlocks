package openblocks.common.block;

import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.OpenBlocks;
import openmods.infobook.BookDocumentation;

@BookDocumentation(hasVideo = true)
public class BlockLadder extends BlockTrapDoor {

	public static boolean disableValidation = false;

	public BlockLadder() {
		super(Material.wood);
		setHardness(3.0F);
		setStepSound(soundTypeWood);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setBlockBounds(0f, 0f, 0f, 1.5f, 1f, 1.5f);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
		setBlockBoundsBasedOnState(world, pos);
		return super.getCollisionBoundingBox(world, pos, state);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
		setBlockBoundsBasedOnState(world, pos);
		return super.getSelectedBoundingBox(world, pos);
	}

	@Override
	public void setBounds(IBlockState state) {
		float f = 0.125f;
		final boolean isOpen = state.getValue(BlockTrapDoor.OPEN);
		// TODO 1.8.9 verify
		if (isOpen) {
			switch (state.getValue(BlockTrapDoor.FACING)) {
				case NORTH:
					setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
					break;

				case SOUTH:
					setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
					break;

				case WEST:
					setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
					break;

				case EAST:
				default:
					setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
					break;
			}
		} else {
			final DoorHalf half = state.getValue(BlockTrapDoor.HALF);
			if (half == BlockTrapDoor.DoorHalf.TOP) setBlockBounds(0.0F, 1.0F - f, 0.0F, 1.0F, 1.0F, 1.0F);
			else setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
		}
	}

	@Override
	public boolean isLadder(IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
		return world.getBlockState(pos).getValue(BlockTrapDoor.OPEN);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getRenderType() {
		return 0;
	}
}
