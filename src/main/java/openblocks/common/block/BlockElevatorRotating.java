package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openblocks.api.IElevatorBlock;
import openblocks.common.tileentity.TileEntityElevatorRotating;
import openmods.block.OpenBlock;
import openmods.colors.ColorMeta;
import openmods.geometry.Orientation;

public class BlockElevatorRotating extends OpenBlock.FourDirections implements IElevatorBlock {

	public BlockElevatorRotating() {
		super(Material.rock);
	}

	private static ColorMeta getColorMeta(IBlockAccess world, BlockPos pos) {
		final TileEntityElevatorRotating te = getTileEntity(world, pos, TileEntityElevatorRotating.class);
		return te != null? te.getColor() : ColorMeta.WHITE;
	}

	@Override
	public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass) {
		return getColorMeta(world, pos).rgb;
	}

	// TODO 1.8.9 getRenderColor(IBlockState state) and item coloring
	// TODO 1.8.9 getActualState with color

	@Override
	public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor color) {
		if (world.isRemote) return false;

		final TileEntityElevatorRotating te = getTileEntity(world, pos, TileEntityElevatorRotating.class);

		if (te != null) {
			ColorMeta current = te.getColor();
			ColorMeta next = ColorMeta.fromVanillaEnum(color);
			if (current != next) {
				te.setColor(next);
				return true;
			}
		}

		return false;
	}

	@Override
	public EnumDyeColor getColor(World world, BlockPos pos, IBlockState state) {
		return getColorMeta(world, pos).vanillaEnum;
	}

	@Override
	public PlayerRotation getRotation(World world, BlockPos pos, IBlockState state) {
		final Orientation orientation = getOrientation(world, pos);
		final EnumFacing rot = orientation.north();
		switch (rot) {
			case NORTH:
				return PlayerRotation.NORTH;
			case SOUTH:
				return PlayerRotation.SOUTH;
			case WEST:
				return PlayerRotation.WEST;
			case EAST:
				return PlayerRotation.EAST;
			default:
				return PlayerRotation.NONE;
		}
	}

}
