package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockFan extends OpenBlock {

	public BlockFan() {
		super(Material.circuits);
		setBlockBounds(0.2f, 0, 0.2f, 0.8f, 1.0f, 0.8f);
	}

	// TODO 1.8.9 You know what
	@Override
	public int getRenderType() {
		return 2; // TESR only
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		return isOnTopOfSolidBlock(world, pos, side);
	}
}
