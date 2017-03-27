package openblocks.common.block;

import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.common.tileentity.TileEntityPaintCan;
import openmods.block.OpenBlock;

public class BlockPaintCan extends OpenBlock.FourDirections {

	private static final int COLOR_WHITE = 0xFFFFFFFF;

	@SideOnly(Side.CLIENT)
	public static class BlockColorHandler implements IBlockColor {
		@Override
		public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
			if (tintIndex != 1) return COLOR_WHITE;
			final TileEntityPaintCan te = getTileEntity(worldIn, pos, TileEntityPaintCan.class);
			return te != null? te.getColor() : COLOR_WHITE;
		}
	}

	public BlockPaintCan() {
		super(Material.CIRCUITS);
		setHardness(0);
		setPlacementMode(BlockPlacementMode.ENTITY_ANGLE);
	}

	protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 0.6875, 0.75);

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
}
