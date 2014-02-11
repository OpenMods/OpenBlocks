package openblocks.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import openblocks.OpenBlocks.Blocks;
import openblocks.OpenBlocks.Items;
import openblocks.client.gui.pages.CreditsPage;
import openblocks.client.gui.pages.IntroPage;
import openmods.gui.component.GuiComponentBook;
import openmods.gui.component.GuiComponentYouTube;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiInfoBook extends GuiScreen {

	private int centerX;
	private int guiLeft;
	private int guiTop;

	private GuiComponentBook book;
	
	public GuiInfoBook() {

		book = new GuiComponentBook();
		
		book.addPage(new CreditsPage());
		book.addPage(new IntroPage());
		book.addStandardRecipePage("openblocks", "elevator", Blocks.elevator);
		book.addStandardRecipePage("openblocks", "sprinkler", Blocks.sprinkler);
		book.addStandardRecipePage("openblocks", "paintmixer", Blocks.paintMixer);
		book.addStandardRecipePage("openblocks", "beartrap", Blocks.bearTrap);
		book.addStandardRecipePage("openblocks", "guide", Blocks.guide);
		book.addStandardRecipePage("openblocks", "canvas", Blocks.canvas);
		book.addStandardRecipePage("openblocks", "projector", Blocks.projector);
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
		book.addStandardRecipePage("openblocks", "luggage", Items.luggage);
		book.addStandardRecipePage("openblocks", "sonicglasses", Items.sonicGlasses);
		book.addStandardRecipePage("openblocks", "hangglider", Items.hangGlider);
		book.addStandardRecipePage("openblocks", "cursor", Items.cursor);
		book.addStandardRecipePage("openblocks", "cartographer", Items.cartographer);
		book.addStandardRecipePage("openblocks", "golden_eye", Items.goldenEye);
		book.addStandardRecipePage("openblocks", "sleepingbag", Items.sleepingBag);
		book.addStandardRecipePage("openblocks", "tasty_clay", Items.tastyClay);
		book.addStandardRecipePage("openblocks", "paintbrush", Items.paintBrush);
		book.addStandardRecipePage("openblocks", "squeegee", Items.squeegee);
		book.addStandardRecipePage("openblocks", "drawingtable", Blocks.drawingTable);
		book.addStandardRecipePage("openblocks", "slimalyzer", Items.slimalyzer);

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

}
