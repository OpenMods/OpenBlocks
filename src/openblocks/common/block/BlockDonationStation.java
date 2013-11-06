package openblocks.common.block;

import openblocks.Config;
import openblocks.common.tileentity.TileEntityDonationStation;
import net.minecraft.block.material.Material;

public class BlockDonationStation extends OpenBlock {

	public BlockDonationStation() {
		super(Config.blockDonationStationId, Material.ground);
		setupBlock(this, "donationStation", TileEntityDonationStation.class);
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

}
