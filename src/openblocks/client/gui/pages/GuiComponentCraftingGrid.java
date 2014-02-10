package openblocks.client.gui.pages;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import openmods.gui.component.BaseComponent;
import openmods.gui.component.GuiComponentSprite;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

public class GuiComponentCraftingGrid extends GuiComponentSprite {

	private static final int CRAZY_1 = 0x505000FF;
	private static final int CRAZY_2 = (CRAZY_1 & 0xFEFEFE) >> 1 | CRAZY_1 & -0xFF000000;
	private static final int CRAZY_3 = 0xF0100010;

	protected static RenderItem itemRenderer = new RenderItem();
	private ItemStack[] items;

	public GuiComponentCraftingGrid(int x, int y, ItemStack[] items, Icon icon, ResourceLocation texture) {
		super(x, y, icon, texture);
		this.items = items;
	}

	@Override
	public void renderOverlay(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.renderOverlay(minecraft, offsetX, offsetY, mouseX, mouseY);
		
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
				int column = i / 3;
				int itemX = offsetX + gridOffsetX + (row * itemBoxSize);
				int itemY = offsetY + gridOffsetY + (column * itemBoxSize);
				drawItemStack(input, x + itemX, y + itemY, "");
				if (relativeMouseX > itemX - 2 && relativeMouseX < itemX - 2 + itemBoxSize &&
						relativeMouseY > itemY - 2 && relativeMouseY < itemY - 2 + itemBoxSize) {
					tooltip = input;
				}
			}
			i++;
		}
		if (tooltip != null) {
			drawItemStackTooltip(tooltip, relativeMouseX + 25, relativeMouseY + 30);
		}
	}

	protected void drawHoveringText(List<String> lines, int x, int y, FontRenderer font) {
		if (lines.isEmpty()) return;

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
		// GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		int width = 0;

		for (String s : lines) {
			int l = font.getStringWidth(s);
			if (l > width) width = l;
		}

		final int i1 = x + 12;
		int j1 = y - 12;

		final int lineCount = lines.size();

		int height = 8;
		if (lineCount > 1) height += 2 + (lineCount - 1) * 10;

		this.zLevel = 350.0F;
		itemRenderer.zLevel = 350.0F;

		drawGradientRect(i1 - 3, j1 - 4, i1 + width + 3, j1 - 3, CRAZY_3, CRAZY_3);
		drawGradientRect(i1 - 3, j1 + height + 3, i1 + width + 3, j1 + height + 4, CRAZY_3, CRAZY_3);
		drawGradientRect(i1 - 3, j1 - 3, i1 + width + 3, j1 + height + 3, CRAZY_3, CRAZY_3);
		drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + height + 3, CRAZY_3, CRAZY_3);
		drawGradientRect(i1 + width + 3, j1 - 3, i1 + width + 4, j1 + height + 3, CRAZY_3, CRAZY_3);

		drawGradientRect(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + height + 3 - 1, CRAZY_1, CRAZY_2);
		drawGradientRect(i1 + width + 2, j1 - 3 + 1, i1 + width + 3, j1 + height + 3 - 1, CRAZY_1, CRAZY_2);
		drawGradientRect(i1 - 3, j1 - 3, i1 + width + 3, j1 - 3 + 1, CRAZY_1, CRAZY_1);
		drawGradientRect(i1 - 3, j1 + height + 2, i1 + width + 3, j1 + height + 3, CRAZY_2, CRAZY_2);

		for (int i = 0; i < lineCount; ++i) {
			String s1 = lines.get(i);
			font.drawStringWithShadow(s1, i1, j1, -1);
			if (i == 0) j1 += 2;
			j1 += 10;
		}

		this.zLevel = 0.0F;
		itemRenderer.zLevel = 0.0F;
		// GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		RenderHelper.enableStandardItemLighting();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);

	}

	protected void drawItemStackTooltip(ItemStack stack, int x, int y) {
		final Minecraft mc = Minecraft.getMinecraft();
		FontRenderer font = Objects.firstNonNull(stack.getItem().getFontRenderer(stack), mc.fontRenderer);

		@SuppressWarnings("unchecked")
		List<String> list = stack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);

		List<String> colored = Lists.newArrayListWithCapacity(list.size());
		colored.add(getRarityColor(stack) + list.get(0));
		for (String line : list)
			colored.add(EnumChatFormatting.GRAY + line);

		drawHoveringText(colored, x, y, font);
	}

	protected EnumChatFormatting getRarityColor(ItemStack stack) {
		return EnumChatFormatting.values()[stack.getRarity().rarityColor];
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
