package openblocks.client.gui;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import openblocks.OpenBlocks.Blocks;
import openblocks.client.gui.pages.*;
import openmods.gui.component.BaseComponent;
import openmods.gui.component.BaseComponent.IComponentListener;
import openmods.gui.component.GuiComponentSprite;
import openmods.utils.render.FakeIcon;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;

public class GuiInfoBook extends GuiScreen {

	private GuiComponentSprite imgLeftBackground;
	private GuiComponentSprite imgRightBackground;

	public static Icon iconPageLeft = FakeIcon.createSheetIcon(-45, 0, -211, 180);
	public static Icon iconPageRight = FakeIcon.createSheetIcon(0, 0, 211, 180);

	private static final ResourceLocation texture = new ResourceLocation("openblocks:textures/gui/book.png");

	public ArrayList<BaseComponent> pages;

	public BaseComponent pageLeft = null;
	public BaseComponent pageRight = null;

	private int centerX;
	private int guiLeft;
	private int guiTop;

	private int index = 0;

	public GuiInfoBook() {

		imgLeftBackground = new GuiComponentSprite(0, 0, iconPageLeft, texture);
		imgRightBackground = new GuiComponentSprite(0, 0, iconPageRight, texture);

		pages = new ArrayList<BaseComponent>();

		pages.add(new BlankPage());
		pages.add(new IntroPage());
		addStandardBlockPage("elevator", Blocks.elevator);
		addStandardBlockPage("sprinkler", Blocks.sprinkler);
		addStandardBlockPage("paintmixer", Blocks.paintMixer);
		addStandardBlockPage("beartrap", Blocks.bearTrap);
		addStandardBlockPage("guide", Blocks.guide);
		addStandardBlockPage("canvas", Blocks.canvas);
		addStandardBlockPage("projector", Blocks.projector);
		addStandardBlockPage("vacuumhopper", Blocks.vacuumHopper);
		addStandardBlockPage("tank", Blocks.tank);
		addStandardBlockPage("path", Blocks.path);
		addStandardBlockPage("fan", Blocks.fan);
		addStandardBlockPage("blockbreaker", Blocks.blockBreaker);
		addStandardBlockPage("blockPlacer", Blocks.blockPlacer);
		addStandardBlockPage("itemDropper", Blocks.itemDropper);
		addStandardBlockPage("bigbutton", Blocks.bigButton);
		addStandardBlockPage("autoanvil", Blocks.autoAnvil);
		addStandardBlockPage("autoenchantmenttable", Blocks.autoEnchantmentTable);
		addStandardBlockPage("sponge", Blocks.sponge);
		addStandardBlockPage("ropeladder", Blocks.ropeLadder);
		addStandardBlockPage("village_highlighter", Blocks.villageHighlighter);
		addStandardBlockPage("xpbottler", Blocks.xpBottler);
		addStandardBlockPage("xpdrain", Blocks.xpDrain);
		addStandardBlockPage("drawingtable", Blocks.drawingTable);
		
		for (BaseComponent page : pages) {
			page.setEnabled(false);
		}

		pageLeft = pages.get(0);
		pageRight = pages.get(1);

	}

	private void addStandardBlockPage(String name, Block block) {
		if (block != null) {
			String tileName = String.format("tile.openblocks.%s.name", name);
			String tileDescription = String.format("tile.openblocks.%s.description", name);
			pages.add(new StandardBlockPage(tileName, tileDescription, new ItemStack(block)));
		}
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		if (pageLeft != null) pageLeft.mouseClicked(x - this.guiLeft, y - this.guiTop, button);
		if (pageRight != null) pageRight.mouseClicked(x - centerX, y - this.guiTop, button);

		if (index > 0 && x > guiLeft && x < guiLeft + 20 && y > guiTop + 160 && y < guiTop + 180) {
			index -= 2;
		} else if (index < pages.size() - 2 && x > centerX + 191 && x < centerX + 211 && y > guiTop + 160 && y < guiTop + 180) {
			index += 2;
		}

		pageLeft = pages.get(index);
		pageRight = index + 1 < pages.size()? pages.get(index + 1) : null;

	}

	public void drawScreen(int mouseX, int mouseY, float par3) {

		centerX = this.width / 2;
		guiLeft = centerX - 211;
		guiTop = (height - 200) / 2;

		imgLeftBackground.render(this.mc, guiLeft, guiTop, mouseX - guiLeft, mouseY - guiTop);
		imgRightBackground.render(this.mc, centerX, guiTop, mouseX - centerX, mouseY - guiTop);
		if (pageLeft != null) {
			pageLeft.setEnabled(true);
			pageLeft.render(this.mc, guiLeft, guiTop, mouseX - guiLeft, mouseY - guiTop);
		}
		if (pageRight != null) {
			pageRight.setEnabled(true);
			pageRight.render(this.mc, centerX, guiTop, mouseX - centerX, mouseY - guiTop);
		}
	}

}
