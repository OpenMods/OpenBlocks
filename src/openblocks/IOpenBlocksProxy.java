package openblocks;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.common.network.IGuiHandler;

public interface IOpenBlocksProxy {
	public IGuiHandler createGuiHandler();
	
	public void preInit();
	public void init();
	public void postInit();
	
	public void registerRenderInformation();
	
	public void spawnLiquidSpray(World worldObj, FluidStack water, double x, double y, double z, ForgeDirection direction, float angleRadians, float spread);
}
