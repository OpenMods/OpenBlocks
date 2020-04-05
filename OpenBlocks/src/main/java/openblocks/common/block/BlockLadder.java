package openblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import openmods.infobook.BookDocumentation;

@BookDocumentation(hasVideo = true)
public class BlockLadder extends TrapDoorBlock {

	public BlockLadder(final Block.Properties properties) {
		super(properties);
	}

	// NOTE vanilla's ladder provides similar capability, but only when bottom block is actual ladder, so this is still useful
	@Override
	public boolean isLadder(final BlockState state, final IWorldReader world, final BlockPos pos, final LivingEntity entity) {
		return world.getBlockState(pos).get(TrapDoorBlock.OPEN);
	}

}
