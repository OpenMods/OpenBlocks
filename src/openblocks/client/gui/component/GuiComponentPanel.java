package openblocks.client.gui.component;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import openblocks.utils.CompatibilityUtils;

import org.lwjgl.opengl.GL11;

public class GuiComponentPanel extends GuiComponentBox {
	
	private Container container;

	public GuiComponentPanel(int x, int y, int width, int height, Container container) {
		super(x, y, width, height, 0, 5, 0xFFFFFF);
		this.container = container;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void render(Minecraft minecraft, int x, int y, int mouseX, int mouseY) {
		super.render(minecraft, x, y, mouseX, mouseY);
		GL11.glColor4f(1, 1, 1, 1);
		CompatibilityUtils.bindTextureToClient("textures/gui/components.png");
		if (container != null) {
			for (Slot slot : (List<Slot>)container.inventorySlots) {
				drawTexturedModalRect(slot.xDisplayPosition-1, slot.yDisplayPosition-1, 0, 20, 18, 18);
			}
		}
	}

}
