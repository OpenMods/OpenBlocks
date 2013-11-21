package openmods.client.gui.component;

import java.lang.ref.WeakReference;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import openmods.utils.CompatibilityUtils;

import org.lwjgl.opengl.GL11;

public class GuiComponentPanel extends GuiComponentBox {

	private WeakReference<Container> container;

	public GuiComponentPanel(int x, int y, int width, int height, Container container) {
		super(x, y, width, height, 0, 5, 0xFFFFFF);
		this.container = new WeakReference<Container>(container);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void render(Minecraft minecraft, int x, int y, int mouseX, int mouseY) {
		super.render(minecraft, x, y, mouseX, mouseY);
		if(BaseComponent.IS_OVERLAY_PASS != isOverlay()) return;
		GL11.glColor4f(1, 1, 1, 1);
		CompatibilityUtils.bindTextureToClient("textures/gui/components.png");
		if (container != null && container.get() != null) {
			for (Slot slot : (List<Slot>)container.get().inventorySlots) {
				drawTexturedModalRect(slot.xDisplayPosition - 1, slot.yDisplayPosition - 1, 0, 20, 18, 18);
			}
		}
	}

}
