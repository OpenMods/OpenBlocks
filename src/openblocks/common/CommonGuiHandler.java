package openblocks.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.OpenBlocks.GuiId;
import openblocks.common.container.ContainerLuggage;
import openblocks.common.entity.EntityLuggage;
import openmods.Log;
import openmods.api.IHasGui;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonGuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if (id != -1) {
			if (id < 0 || id >= OpenBlocks.GUIS.length) {
				Log.warn("Invalid GUI id: %d", id);
				return null;
			}

			GuiId guiId = OpenBlocks.GUIS[id];
			if (guiId == OpenBlocks.GuiId.luggage) return new ContainerLuggage(player.inventory, (EntityLuggage)world.getEntityByID(x));
		} else {
			TileEntity tile = world.getBlockTileEntity(x, y, z);
			if (tile instanceof IHasGui) { return ((IHasGui)tile).getServerGui(player); }
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}
}
