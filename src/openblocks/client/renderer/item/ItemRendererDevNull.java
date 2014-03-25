package openblocks.client.renderer.item;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import openmods.GenericInventory;
import openmods.ItemInventory;
import openmods.utils.ItemUtils;

public class ItemRendererDevNull implements IItemRenderer {

	protected static RenderItem itemRenderer = new RenderItem();

	private GenericInventory inventory = new GenericInventory("", false, 1);

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack stack, ItemRendererHelper helper) {
		return false;
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

		if (containedStack.isItemEqual(stack)) return;

		RenderBlocks renderBlocks = (RenderBlocks)data[0];

		if (type == ItemRenderType.INVENTORY) {

			Slot fakeSlot = new Slot(inventory, 0, 0, 0);

			FontRenderer fontRenderer = RenderManager.instance.getFontRenderer();
			RenderHelper.enableGUIStandardItemLighting();
			GL11.glPushMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			short short1 = 240;
			short short2 = 240;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)short1 / 1.0F, (float)short2 / 1.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			itemRenderer.renderItemAndEffectIntoGUI(fontRenderer, mc.getTextureManager(), containedStack, 0, 0);
			String sizeToRender = "";
			if (containedStack.stackSize > 1) {
				sizeToRender = "" + containedStack.stackSize;
			}
			itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.getTextureManager(), containedStack, 0, 0, sizeToRender);
			GL11.glPopMatrix();
		} else {

			// argh now idea how to best do 1st person and all that jazz.
			// HELP ME BOQ :'(

		}
	}

}
