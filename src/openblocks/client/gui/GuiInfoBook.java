package openblocks.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.StatCollector;
import openblocks.OpenBlocks.Blocks;
import openblocks.OpenBlocks.Items;
import openblocks.client.gui.pages.*;
import openblocks.common.item.MetasGeneric;
import openmods.gui.component.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiInfoBook extends GuiScreen implements IComponentListener {

	private int centerX;
	private int guiLeft;
	private int guiTop;

	private GuiComponentBook book;
	private GuiComponentLabel lblBlocks;
	private GuiComponentLabel lblItems;
	private GuiComponentLabel lblMisc;

	private int itemsIndex = 0;
	private int miscIndex = 0;
	private int blocksIndex = 0;

	public GuiInfoBook() {

		book = new GuiComponentBook();
		BlankPage contentsPage = new TitledPage("openblocks.gui.welcome.title", "openblocks.gui.welcome.content");

		lblBlocks = new GuiComponentLabel(27, 90, "- " + StatCollector.translateToLocal("openblocks.gui.blocks"));
		lblBlocks.addListener(this);
		lblItems = new GuiComponentLabel(27, 105, "- " + StatCollector.translateToLocal("openblocks.gui.items"));
		lblItems.addListener(this);
		lblMisc = new GuiComponentLabel(27, 120, "- " + StatCollector.translateToLocal("openblocks.gui.misc"));
		lblMisc.addListener(this);

		contentsPage.addComponent(lblBlocks);
		contentsPage.addComponent(lblItems);
		contentsPage.addComponent(lblMisc);

		book.addPage(new BlankPage());
		book.addPage(new IntroPage());
		book.addPage(new TitledPage("openblocks.gui.credits.title", "openblocks.gui.credits.content"));
		book.addPage(contentsPage);
		blocksIndex = book.getNumberOfPages();
		book.addPage(new BlankPage());
		book.addPage(new SectionPage("openblocks.gui.blocks"));
		book.addStandardRecipePage("openblocks", "elevator", Blocks.elevator);
		book.addStandardRecipePage("openblocks", "sprinkler", Blocks.sprinkler);
		book.addStandardRecipePage("openblocks", "radio", Blocks.radio);
		book.addStandardRecipePage("openblocks", "paintmixer", Blocks.paintMixer);
		book.addStandardRecipePage("openblocks", "beartrap", Blocks.bearTrap);
		book.addStandardRecipePage("openblocks", "guide", Blocks.guide);
		book.addStandardRecipePage("openblocks", "canvas", Blocks.canvas);
		/**
		 * leaving for boq
		 * book.addStandardRecipePage("openblocks", "projector",
		 * Blocks.projector);
		 **/
		book.addStandardRecipePage("openblocks", "vacuumhopper", Blocks.vacuumHopper);
		book.addStandardRecipePage("openblocks", "tank", Blocks.tank);
		book.addStandardRecipePage("openblocks", "path", Blocks.path);
		book.addStandardRecipePage("openblocks", "fan", Blocks.fan);
		book.addStandardRecipePage("openblocks", "blockbreaker", Blocks.blockBreaker);
		book.addStandardRecipePage("openblocks", "blockPlacer", Blocks.blockPlacer);
		book.addStandardRecipePage("openblocks", "itemDropper", Blocks.itemDropper);
		book.addStandardRecipePage("openblocks", "bigbutton", Blocks.bigButton);
		book.addStandardRecipePage("openblocks", "autoanvil", Blocks.autoAnvil);
		book.addStandardRecipePage("openblocks", "autoenchantmenttable", Blocks.autoEnchantmentTable);
		book.addStandardRecipePage("openblocks", "sponge", Blocks.sponge);
		book.addStandardRecipePage("openblocks", "ropeladder", Blocks.ropeLadder);
		book.addStandardRecipePage("openblocks", "village_highlighter", Blocks.villageHighlighter);
		book.addStandardRecipePage("openblocks", "xpbottler", Blocks.xpBottler);
		book.addStandardRecipePage("openblocks", "xpdrain", Blocks.xpDrain);
		book.addStandardRecipePage("openblocks", "drawingtable", Blocks.drawingTable);
		book.addStandardRecipePage("openblocks", "sky.normal", Blocks.sky);
		book.addStandardRecipePage("openblocks", "xpshower", Blocks.xpShower);

		itemsIndex = book.getNumberOfPages();
		if (itemsIndex % 2 == 1) {
			book.addPage(new BlankPage());
			itemsIndex++;
		}
		book.addPage(new BlankPage());
		book.addPage(new SectionPage("openblocks.gui.items"));
		book.addStandardRecipePage("openblocks", "luggage", Items.luggage);
		book.addStandardRecipePage("openblocks", "sonicglasses", Items.sonicGlasses);
		book.addStandardRecipePage("openblocks", "hangglider", Items.hangGlider);
		book.addStandardRecipePage("openblocks", "cursor", Items.cursor);
		book.addStandardRecipePage("openblocks", "unprepared_stencil", MetasGeneric.unpreparedStencil.newItemStack());
		book.addStandardRecipePage("openblocks", "sleepingbag", Items.sleepingBag);
		/**
		 * leaving for boq
		 * book.addStandardRecipePage("openblocks", "cartographer",
		 * Items.cartographer);
		 * book.addStandardRecipePage("openblocks", "golden_eye",
		 * Items.goldenEye);
		 * book.addStandardRecipePage("openblocks", "tasty_clay",
		 * Items.tastyClay);
		 **/
		book.addStandardRecipePage("openblocks", "paintbrush", Items.paintBrush);
		book.addStandardRecipePage("openblocks", "squeegee", Items.squeegee);
		book.addStandardRecipePage("openblocks", "slimalyzer", Items.slimalyzer);

		miscIndex = book.getNumberOfPages();
		if (miscIndex % 2 == 1) {
			book.addPage(new BlankPage());
			miscIndex++;
		}
		book.addPage(new BlankPage());
		book.addPage(new SectionPage("openblocks.gui.misc"));
		book.addPage(new TitledPage("openblocks.gui.config.title", "openblocks.gui.config.content"));
		book.addPage(new TitledPage("openblocks.gui.bkey.title", "openblocks.gui.bkey.content"));
		book.addPage(new TitledPage("openblocks.gui.unstable.title", "openblocks.gui.unstable.content"));
		book.addPage(new TitledPage("openblocks.gui.laststand.title", "openblocks.gui.laststand.content"));
		book.addPage(new TitledPage("openblocks.gui.flimflam.title", "openblocks.gui.flimflam.content"));
		book.enablePages();

	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		book.mouseClicked(x - this.guiLeft, y - this.guiTop, button);
	}

	@Override
	protected void mouseMovedOrUp(int x, int y, int button) {
		super.mouseMovedOrUp(x, y, button);
		book.mouseMovedOrUp(x - this.guiLeft, y - this.guiTop, button);
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int button, long time) {
		super.mouseClickMove(mouseX, mouseY, button, time);
		book.mouseClickMove(mouseX - this.guiLeft, mouseY - this.guiTop, button, time);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
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
		super.drawScreen(mouseX, mouseY, par3);
		centerX = this.width / 2;
		guiLeft = centerX - 211;
		guiTop = (height - 200) / 2;

		GL11.glPushMatrix();
		book.render(this.mc, guiLeft, guiTop, mouseX - this.guiLeft, mouseY - this.guiTop);
		GL11.glPopMatrix();

		// second pass
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glPushMatrix();
		book.renderOverlay(this.mc, guiLeft, guiTop, mouseX - this.guiLeft, mouseY - this.guiTop);
		GL11.glPopMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		RenderHelper.enableStandardItemLighting();
	}

	@Override
	public void componentMouseDown(BaseComponent component, int offsetX, int offsetY, int button) {
		if (component.equals(lblBlocks)) {
			book.gotoIndex(blocksIndex);
		} else if (component.equals(lblItems)) {
			book.gotoIndex(itemsIndex);
		} else if (component.equals(lblMisc)) {
			book.gotoIndex(miscIndex);
		}
	}

	@Override
	public void componentMouseDrag(BaseComponent component, int offsetX, int offsetY, int button, long time) {

	}

	@Override
	public void componentMouseMove(BaseComponent component, int offsetX, int offsetY) {

	}

	@Override
	public void componentMouseUp(BaseComponent component, int offsetX, int offsetY, int button) {

	}

	@Override
	public void componentKeyTyped(BaseComponent component, char par1, int par2) {

	}

}
