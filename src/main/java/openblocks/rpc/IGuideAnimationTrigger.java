package openblocks.rpc;

import net.minecraft.util.BlockPos;

public interface IGuideAnimationTrigger {
	public void trigger(BlockPos pos, int stateId);
}
