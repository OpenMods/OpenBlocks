package openblocks.client.renderer.item;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import openblocks.common.item.ItemDevNull;
import openblocks.common.item.ItemDevNull.Icons;
import openmods.inventory.ItemInventory;
import openmods.renderer.DisplayListWrapper;
import openmods.utils.TextureUtils;
import openmods.utils.render.RenderUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ItemRendererDevNull implements IItemRenderer {

	protected static RenderItem itemRenderer = new RenderItem();

	private static class Counter {
		private int value;

		public int enter() {
			return ++value;
		}

		public void exit() {
			--value;
		}
	}

	private final ThreadLocal<Counter> counter = new ThreadLocal<Counter>() {
		@Override
		protected Counter initialValue() {
			return new Counter();
		}
	};

	private DisplayListWrapper cube = new DisplayListWrapper() {

		@Override
		public void compile() {
			IIcon backgroundIcon = Icons.iconFull;
			final float minU = backgroundIcon.getMinU();
			final float minV = backgroundIcon.getMinV();
			final float maxV = backgroundIcon.getMaxV();
			final float maxU = backgroundIcon.getMaxU();

			final Tessellator tes = new Tessellator();
			tes.startDrawingQuads();

			tes.addVertexWithUV(0, 0, 0, minU, minV);
			tes.addVertexWithUV(0, 1, 0, minU, maxV);
			tes.addVertexWithUV(1, 1, 0, maxU, maxV);
			tes.addVertexWithUV(1, 0, 0, maxU, minV);

			tes.addVertexWithUV(0, 0, 1, minU, minV);
			tes.addVertexWithUV(1, 0, 1, minU, maxV);
			tes.addVertexWithUV(1, 1, 1, maxU, maxV);
			tes.addVertexWithUV(0, 1, 1, maxU, minV);

			tes.addVertexWithUV(0, 0, 0, minU, minV);
			tes.addVertexWithUV(0, 0, 1, minU, maxV);
			tes.addVertexWithUV(0, 1, 1, maxU, maxV);
			tes.addVertexWithUV(0, 1, 0, maxU, minV);

			tes.addVertexWithUV(1, 0, 0, minU, minV);
			tes.addVertexWithUV(1, 1, 0, minU, maxV);
			tes.addVertexWithUV(1, 1, 1, maxU, maxV);
			tes.addVertexWithUV(1, 0, 1, maxU, minV);

			tes.addVertexWithUV(0, 0, 0, minU, minV);
			tes.addVertexWithUV(1, 0, 0, minU, maxV);
			tes.addVertexWithUV(1, 0, 1, maxU, maxV);
			tes.addVertexWithUV(0, 0, 1, maxU, minV);

			tes.addVertexWithUV(0, 1, 0, minU, minV);
			tes.addVertexWithUV(0, 1, 1, minU, maxV);
			tes.addVertexWithUV(1, 1, 1, maxU, maxV);
			tes.addVertexWithUV(1, 1, 0, maxU, minV);

			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glFrontFace(GL11.GL_CW);
			GL11.glDisable(GL11.GL_LIGHTING);
			tes.draw();
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glFrontFace(GL11.GL_CCW);
		}
	};

	public ItemRendererDevNull() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onTextuteChange(TextureStitchEvent evt) {
		if (evt.map.getTextureType() == TextureUtils.TEXTURE_MAP_ITEMS) cube.reset();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack stack, ItemRendererHelper helper) {
		return type != ItemRenderType.INVENTORY;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack containerStack, Object... data) {
		if (data.length == 0 || !(data[0] instanceof RenderBlocks)) { return; }

		final Counter counter = this.counter.get();
		if (counter.enter() < ItemDevNull.STACK_LIMIT) {
			ItemInventory inv = new ItemInventory(containerStack, 1);
			ItemStack containedStack = inv.getStackInSlot(0);

			if (type == ItemRenderType.INVENTORY) renderInventoryStack(containerStack, containedStack);
			else renderInHandStack(type, containerStack, containedStack);
		} else {
			if (type == ItemRenderType.INVENTORY) renderOverload();
		}

		counter.exit();
	}

	private static void renderOverload() {
		RenderUtils.disableLightmap();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		TextureUtils.bindDefaultItemsTexture();
		itemRenderer.renderIcon(0, 0, Icons.iconOverload, 16, 16);
	}

	protected void renderInHandStack(ItemRenderType type, ItemStack containerStack, ItemStack containedStack) {
		GL11.glPushMatrix();

		if (type == ItemRenderType.ENTITY) {
			GL11.glTranslated(-0.25, -0.25, -0.25);
			GL11.glScaled(0.5, 0.5, 0.5);
			RenderUtils.disableLightmap();
		}

		TextureUtils.bindDefaultItemsTexture();
		cube.render();

		if (type == ItemRenderType.ENTITY) {
			RenderUtils.enableLightmap();
		}

		if (containedStack != null && (containedStack.getItem() instanceof ItemBlock)) {
			GL11.glTranslated(0.5, 0.5, 0.5);
			GL11.glScalef(0.8f, 0.8f, 0.8f);
			Minecraft mc = Minecraft.getMinecraft();
			RenderManager.instance.itemRenderer.renderItem(mc.thePlayer, containedStack, 0, ItemRenderType.EQUIPPED);
		}

		GL11.glPopMatrix();
	}

	private static void renderInventoryStack(ItemStack containerStack, ItemStack containedStack) {
		Minecraft mc = Minecraft.getMinecraft();
		TextureManager textureManager = mc.getTextureManager();
		FontRenderer fontRenderer = RenderManager.instance.getFontRenderer();

		RenderUtils.disableLightmap();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);

		TextureUtils.bindDefaultItemsTexture();
		itemRenderer.renderIcon(0, 0, Icons.iconTransparent, 16, 16);

		if (fontRenderer != null && containedStack != null) {
			GL11.glPushMatrix();
			RenderHelper.enableGUIStandardItemLighting();
			GL11.glScalef(14.0f / 16.0f, 14.0f / 16.0f, 1);
			itemRenderer.renderItemAndEffectIntoGUI(fontRenderer, textureManager, containedStack, 1, 1);
			GL11.glPopMatrix();
			final String sizeToRender = (containedStack.stackSize > 1)? Integer.toString(containedStack.stackSize) : "";
			itemRenderer.renderItemOverlayIntoGUI(fontRenderer, textureManager, containedStack, 0, 0, sizeToRender);
		}
	}
}
