package openblocks.client.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;

public class GuiComponentTextbox extends BaseComponent {

	private int width;
	private int height;

	private GuiTextField textfield;

	public GuiComponentTextbox(int x, int y, int width, int height) {
		super(x, y);
		this.width = width;
		this.height = height;
		textfield = new GuiTextField(Minecraft.getMinecraft().fontRenderer, x, y, width, height);
		// textfield.setEnableBackgroundDrawing(false);
		// commandTextField.mouseClicked(par1, par2, par3);
	}

	public GuiComponentTextbox setText(String text) {
		textfield.setText(text);
		return this;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
		textfield.drawTextBox();
	}

	@Override
	public void keyTyped(char par1, int par2) {
		textfield.textboxKeyTyped(par1, par2);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		textfield.mouseClicked(mouseX + x, mouseY + y, button);
	}

	public String getText() {
		return textfield.getText();
	}
}
