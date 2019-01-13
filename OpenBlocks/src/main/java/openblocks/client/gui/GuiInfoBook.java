package openblocks.client.gui;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import openblocks.OpenBlocks;
import openblocks.client.ChangelogBuilder;
import openblocks.client.ChangelogBuilder.Changelog;
import openblocks.client.ChangelogBuilder.ChangelogSection;
import openblocks.client.gui.page.IntroPage;
import openblocks.common.PlayerInventoryStore;
import openmods.Log;
import openmods.gui.ComponentGui;
import openmods.gui.DummyContainer;
import openmods.gui.component.BaseComposite;
import openmods.gui.component.GuiComponentBook;
import openmods.gui.component.page.PageBase;
import openmods.gui.component.page.PageBase.ActionIcon;
import openmods.gui.component.page.SectionPage;
import openmods.gui.component.page.TitledPage;
import openmods.infobook.PageBuilder;
import org.lwjgl.input.Keyboard;

public class GuiInfoBook extends ComponentGui {

	private GuiComponentBook book;

	public GuiInfoBook() {
		super(new DummyContainer(), 0, 0);
	}

	private static final PageBase blankPage = new PageBase() {};

	@Override
	public void initGui() {
		super.initGui();
		// Nothing can change this value, otherwise client will crash when player picks item
		// this.mc.thePlayer.openContainer = this.inventorySlots;
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
	}

	private static int alignToEven(GuiComponentBook book) {
		int index = book.getNumberOfPages();
		if ((index & 1) == 1) {
			book.addPage(blankPage);
			index++;
		}
		return index;
	}

	@Override
	public void handleKeyboardInput() throws IOException {
		super.handleKeyboardInput();

		if (Keyboard.getEventKeyState()) {
			switch (Keyboard.getEventKey()) {
				case Keyboard.KEY_PRIOR:
					book.prevPage();
					break;
				case Keyboard.KEY_NEXT:
					book.nextPage();
					break;
				case Keyboard.KEY_HOME:
					book.firstPage();
					break;
				case Keyboard.KEY_END:
					book.lastPage();
					break;
			}
		}
	}

	@Override
	protected BaseComposite createRoot() {
		book = new GuiComponentBook();

		book.addPage(blankPage);
		book.addPage(new IntroPage());
		book.addPage(new TitledPage("openblocks.gui.credits.title", "openblocks.gui.credits.content"));

		final TocPage contentsPage = new TocPage(book, Minecraft.getMinecraft().fontRenderer);
		book.addPage(contentsPage);

		{
			addSectionPage(book, contentsPage, "openblocks.gui.blocks");

			PageBuilder builder = new PageBuilder();
			builder.includeModId(OpenBlocks.MODID);
			builder.createBlockPages();
			builder.insertTocPages(book, 4, 4, 1.5f);
			alignToEven(book);
			builder.insertPages(book);
		}

		{
			addSectionPage(book, contentsPage, "openblocks.gui.items");

			PageBuilder builder = new PageBuilder();
			builder.includeModId(OpenBlocks.MODID);
			builder.createItemPages();
			builder.insertTocPages(book, 4, 4, 1.5f);
			alignToEven(book);
			builder.insertPages(book);
		}

		{
			addSectionPage(book, contentsPage, "openblocks.gui.misc");

			book.addPage(new TitledPage("openblocks.gui.config.title", "openblocks.gui.config.content"));
			book.addPage(new TitledPage("openblocks.gui.restore_inv.title", "openblocks.gui.restore_inv.content")
					.addActionButton(10, 133, getSavePath(), ActionIcon.FOLDER.icon, "openblocks.gui.save_folder"));
			book.addPage(new TitledPage("openblocks.gui.bkey.title", "openblocks.gui.bkey.content"));

			if (OpenBlocks.Enchantments.explosive != null) book.addPage(new TitledPage("openblocks.gui.unstable.title", "openblocks.gui.unstable.content"));
			if (OpenBlocks.Enchantments.lastStand != null) book.addPage(new TitledPage("openblocks.gui.laststand.title", "openblocks.gui.laststand.content"));
			if (OpenBlocks.Enchantments.flimFlam != null) book.addPage(new TitledPage("openblocks.gui.flimflam.title", "openblocks.gui.flimflam.content"));

		}

		{
			addSectionPage(book, contentsPage, "openblocks.gui.changelogs");
			createChangelogPages(book);
		}

		book.enablePages();

		xSize = book.getWidth();
		ySize = book.getHeight();

		return book;
	}

	private static void addSectionPage(GuiComponentBook book, TocPage contentsPage, String sectionLabel) {
		final int startIndex = alignToEven(book);

		book.addPage(blankPage);
		book.addPage(new SectionPage(sectionLabel));
		contentsPage.addTocEntry(sectionLabel, startIndex, startIndex + 2);
	}

	private static File getSavePath() {
		try {
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

			if (server != null) {
				World world = server.getWorld(0);
				return PlayerInventoryStore.getSaveFolder(world);
			}
		} catch (Throwable t) {
			Log.warn(t, "Failed to get save folder from local server");
		}

		try {
			return Minecraft.getMinecraft().mcDataDir;
		} catch (Throwable t) {
			Log.warn(t, "Failed to get save folder from MC data dir");
		}

		return new File("invalid.path");
	}

	private static void createChangelogPages(GuiComponentBook book) {
		String prevVersion = null;
		int prevIndex = 0;
		List<ChangelogPage> prevPages = Lists.newArrayList();

		final List<Changelog> changelogs = ChangelogBuilder.readChangeLogs();
		for (int i = 0; i < changelogs.size(); i++) {
			Changelog changelog = changelogs.get(i);
			final String currentVersion = changelog.version;
			int currentPage = book.getNumberOfPages();

			for (ChangelogPage prevPage : prevPages)
				prevPage.setNextVersionBookmark(book, currentVersion, currentPage);

			prevPages.clear();

			for (ChangelogSection section : changelog.sections) {
				ChangelogPage page = new ChangelogPage(currentVersion, section.title, section.lines);
				book.addPage(page);
				prevPages.add(page);

				if (i > 0) {
					page.setPrevVersionBookmark(book, prevVersion, prevIndex);
				}
			}

			alignToEven(book);

			prevVersion = currentVersion;
			prevIndex = currentPage;
		}
	}
}
