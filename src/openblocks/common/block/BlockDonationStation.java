package openblocks.common.block;

import openblocks.Config;
import openblocks.common.tileentity.TileEntityDonationStation;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;

public class BlockDonationStation extends OpenBlock {

	public BlockDonationStation() {
		super(Config.blockDonationStationId, Material.ground);
		setupBlock(this, "donationStation", TileEntityDonationStation.class);
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
