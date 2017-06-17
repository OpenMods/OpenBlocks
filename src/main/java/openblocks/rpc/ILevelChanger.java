package openblocks.rpc;

import openmods.utils.VanillaEnchantLogic;

public interface ILevelChanger {
	public void changePowerLimit(int powerLimit);

	public void changeLevel(VanillaEnchantLogic.Level level);
}
