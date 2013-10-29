package openblocks.client.gui;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import openblocks.OpenBlocks;
import openblocks.client.gui.component.*;
import openblocks.common.container.ContainerAutoAnvil;
import openblocks.common.tileentity.TileEntityAutoAnvil;
import openblocks.common.tileentity.TileEntityAutoAnvil.AutoSides;
import openblocks.sync.SyncableFlags;

import org.lwjgl.opengl.GL11;

public class GuiAutoAnvil extends BaseGuiContainer<ContainerAutoAnvil> {

	private GuiComponentPanel main;
	
	//tank
	private GuiComponentTankLevel xpLevel;

	// side selectors
	private GuiComponentSideSelector sideSelectorTool;
	private GuiComponentSideSelector sideSelectorModifier;
	private GuiComponentSideSelector sideSelectorOutput;
	private GuiComponentSideSelector sideSelectorXP;
	
	private GuiComponentTabs tabs;

	// tabs
	private GuiComponentTab tabTool;
	private GuiComponentTab tabModifier;
	private GuiComponentTab tabOutput;
	private GuiComponentTab tabXP;

	// checkboxes
	private GuiComponentCheckbox checkboxAutoExtractTool;
	private GuiComponentCheckbox checkboxAutoExtractModifier;
	private GuiComponentCheckbox checkboxAutoEjectOutput;
	private GuiComponentCheckbox checkboxAutoDrinkXP;
	
	// labels
	private GuiComponentLabel labelAutoExtractTool;
	private GuiComponentLabel labelAutoExtractModifier;
	private GuiComponentLabel labelAutoEjectOutput;
	private GuiComponentLabel labelAutoDrinkXP;
	
	// sprites
	private GuiComponentSprite spriteHammer;
	private GuiComponentSprite spritePlus;
	
