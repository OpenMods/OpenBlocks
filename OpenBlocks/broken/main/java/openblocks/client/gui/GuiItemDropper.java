package openblocks.client.gui;

import openblocks.Config;
import openblocks.common.container.ContainerItemDropper;
import openblocks.common.tileentity.TileEntityItemDropper;
import openblocks.rpc.IItemDropper;
import openmods.gui.BaseGuiContainer;
import openmods.gui.component.GuiComponentCheckbox;
import openmods.gui.component.GuiComponentLabel;
import openmods.gui.component.GuiComponentSlider;
import openmods.utils.TranslationUtils;

public class GuiItemDropper extends BaseGuiContainer<ContainerItemDropper> {
	public GuiItemDropper(ContainerItemDropper container) {
		super(container, 300, 167, "openblocks.gui.itemdropper");

		final TileEntityItemDropper owner = container.getOwner();
		final IItemDropper rpc = owner.createRpcProxy();

		root.addComponent(new GuiComponentLabel(85, 50, TranslationUtils.translateToLocal("openblocks.misc.dropper.use_redstone")));

		final int sliderSteps = (int)Config.maxItemDropSpeed * 10;
		final GuiComponentSlider slider = new GuiComponentSlider(70, 20, 120, 0, Config.maxItemDropSpeed, owner.getItemSpeed(), sliderSteps, true, TranslationUtils.translateToLocal("openblocks.misc.dropper.speed"));
		slider.setListener(rpc::setItemSpeed);
		root.addComponent(slider);

		final GuiComponentCheckbox redstoneSwitch = new GuiComponentCheckbox(70, 50, owner.getUseRedstoneStrength());
		redstoneSwitch.setListener(rpc::setUseRedstoneStrength);
		root.addComponent(redstoneSwitch);
	}

}
