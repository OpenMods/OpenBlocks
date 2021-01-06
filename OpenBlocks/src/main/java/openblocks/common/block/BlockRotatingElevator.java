package openblocks.common.block;

import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.state.Property;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import openblocks.OpenBlocks.Blocks;
import openblocks.api.IElevatorBlock;
import openblocks.api.IPaintableBlock;
import openmods.block.BlockRotationMode;
import openmods.block.IBlockRotationMode;
import openmods.block.OpenBlock;
import openmods.colors.ColorMeta;
import openmods.geometry.Orientation;
import openmods.utils.CollectionUtils;

public class BlockRotatingElevator extends OpenBlock.Orientable implements IElevatorBlock, IPaintableBlock {
	@Nullable
	public static Block colorToBlock(ColorMeta color) {
		switch (color) {
			case BLACK:
				return Blocks.blackRotatingElevator;
			case BLUE:
				return Blocks.blueRotatingElevator;
			case BROWN:
				return Blocks.brownRotatingElevator;
			case CYAN:
				return Blocks.cyanRotatingElevator;
			case GRAY:
				return Blocks.grayRotatingElevator;
			case GREEN:
				return Blocks.greenRotatingElevator;
			case LIGHT_BLUE:
				return Blocks.lightBlueRotatingElevator;
			case LIGHT_GRAY:
				return Blocks.lightGrayRotatingElevator;
			case LIME:
				return Blocks.limeRotatingElevator;
			case MAGENTA:
				return Blocks.magentaRotatingElevator;
			case ORANGE:
				return Blocks.orangeRotatingElevator;
			case PINK:
				return Blocks.pinkRotatingElevator;
			case PURPLE:
				return Blocks.purpleRotatingElevator;
			case RED:
				return Blocks.redRotatingElevator;
			case WHITE:
				return Blocks.whiteRotatingElevator;
			case YELLOW:
				return Blocks.yellowRotatingElevator;
			default:
				return null;
		}
	}

	private final ColorMeta color;

	public BlockRotatingElevator(Block.Properties properties, final ColorMeta color) {
		super(properties);
		this.color = color;
	}

	public static BlockRotatingElevator create(final Material material, final ColorMeta color) {
		return new BlockRotatingElevator(Block.Properties.create(material, color.vanillaEnum), color);
	}

	@Override
	public IBlockRotationMode getRotationMode() {
		return BlockRotationMode.FOUR_DIRECTIONS;
	}

	@Override
	public boolean recolorBlock(World world, BlockPos pos, Direction side, DyeColor colour) {
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
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos blockPos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (hand == Hand.MAIN_HAND) {
			final ItemStack heldItem = player.getHeldItemMainhand();
			if (!heldItem.isEmpty()) {
				Set<ColorMeta> metas = ColorMeta.fromStack(heldItem);
				if (!metas.isEmpty()) {
					final ColorMeta meta = CollectionUtils.getRandom(metas);
					final Block newBlock = colorToBlock(meta);
					if (newBlock != null) {
						Property<Orientation> orientationProperty = getOrientationProperty();
						final BlockState newState = newBlock.getDefaultState().with(orientationProperty, state.get(orientationProperty));
						world.setBlockState(blockPos, newState);
						return ActionResultType.SUCCESS;
					}
				}
			}
		}
		return ActionResultType.PASS;
	}

	@Override
	public DyeColor getColor(World world, BlockPos pos, BlockState state) {
		return color.vanillaEnum;
	}

	@Override
	public PlayerRotation getRotation(World world, BlockPos pos, BlockState state) {
		final Orientation orientation = getOrientation(world, pos);
		final Direction rot = orientation.north();
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

	@Override
	public String getTranslationKey() {
		return "block.openblocks.rotating_elevator.name";
	}
}
