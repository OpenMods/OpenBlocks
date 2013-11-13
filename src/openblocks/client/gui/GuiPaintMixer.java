package openblocks.client.gui;

import openblocks.client.gui.component.*;
import openblocks.client.gui.component.BaseComponent.IComponentListener;
import openblocks.common.container.ContainerPaintMixer;
import openblocks.common.tileentity.TileEntityPaintMixer;

public class GuiPaintMixer extends BaseGuiContainer<ContainerPaintMixer>
		implements IComponentListener {

	private GuiComponentButton buttonMix;

	public GuiPaintMixer(ContainerPaintMixer container) {
		super(container, 176, 200, "openblocks.gui.paintmixer");
		TileEntityPaintMixer mixer = container.getOwner();

		panel.addComponent(new GuiComponentRect(121, 74, 20, 20, 0xFF4b9fc1));
		panel.addComponent(new GuiComponentRect(141, 74, 20, 20, 0xFFdb7ad5));
		panel.addComponent(new GuiComponentRect(121, 94, 20, 20, 0xFFe7e72a));
		panel.addComponent(new GuiComponentRect(141, 94, 20, 20, 0xFF000000));		

		panel.addComponent(new GuiComponentLevel(118, 74+6, 2, 14, 0xFF4b9fc1, 0xFF888888, 0f, 2f, mixer.lvlCyan));
		panel.addComponent(new GuiComponentLevel(141+21, 74+6, 2, 14, 0xFFdb7ad5, 0xFF888888, 0f, 2f, mixer.lvlMagenta));
		panel.addComponent(new GuiComponentLevel(118, 94+6, 2, 14, 0xFFe7e72a, 0xFF888888, 0f, 2f, mixer.lvlYellow));
		panel.addComponent(new GuiComponentLevel(141+21, 94+6, 2, 14, 0xFF000000, 0xFF888888, 0f, 2f, mixer.lvlBlack));
		
		panel.addComponent((buttonMix = new GuiComponentButton(125, 57, 30, 13, 0xFFFFFF))
				.setText("Mix")
				.setName("btnMix")
				.addListener(this));
		
		// TODO: Make GuiComponentTextBox and add Hex input
		// panel.addComponent(new GuiComponentLabel(10, 92, "#"));

		panel.addComponent(new GuiComponentColorPicker(10, 20, mixer.getColor()));
		panel.addComponent(new GuiComponentProgress(125, 43, mixer.getProgress()));
	}

	@Override
	public void componentMouseDown(BaseComponent component, int offsetX, int offsetY, int button) {
		if (component.equals(buttonMix)) {
			sendButtonClick(0);
		}
	}

	@Override
	public void componentMouseDrag(BaseComponent component, int offsetX, int offsetY, int button, long time) {}

	@Override
	public void componentMouseMove(BaseComponent component, int offsetX, int offsetY) {}

	@Override
	public void componentMouseUp(BaseComponent component, int offsetX, int offsetY, int button) {}

}
