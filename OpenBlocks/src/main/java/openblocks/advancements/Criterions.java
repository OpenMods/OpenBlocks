package openblocks.advancements;

import net.minecraft.advancements.CriteriaTriggers;

public class Criterions {

	public static TriggerDevNullStack devNullStack = new TriggerDevNullStack();

	public static TriggerBrickDropped brickDropped = new TriggerBrickDropped();

	public static void init() {
		CriteriaTriggers.register(devNullStack);
		CriteriaTriggers.register(brickDropped);
	}

}
