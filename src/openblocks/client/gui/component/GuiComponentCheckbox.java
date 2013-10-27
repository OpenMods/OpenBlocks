package openblocks.client.gui.component;

import openblocks.sync.SyncableFlags;
import openblocks.utils.CompatibilityUtils;

import net.minecraft.client.Minecraft;

public class GuiComponentCheckbox extends BaseComponent {

	protected int color;
	
	protected boolean isMouseOver = false;
	protected SyncableFlags flags;
	protected int flagSlot;
	protected ICheckboxCallback callback;
	
	public GuiComponentCheckbox(int x, int y, SyncableFlags flags, int flagSlot, int color, ICheckboxCallback checkCallback) {
		super(x, y);
		this.color = color;
		this.flags = flags;
		this.flagSlot = flagSlot;
		this.callback = checkCallback;
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
		CompatibilityUtils.bindTextureToClient("textures/gui/components.png");
		drawTexturedModalRect(offsetX + x, offsetY + y, flags.get(flagSlot) ? 16 : 0, 62, 8, 8);
	}

	@Override
	public void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		if (isMouseOver(x, y)) {
			callback.onTick();
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
