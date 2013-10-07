package openblocks.client.gui;

import openblocks.common.container.ContainerInventory;
import net.minecraft.client.gui.inventory.GuiContainer;

public abstract class BaseGuiContainer<T extends ContainerInventory<?>> extends GuiContainer {

	private T container;
	
	public BaseGuiContainer(T container) {
		super(container);
		this.container = container;
	}
	
	public T getContainer() {
		return container;
	}
	

}
