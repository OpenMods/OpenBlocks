package openblocks.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.common.container.*;
import openblocks.common.entity.*;
import openblocks.common.tileentity.*;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonGuiHandler implements IGuiHandler {

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
}
