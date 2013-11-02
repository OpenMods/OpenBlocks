package openblocks.common.block;

import openblocks.Config;
import openblocks.common.tileentity.TileEntityRopeLadder;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockRopeLadder extends OpenBlock {

	public BlockRopeLadder() {
		super(Config.blockRopeLadderId, Material.wood);
		setupBlock(this, "ropeladder", TileEntityRopeLadder.class);
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, ForgeDirection side) {
		
		return isNeighborBlockSolid(world, x, y, z, side);
	}
}
