package openblocks;

import java.io.File;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.common.network.IGuiHandler;

public interface IProxy {
	public void init();
	public void postInit();
	public void registerRenderInformation();
	
	/**
	 * Is this the server
	 *
	 * @return true if this is the server
	 */
	public boolean isServer();
	
	/**
	 * Is this the client
	 *
	 * @return true if this is the client
	 */
	public boolean isClient();
	
	public File getWorldDir(World world);
	
	public void spawnLiquidSpray(World worldObj, FluidStack water, double x, double y, double z, ForgeDirection sprayDirection, float angleRadians, float spread);
	
	public EntityPlayer getThePlayer();
	
	public IGuiHandler createGuiHandler();
}
