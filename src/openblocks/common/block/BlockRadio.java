package openblocks.common.block;

import net.minecraft.block.material.Material;
import openblocks.Config;

public class BlockRadio extends OpenBlock {

	public BlockRadio() {
		super(Config.blockRadioId, Material.ground);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

}
