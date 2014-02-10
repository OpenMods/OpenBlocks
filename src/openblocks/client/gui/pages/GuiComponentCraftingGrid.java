package openblocks.client.gui.pages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import openmods.gui.component.BaseComponent;
import openmods.gui.component.GuiComponentSprite;

public class GuiComponentCraftingGrid extends GuiComponentSprite {

	protected static RenderItem itemRenderer = new RenderItem();
	private ItemStack[] items;

	public GuiComponentCraftingGrid(int x, int y, ItemStack[] items, Icon icon, ResourceLocation texture) {
		super(x, y, icon, texture);
		this.items = items;
		setOverlayMode(true);
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);

		int relativeMouseX = mouseX + offsetX - x;
		int relativeMouseY = mouseY + offsetY - y;
		int gridOffsetX = 1;
		int gridOffsetY = 1;
		int itemBoxSize = 19;

		ItemStack tooltip = null;
		int i = 0;
		for (ItemStack input : items) {
			if (input != null) {
				int row = (i % 3);
				int column = (int)(i / 3);
				int itemX = offsetX + gridOffsetX + (row * itemBoxSize);
				int itemY = offsetY + gridOffsetY + (column * itemBoxSize);
				drawItemStack((ItemStack)input, x + itemX, y + itemY, "");
				if (relativeMouseX > itemX - 2 && relativeMouseX < itemX - 2 + itemBoxSize &&
						relativeMouseY > itemY - 2 && relativeMouseY < itemY - 2 + itemBoxSize) {
					tooltip = (ItemStack)input;
				}
			}
			i++;
		}
		GL11.glDisable(GL11.GL_LIGHTING);
		if (tooltip != null) {
			drawItemStackTooltip(tooltip, relativeMouseX + 25, relativeMouseY + 30);
		}
	}

	protected void drawHoveringText(List par1List, int par2, int par3, FontRenderer font) {
		
		if (!par1List.isEmpty()) {
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			RenderHelper.disableStandardItemLighting();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			int k = 0;
			Iterator iterator = par1List.iterator();

			while (iterator.hasNext())
			{
				String s = (String)iterator.next();
				int l = font.getStringWidth(s);

				if (l > k)
				{
					k = l;
				}
			}

			int i1 = par2 + 12;
			int j1 = par3 - 12;
			int k1 = 8;

			if (par1List.size() > 1)
			{
				k1 += 2 + (par1List.size() - 1) * 10;
			}

			this.zLevel = 350.0F;
			itemRenderer.zLevel = 350.0F;
			int l1 = -267386864;
			this.drawGradientRect(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, l1, l1);
			this.drawGradientRect(i1 - 3, j1 + k1 + 3, i1 + k + 3, j1 + k1 + 4, l1, l1);
			this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 + k1 + 3, l1, l1);
			this.drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + k1 + 3, l1, l1);
			this.drawGradientRect(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k1 + 3, l1, l1);
			int i2 = 1347420415;
			int j2 = (i2 & 16711422) >> 1 | i2 & -16777216;
			this.drawGradientRect(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + k1 + 3 - 1, i2, j2);
			this.drawGradientRect(i1 + k + 2, j1 - 3 + 1, i1 + k + 3, j1 + k1 + 3 - 1, i2, j2);
			this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 - 3 + 1, i2, i2);
			this.drawGradientRect(i1 - 3, j1 + k1 + 2, i1 + k + 3, j1 + k1 + 3, j2, j2);

			for (int k2 = 0; k2 < par1List.size(); ++k2)
			{
				String s1 = (String)par1List.get(k2);
				font.drawStringWithShadow(s1, i1, j1, -1);

				if (k2 == 0)
				{
					j1 += 2;
				}

				j1 += 10;
			}

			this.zLevel = 0.0F;
			itemRenderer.zLevel = 0.0F;
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			RenderHelper.enableStandardItemLighting();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		}
		GL11.glColor3f(1f, 1f, 1f);
	}

	protected void drawItemStackTooltip(ItemStack par1ItemStack, int par2, int par3)
	{
		List list = par1ItemStack.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);

		for (int k = 0; k < list.size(); ++k)
		{
			if (k == 0)
			{
				list.set(k, "\u00a7" + Integer.toHexString(par1ItemStack.getRarity().rarityColor) + (String)list.get(k));
			}
			else
			{
				list.set(k, EnumChatFormatting.GRAY + (String)list.get(k));
			}
		}

		FontRenderer font = par1ItemStack.getItem().getFontRenderer(par1ItemStack);
		drawHoveringText(list, par2, par3, (font == null? Minecraft.getMinecraft().fontRenderer : font));
	}

	private void drawItemStack(ItemStack par1ItemStack, int par2, int par3, String par4Str)
	{
		GL11.glTranslatef(0.0F, 0.0F, 32.0F);
		this.zLevel = 200.0F;
		itemRenderer.zLevel = 200.0F;
		FontRenderer font = null;
		if (par1ItemStack != null) font = par1ItemStack.getItem().getFontRenderer(par1ItemStack);
		if (font == null) font = Minecraft.getMinecraft().fontRenderer;
		itemRenderer.renderItemAndEffectIntoGUI(font, Minecraft.getMinecraft().getTextureManager(), par1ItemStack, par2, par3);
		itemRenderer.renderItemOverlayIntoGUI(font, Minecraft.getMinecraft().getTextureManager(), par1ItemStack, par2, par3, par4Str);
		this.zLevel = 0.0F;
		itemRenderer.zLevel = 0.0F;
	}
}
