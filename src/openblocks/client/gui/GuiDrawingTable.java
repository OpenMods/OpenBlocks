package openblocks.client.gui;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import openblocks.common.Stencil;
import openblocks.common.container.ContainerDrawingTable;
import openmods.gui.BaseGuiContainer;
import openmods.gui.component.*;
import openmods.gui.component.BaseComponent.IComponentListener;
import openmods.utils.FakeIcon;

public class GuiDrawingTable extends BaseGuiContainer<ContainerDrawingTable>
		implements IComponentListener {
	
	public static final int BUTTON_DRAW = 0;
	private static final ResourceLocation texture = new ResourceLocation("openblocks:textures/gui/components.png");

	private GuiComponentIconButton buttonLeft;
	private GuiComponentIconButton buttonRight;
	private GuiComponentTextButton buttonDraw;
	private GuiComponentSprite iconDisplay;
	
	private int patternIndex = 0;
	
	public GuiDrawingTable(ContainerDrawingTable container) {
		super(container, 176, 172, "openblocks.gui.drawingtable");
		buttonLeft = new GuiComponentIconButton(47, 32, 0xFFFFFF, FakeIcon.createSheetIcon(0, 82, 16, 16), texture);
		buttonLeft.addListener(this);
		buttonRight = new GuiComponentIconButton(108, 32, 0xFFFFFF, FakeIcon.createSheetIcon(16, 82, -16, 16), texture);
		buttonRight.addListener(this);
		root.addComponent((buttonDraw = new GuiComponentTextButton(68, 57, 40, 13, 0xFFFFFF))
				.setText("Draw")
				.setName("btnDraw")
				.addListener(this));
		(iconDisplay = new GuiComponentSprite(80, 34, Stencil.values()[0].getBlockIcon(), TextureMap.locationBlocksTexture)
				.setColor(0f,0f,0f))
				.setOverlayMode(true) // New Feature, draws very last, above items and all.	
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
	
	@Override
	public void componentMouseDown(BaseComponent component, int offsetX,
			int offsetY, int button) {

		Stencil[] stencils = Stencil.values();
		if (component.equals(buttonDraw)) {
			getContainer().getOwner().onRequestStencilCreate(stencils[patternIndex]);
		}else {
			if (component.equals(buttonLeft)) {
				patternIndex--;
				if (patternIndex < 0) {
					patternIndex = stencils.length - 1;
				}
			} else if (component.equals(buttonRight)) {
				patternIndex++;
				patternIndex = patternIndex % stencils.length;
			}
			iconDisplay.setIcon(stencils[patternIndex].getBlockIcon());
		}
	}

	@Override
	public void componentMouseDrag(BaseComponent component, int offsetX,
			int offsetY, int button, long time) {
	}

	@Override
	public void componentMouseMove(BaseComponent component, int offsetX,
			int offsetY) {
	}

	@Override
	public void componentMouseUp(BaseComponent component, int offsetX,
			int offsetY, int button) {
	}

	@Override
	public void componentKeyTyped(BaseComponent component, char par1, int par2) {
	}

}
