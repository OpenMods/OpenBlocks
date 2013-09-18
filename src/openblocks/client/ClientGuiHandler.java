package openblocks.client;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openblocks.OpenBlocks.Gui;
import openblocks.common.CommonGuiHandler;
import openblocks.client.gui.*;
import openblocks.common.container.*;
import openblocks.common.entity.*;
import openblocks.common.tileentity.*;

public class ClientGuiHandler extends CommonGuiHandler {
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if ((world instanceof WorldClient)) {
			if (ID == Gui.Luggage.ordinal()) { return new GuiLuggage(new ContainerLuggage(player.inventory, (EntityLuggage)world.getEntityByID(x))); }
			TileEntity tile = world.getBlockTileEntity(x, y, z);
			if (ID == Gui.Lightbox.ordinal()) { return new GuiLightbox(new ContainerLightbox(player.inventory, (TileEntityLightbox)tile)); }
			if (ID == Gui.Sprinkler.ordinal()) { return new GuiSprinkler(new ContainerSprinkler(player.inventory, (TileEntitySprinkler)tile)); }
			if (ID == Gui.VacuumHopper.ordinal()) { return new GuiVacuumHopper(new ContainerVacuumHopper(player.inventory, (TileEntityVacuumHopper)tile)); }
			if (ID == Gui.BigButton.ordinal()) { return new GuiBigButton(new ContainerBigButton(player.inventory, (TileEntityBigButton)tile)); }

		}
		return null;
	}
}
