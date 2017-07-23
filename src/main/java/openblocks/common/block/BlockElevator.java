package openblocks.common.block;

import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.api.IElevatorBlock;
import openmods.block.OpenBlock;
import openmods.colors.ColorMeta;
import openmods.infobook.BookDocumentation;
import openmods.utils.CollectionUtils;

@BookDocumentation(hasVideo = true)
public class BlockElevator extends OpenBlock implements IElevatorBlock {

	public static final PropertyEnum<ColorMeta> COLOR = PropertyEnum.create("color", ColorMeta.class);

	@SideOnly(Side.CLIENT)
	public static class BlockColorHandler implements IBlockColor {
		private static final int WHITE = 0xFFFFFFFF;

		@Override
		public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
			return tintIndex == 0? state.getValue(COLOR).rgb : WHITE;
		}
	}

	public BlockElevator() {
		super(Material.ROCK);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(COLOR, ColorMeta.fromBlockMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(COLOR).vanillaBlockId;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { getPropertyOrientation(), COLOR });
	}

	@Override
	public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor colour) {
		final ColorMeta newColor = ColorMeta.fromVanillaEnum(colour);

		final IBlockState state = world.getBlockState(pos);
		final ColorMeta currentColor = state.getValue(COLOR);

		if (newColor != currentColor) {
			world.setBlockState(pos, state.withProperty(COLOR, newColor));
			return true;
		}

		return false;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(COLOR).vanillaBlockId;
	}

	@Override
	public MapColor getMapColor(IBlockState state) {
		return state.getValue(COLOR).vanillaEnum.getMapColor();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (hand == EnumHand.MAIN_HAND && heldItem != null) {
			Set<ColorMeta> metas = ColorMeta.fromStack(heldItem);
			if (!metas.isEmpty()) {
				final ColorMeta meta = CollectionUtils.getRandom(metas);
				return world.setBlockState(pos, state.withProperty(COLOR, meta));
			}
		}
		return false;
	}

	@Override
	public EnumDyeColor getColor(World world, BlockPos pos, IBlockState state) {
		return state.getValue(COLOR).vanillaEnum;
	}

	@Override
	public PlayerRotation getRotation(World world, BlockPos pos, IBlockState state) {
		return PlayerRotation.NONE;
	}

}
