package openblocks.client.gui;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import openblocks.common.Stencil;
import openblocks.common.container.ContainerDrawingTable;
import openblocks.rpc.IStencilCrafter;
import openmods.gui.BaseGuiContainer;
import openmods.gui.Icon;
import openmods.gui.component.*;
import openmods.gui.listener.IMouseDownListener;

public class GuiDrawingTable extends BaseGuiContainer<ContainerDrawingTable> {

	public static final int BUTTON_DRAW = 0;

	private GuiComponentSprite iconDisplay;

	private int patternIndex = 0;

	private TextureAtlasSprite getStencilIcon(int index) {
		final ResourceLocation blockIcon = Stencil.VALUES[index].blockIcon;
		return mc.getTextureMapBlocks().getAtlasSprite(blockIcon.toString());
	}

	public GuiDrawingTable(ContainerDrawingTable container) {
		super(container, 176, 172, "openblocks.gui.drawingtable");
		final IStencilCrafter rpcProxy = getContainer().getOwner().createClientRpcProxy(IStencilCrafter.class);

		GuiComponentIconButton buttonLeft = new GuiComponentIconButton(47, 32, 0xFFFFFF, Icon.createSheetIcon(BaseComponent.WIDGETS, 0, 82, 16, 16));
		buttonLeft.setListener(new IMouseDownListener() {
			@Override
			public void componentMouseDown(BaseComponent component, int x, int y, int button) {
				patternIndex--;
				if (patternIndex < 0) patternIndex = Stencil.VALUES.length - 1;
				iconDisplay.setIcon(getStencilIcon(patternIndex));
			}
		});

		// TODO 1.8.9 verify stencil icon rendering
		GuiComponentIconButton buttonRight = new GuiComponentIconButton(108, 32, 0xFFFFFF, Icon.createSheetIcon(BaseComponent.WIDGETS, 16, 82, -16, 16));
		buttonRight.setListener(new IMouseDownListener() {
			@Override
			public void componentMouseDown(BaseComponent component, int x, int y, int button) {
				patternIndex++;
				if (patternIndex >= Stencil.VALUES.length) patternIndex = 0;
				iconDisplay.setIcon(getStencilIcon(patternIndex));
			}
		});
		GuiComponentTextButton buttonDraw = new GuiComponentTextButton(68, 57, 40, 13, 0xFFFFFF);
		buttonDraw.setText("Draw").setListener(new IMouseDownListener() {
			@Override
			public void componentMouseDown(BaseComponent component, int x, int y, int button) {
				rpcProxy.craft(Stencil.VALUES[patternIndex]);
			}
		});

		root.addComponent(buttonDraw);
		(iconDisplay = new GuiComponentSprite(80, 34)
				.setColor(0f, 0f, 0f))
				.setOverlayMode(true)
				.setEnabled(inventorySlots.getSlot(0).getStack() != null);
		root.addComponent(iconDisplay);
		root.addComponent(buttonLeft);
		root.addComponent(buttonRight);
	}

	@Override
	public void initGui() {
		super.initGui();
		iconDisplay.setIcon(getStencilIcon(patternIndex));
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		final Slot slot = inventorySlots.getSlot(0);
		final ItemStack stack = slot.getStack();
		iconDisplay.setEnabled(stack != null && slot.isItemValid(stack));
	}
}
