package openblocks.client.gui;

import openblocks.common.container.ContainerDigitalFuse;
import openblocks.common.tileentity.TileEntityDigitalFuse;
import openmods.gui.BaseGuiContainer;
import openmods.gui.component.GuiComponentLabel;
import openmods.gui.component.GuiComponentSlider;
import openmods.sync.SyncableInt;

public class GuiDigitalFuse extends BaseGuiContainer<ContainerDigitalFuse> {

	private GuiComponentSlider sliderTimer;
	private GuiComponentSlider sliderReset;
	private GuiComponentLabel timerLabel;
	private GuiComponentLabel resetLabel;
	private SyncableInt fauxClientTimer;

	public GuiDigitalFuse(ContainerDigitalFuse container) {
		super(container, 176, 177, "openblocks.gui.digitalfuse");

		final TileEntityDigitalFuse fuse = container.getOwner();

		final SyncableInt resetTime = fuse.getResetTime();
		final SyncableInt timeLeft = fuse.getTimeLeft();

		fauxClientTimer = new SyncableInt(timeLeft.getValue());

		sliderTimer = new GuiComponentSlider(45, 25, 115, 0, 800, fauxClientTimer) {
			@Override
			public void onMouseUp() {
				timeLeft.setValue(fauxClientTimer.getValue());
			}

			@Override
			public String formatValue(int value) {
				return String.format("%02d:%02d", (value % 3600) / 60, (value % 60));
			}
		};
		sliderReset = new GuiComponentSlider(45, 57, 115, 0, 800, resetTime) {
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

	@Override
	public void preRender(float mouseX, float mouseY) {
		super.preRender(mouseX, mouseY);
		SyncableInt timeLeft = getContainer().getOwner().getTimeLeft();
		if (!sliderTimer.isDragging()) {
			fauxClientTimer.setValue(timeLeft.getValue());
		}
	}

}
