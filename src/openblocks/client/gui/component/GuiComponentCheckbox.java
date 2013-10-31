package openblocks.client.gui.component;

import net.minecraft.client.Minecraft;
import openblocks.sync.SyncableFlags;
import openblocks.utils.CompatibilityUtils;

import org.lwjgl.opengl.GL11;

public class GuiComponentCheckbox extends BaseComponent {

	protected int color;

	protected boolean isMouseOver = false;
	protected SyncableFlags flags;
	protected int flagSlot;

	public GuiComponentCheckbox(int x, int y, SyncableFlags flags, int flagSlot, int color) {
		super(x, y);
		this.color = color;
		this.flags = flags;
		this.flagSlot = flagSlot;
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
		GL11.glColor4f(1, 1, 1, 1);
		CompatibilityUtils.bindTextureToClient("textures/gui/components.png");
		drawTexturedModalRect(offsetX + x, offsetY + y, flags.get(flagSlot)? 16 : 0, 62, 8, 8);
	}

	@Override
	public void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		if (isMouseOver(x, y)) {
			flags.toggle(flagSlot);
		}
	}

	@Override
	public void mouseMovedOrUp(int mouseX, int mouseY, int button) {
		super.mouseMovedOrUp(mouseX, mouseY, button);
		isMouseOver = isMouseOver(x, y);
	}

	protected boolean isMouseOver(int mouseX, int mouseY) {
		return mouseX >= x && mouseX < x + 8 && mouseY >= y && mouseY < y + 8;
	}
}
