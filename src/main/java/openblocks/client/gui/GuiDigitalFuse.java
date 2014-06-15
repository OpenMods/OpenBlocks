package openblocks.client.gui;

import openblocks.common.container.ContainerDigitalFuse;
import openmods.gui.BaseGuiContainer;
import openmods.gui.component.GuiComponentLabel;
import openmods.gui.component.GuiComponentSlider;

public class GuiDigitalFuse extends BaseGuiContainer<ContainerDigitalFuse> {

	private GuiComponentSlider sliderTimer;
	private GuiComponentSlider sliderReset;
	private GuiComponentLabel timerLabel;
	private GuiComponentLabel resetLabel;

	public GuiDigitalFuse(ContainerDigitalFuse container) {
		super(container, 176, 177, "openblocks.gui.digitalfuse");

		// TODO reimplement functionality
		sliderTimer = new GuiComponentSlider(45, 25, 115, 0, 800, 0) {
			@Override
			public String formatValue(int value) {
				return String.format("%02d:%02d", (value % 3600) / 60, (value % 60));
			}
		};
		sliderReset = new GuiComponentSlider(45, 57, 115, 0, 800, 0) {
			@Override
			public String formatValue(int value) {
				return String.format("%02d:%02d", (value % 3600) / 60, (value % 60));
			}
		};

		timerLabel = new GuiComponentLabel(10, 27, "Timer:");
		resetLabel = new GuiComponentLabel(10, 59, "Reset:");

		root.addComponent(sliderTimer);
		root.addComponent(sliderReset);
		root.addComponent(timerLabel);
		root.addComponent(resetLabel);
	}
}
