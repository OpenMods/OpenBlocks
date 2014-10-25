package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import openblocks.common.tileentity.TileEntityVillageHighlighter;
import openmods.block.BlockRotationMode;

public class BlockVillageHighlighter extends OpenBlock {

	public BlockVillageHighlighter() {
		super(Material.rock);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
		setRenderMode(RenderMode.TESR_ONLY);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int m) {
		TileEntityVillageHighlighter tile = getTileEntity(world, x, y, z, TileEntityVillageHighlighter.class);
		if (tile != null) { return tile.getSignalStrength(); }
		return 0;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int m) {
		return isProvidingWeakPower(world, x, y, z, m);
	}
}
