package openblocks.advancements;

import net.minecraft.advancements.CriteriaTriggers;

public class Criterions {

	public static final TriggerDevNullStack devNullStack = new TriggerDevNullStack();

	public static final TriggerBrickDropped brickDropped = new TriggerBrickDropped();

	public static void init() {
		CriteriaTriggers.register(devNullStack);
		CriteriaTriggers.register(brickDropped);
	}

}
