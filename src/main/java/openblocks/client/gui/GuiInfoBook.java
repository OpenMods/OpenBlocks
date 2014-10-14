package openblocks.client.gui;

import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.util.StatCollector;
import openblocks.OpenBlocks;
import openblocks.OpenBlocks.Blocks;
import openblocks.OpenBlocks.Items;
import openblocks.client.gui.page.IntroPage;
import openblocks.common.item.MetasGeneric;
import openmods.gui.ComponentGui;
import openmods.gui.DummyContainer;
import openmods.gui.component.*;
import openmods.gui.component.page.PageBase;
import openmods.gui.component.page.SectionPage;
import openmods.gui.component.page.TitledPage;
import openmods.gui.listener.IMouseDownListener;

import org.lwjgl.opengl.GL11;

public class GuiInfoBook extends ComponentGui implements GuiYesNoCallback {

	private static final String MODID = "openblocks";

	private int itemsIndex;
	private int miscIndex;
	private int blocksIndex;

	public GuiInfoBook() {
		super(new DummyContainer(), 0, 0);
	}

	@Override
	protected BaseComposite createRoot() {
		final GuiComponentBook book = new GuiComponentBook();
		PageBase contentsPage = new TitledPage("openblocks.gui.welcome.title", "openblocks.gui.welcome.content");

		GuiComponentLabel lblBlocks = new GuiComponentLabel(27, 90, "- " + StatCollector.translateToLocal("openblocks.gui.blocks"));
		lblBlocks.setListener(new IMouseDownListener() {
			@Override
			public void componentMouseDown(BaseComponent component, int x, int y, int button) {
				book.gotoIndex(blocksIndex);
				book.enablePages();
			}
		});
		GuiComponentLabel lblItems = new GuiComponentLabel(27, 105, "- " + StatCollector.translateToLocal("openblocks.gui.items"));
		lblItems.setListener(new IMouseDownListener() {
			@Override
			public void componentMouseDown(BaseComponent component, int x, int y, int button) {
				book.gotoIndex(itemsIndex);
				book.enablePages();
			}
		});
		GuiComponentLabel lblMisc = new GuiComponentLabel(27, 120, "- " + StatCollector.translateToLocal("openblocks.gui.misc"));
		lblMisc.setListener(new IMouseDownListener() {
			@Override
			public void componentMouseDown(BaseComponent component, int x, int y, int button) {
				book.gotoIndex(miscIndex);
				book.enablePages();
			}
		});

		contentsPage.addComponent(lblBlocks);
		contentsPage.addComponent(lblItems);
		contentsPage.addComponent(lblMisc);

		book.addPage(PageBase.BLANK_PAGE);
		book.addPage(new IntroPage());
		book.addPage(new TitledPage("openblocks.gui.credits.title", "openblocks.gui.credits.content"));
		book.addPage(contentsPage);
		blocksIndex = book.getNumberOfPages();
		book.addPage(PageBase.BLANK_PAGE);
		book.addPage(new SectionPage("openblocks.gui.blocks"));
		book.addStandardRecipePage(MODID, "elevator", Blocks.elevator);
		book.addStandardRecipePage(MODID, "sprinkler", Blocks.sprinkler);
		book.addStandardRecipePage(MODID, "paintmixer", Blocks.paintMixer);
		book.addStandardRecipePage(MODID, "beartrap", Blocks.bearTrap);
		book.addStandardRecipePage(MODID, "guide", Blocks.guide);
		book.addStandardRecipePage(MODID, "canvas", Blocks.canvas);
		/**
		 * leaving for boq
		 * book.addStandardRecipePage("openblocks", "projector",
		 * Blocks.projector);
		 **/
		book.addStandardRecipePage(MODID, "vacuumhopper", Blocks.vacuumHopper);
		book.addStandardRecipePage(MODID, "tank", Blocks.tank);
		book.addStandardRecipePage(MODID, "path", Blocks.path);
		book.addStandardRecipePage(MODID, "fan", Blocks.fan);
		book.addStandardRecipePage(MODID, "blockbreaker", Blocks.blockBreaker);
		book.addStandardRecipePage(MODID, "blockPlacer", Blocks.blockPlacer);
		book.addStandardRecipePage(MODID, "itemDropper", Blocks.itemDropper);
		book.addStandardRecipePage(MODID, "bigbutton", Blocks.bigButton);
		book.addStandardRecipePage(MODID, "autoanvil", Blocks.autoAnvil);
		book.addStandardRecipePage(MODID, "autoenchantmenttable", Blocks.autoEnchantmentTable);
		book.addStandardRecipePage(MODID, "sponge", Blocks.sponge);
		book.addStandardRecipePage(MODID, "ropeladder", Blocks.ropeLadder);
		book.addStandardRecipePage(MODID, "village_highlighter", Blocks.villageHighlighter);
		book.addStandardRecipePage(MODID, "xpbottler", Blocks.xpBottler);
		book.addStandardRecipePage(MODID, "xpdrain", Blocks.xpDrain);
		book.addStandardRecipePage(MODID, "drawingtable", Blocks.drawingTable);
		book.addStandardRecipePage(MODID, "sky.normal", Blocks.sky);
		book.addStandardRecipePage(MODID, "xpshower", Blocks.xpShower);

		itemsIndex = book.getNumberOfPages();
		if (itemsIndex % 2 == 1) {
			book.addPage(PageBase.BLANK_PAGE);
			itemsIndex++;
		}
		book.addPage(PageBase.BLANK_PAGE);
		book.addPage(new SectionPage("openblocks.gui.items"));
		book.addStandardRecipePage(MODID, "luggage", Items.luggage);
		book.addStandardRecipePage(MODID, "sonicglasses", Items.sonicGlasses);
		book.addStandardRecipePage(MODID, "hangglider", Items.hangGlider);
		book.addStandardRecipePage(MODID, "cursor", Items.cursor);
		book.addStandardRecipePage(MODID, "unprepared_stencil", MetasGeneric.unpreparedStencil.newItemStack());
		book.addStandardRecipePage(MODID, "sleepingbag", Items.sleepingBag);
		book.addStandardRecipePage(MODID, "devnull", Items.devNull);
		/**
		 * leaving for boq
		 * book.addStandardRecipePage("openblocks", "cartographer",
		 * Items.cartographer);
		 * book.addStandardRecipePage("openblocks", "golden_eye",
		 * Items.goldenEye);
		 * book.addStandardRecipePage("openblocks", "tasty_clay",
		 * Items.tastyClay);
		 **/
		book.addStandardRecipePage(MODID, "paintbrush", Items.paintBrush);
		book.addStandardRecipePage(MODID, "squeegee", Items.squeegee);
		book.addStandardRecipePage(MODID, "slimalyzer", Items.slimalyzer);
		book.addStandardRecipePage(MODID, "spongeonastick", Items.spongeonastick);

		miscIndex = book.getNumberOfPages();
		if (miscIndex % 2 == 1) {
			book.addPage(PageBase.BLANK_PAGE);
			miscIndex++;
		}
		book.addPage(PageBase.BLANK_PAGE);
		book.addPage(new SectionPage("openblocks.gui.misc"));
		book.addPage(new TitledPage("openblocks.gui.config.title", "openblocks.gui.config.content"));
		book.addPage(new TitledPage("openblocks.gui.bkey.title", "openblocks.gui.bkey.content"));

		if (OpenBlocks.Enchantments.explosive != null) book.addPage(new TitledPage("openblocks.gui.unstable.title", "openblocks.gui.unstable.content"));
		if (OpenBlocks.Enchantments.lastStand != null) book.addPage(new TitledPage("openblocks.gui.laststand.title", "openblocks.gui.laststand.content"));
		if (OpenBlocks.Enchantments.flimFlam != null) book.addPage(new TitledPage("openblocks.gui.flimflam.title", "openblocks.gui.flimflam.content"));
		book.enablePages();

		xSize = book.getWidth();
		ySize = book.getHeight();
		return book;
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		prepareRenderState();
		GL11.glPushMatrix();
		root.renderOverlay(this.mc, this.guiLeft, this.guiTop, par1 - this.guiLeft, par2 - this.guiTop);
		GL11.glPopMatrix();
		restoreRenderState();
	}
}
