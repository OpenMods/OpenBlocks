package openblocks.common.block;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import openblocks.OpenBlocks;
import openblocks.api.IPaintableBlock;
import openblocks.client.renderer.block.canvas.CanvasState;
import openblocks.client.renderer.block.canvas.InnerBlockState;
import openblocks.common.tileentity.TileEntityCanvas;
import openmods.block.OpenBlock;
import openmods.colors.ColorMeta;
import openmods.infobook.BookDocumentation;

@BookDocumentation(hasVideo = true)
public class BlockCanvas extends OpenBlock implements IPaintableBlock {

	public static class InnerBlockColorHandler implements IBlockColor {

		private final BlockColors blockColors;

		public InnerBlockColorHandler(BlockColors blockColors) {
			this.blockColors = blockColors;
		}

		@Override
		public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
			if (state instanceof IExtendedBlockState) {
				final IExtendedBlockState extendedState = (IExtendedBlockState)state;
				final IBlockState innerState = extendedState.getValue(InnerBlockState.PROPERTY);
				if (innerState != null) return blockColors.colorMultiplier(innerState, worldIn, pos, tintIndex);
			}

			return 0xFFFFFFFF;
		}
	}

	public BlockCanvas() {
		this(Material.SPONGE);
	}

	public BlockCanvas(Material material) {
		super(material);
	}

	public static void replaceBlock(World world, BlockPos pos) {
		final IBlockState state = world.getBlockState(pos);

		final Block toReplace = (state.getMaterial() == Material.GLASS)? OpenBlocks.Blocks.canvasGlass : OpenBlocks.Blocks.canvas;
		world.setBlockState(pos, toReplace.getDefaultState());

		final TileEntityCanvas tile = getTileEntity(world, pos, TileEntityCanvas.class);
		if (tile != null) tile.setPaintedBlock(state);
	}

	@Override
	public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, int color) {
		final TileEntityCanvas te = getTileEntity(world, pos, TileEntityCanvas.class);
		return te != null? te.applyPaint(0xFF000000 | color, side) : false;
	}

	@Override
	public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor colour) {
		ColorMeta color = ColorMeta.fromVanillaEnum(colour);
		return recolorBlock(world, pos, side, color.rgb);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this,
				new IProperty[] { getPropertyOrientation() },
				new IUnlistedProperty[] { CanvasState.PROPERTY, InnerBlockState.PROPERTY });
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		final IExtendedBlockState extendedState = (IExtendedBlockState)super.getExtendedState(state, world, pos);
		final TileEntityCanvas te = getTileEntity(world, pos, TileEntityCanvas.class);

		if (te != null) {
			return extendedState
					.withProperty(CanvasState.PROPERTY, te.getCanvasState())
					.withProperty(InnerBlockState.PROPERTY, te.getPaintedBlockState());
		} else {
			return extendedState;
		}
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
	}

}
