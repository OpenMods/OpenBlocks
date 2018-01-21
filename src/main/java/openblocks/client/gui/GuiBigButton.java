package openblocks.client.gui;

import openblocks.common.container.ContainerBigButton;
import openmods.gui.BaseGuiContainer;
import openmods.gui.component.GuiComponentLabel;
import openmods.utils.TranslationUtils;

public class GuiBigButton extends BaseGuiContainer<ContainerBigButton> {

	private int totalTicks;

	private final GuiComponentLabel ticksLabel;

	private String createTickLabelContents() {
		return TranslationUtils.translateToLocalFormatted("openblocks.misc.total_ticks", totalTicks, totalTicks / 20.0);
	}

	public GuiBigButton(ContainerBigButton container) {
		super(container, 176, 182, "openblocks.gui.bigbutton");
		ticksLabel = new GuiComponentLabel(7, 75, 162, -1, createTickLabelContents());
		root.addComponent(ticksLabel);
	}

	@Override
	public void preRender(float mouseX, float mouseY) {
		final int containerTotalTicks = getContainer().getTotalTicks();
		if (totalTicks != containerTotalTicks) {
			totalTicks = containerTotalTicks;
			ticksLabel.setText(createTickLabelContents());
		}

		super.preRender(mouseX, mouseY);
	}
}
