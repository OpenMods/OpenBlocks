package openblocks.client.gui;

import java.util.List;

import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.util.StatCollector;
import openblocks.OpenBlocks;
import openblocks.client.ChangelogBuilder;
import openblocks.client.ChangelogBuilder.Changelog;
import openblocks.client.ChangelogBuilder.ChangelogSection;
import openblocks.client.gui.page.IntroPage;
import openmods.gui.ComponentGui;
import openmods.gui.DummyContainer;
import openmods.gui.component.BaseComposite;
import openmods.gui.component.GuiComponentBook;
import openmods.gui.component.GuiComponentLabel;
import openmods.gui.component.page.PageBase;
import openmods.gui.component.page.SectionPage;
import openmods.gui.component.page.TitledPage;
import openmods.infobook.PageBuilder;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

public class GuiInfoBook extends ComponentGui implements GuiYesNoCallback {

	private static final String MODID = "OpenBlocks";

	public GuiInfoBook() {
		super(new DummyContainer(), 0, 0);
	}

	private static void setupBookmark(GuiComponentLabel label, GuiComponentBook book, int index) {
		label.setListener(book.createBookmarkListener(index));
	}

	private static int alignToEven(final GuiComponentBook book) {
		int index = book.getNumberOfPages();
		if (index % 2 == 1) {
			book.addPage(PageBase.BLANK_PAGE);
			index++;
		}
		return index;
	}

	private static int tocLine(int index) {
		final int tocStartHeight = 80;
		final int tocLineHeight = 15;
		return tocStartHeight + index * tocLineHeight;
	}

	@Override
	protected BaseComposite createRoot() {

		final GuiComponentBook book = new GuiComponentBook();
		PageBase contentsPage = new TitledPage("openblocks.gui.welcome.title", "openblocks.gui.welcome.content");

		GuiComponentLabel lblBlocks = new GuiComponentLabel(27, tocLine(0), "- " + StatCollector.translateToLocal("openblocks.gui.blocks"));
		contentsPage.addComponent(lblBlocks);

		GuiComponentLabel lblItems = new GuiComponentLabel(27, tocLine(1), "- " + StatCollector.translateToLocal("openblocks.gui.items"));
		contentsPage.addComponent(lblItems);

		GuiComponentLabel lblMisc = new GuiComponentLabel(27, tocLine(2), "- " + StatCollector.translateToLocal("openblocks.gui.misc"));
		contentsPage.addComponent(lblMisc);

		GuiComponentLabel lblChangelogs = new GuiComponentLabel(27, tocLine(3), "- " + StatCollector.translateToLocal("openblocks.gui.changelogs"));
		contentsPage.addComponent(lblChangelogs);

		book.addPage(PageBase.BLANK_PAGE);
		book.addPage(new IntroPage());
		book.addPage(new TitledPage("openblocks.gui.credits.title", "openblocks.gui.credits.content"));
		book.addPage(contentsPage);

		PageBuilder builder = new PageBuilder();

		{
			int blocksIndex = alignToEven(book);
			setupBookmark(lblBlocks, book, blocksIndex);
			book.addPage(PageBase.BLANK_PAGE);
			book.addPage(new SectionPage("openblocks.gui.blocks"));
			builder.addBlockPages(MODID);
			builder.addPages(book);
		}

		{
			int itemsIndex = alignToEven(book);
			setupBookmark(lblItems, book, itemsIndex);
			book.addPage(PageBase.BLANK_PAGE);
			book.addPage(new SectionPage("openblocks.gui.items"));
			builder.addItemPages(MODID);
			builder.addPages(book);
		}

		{
			int miscIndex = alignToEven(book);
			setupBookmark(lblMisc, book, miscIndex);
			book.addPage(PageBase.BLANK_PAGE);
			book.addPage(new SectionPage("openblocks.gui.misc"));
			book.addPage(new TitledPage("openblocks.gui.config.title", "openblocks.gui.config.content"));
			book.addPage(new TitledPage("openblocks.gui.bkey.title", "openblocks.gui.bkey.content"));
		}

		if (OpenBlocks.Enchantments.explosive != null) book.addPage(new TitledPage("openblocks.gui.unstable.title", "openblocks.gui.unstable.content"));
		if (OpenBlocks.Enchantments.lastStand != null) book.addPage(new TitledPage("openblocks.gui.laststand.title", "openblocks.gui.laststand.content"));
		if (OpenBlocks.Enchantments.flimFlam != null) book.addPage(new TitledPage("openblocks.gui.flimflam.title", "openblocks.gui.flimflam.content"));

		int changelogsIndex = alignToEven(book);
		book.addPage(PageBase.BLANK_PAGE);
		setupBookmark(lblChangelogs, book, changelogsIndex);
		book.addPage(new SectionPage("openblocks.gui.changelogs"));

		createChangelogPages(book);

		book.enablePages();

		xSize = book.getWidth();
		ySize = book.getHeight();

		return book;
	}

	private static void createChangelogPages(final GuiComponentBook book) {
		String prevVersion = null;
		int prevIndex = 0;
		List<ChangelogPage> prevPages = Lists.newArrayList();

		final List<Changelog> changelogs = ChangelogBuilder.readChangeLogs();
		for (int i = 0; i < changelogs.size(); i++) {
			Changelog changelog = changelogs.get(i);
			final String currentVersion = changelog.version;
			int currentPage = book.getNumberOfPages();

			for (ChangelogPage prevPage : prevPages)
				prevPage.addNextVersionBookmark(book, currentVersion, currentPage);

			prevPages.clear();

			for (ChangelogSection section : changelog.sections) {
				ChangelogPage page = new ChangelogPage(currentVersion, section.title, section.lines);
				book.addPage(page);
				prevPages.add(page);

				if (i > 0) {
					page.addPrevVersionBookmark(book, prevVersion, prevIndex);
				}
			}

			alignToEven(book);

			prevVersion = currentVersion;
			prevIndex = currentPage;
		}
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
