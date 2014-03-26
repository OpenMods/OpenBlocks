package openblocks.client.renderer.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraftforge.client.IItemRenderer;
import openmods.GenericInventory;
import openmods.ItemInventory;
import openmods.utils.ItemUtils;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ItemRendererDevNull implements IItemRenderer {

	protected static RenderItem itemRenderer = new RenderItem();

	private GenericInventory inventory = new GenericInventory("", false, 1);

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack stack, ItemRendererHelper helper) {
		return type != ItemRenderType.INVENTORY;
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
		
		if (type == ItemRenderType.INVENTORY) {
			
			TextureManager textureManager = mc.getTextureManager();
			FontRenderer fontRenderer = RenderManager.instance.getFontRenderer();
			
			GL11.glPushMatrix();
			RenderHelper.enableGUIStandardItemLighting();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);

			renderGUIBackground(player, stack, textureManager);
			
			// nope, nothing to render
			if (fontRenderer == null || containedStack == null || containedStack.isItemEqual(stack))  {
	            GL11.glPopMatrix();
				return;
			}
			
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
			itemRenderer.renderItemAndEffectIntoGUI(fontRenderer, textureManager, containedStack, 0, 0);
			
			String sizeToRender = "";
			if (containedStack.stackSize > 1) {
				sizeToRender = "" + containedStack.stackSize;
			}
			
			itemRenderer.renderItemOverlayIntoGUI(fontRenderer, textureManager, containedStack, 0, 0, sizeToRender);

            GL11.glPopMatrix();
		} else {
			if (containedStack == null || containedStack.isItemEqual(stack)) {
				return;
			}
			GL11.glPushMatrix();
			GL11.glTranslated(0.5, 0.5, 0.5);
			//GL11.glLineWidth(1.0f);
			//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			RenderManager.instance.itemRenderer.renderItem(player, containedStack, 0, type);
			//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
			GL11.glLineWidth(1f);
			GL11.glPopMatrix();
		}
	}

	private void renderGUIBackground(EntityPlayer player, ItemStack stack, TextureManager textureManager) {
		Icon icon = player.getItemIcon(stack, 0);
		textureManager.bindTexture(textureManager.getResourceLocation(stack.getItemSpriteNumber()));
        Tessellator tessellator = Tessellator.instance;
        itemRenderer.renderIcon(0, 0, icon, 16, 16);
	}
	
}
