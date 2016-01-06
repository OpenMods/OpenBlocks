package openblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.api.IPaintableBlock;
import openblocks.common.tileentity.TileEntityCanvas;
import openmods.block.OpenBlock;
import openmods.colors.ColorMeta;
import openmods.infobook.BookDocumentation;

@BookDocumentation(hasVideo = true)
public class BlockCanvas extends OpenBlock implements IPaintableBlock {

	// TODO 1.8.9 everything

	public BlockCanvas() {
		this(Material.sponge);
	}

	public BlockCanvas(Material material) {
		super(material);
	}

	public static void replaceBlock(World world, BlockPos pos) {
		final IBlockState state = world.getBlockState(pos);
		final Block block = state.getBlock();
		final int meta = block.getMetaFromState(state);

		final Block toReplace = (block.getMaterial() == Material.glass)? OpenBlocks.Blocks.canvasGlass : OpenBlocks.Blocks.canvas;
		world.setBlockState(pos, toReplace.getDefaultState());

		final TileEntityCanvas tile = getTileEntity(world, pos, TileEntityCanvas.class);
		if (tile != null) tile.setPaintedBlockBlock(block, meta);
	}

	@Override
	public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, int color) {
		final TileEntity te = world.getTileEntity(pos);
		return (te instanceof TileEntityCanvas)? ((TileEntityCanvas)te).applyPaint(color, side) : false;
	}

	@Override
	public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor colour) {
		ColorMeta color = ColorMeta.fromVanillaEnum(colour);
		return recolorBlock(world, pos, side, color.rgb);
	}

}
