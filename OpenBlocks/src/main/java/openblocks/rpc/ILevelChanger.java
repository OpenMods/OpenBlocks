package openblocks.rpc;

import openmods.utils.VanillaEnchantLogic;

public interface ILevelChanger {
	void changePowerLimit(int powerLimit);

	void changeLevel(VanillaEnchantLogic.Level level);
}
