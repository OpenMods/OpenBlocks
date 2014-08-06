package openblocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import openblocks.client.gui.GuiDevNull;
import openblocks.client.gui.GuiInfoBook;
import openblocks.client.gui.GuiLuggage;
import openblocks.common.container.ContainerDevNull;
import openblocks.common.container.ContainerLuggage;
import openblocks.common.entity.EntityLuggage;
import openmods.Log;
import openmods.gui.DummyContainer;
import openmods.inventory.PlayerItemInventory;
import cpw.mods.fml.common.network.IGuiHandler;

public class OpenBlocksGuiHandler implements IGuiHandler {

	public static enum GuiId {
		luggage,
		infoBook,
		devNull;

		public static final GuiId[] VALUES = GuiId.values();
	}

	private static ContainerDevNull createDevNullContainer(EntityPlayer player) {
		return new ContainerDevNull(player.inventory, new PlayerItemInventory(player, 1));
	}

	private static ContainerLuggage createLuggageContainer(EntityPlayer player, World world, int x) {
		return new ContainerLuggage(player.inventory, (EntityLuggage)world.getEntityByID(x));
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		final GuiId guiId = getGuiId(id);
		if (guiId == null) return null;

		switch (guiId) {
			case luggage:
				return createLuggageContainer(player, world, x);
			case devNull:
				if (player.inventory.getCurrentItem() == null) return null;
				return createDevNullContainer(player);
			case infoBook:
				return new DummyContainer();
			default:
				return null;
		}
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		final GuiId guiId = getGuiId(id);
		if (guiId == null) return null;

		switch (guiId) {
			case luggage:
				return new GuiLuggage(createLuggageContainer(player, world, x));
			case infoBook:
				return new GuiInfoBook();
			case devNull:
				return new GuiDevNull(createDevNullContainer(player));
			default:
				return null;
		}
	}

	private static GuiId getGuiId(int id) {
		try {
			return GuiId.VALUES[id];
		} catch (ArrayIndexOutOfBoundsException e) {
			Log.warn("Invalid GUI id: %d", id);
			return null;
		}
	}
}
