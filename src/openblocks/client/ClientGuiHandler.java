package openblocks.client;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openblocks.OpenBlocks.Gui;
import openblocks.client.gui.GuiLuggage;
import openblocks.common.CommonGuiHandler;
import openblocks.common.api.IHasGui;
import openblocks.common.container.ContainerLuggage;
import openblocks.common.entity.EntityLuggage;

public class ClientGuiHandler extends CommonGuiHandler {

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if ((world instanceof WorldClient)) {
			if (ID == Gui.luggage.ordinal()) { return new GuiLuggage(new ContainerLuggage(player.inventory, (EntityLuggage)world.getEntityByID(x))); }
			TileEntity tile = world.getBlockTileEntity(x, y, z);
			if (tile instanceof IHasGui) { return ((IHasGui)tile).getClientGui(player); }
		}
		return null;
	}
}
