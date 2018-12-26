package openblocks.client.gui;

import openblocks.common.container.ContainerDrawingTable;
import openblocks.common.tileentity.TileEntityDrawingTable;
import openblocks.rpc.IStencilCrafter;
import openblocks.rpc.IStencilCrafter.Mode;
import openmods.api.IValueProvider;
import openmods.gui.Icon;
import openmods.gui.SyncedGuiContainer;
import openmods.gui.component.BaseComponent;
import openmods.gui.component.GuiComponentIconButton;
import openmods.gui.component.GuiComponentSprite;
import openmods.gui.component.GuiComponentTextButton;
import openmods.gui.component.GuiComponentTextbox;
import openmods.gui.listener.IMouseDownListener;
import openmods.gui.logic.ValueCopyAction;
import openmods.utils.TranslationUtils;

public class GuiDrawingTable extends SyncedGuiContainer<ContainerDrawingTable> {

	private static final Icon ARROW_LEFT = Icon.createSheetIcon(BaseComponent.WIDGETS, 0, 82, 16, 16);

	private static final Icon ARROW_UP = Icon.createSheetIcon(BaseComponent.WIDGETS, 16, 82, 16, 16);

	public GuiDrawingTable(ContainerDrawingTable container) {
		super(container, 176, 204, "openblocks.gui.drawingtable");
		final IStencilCrafter rpcProxy = getContainer().getOwner().createClientRpcProxy(IStencilCrafter.class);

		TileEntityDrawingTable owner = container.getOwner();
		final IValueProvider<IStencilCrafter.Mode> mode = owner.getMode();
		final IValueProvider<String> text = owner.getTextToPrint();

		final GuiComponentTextButton modeSelection = new GuiComponentTextButton(8, 35, 50, 14, 0xFFFFFF, IStencilCrafter.Mode.STENCILS.getTranslatedName());
		modeSelection.setListener((IMouseDownListener)(component, x, y, button) -> rpcProxy.cycleMode());

		final int buttonHeight = ARROW_UP.height + GuiComponentIconButton.BORDER_SIZE;
		final GuiComponentIconButton buttonTop = new GuiComponentIconButton(88 + 8 + 2 + 18, 34 + 9 - buttonHeight, 0xFFFFFF, ARROW_UP);
		buttonTop.setListener((IMouseDownListener)(component, x, y, button) -> rpcProxy.selectionUp());

		final GuiComponentIconButton buttonBottom = new GuiComponentIconButton(88 + 8 + 2 + 18, 34 + 9, 0xFFFFFF, ARROW_UP.mirrorHorizontal());
		buttonBottom.setListener((IMouseDownListener)(component, x, y, button) -> rpcProxy.selectionDown());

		final GuiComponentTextbox textToPrint = new GuiComponentTextbox(8, 90, 120, 14);
		addSyncUpdateListener(ValueCopyAction.create(text, textToPrint));

		final GuiComponentTextButton print = new GuiComponentTextButton(130, 90, 40, 14, 0xFFFFFF, TranslationUtils.translateToLocal("openblocks.gui.drawingtable.print"));
		print.setListener((IMouseDownListener)(component, x, y, button) -> rpcProxy.printGlyphs(textToPrint.getText()));

		addSyncUpdateListener(ValueCopyAction.create(mode, newMode -> {
			modeSelection.setText(newMode.getTranslatedName());
			final boolean enablePrint = newMode == Mode.GLYPHS;
			textToPrint.setEnabled(enablePrint);
			print.setEnabled(enablePrint);
		}));

		root.addComponent(new GuiComponentSprite(88 - 8, 34, ARROW_LEFT.mirrorVertical()));
		root.addComponent(buttonTop);
		root.addComponent(buttonBottom);
		root.addComponent(modeSelection);
		root.addComponent(textToPrint);
		root.addComponent(print);
	}

}
