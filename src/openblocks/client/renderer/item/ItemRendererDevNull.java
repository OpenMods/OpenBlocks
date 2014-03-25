package openblocks.client.renderer.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.IItemRenderer;
import openmods.GenericInventory;
import openmods.ItemInventory;
import openmods.utils.ItemUtils;

public class ItemRendererDevNull implements IItemRenderer {

	private GenericInventory inventory = new GenericInventory("", false, 1);

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack stack, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {

		if (data.length == 0 || !(data[0] instanceof RenderBlocks)) { return; }

		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.thePlayer;

		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		NBTTagCompound inventoryTag = ItemInventory.getInventoryTag(tag);
		inventory.readFromNBT(inventoryTag);
		ItemStack containedStack = inventory.getStackInSlot(0);

		if (containedStack == null) return;

		RenderManager.instance.itemRenderer.renderItem(player, containedStack, 0);
	}

}
