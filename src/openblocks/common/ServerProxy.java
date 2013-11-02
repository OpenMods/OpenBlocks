package openblocks.common;

import java.io.File;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import openblocks.IProxy;
import openblocks.OpenBlocks;
import cpw.mods.fml.common.network.IGuiHandler;

public class ServerProxy implements IProxy {

	@Override
	public void init() {}

	@Override
	public void postInit() {}

	@Override
	public void registerRenderInformation() {}

	@Override
	public File getWorldDir(World world) {
		return new File(OpenBlocks.getBaseDir(), DimensionManager.getWorld(0).getSaveHandler().getWorldDirectoryName());
	}

	@Override
	public boolean isServerOnly() {
		return true;
	}

	@Override
	public boolean isServerThread() {
		return true;
	}

	/**
	 * Checks if this game is SinglePlayer
	 * 
	 * @return true if this is single player
	 */
	public boolean isSinglePlayer() {
		// Yeah I know it doesn't matter now but why not have it :P
		MinecraftServer serverInstance = MinecraftServer.getServer();
		if (serverInstance == null) return false;
		return serverInstance.isSinglePlayer();
	}

	@Override
	public void spawnLiquidSpray(World worldObj, FluidStack water, double x, double y, double z, ForgeDirection sprayDirection, float angleRadians, float spread) {}

	@Override
	public EntityPlayer getThePlayer() {
		return null;
	}

	@Override
	public IGuiHandler createGuiHandler() {
		return new CommonGuiHandler();
	}

	@Override
	public long getTicks(World worldObj) {
		return worldObj.getTotalWorldTime();
	}

	@Override
	public World getClientWorld() {
		return null;
	}

	@Override
	public World getServerWorld(int id) {
		return DimensionManager.getWorld(id);
	}
}
