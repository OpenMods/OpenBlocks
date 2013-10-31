package openblocks.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.common.api.IHasGui;
import openblocks.common.container.ContainerLuggage;
import openblocks.common.entity.EntityLuggage;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonGuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == OpenBlocks.Gui.luggage.ordinal()) { return new ContainerLuggage(player.inventory, (EntityLuggage)world.getEntityByID(x)); }

		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (tile instanceof IHasGui) { return ((IHasGui)tile).getServerGui(player); }
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}
}
