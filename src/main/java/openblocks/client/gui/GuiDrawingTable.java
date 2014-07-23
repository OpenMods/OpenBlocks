package openblocks.client.gui;

import net.minecraft.client.renderer.texture.TextureMap;
import openblocks.common.Stencil;
import openblocks.common.container.ContainerDrawingTable;
import openblocks.rpc.IStencilCrafter;
import openmods.gui.BaseGuiContainer;
import openmods.gui.component.*;
import openmods.gui.listener.IMouseDownListener;
import openmods.utils.render.FakeIcon;

public class GuiDrawingTable extends BaseGuiContainer<ContainerDrawingTable> {

	public static final int BUTTON_DRAW = 0;

	private GuiComponentIconButton buttonLeft;
	private GuiComponentIconButton buttonRight;
	private GuiComponentTextButton buttonDraw;
	private GuiComponentSprite iconDisplay;

	private int patternIndex = 0;

	public GuiDrawingTable(ContainerDrawingTable container) {
		super(container, 176, 172, "openblocks.gui.drawingtable");
		buttonLeft = new GuiComponentIconButton(47, 32, 0xFFFFFF, FakeIcon.createSheetIcon(0, 82, 16, 16), BaseComponent.TEXTURE_SHEET);
		buttonLeft.setListener(new IMouseDownListener() {
			@Override
			public void componentMouseDown(BaseComponent component, int x, int y, int button) {
				patternIndex--;
				if (patternIndex < 0) patternIndex = Stencil.VALUES.length - 1;
				iconDisplay.setIcon(Stencil.VALUES[patternIndex].getBlockIcon());
			}
		});
		buttonRight = new GuiComponentIconButton(108, 32, 0xFFFFFF, FakeIcon.createSheetIcon(16, 82, -16, 16), BaseComponent.TEXTURE_SHEET);
		buttonRight.setListener(new IMouseDownListener() {
			@Override
			public void componentMouseDown(BaseComponent component, int x, int y, int button) {
				patternIndex++;
				if (patternIndex >= Stencil.VALUES.length) patternIndex = 0;
				iconDisplay.setIcon(Stencil.VALUES[patternIndex].getBlockIcon());
			}
		});
		buttonDraw = new GuiComponentTextButton(68, 57, 40, 13, 0xFFFFFF);
		buttonDraw.setText("Draw").setListener(new IMouseDownListener() {
			@Override
			public void componentMouseDown(BaseComponent component, int x, int y, int button) {
				IStencilCrafter rpcProxy = getContainer().getOwner().createRpcProxy(IStencilCrafter.class);
				rpcProxy.craft(Stencil.VALUES[patternIndex]);
			}
		});

		root.addComponent(buttonDraw);
		(iconDisplay = new GuiComponentSprite(80, 34, Stencil.values()[0].getBlockIcon(), TextureMap.locationBlocksTexture)
				.setColor(0f, 0f, 0f))
				.setOverlayMode(true)
				.setEnabled(inventorySlots.getSlot(0).getStack() != null);
		root.addComponent(iconDisplay);
		root.addComponent(buttonLeft);
		root.addComponent(buttonRight);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		iconDisplay.setEnabled(inventorySlots.getSlot(0).getStack() != null && inventorySlots.getSlot(0).isItemValid(inventorySlots.getSlot(0).getStack()));
	}
}
