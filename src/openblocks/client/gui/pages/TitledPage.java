package openblocks.client.gui.pages;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;
import openmods.gui.component.GuiComponentLabel;
import openmods.utils.StringUtils;

public class TitledPage extends BlankPage {

	private GuiComponentLabel lblTitle;
	private GuiComponentLabel lblContent;

	public TitledPage(String title, String content) {

		String translatedTitle = StatCollector.translateToLocal(title);
		String translatedContent = StringUtils.format(StatCollector.translateToLocal(content));

		int x = (getWidth() - Minecraft.getMinecraft().fontRenderer.getStringWidth(translatedTitle)) / 2;
		lblTitle = new GuiComponentLabel(x, 12, translatedTitle);

		lblContent = new GuiComponentLabel(27, 40, 300, 300, translatedContent);
		lblContent.setScale(0.5f);
		lblContent.setAdditionalLineHeight(2);

		addComponent(lblTitle);
		addComponent(lblContent);
	}

}