	public GuiAutoAnvil(ContainerAutoAnvil container) {
		super(container);
		xSize = 176;
		ySize = 175;
		
		ItemStack enchantedAxe = new ItemStack(Item.pickaxeDiamond, 1);
		enchantedAxe.addEnchantment(Enchantment.fortune, 1);
		
		// create main panel
		main = new GuiComponentPanel(0, 0, xSize, ySize, container);
		
		// create tank level
		xpLevel = new GuiComponentTankLevel(140, 30, 17, 37);
		xpLevel.setFluidStack(new FluidStack(OpenBlocks.Fluids.openBlocksXPJuice, 1));
		
		TileEntityAutoAnvil te = container.getTileEntity();
		int meta = te.getWorldObj().getBlockMetadata(te.xCoord, te.yCoord, te.zCoord);
		
		// create tabs container
		tabs = new GuiComponentTabs(xSize - 3, 4);
		
		// create tabs
		tabTool = new GuiComponentTab(0xe4b9b0, new ItemStack(Item.pickaxeDiamond, 1), 100, 100);
		tabModifier = new GuiComponentTab(0xe4b9b0, new ItemStack(Item.enchantedBook, 1), 100, 100);
		tabOutput = new GuiComponentTab(0xe4b9b0, enchantedAxe, 100, 100);
		tabXP = new GuiComponentTab(0xe4b9b0, new ItemStack(Item.bucketEmpty, 1), 100, 100);
		
		// create side selectors
		sideSelectorTool = new GuiComponentSideSelector(30, 30, 40.0, te, meta, OpenBlocks.Blocks.autoAnvil, te.getToolSides(), true, new ISideSelectionCallback() {
			@Override
			public void onSideSelected(ForgeDirection direction) {
				getContainer().sendButtonClick(direction.ordinal());
			}
		});
		sideSelectorModifier = new GuiComponentSideSelector(30, 30, 40.0, te, meta, OpenBlocks.Blocks.autoAnvil, te.getModifierSides(), true, new ISideSelectionCallback() {
			@Override
			public void onSideSelected(ForgeDirection direction) {
				getContainer().sendButtonClick(direction.ordinal() + 7);
			}
		});
		sideSelectorOutput = new GuiComponentSideSelector(30, 30, 40.0, te, meta, OpenBlocks.Blocks.autoAnvil, te.getOutputSides(), true, new ISideSelectionCallback() {
			@Override
			public void onSideSelected(ForgeDirection direction) {
				getContainer().sendButtonClick(direction.ordinal() + 14);
			}
		});
		sideSelectorXP = new GuiComponentSideSelector(30, 30, 40.0, te, meta, OpenBlocks.Blocks.autoAnvil, te.getXPSides(), true, new ISideSelectionCallback() {
			@Override
			public void onSideSelected(ForgeDirection direction) {
				getContainer().sendButtonClick(direction.ordinal() + 21);
			}
		});

		SyncableFlags autoFlags = te.getAutoFlags();
		
		checkboxAutoExtractTool = new GuiComponentCheckbox(10, 82, autoFlags, AutoSides.tool.ordinal(), 0xFFFFFF, new ICheckboxCallback() {
			@Override
			public void onTick() {
				getContainer().sendButtonClick(28);
			}
		});
		
		checkboxAutoExtractModifier = new GuiComponentCheckbox(10, 82, autoFlags, AutoSides.modifier.ordinal(), 0xFFFFFF, new ICheckboxCallback() {
			@Override
			public void onTick() {
				getContainer().sendButtonClick(29);
			}
		});

		checkboxAutoEjectOutput = new GuiComponentCheckbox(10, 82, autoFlags, AutoSides.output.ordinal(), 0xFFFFFF, new ICheckboxCallback() {
			@Override
			public void onTick() {
				getContainer().sendButtonClick(30);
			}
		});

		checkboxAutoDrinkXP = new GuiComponentCheckbox(10, 82, autoFlags, AutoSides.xp.ordinal(), 0xFFFFFF, new ICheckboxCallback() {
			@Override
			public void onTick() {
				getContainer().sendButtonClick(31);
			}
		});
		
		// create labels
		labelAutoExtractTool = new GuiComponentLabel(22, 82, StatCollector.translateToLocal("openblocks.gui.autoextract"));
		labelAutoExtractModifier = new GuiComponentLabel(22, 82, StatCollector.translateToLocal("openblocks.gui.autoextract"));
		labelAutoEjectOutput = new GuiComponentLabel(22, 82, StatCollector.translateToLocal("openblocks.gui.autoeject"));
		labelAutoDrinkXP = new GuiComponentLabel(22, 82, StatCollector.translateToLocal("openblocks.gui.autodrink"));
		
		// create sprites
		spriteHammer = new GuiComponentSprite(80, 34, GuiComponentSprite.Sprite.hammer);
		spritePlus = new GuiComponentSprite(36, 41, GuiComponentSprite.Sprite.plus);
		
		// add checkboxes
		tabTool.addComponent(checkboxAutoExtractTool);
		tabModifier.addComponent(checkboxAutoExtractModifier);
		tabOutput.addComponent(checkboxAutoEjectOutput);
		tabXP.addComponent(checkboxAutoDrinkXP);
		
		// add side selectors
		tabTool.addComponent(sideSelectorTool);
		tabModifier.addComponent(sideSelectorModifier);
		tabOutput.addComponent(sideSelectorOutput);
		tabXP.addComponent(sideSelectorXP);
		
		// add labels
		tabTool.addComponent(labelAutoExtractTool);
		tabModifier.addComponent(labelAutoExtractModifier);
		tabOutput.addComponent(labelAutoEjectOutput);
		tabXP.addComponent(labelAutoDrinkXP);
		
		// add tabs
		tabs.addComponent(tabTool);
		tabs.addComponent(tabModifier);
		tabs.addComponent(tabOutput);
		tabs.addComponent(tabXP);
		
		// append to main
		main.addComponent(spriteHammer);
		main.addComponent(spritePlus);
		main.addComponent(tabs);
		main.addComponent(xpLevel);
		
	}


	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		GL11.glPushMatrix();
		GL11.glTranslated(this.guiLeft, this.guiTop, 0);
		xpLevel.setPercentFull(getContainer().getTileEntity().getXPBufferRatio());
		main.render(this.mc, 0, 0, mouseX - this.guiLeft, mouseY - this.guiTop);
		GL11.glPopMatrix();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String machineName = StatCollector.translateToLocal("openblocks.gui.autoanvil");
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
