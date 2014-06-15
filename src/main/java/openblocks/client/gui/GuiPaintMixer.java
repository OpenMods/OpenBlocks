package openblocks.client.gui;

import openblocks.common.container.ContainerPaintMixer;
import openblocks.common.tileentity.TileEntityPaintMixer;
import openmods.gui.BaseGuiContainer;
import openmods.gui.component.*;
import openmods.gui.listener.IMouseDownListener;

public class GuiPaintMixer extends BaseGuiContainer<ContainerPaintMixer> {

	private GuiComponentTextButton buttonMix;
	private GuiComponentTextbox textbox;
	private GuiComponentColorPicker colorPicker;
	private GuiComponentSlider slider;

	public GuiPaintMixer(ContainerPaintMixer container) {
		super(container, 176, 200, "openblocks.gui.paintmixer");
		TileEntityPaintMixer mixer = container.getOwner();

		root.addComponent(new GuiComponentRect(121, 74, 20, 20, 0xFF4b9fc1));
		root.addComponent(new GuiComponentRect(141, 74, 20, 20, 0xFFdb7ad5));
		root.addComponent(new GuiComponentRect(121, 94, 20, 20, 0xFFe7e72a));
		root.addComponent(new GuiComponentRect(141, 94, 20, 20, 0xFF000000));

		root.addComponent(new GuiComponentLevel(118, 74 + 6, 2, 14, 0xFF4b9fc1, 0xFF888888, 0f, 2f, 0));
		root.addComponent(new GuiComponentLevel(141 + 21, 74 + 6, 2, 14, 0xFFdb7ad5, 0xFF888888, 0f, 2f, 0));
		root.addComponent(new GuiComponentLevel(118, 94 + 6, 2, 14, 0xFFe7e72a, 0xFF888888, 0f, 2f, 0));
		root.addComponent(new GuiComponentLevel(141 + 21, 94 + 6, 2, 14, 0xFF000000, 0xFF888888, 0f, 2f, 0));

		textbox = new GuiComponentTextbox(65, 90, 44, 10);
		textbox.setText(String.format("#%06X", mixer.getColor().getValue()));
		root.addComponent(textbox);

		buttonMix = new GuiComponentTextButton(125, 57, 30, 13, 0xFFFFFF);
		buttonMix.setText("Mix").setName("btnMix")
				.addListener(new IMouseDownListener() {
					@Override
					public void componentMouseDown(BaseComponent component, int x, int y, int button) {
						sendButtonClick(0);
					}
				});
		root.addComponent(buttonMix);
		colorPicker = new GuiComponentColorPicker(10, 20);
		root.addComponent(colorPicker);

		slider = new GuiComponentSlider(10, 75, 100, 0, 255, 0, false);
		root.addComponent(slider);

		root.addComponent(new GuiComponentProgress(125, 43, 0));
		root.addComponent(new GuiComponentColorBox(10, 90, 45, 10, 0xFFFFFF));
	}

	// TODO: textbox.setText(String.format("%06X", colorPicker.getColor()));

}
