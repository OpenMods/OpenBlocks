package openblocks.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.common.container.*;
import openblocks.common.entity.EntityLuggage;
import openblocks.common.tileentity.*;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonGuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == OpenBlocks.Gui.luggage.ordinal()) { return new ContainerLuggage(player.inventory, (EntityLuggage)world.getEntityByID(x)); }

		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (ID == OpenBlocks.Gui.lightbox.ordinal()) { return new ContainerLightbox(player.inventory, (TileEntityLightbox)tile); }
		if (ID == OpenBlocks.Gui.sprinkler.ordinal()) { return new ContainerSprinkler(player.inventory, (TileEntitySprinkler)tile); }
		if (ID == OpenBlocks.Gui.vacuumHopper.ordinal()) { return new ContainerVacuumHopper(player.inventory, (TileEntityVacuumHopper)tile); }
		if (ID == OpenBlocks.Gui.bigButton.ordinal()) { return new ContainerBigButton(player.inventory, (TileEntityBigButton)tile); }
		if (ID == OpenBlocks.Gui.XPBottler.ordinal()) { return new ContainerXPBottler(player.inventory, (TileEntityXPBottler)tile); }
		if (ID == OpenBlocks.Gui.autoAnvil.ordinal()) { return new ContainerAutoAnvil(player.inventory, (TileEntityAutoAnvil)tile); }
        if (ID == OpenBlocks.Gui.blockPlacer.ordinal()) { return new ContainerBlockPlacer(player.inventory, (TileEntityBlockPlacer)tile); }

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}
}
