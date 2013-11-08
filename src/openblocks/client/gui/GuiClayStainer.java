package openblocks.client.gui;

import net.minecraft.item.ItemStack;
import openblocks.OpenBlocks;
import openblocks.client.gui.component.GuiComponentColorPicker;
import openblocks.common.block.BlockSpecialStainedClay;
import openblocks.common.container.ContainerClayStainer;
import openblocks.common.tileentity.TileEntityClayStainer;

public class GuiClayStainer extends BaseGuiContainer<ContainerClayStainer> {

	private GuiComponentColorPicker colorPicker;
	
	public GuiClayStainer(ContainerClayStainer container) {
		super(container, 176, 190, "openblocks.gui.claystainer");
		
		TileEntityClayStainer stainer = container.getOwner();
		
		colorPicker = new GuiComponentColorPicker(10, 20, stainer.getColor(), stainer.getTone()) {
			@Override
			public void mouseClicked(int mouseX, int mouseY, int button) {
				super.mouseClicked(mouseX, mouseY, button);
				ItemStack stack = mc.thePlayer.inventory.getItemStack();
				if (stack != null && stack.getItem() == new ItemStack(OpenBlocks.Blocks.specialStainedClay).getItem()) {
					int color = BlockSpecialStainedClay.getColorFromNBT(stack);
					setFromColor(color);
					calculateColor();
				}
			}
		};
		
		panel.addComponent(colorPicker);
	}

}
