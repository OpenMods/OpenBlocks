package openblocks.common.block;

import net.minecraft.block.material.Material;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityVillageHighlighter;

public class BlockVillageHighlighter extends OpenBlock {

	public BlockVillageHighlighter() {
		super(Config.blockVillageHighlighterId, Material.ground);
		setupBlock(this, "village_highlighter", TileEntityVillageHighlighter.class);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return OpenBlocks.renderId;
	}
}
