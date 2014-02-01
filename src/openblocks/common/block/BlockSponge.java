package openblocks.common.block;

import net.minecraft.block.material.Material;
import openblocks.Config;

public class BlockSponge extends OpenBlock {

	public BlockSponge() {
		super(Config.blockSpongeId, Material.sponge);
		setStepSound(soundClothFootstep);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}
}
