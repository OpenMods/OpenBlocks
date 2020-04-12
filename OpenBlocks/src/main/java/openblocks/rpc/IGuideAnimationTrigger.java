package openblocks.rpc;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import openmods.network.rpc.RpcMethod;

public interface IGuideAnimationTrigger {
	@RpcMethod("trigger")
	void trigger(BlockPos pos, BlockState state);
}
