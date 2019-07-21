package openblocks;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import openblocks.client.gui.GuiDevNull;
import openblocks.client.gui.GuiLuggage;
import openblocks.common.container.ContainerDevNull;
import openblocks.common.container.ContainerLuggage;
import openblocks.common.entity.EntityLuggage;
import openblocks.common.item.ItemDevNull;
import openmods.Log;

public class OpenBlocksGuiHandler implements IGuiHandler {

	public enum GuiId {
		luggage,
		devNull;

		public static final GuiId[] VALUES = GuiId.values();
	}

	private static ContainerDevNull createDevNullContainer(PlayerEntity player, int selectedItem) {
		return new ContainerDevNull(player.inventory, new ItemDevNull.DevNullInventory(player, selectedItem), selectedItem);
	}

	private static ContainerLuggage createLuggageContainer(PlayerEntity player, World world, int entityId) {
		final Entity entity = world.getEntityByID(entityId);
		if (entity instanceof EntityLuggage) return new ContainerLuggage(player.inventory, (EntityLuggage)entity);

		Log.warn("Trying to open luggage container for invalid entity %d:%s", entityId, entity);
		return null;
	}

	@Override
	public Object getServerGuiElement(int id, PlayerEntity player, World world, int x, int y, int z) {
		final GuiId guiId = getGuiId(id);
		if (guiId == null) return null;

		switch (guiId) {
			case luggage:
				return createLuggageContainer(player, world, x);
			case devNull:
				if (player.inventory.getCurrentItem() == null) return null;
				return createDevNullContainer(player, x);
			default:
				return null;
		}
	}

	@Override
	public Object getClientGuiElement(int id, PlayerEntity player, World world, int x, int y, int z) {
		final GuiId guiId = getGuiId(id);
		if (guiId == null) return null;

		switch (guiId) {
			case luggage: {
				final ContainerLuggage container = createLuggageContainer(player, world, x);
				return container != null? new GuiLuggage(container) : null;
			}
			case devNull:
				return new GuiDevNull(createDevNullContainer(player, x));
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
