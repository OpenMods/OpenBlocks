	package openblocks.common;

import java.io.File;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import openblocks.IProxy;
import openblocks.OpenBlocks;
import openblocks.common.container.*;
import openblocks.common.entity.*;
import openblocks.common.tileentity.*;

public class ServerProxy implements IProxy {
	
	@Override
	public void init() {}

	@Override
	public void postInit() {}

	@Override
	public void registerRenderInformation() {

	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == OpenBlocks.Gui.Luggage.ordinal()) { return new ContainerLuggage(player.inventory, (EntityLuggage)world.getEntityByID(x)); }

		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (ID == OpenBlocks.Gui.Lightbox.ordinal()) { return new ContainerLightbox(player.inventory, (TileEntityLightbox)tile); }
		if (ID == OpenBlocks.Gui.Sprinkler.ordinal()) { return new ContainerSprinkler(player.inventory, (TileEntitySprinkler)tile); }
		if (ID == OpenBlocks.Gui.VacuumHopper.ordinal()) { return new ContainerVacuumHopper(player.inventory, (TileEntityVacuumHopper)tile); }
		if (ID == OpenBlocks.Gui.BigButton.ordinal()) { return new ContainerBigButton(player.inventory, (TileEntityBigButton)tile); }

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public File getWorldDir(World world) {
		return new File(OpenBlocks.getBaseDir(), DimensionManager.getWorld(0).getSaveHandler().getWorldDirectoryName());
	}

	/**
	 * Is this the server
	 *
	 * @return true if this is the server
	 */
	@Override
	public boolean isServer() {
		return true; // Why have this method? If the checking method changes in
						// the future we fix it in one place.
	}

	/**
	 * Is this the client
	 *
	 * @return true if this is the client
	 */
	@Override
	public boolean isClient() {
		return false;
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
}
