package openmods.client.gui.component;

import net.minecraft.client.Minecraft;
import openmods.sync.SyncableFlags;
import openmods.utils.CompatibilityUtils;

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
		flags.toggle(flagSlot);
	}

	@Override
	public void mouseMovedOrUp(int mouseX, int mouseY, int button) {
		super.mouseMovedOrUp(mouseX, mouseY, button);
		// The button in a MoveOrUp event is -1 on a move
		// Because these methods are ONLY called if the component
		// Is under the cursor. This actually makes logical sense ;)
		// -NC
		isMouseOver = button == -1;
	}

	@Override
	public int getHeight() {
		return 8;
	}

	@Override
	public int getWidth() {
		return 8;
	}
}
