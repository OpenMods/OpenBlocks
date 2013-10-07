package openblocks.client.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiComponentTab extends GuiComponentBox {

	protected static RenderItem itemRenderer = new RenderItem();
	protected GuiComponentTabs container;
	protected int expandedWidth;
	protected int expandedHeight;
	private boolean active = false;
	private ItemStack iconStack;

	public GuiComponentTab(int color, ItemStack iconStack, int expandedWidth, int expandedHeight) {
		super(0, 0, 24, 24, 0, 5, color);
		this.expandedWidth = expandedWidth;
		this.expandedHeight = expandedHeight;
		this.iconStack = iconStack;
	}

	@Override
	public void renderTopLeftCorner(int offsetX, int offsetY) {}

	@Override
	public void renderBottomLeftCorner(int offsetX, int offsetY) {}

	@Override
	public void renderLeftEdge(int offsetX, int offsetY) {}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		int targetWidth = active? expandedWidth : 24;
		int targetHeight = active? expandedHeight : 24;
		if (width != targetWidth) {
			width += Math.round((double)(targetWidth - width) / 2);
		}
		if (height != targetHeight) {
			height += Math.round((double)(targetHeight - height) / 2);
		}
		renderChildren = active && width == targetWidth && height == targetHeight;
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
		GL11.glColor4f(1, 1, 1, 1);

		RenderHelper.enableGUIStandardItemLighting();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		itemRenderer.renderItemIntoGUI(minecraft.fontRenderer, minecraft.getTextureManager(), iconStack, offsetX + x + 4, offsetY + y + 4);
		GL11.glDisable(GL11.GL_LIGHTING);

	}

	@Override
	public void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		if (isMouseOver(x, y)) {
			container.onTabClicked(this);
		}
	}

	public void setContainer(GuiComponentTabs container) {
		this.container = container;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
