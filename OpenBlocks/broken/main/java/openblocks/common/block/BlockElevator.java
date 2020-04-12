package openblocks.common.block;

import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openblocks.OpenBlocks.Blocks;
import openblocks.api.IElevatorBlock;
import openmods.block.OpenBlock;
import openmods.colors.ColorMeta;
import openmods.infobook.BookDocumentation;
import openmods.utils.CollectionUtils;

@BookDocumentation(hasVideo = true, customName = "elevator")
public class BlockElevator extends OpenBlock implements IElevatorBlock {

	@Nullable
	public static Block colorToBlock(final ColorMeta color) {
		switch (color) {
			case BLACK:
				return Blocks.blackElevator;
			case BLUE:
				return Blocks.blueElevator;
			case BROWN:
				return Blocks.brownElevator;
			case CYAN:
				return Blocks.cyanElevator;
			case GRAY:
				return Blocks.grayElevator;
			case GREEN:
				return Blocks.greenElevator;
			case LIGHT_BLUE:
				return Blocks.lightBlueElevator;
			case LIGHT_GRAY:
				return Blocks.lightGrayElevator;
			case LIME:
				return Blocks.limeElevator;
			case MAGENTA:
				return Blocks.magentaElevator;
			case ORANGE:
				return Blocks.orangeElevator;
			case PINK:
				return Blocks.pinkElevator;
			case PURPLE:
				return Blocks.purpleElevator;
			case RED:
				return Blocks.redElevator;
			case WHITE:
				return Blocks.whiteElevator;
			case YELLOW:
				return Blocks.yellowElevator;
			default:
				return null;
		}
	}

	private final ColorMeta color;

	public BlockElevator(final ColorMeta color) {
		super(Material.ROCK);
		this.color = color;
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
	public MapColor getMapColor(BlockState state, IBlockAccess worldIn, BlockPos pos) {
		return MapColor.getBlockColor(color.vanillaEnum);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
		if (hand == Hand.MAIN_HAND) {
			final ItemStack heldItem = player.getHeldItemMainhand();
			if (!heldItem.isEmpty()) {
				Set<ColorMeta> metas = ColorMeta.fromStack(heldItem);
				if (!metas.isEmpty()) {
					final ColorMeta meta = CollectionUtils.getRandom(metas);
					final Block newBlock = colorToBlock(meta);
					if (newBlock != null) return world.setBlockState(pos, newBlock.getDefaultState());
				}
			}
		}
		return false;
	}

	@Override
	public DyeColor getColor(World world, BlockPos pos, BlockState state) {
		return color.vanillaEnum;
	}

	@Override
	public PlayerRotation getRotation(World world, BlockPos pos, BlockState state) {
		return PlayerRotation.NONE;
	}

}
