package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockAutoAnvil extends OpenBlock.TwoDirections {

	public BlockAutoAnvil() {
		super(Material.anvil);
		setStepSound(soundTypeAnvil);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isBlockSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	// TODO 1.8.9 too trivial to live
	@Override
	public int getRenderType() {
		return 2; // TESR only
	}
}
