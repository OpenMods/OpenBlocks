package openperipheral.api;

import java.util.Map;

import net.minecraft.item.ItemStack;

public interface IRobotUpgradeProvider {

	/**
	 * Create a new instance of the robot upgrade.
	 * Please only ever supply one type of upgrade from this. If you want to
	 * make
	 * another upgrade, create another upgradedefinition!
	 * 
	 * @return
	 */
	public IRobotUpgradeAdapter provideUpgradeInstance(IRobot robot, int tier);

	/**
	 * A unique string ID
	 * 
	 * @return
	 */
	public String getUpgradeId();

	/**
	 * tier/itemstack
	 * 
	 * @return
	 */
	public Map<Integer, ItemStack> getUpgradeItems();

	/**
	 * Is this module installed by default and unable to be removed?
	 * 
	 * @return
	 */
	public boolean isForced();

	/**
	 * Get the upgrade class
	 * 
	 * @return
	 */
	public Class getUpgradeClass();

	/**
	 * Is the upgrade applicable for this robot type?
	 * 
	 * @param type
	 * @return
	 */
	public boolean isApplicableForRobot(IRobot robot);
}
