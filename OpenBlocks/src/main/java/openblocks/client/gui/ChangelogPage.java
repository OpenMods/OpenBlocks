package openblocks.client.gui;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import java.util.List;
import openmods.gui.component.EmptyComposite;
import openmods.gui.component.GuiComponentBook;
import openmods.gui.component.GuiComponentHBox;
import openmods.gui.component.GuiComponentHCenter;
import openmods.gui.component.GuiComponentLabel;
import openmods.gui.component.page.BookScaleConfig;
import openmods.gui.component.page.PageBase;
import openmods.utils.TranslationUtils;

public class ChangelogPage extends PageBase {

	final GuiComponentLabel prevVersionLabel;

	final GuiComponentLabel nextVersionBookmark;

	public ChangelogPage(String currentVersion, String section, List<String> lines) {
		final float scaleTitle = BookScaleConfig.getPageTitleScale();
		final float scaleContent = BookScaleConfig.getPageContentScale();
		final int lineSpace = BookScaleConfig.getTitlePageSeparator();

		section = TranslationUtils.translateToLocal(section);

		final GuiComponentLabel currentVersionLabel = new GuiComponentLabel(0, 0, section).setScale(scaleTitle);
		addComponent(new GuiComponentHCenter(0, 24, getWidth()).addComponent(currentVersionLabel));

		{
			final GuiComponentHBox titleLine = new GuiComponentHBox(0, 0);

			{
				prevVersionLabel = new GuiComponentLabel(0, 2, "");
				prevVersionLabel.setScale(BookScaleConfig.getPageContentScale());
				titleLine.addComponent(prevVersionLabel);
			}

			titleLine.addComponent(new EmptyComposite(0, 0, 0, 7));

			{
				final GuiComponentLabel centerVersionLabel = new GuiComponentLabel(0, 0, currentVersion);
				centerVersionLabel.setScale(scaleTitle);
				titleLine.addComponent(centerVersionLabel);
			}

			titleLine.addComponent(new EmptyComposite(0, 0, 0, 7));

			{
				nextVersionBookmark = new GuiComponentLabel(0, 2, "");
				nextVersionBookmark.setScale(BookScaleConfig.getPageContentScale());
				titleLine.addComponent(nextVersionBookmark);
			}

			addComponent(new GuiComponentHCenter(0, 12, getWidth()).addComponent(titleLine));
		}

		String contents = Joiner.on('\n').join(Iterables.transform(lines, value -> "\u00B7" + value));

		final GuiComponentLabel lblContent = new GuiComponentLabel(15, 40, getWidth() - 10, getHeight(), contents);
		lblContent.setScale(scaleContent);
		lblContent.setAdditionalLineHeight(lineSpace);
		addComponent(lblContent);
	}

	void setPrevVersionBookmark(GuiComponentBook book, String name, int page) {
		prevVersionLabel.setText("\u00a77" + name);
		prevVersionLabel.setListener(book.createBookmarkListener(page));
	}

	void setNextVersionBookmark(GuiComponentBook book, String name, int page) {
		nextVersionBookmark.setText("\u00a77" + name);
		nextVersionBookmark.setListener(book.createBookmarkListener(page));
	}
}
