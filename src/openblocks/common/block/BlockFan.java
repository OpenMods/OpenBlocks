package openblocks.common.block;

import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityFan;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockFan extends OpenBlock {

	public BlockFan() {
		super(Config.blockFanId, Material.ground);
		setupBlock(this, "fan", TileEntityFan.class);
		setBlockBounds(0.2f, 0, 0.2f, 0.8f, 1.0f, 0.8f);
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
	
	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return canPlaceOnlyOnGround(world, x, y, z, side);
	}
}
