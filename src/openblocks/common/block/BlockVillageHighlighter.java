package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityVillageHighlighter;

public class BlockVillageHighlighter extends OpenBlock {

	public BlockVillageHighlighter() {
		super(Config.blockVillageHighlighterId, Material.ground);
		setupBlock(this, "village_highlighter", TileEntityVillageHighlighter.class);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean shouldRenderBlock() {
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
