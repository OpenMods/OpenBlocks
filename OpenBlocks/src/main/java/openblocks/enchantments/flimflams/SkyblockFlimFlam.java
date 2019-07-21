package openblocks.enchantments.flimflams;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import openblocks.api.IFlimFlamAction;

public class SkyblockFlimFlam implements IFlimFlamAction {

	@Override
	public boolean execute(ServerPlayerEntity target) {
		final World world = target.world;
		if (world.provider.isNether() || world.provider.doesWaterVaporize()) return false;

		BlockPos trapCenter = new BlockPos(target.posX, Math.min(target.posY + 150, 250), target.posZ);

		BlockPos[] blocks = new BlockPos[5];
		blocks[0] = trapCenter.offset(Direction.DOWN);
		blocks[1] = trapCenter.offset(Direction.EAST);
		blocks[2] = trapCenter.offset(Direction.NORTH);
		blocks[3] = trapCenter.offset(Direction.SOUTH);
		blocks[4] = trapCenter.offset(Direction.WEST);

		for (BlockPos pos : blocks)
			if (!world.isAirBlock(pos)) return false;

		final BlockState state = Blocks.ICE.getDefaultState();

		for (BlockPos pos : blocks)
			if (!world.setBlockState(pos, state)) return false;

		target.setPositionAndUpdate(trapCenter.getX() + 0.5, trapCenter.getY() + 1, trapCenter.getZ() + 0.5);
		return true;
	}
}
