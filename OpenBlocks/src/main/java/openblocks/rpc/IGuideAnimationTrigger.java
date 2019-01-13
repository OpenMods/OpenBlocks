package openblocks.rpc;

import net.minecraft.util.math.BlockPos;

public interface IGuideAnimationTrigger {
	void trigger(BlockPos pos, int stateId);
}
