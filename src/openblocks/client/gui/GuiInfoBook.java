package openblocks.client.gui;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import openblocks.OpenBlocks.Blocks;
import openblocks.OpenBlocks.Items;
import openblocks.client.gui.pages.*;
import openmods.gui.component.BaseComponent;
import openmods.gui.component.GuiComponentSprite;
import openmods.utils.render.FakeIcon;

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
		addStandardRecipePage("elevator", Blocks.elevator);
		addStandardRecipePage("sprinkler", Blocks.sprinkler);
		addStandardRecipePage("paintmixer", Blocks.paintMixer);
		addStandardRecipePage("beartrap", Blocks.bearTrap);
		addStandardRecipePage("guide", Blocks.guide);
		addStandardRecipePage("canvas", Blocks.canvas);
		addStandardRecipePage("projector", Blocks.projector);
		addStandardRecipePage("vacuumhopper", Blocks.vacuumHopper);
		addStandardRecipePage("tank", Blocks.tank);
		addStandardRecipePage("path", Blocks.path);
		addStandardRecipePage("fan", Blocks.fan);
		addStandardRecipePage("blockbreaker", Blocks.blockBreaker);
		addStandardRecipePage("blockPlacer", Blocks.blockPlacer);
		addStandardRecipePage("itemDropper", Blocks.itemDropper);
		addStandardRecipePage("bigbutton", Blocks.bigButton);
		addStandardRecipePage("autoanvil", Blocks.autoAnvil);
		addStandardRecipePage("autoenchantmenttable", Blocks.autoEnchantmentTable);
		addStandardRecipePage("sponge", Blocks.sponge);
		addStandardRecipePage("ropeladder", Blocks.ropeLadder);
		addStandardRecipePage("village_highlighter", Blocks.villageHighlighter);
		addStandardRecipePage("xpbottler", Blocks.xpBottler);
		addStandardRecipePage("xpdrain", Blocks.xpDrain);
		addStandardRecipePage("luggage", Items.luggage);
		addStandardRecipePage("sonicglasses", Items.sonicGlasses);
		addStandardRecipePage("hangglider", Items.hangGlider);
		addStandardRecipePage("cursor", Items.cursor);
		addStandardRecipePage("cartographer", Items.cartographer);
		addStandardRecipePage("golden_eye", Items.goldenEye);
		addStandardRecipePage("sleepingbag", Items.sleepingBag);
		addStandardRecipePage("tasty_clay", Items.tastyClay);
		addStandardRecipePage("paintbrush", Items.paintBrush);
		addStandardRecipePage("squeegee", Items.squeegee);
		addStandardRecipePage("drawingtable", Blocks.drawingTable);
		addStandardRecipePage("slimalyzer", Items.slimalyzer);

		for (BaseComponent page : pages) {
			page.setEnabled(false);
		}

		pageLeft = pages.get(0);
		pageRight = pages.get(1);

	}

	private boolean addStandardRecipePage(String name, Object item) {
		ItemStack stack = null;
		String type = "";
		if (item instanceof ItemStack) {
			stack = (ItemStack)item;
			type = (stack.getItem() instanceof ItemBlock)? "tile" : "item";
		}
		if (item instanceof Item) {
			stack = new ItemStack((Item)item);
			type = "item";
		} else if (item instanceof Block) {
			stack = new ItemStack((Block)item);
			type = "tile";
		}
		if (stack != null) {
			String fullName = String.format("%s.openblocks.%s.name", type, name);
			String description = String.format("%s.openblocks.%s.description", type, name);
			String video = String.format("%s.openblocks.%s.video", type, name);
			pages.add(new StandardRecipePage(fullName, description, video, stack));
			return true;
		}
		return false;
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

		if (index > 0 && x > guiLeft && x < guiLeft + 30 && y > guiTop + 150 && y < guiTop + 180) {
			index -= 2;
		} else if (index < pages.size() - 2 && x > centerX + 181 && x < centerX + 211 && y > guiTop + 150 && y < guiTop + 180) {
			index += 2;
		}

		pageLeft = pages.get(index);
		pageRight = index + 1 < pages.size()? pages.get(index + 1) : null;

	}

	@Override
	public void confirmClicked(boolean result, int action) {
		if (action == 0 && result) {
			GuiComponentYouTube.openURI(GuiComponentYouTube.youtubeUrl);
		}
		this.mc.displayGuiScreen(this);
	}

	@Override
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
