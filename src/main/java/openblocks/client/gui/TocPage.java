package openblocks.client.gui;

import net.minecraft.client.gui.FontRenderer;
import openmods.gui.component.GuiComponentBook;
import openmods.gui.component.GuiComponentLabel;
import openmods.gui.component.page.TitledPage;
import openmods.gui.listener.IMouseDownListener;
import openmods.utils.TranslationUtils;

public class TocPage extends TitledPage {

	private static final int TOC_LINE_LEFT_MARGIN = 20;
	private static final int TOC_TOTAL_LINE_WIDTH = 140;
	private static final int TOC_START_HEIGHT = 70;
	private static final int TOC_LINE_HEIGHT = 15;

	private final GuiComponentBook book;

	private final FontRenderer fontRenderer;

	private int nextTocEntry;

	public TocPage(GuiComponentBook book, FontRenderer fontRenderer) {
		super("openblocks.gui.welcome.title", "openblocks.gui.welcome.content");
		this.book = book;
		this.fontRenderer = fontRenderer;
	}

	private static int tocLine(int index) {
		return TOC_START_HEIGHT + index * TOC_LINE_HEIGHT;
	}

	public void addTocEntry(String untranslatedLabel, int pageIndex, int displayedPage) {
		int lineWidthBudget = TOC_TOTAL_LINE_WIDTH;

		final String translatedLabel = TranslationUtils.translateToLocal(untranslatedLabel);

		final String pageNumber = Integer.toString(displayedPage);

		final int labelWidth = fontRenderer.getStringWidth(translatedLabel);
		lineWidthBudget -= labelWidth;

		final int pageNumberWidth = fontRenderer.getStringWidth(pageNumber);
		lineWidthBudget -= pageNumberWidth;

		final int spaceWidth = fontRenderer.getCharWidth(' ');
		lineWidthBudget -= 2 * spaceWidth;

		final int dotWidth = fontRenderer.getCharWidth('.');
		final StringBuilder paddedPageNumberBuilder = new StringBuilder();

		while (lineWidthBudget - dotWidth > 0) {
			paddedPageNumberBuilder.append('.');
			lineWidthBudget -= dotWidth;
		}

		paddedPageNumberBuilder.append(' ');
		paddedPageNumberBuilder.append(pageNumber);

		String paddedPageNumber = paddedPageNumberBuilder.toString();

		final IMouseDownListener pageJumpListener = book.createBookmarkListener(pageIndex);

		{
			final GuiComponentLabel label = new GuiComponentLabel(TOC_LINE_LEFT_MARGIN, tocLine(nextTocEntry), translatedLabel);
			label.setListener(pageJumpListener);
			addComponent(label);
		}

		{
			final int pos = TOC_LINE_LEFT_MARGIN + labelWidth + lineWidthBudget;
			final GuiComponentLabel label = new GuiComponentLabel(pos, tocLine(nextTocEntry), paddedPageNumber);
			label.setListener(pageJumpListener);
			addComponent(label);
		}

		nextTocEntry++;
	}
}
