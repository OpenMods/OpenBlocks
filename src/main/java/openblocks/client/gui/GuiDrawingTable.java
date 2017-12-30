package openblocks.client.gui;

import openblocks.common.container.ContainerDrawingTable;
import openblocks.rpc.IStencilCrafter;
import openmods.gui.BaseGuiContainer;
import openmods.gui.Icon;
import openmods.gui.component.BaseComponent;
import openmods.gui.component.GuiComponentIconButton;
import openmods.gui.component.GuiComponentSprite;
import openmods.gui.listener.IMouseDownListener;

public class GuiDrawingTable extends BaseGuiContainer<ContainerDrawingTable> {

	private static final Icon ARROW_LEFT = Icon.createSheetIcon(BaseComponent.WIDGETS, 0, 82, 16, 16);

	private static final Icon ARROW_UP = Icon.createSheetIcon(BaseComponent.WIDGETS, 16, 82, 16, 16);

	public GuiDrawingTable(ContainerDrawingTable container) {
		super(container, 176, 172, "openblocks.gui.drawingtable");
		final IStencilCrafter rpcProxy = getContainer().getOwner().createClientRpcProxy(IStencilCrafter.class);

		final int buttonHeight = ARROW_UP.height + GuiComponentIconButton.BORDER_SIZE;
		GuiComponentIconButton buttonTop = new GuiComponentIconButton(88 + 8 + 2 + 18, 34 + 9 - buttonHeight, 0xFFFFFF, ARROW_UP);
		buttonTop.setListener((IMouseDownListener)(component, x, y, button) -> rpcProxy.selectionUp());

		GuiComponentIconButton buttonBottom = new GuiComponentIconButton(88 + 8 + 2 + 18, 34 + 9, 0xFFFFFF, ARROW_UP.mirrorHorizontal());
		buttonBottom.setListener((IMouseDownListener)(component, x, y, button) -> rpcProxy.selectionDown());

		root.addComponent(new GuiComponentSprite(88 - 8, 34, ARROW_LEFT.mirrorVertical()));
		root.addComponent(buttonTop);
		root.addComponent(buttonBottom);
	}

}
