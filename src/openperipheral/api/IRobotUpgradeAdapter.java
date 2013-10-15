package openperipheral.api;

import java.util.HashMap;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.nbt.NBTTagCompound;

public interface IRobotUpgradeAdapter extends IAdapterBase {
	public void writeToNBT(NBTTagCompound nbt);

	public void readFromNBT(NBTTagCompound nbt);

	public HashMap<Integer, EntityAIBase> getAITasks();

	public void onTierChanged(int tier);

	public void update();
}
