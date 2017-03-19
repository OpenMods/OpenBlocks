package openblocks.enchantments.flimflams;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import openblocks.api.IFlimFlamAction;

public class SkyblockFlimFlam implements IFlimFlamAction {

	@Override
	public boolean execute(EntityPlayerMP target) {
		final World world = target.worldObj;
		if (world.provider.getHasNoSky() || world.provider.doesWaterVaporize()) return false;

		BlockPos trapCenter = new BlockPos(target.posX, Math.min(target.posY + 150, 250), target.posZ);

		BlockPos[] blocks = new BlockPos[5];
		blocks[0] = trapCenter.offset(EnumFacing.DOWN);
		blocks[1] = trapCenter.offset(EnumFacing.EAST);
		blocks[2] = trapCenter.offset(EnumFacing.NORTH);
		blocks[3] = trapCenter.offset(EnumFacing.SOUTH);
		blocks[4] = trapCenter.offset(EnumFacing.WEST);

		for (BlockPos pos : blocks)
			if (!world.isAirBlock(pos)) return false;

		final IBlockState state = Blocks.ICE.getDefaultState();

		for (BlockPos pos : blocks)
			world.setBlockState(pos, state);

		target.setPositionAndUpdate(trapCenter.getY() + 0.5, trapCenter.getY() + 1, trapCenter.getZ() + 0.5);
		return true;
	}
}
