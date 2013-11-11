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
		panel.addComponent((buttonMix = new GuiComponentButton(125, 57, 30, 13, 0xFFFFFF))
				.setText("Mix")
				.setName("btnMix")
				.addListener(this));

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
