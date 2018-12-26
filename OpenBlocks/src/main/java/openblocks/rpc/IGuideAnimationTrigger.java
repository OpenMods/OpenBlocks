package openblocks.rpc;

import net.minecraft.util.math.BlockPos;

public interface IGuideAnimationTrigger {
	public void trigger(BlockPos pos, int stateId);
}
