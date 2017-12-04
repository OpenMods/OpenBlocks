package openblocks.advancements;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import openmods.reflection.MethodAccess;
import openmods.reflection.MethodAccess.Function1;

public class Criterions {

	@SuppressWarnings("rawtypes")
	private static final Function1<ICriterionTrigger, ICriterionTrigger> register =
			MethodAccess.create(ICriterionTrigger.class, CriteriaTriggers.class, ICriterionTrigger.class, "func_192118_a", "register");

	public static TriggerDevNullStack devNullStack = new TriggerDevNullStack();

	public static TriggerBrickDropped brickDropped = new TriggerBrickDropped();

	public static void init() {
		register.call(null, devNullStack);
		register.call(null, brickDropped);
	}

}
