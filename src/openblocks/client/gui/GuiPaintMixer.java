package openblocks.client.gui;

import openblocks.client.gui.component.*;
import openblocks.client.gui.component.BaseComponent.IComponentListener;
import openblocks.common.container.ContainerPaintMixer;
import openblocks.common.tileentity.TileEntityPaintMixer;

public class GuiPaintMixer extends BaseGuiContainer<ContainerPaintMixer>
		implements IComponentListener {

	private static final int INVALIDATE_COLOR = 1;
	private GuiComponentButton buttonMix;
	private GuiComponentTextbox textbox;
	private GuiComponentColorPicker colorPicker;

	public GuiPaintMixer(ContainerPaintMixer container) {
		super(container, 176, 200, "openblocks.gui.paintmixer");
		TileEntityPaintMixer mixer = container.getOwner();

		panel.addComponent(new GuiComponentRect(121, 74, 20, 20, 0xFF4b9fc1));
		panel.addComponent(new GuiComponentRect(141, 74, 20, 20, 0xFFdb7ad5));
		panel.addComponent(new GuiComponentRect(121, 94, 20, 20, 0xFFe7e72a));
		panel.addComponent(new GuiComponentRect(141, 94, 20, 20, 0xFF000000));

		panel.addComponent(new GuiComponentLevel(118, 74 + 6, 2, 14, 0xFF4b9fc1, 0xFF888888, 0f, 2f, mixer.lvlCyan));
		panel.addComponent(new GuiComponentLevel(141 + 21, 74 + 6, 2, 14, 0xFFdb7ad5, 0xFF888888, 0f, 2f, mixer.lvlMagenta));
		panel.addComponent(new GuiComponentLevel(118, 94 + 6, 2, 14, 0xFFe7e72a, 0xFF888888, 0f, 2f, mixer.lvlYellow));
		panel.addComponent(new GuiComponentLevel(141 + 21, 94 + 6, 2, 14, 0xFF000000, 0xFF888888, 0f, 2f, mixer.lvlBlack));

		panel.addComponent(new GuiComponentLabel(57, 91, "#"));
		panel.addComponent(textbox = new GuiComponentTextbox(65, 90, 44, 10)
				.setText(Integer.toHexString(mixer.getColor().getValue())));

		panel.addComponent((buttonMix = new GuiComponentButton(125, 57, 30, 13, 0xFFFFFF))
				.setText("Mix")
				.setName("btnMix")
				.addListener(this));

		panel.addComponent((colorPicker = new GuiComponentColorPicker(10, 20, mixer.getColor())).addListener(this));
		panel.addComponent(new GuiComponentProgress(125, 43, mixer.getProgress()));
		panel.addComponent(new GuiComponentColorBox(10, 90, 45, 10, mixer.getColor()));
	}

	@Override
	public void confirmClicked(boolean par1, int par2) {
		super.confirmClicked(par1, par2);
		if (!par1) return;
		switch (par2) {
			case INVALIDATE_COLOR:
				textbox.setText(Integer.toHexString(colorPicker.getColor().getValue()));
				break;
		}
	}

	@Override
	public void componentMouseDown(BaseComponent component, int offsetX, int offsetY, int button) {
		if (component.equals(buttonMix)) {
			sendButtonClick(0);
		} else if (component.equals(colorPicker)) {
			confirmClicked(true, INVALIDATE_COLOR);
		}
	}

	@Override
	public void componentMouseDrag(BaseComponent component, int offsetX, int offsetY, int button, long time) {
		if (component.equals(colorPicker)) {
			confirmClicked(true, INVALIDATE_COLOR);
		}
	}

	@Override
	public void componentMouseMove(BaseComponent component, int offsetX, int offsetY) {}

	@Override
	public void componentMouseUp(BaseComponent component, int offsetX, int offsetY, int button) {
		if (component.equals(colorPicker)) {
			confirmClicked(true, INVALIDATE_COLOR);
		}
	}

	@Override
	public void componentKeyTyped(BaseComponent component, char par1, int par2) {
		try {
			int col = Integer.parseInt(textbox.getText(), 16);
			getContainer().getOwner().getColor().setValue(col);
			colorPicker.setFromColor(col);
			confirmClicked(true, INVALIDATE_COLOR);
		} catch (Exception e) {}
	}

}
