package openblocks.client.gui;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.StatCollector;
import openmods.gui.IComponentParent;
import openmods.gui.component.GuiComponentBook;
import openmods.gui.component.GuiComponentLabel;
import openmods.gui.component.page.BookScaleConfig;
import openmods.gui.component.page.PageBase;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

public class ChangelogPage extends PageBase {

	private static final Function<String, String> BULLET_TRANSFORMER = new Function<String, String>() {
		@Override
		public String apply(String value) {
			return "\u00B7" + value;
		}
	};

	private final GuiComponentLabel centerTitle;

	private int center(FontRenderer renderer, String text) {
		return (getWidth() - renderer.getStringWidth(text)) / 2;
	}

	public ChangelogPage(IComponentParent parent, String currentVersion, String section, List<String> lines) {
		super(parent);
		final float scaleTitle = BookScaleConfig.getPageTitleScale();
		final float scaleContent = BookScaleConfig.getPageContentScale();
		final int lineSpace = BookScaleConfig.getTitlePageSeparator();

		section = StatCollector.translateToLocal(section);

		FontRenderer renderer = parent.getFontRenderer();
		addComponent(new GuiComponentLabel(parent, center(renderer, section), 24, section).setScale(scaleTitle));

		int titleMiddle = center(renderer, currentVersion);

		centerTitle = new GuiComponentLabel(parent, titleMiddle, 12, currentVersion);
		centerTitle.setScale(scaleTitle);
		addComponent(centerTitle);

		String contents = Joiner.on('\n').join(Iterables.transform(lines, BULLET_TRANSFORMER));

		final GuiComponentLabel lblContent = new GuiComponentLabel(parent, 15, 40, getWidth() - 10, getHeight(), contents);
		lblContent.setScale(scaleContent);
		lblContent.setAdditionalLineHeight(lineSpace);
		addComponent(lblContent);
	}

	void addPrevVersionBookmark(final GuiComponentBook book, String name, int page) {
		IComponentParent parent = book.parent;
		FontRenderer renderer = parent.getFontRenderer();
		int leftSize = renderer.getStringWidth(name) / 2;
		GuiComponentLabel bookmark = new GuiComponentLabel(parent, centerTitle.getX() - 7 - leftSize, 14, "\u00a77" + name);
		bookmark.setListener(book.createBookmarkListener(page));
		bookmark.setScale(BookScaleConfig.getPageContentScale());
		addComponent(bookmark);
	}

	void addNextVersionBookmark(final GuiComponentBook book, String name, int page) {
		IComponentParent parent = book.parent;
		GuiComponentLabel bookmark = new GuiComponentLabel(parent, centerTitle.getX() + centerTitle.getWidth() + 7, 14, "\u00a77" + name);
		bookmark.setListener(book.createBookmarkListener(page));
		bookmark.setScale(BookScaleConfig.getPageContentScale());
		addComponent(bookmark);
	}
}
