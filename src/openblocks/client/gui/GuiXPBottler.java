package openblocks.client.gui;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import openblocks.OpenBlocks;
import openblocks.client.gui.component.*;
import openblocks.common.container.ContainerXPBottler;
import openblocks.common.tileentity.TileEntityXPBottler;
import openblocks.common.tileentity.TileEntityXPBottler.AutoSides;
import openblocks.sync.SyncableFlags;

import org.lwjgl.opengl.GL11;

public class GuiXPBottler extends BaseGuiContainer<ContainerXPBottler> {

	private GuiComponentPanel main;
	private GuiComponentTankLevel xpLevel;
	private GuiComponentProgress progress;
	private GuiComponentTabs tabs;
	private GuiComponentTab tabGlassBottle;
	private GuiComponentTab tabXPBottle;
	private GuiComponentTab tabXPFluid;
	private GuiComponentSideSelector sideSelectorGlassBottle;
	private GuiComponentSideSelector sideSelectorXPBottle;
	private GuiComponentSideSelector sideSelectorXPFluid;
	private GuiComponentCheckbox checkboxInsertGlass;
	private GuiComponentCheckbox checkboxEjectXP;
	private GuiComponentCheckbox checkboxDrinkXP;
	private GuiComponentLabel labelInsertGlass;
	private GuiComponentLabel labelEjectXPBottle;
	private GuiComponentLabel labelDrinkXP;

	public GuiXPBottler(ContainerXPBottler container) {
		super(container);

		xSize = 176;
		ySize = 151;

		TileEntityXPBottler te = container.getTileEntity();
		int meta = te.getWorldObj().getBlockMetadata(te.xCoord, te.yCoord, te.zCoord);

		main = new GuiComponentPanel(0, 0, xSize, ySize, container);

		// progress bar
		progress = new GuiComponentProgress(72, 33);

		// tank
		xpLevel = new GuiComponentTankLevel(140, 18, 17, 37);
		xpLevel.setFluidStack(new FluidStack(OpenBlocks.Fluids.openBlocksXPJuice, 1));

		// create tabs
		tabs = new GuiComponentTabs(xSize - 3, 4);

		tabGlassBottle = new GuiComponentTab(0xe4b9b0, new ItemStack(Item.glassBottle, 1), 100, 100);
		tabXPBottle = new GuiComponentTab(0xd2e58f, new ItemStack(Item.expBottle), 100, 100);
		tabXPFluid = new GuiComponentTab(0xd2e58f, new ItemStack(Item.bucketEmpty), 100, 100);

		// create side selectors
		sideSelectorGlassBottle = new GuiComponentSideSelector(30, 30, 40.0, null, meta, OpenBlocks.Blocks.xpBottler, te.getGlassSides(), true, new ISideSelectionCallback() {
			@Override
			public void onSideSelected(ForgeDirection direction) {
				getContainer().sendButtonClick(direction.ordinal());
			}
		});
		sideSelectorXPBottle = new GuiComponentSideSelector(30, 30, 40.0, null, meta, OpenBlocks.Blocks.xpBottler, te.getXPBottleSides(), true, new ISideSelectionCallback() {
			@Override
			public void onSideSelected(ForgeDirection direction) {
				getContainer().sendButtonClick(direction.ordinal() + 7);
			}
		});
		sideSelectorXPFluid = new GuiComponentSideSelector(30, 30, 40.0, null, meta, OpenBlocks.Blocks.xpBottler, te.getXPSides(), true, new ISideSelectionCallback() {
			@Override
			public void onSideSelected(ForgeDirection direction) {
				getContainer().sendButtonClick(direction.ordinal() + 14);
			}
		});

		// create checkboxes
		SyncableFlags autoFlags = te.getAutoFlags();
		checkboxInsertGlass = new GuiComponentCheckbox(10, 82, autoFlags, AutoSides.input.ordinal(), 0xFFFFFF, new ICheckboxCallback() {
			@Override
			public void onTick() {
				getContainer().sendButtonClick(21);
			}
		});
		checkboxEjectXP = new GuiComponentCheckbox(10, 82, autoFlags, AutoSides.output.ordinal(), 0xFFFFFF, new ICheckboxCallback() {
			@Override
			public void onTick() {
				getContainer().sendButtonClick(22);
			}
		});
		checkboxDrinkXP = new GuiComponentCheckbox(10, 82, autoFlags, AutoSides.xp.ordinal(), 0xFFFFFF, new ICheckboxCallback() {
			@Override
			public void onTick() {
				getContainer().sendButtonClick(23);
			}
		});

		labelInsertGlass = new GuiComponentLabel(22, 82, StatCollector.translateToLocal("openblocks.gui.autoinsert"));
		labelEjectXPBottle = new GuiComponentLabel(22, 82, StatCollector.translateToLocal("openblocks.gui.autoeject"));
		labelDrinkXP = new GuiComponentLabel(22, 82, StatCollector.translateToLocal("openblocks.gui.autodrink"));

		tabGlassBottle.addComponent(sideSelectorGlassBottle);
		tabGlassBottle.addComponent(checkboxInsertGlass);
		tabGlassBottle.addComponent(labelInsertGlass);

		tabXPBottle.addComponent(sideSelectorXPBottle);
		tabXPBottle.addComponent(checkboxEjectXP);
		tabXPBottle.addComponent(labelEjectXPBottle);

		tabXPFluid.addComponent(sideSelectorXPFluid);
		tabXPFluid.addComponent(checkboxDrinkXP);
		tabXPFluid.addComponent(labelDrinkXP);

		tabs.addComponent(tabGlassBottle);
		tabs.addComponent(tabXPBottle);
		tabs.addComponent(tabXPFluid);

		main.addComponent(xpLevel);
		main.addComponent(tabs);
		main.addComponent(progress);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		GL11.glPushMatrix();
		GL11.glTranslated(this.guiLeft, this.guiTop, 0);
		xpLevel.setPercentFull(getContainer().getTileEntity().getXPBufferRatio());
		progress.setProgress(getContainer().getTileEntity().getProgressRatio());
		main.render(this.mc, 0, 0, mouseX - this.guiLeft, mouseY - this.guiTop);
		GL11.glPopMatrix();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String machineName = StatCollector.translateToLocal("openblocks.gui.xpbottler");
		int x = this.xSize / 2 - (fontRenderer.getStringWidth(machineName) / 2);
		fontRenderer.drawString(machineName, x, 6, 4210752);
		String translatedName = StatCollector.translateToLocal("container.inventory");
		fontRenderer.drawString(translatedName, 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		main.mouseClicked(x - this.guiLeft, y - this.guiTop, button);
	}

	@Override
	protected void mouseMovedOrUp(int x, int y, int button) {
		super.mouseMovedOrUp(x, y, button);
		main.mouseMovedOrUp(x - this.guiLeft, y - this.guiTop, button);
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int button, long time) {
		super.mouseClickMove(mouseX, mouseY, button, time);
		main.mouseClickMove(mouseX - this.guiLeft, mouseY - this.guiTop, button, time);
	}

}
