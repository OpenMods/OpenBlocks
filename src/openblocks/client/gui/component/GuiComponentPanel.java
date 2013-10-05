package openblocks.client.gui.component;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class GuiComponentPanel extends GuiComponentBox {
	
	private Container container;

	public GuiComponentPanel(int x, int y, int width, int height, Container container) {
		super(x, y, width, height, 0, 5, 0xc6c6c6);
		this.container = container;
	}
	
	@Override
	public void render(Minecraft minecraft, int mouseX, int mouseY) {
		super.render(minecraft, mouseX, mouseY);
		if (container != null) {
			for (Slot slot : (List<Slot>)container.inventorySlots) {
				drawTexturedModalRect(slot.xDisplayPosition-1, slot.yDisplayPosition-1, 0, 20, 18, 18);
			}
		}
	}

}
