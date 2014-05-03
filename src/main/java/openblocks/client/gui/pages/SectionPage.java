package openblocks.client.gui.pages;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;
import openmods.gui.component.GuiComponentLabel;

public class SectionPage extends BlankPage {

	private GuiComponentLabel title;

	public SectionPage(String name) {
		String txt = StatCollector.translateToLocal(name);
		int strWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(txt) * 2;
		int x = (getWidth() - strWidth) / 2;
		title = new GuiComponentLabel(x - 10, 70, txt);
		title.setScale(2f);
		addComponent(title);
	}
}
