package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import openblocks.Config;

public class BlockDonationStation extends OpenBlock {

	public BlockDonationStation() {
		super(Config.blockDonationStationId, Material.ground);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
		setBlockBounds(0.2f, 0.25f, 0.2f, 0.8f, 0.85f, 0.8f);
	}

	@Override
	public void registerIcons(IconRegister registry) {
		blockIcon = registry.registerIcon("openblocks:donationstation");
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean shouldRenderBlock() {
		return false;
	}

}
