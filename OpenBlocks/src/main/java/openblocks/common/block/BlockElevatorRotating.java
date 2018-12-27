package openblocks.common.block;

import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import openblocks.OpenBlocks.Blocks;
import openblocks.api.IElevatorBlock;
import openmods.block.OpenBlock;
import openmods.colors.ColorMeta;
import openmods.geometry.Orientation;
import openmods.utils.CollectionUtils;

public class BlockElevatorRotating extends OpenBlock.FourDirections implements IElevatorBlock {

	public static Block colorToBlock(ColorMeta color) {
		switch (color) {
			case BLACK:
				return Blocks.blackElevatorRotating;
			case BLUE:
				return Blocks.blueElevatorRotating;
			case BROWN:
				return Blocks.brownElevatorRotating;
			case CYAN:
				return Blocks.cyanElevatorRotating;
			case GRAY:
				return Blocks.grayElevatorRotating;
			case GREEN:
				return Blocks.greenElevatorRotating;
			case LIGHT_BLUE:
				return Blocks.lightBlueElevatorRotating;
			case LIGHT_GRAY:
				return Blocks.lightGrayElevatorRotating;
			case LIME:
				return Blocks.limeElevatorRotating;
			case MAGENTA:
				return Blocks.magentaElevatorRotating;
			case ORANGE:
				return Blocks.orangeElevatorRotating;
			case PINK:
				return Blocks.pinkElevatorRotating;
			case PURPLE:
				return Blocks.purpleElevatorRotating;
			case RED:
				return Blocks.redElevatorRotating;
			case WHITE:
				return Blocks.whiteElevatorRotating;
			case YELLOW:
				return Blocks.yellowElevatorRotating;
			default:
				return null;
		}
	}

	private final ColorMeta color;

	public BlockElevatorRotating(final ColorMeta color) {
		super(Material.ROCK);
		this.color = color;
	}

	@Override
	public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor colour) {
		final ColorMeta newColor = ColorMeta.fromVanillaEnum(colour);

		if (newColor != color) {
			final Block newBlock = colorToBlock(newColor);
			if (newBlock != null) {
				world.setBlockState(pos, newBlock.getDefaultState());
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (hand == EnumHand.MAIN_HAND) {
			final ItemStack heldItem = player.getHeldItemMainhand();
			if (!heldItem.isEmpty()) {
				Set<ColorMeta> metas = ColorMeta.fromStack(heldItem);
				if (!metas.isEmpty()) {
					final ColorMeta meta = CollectionUtils.getRandom(metas);
					final Block newBlock = colorToBlock(meta);
					if (newBlock != null) {
						final IBlockState newState = newBlock.getDefaultState()
								.withProperty(getPropertyOrientation(), state.getValue(getPropertyOrientation()));
						return world.setBlockState(pos, newState);
					}
				}
			}
		}
		return false;
	}

	@Override
	public EnumDyeColor getColor(World world, BlockPos pos, IBlockState state) {
		return color.vanillaEnum;
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
