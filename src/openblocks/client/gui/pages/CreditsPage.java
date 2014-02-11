package openblocks.client.gui.pages;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;
import openmods.gui.component.BaseComponent;
import openmods.gui.component.GuiComponentLabel;
import openmods.utils.StringUtils;

public class CreditsPage extends BlankPage {

	private GuiComponentLabel lblTitle;
	private GuiComponentLabel lblCredits;
	
	public CreditsPage() {
		
		String translatedTitle = StatCollector.translateToLocal("openblocks.gui.credits_title");
		String translatedCredits = StringUtils.format(StatCollector.translateToLocal("openblocks.gui.credits"));
		
		int x = (getWidth() - Minecraft.getMinecraft().fontRenderer.getStringWidth(translatedTitle)) / 2;
		lblTitle = new GuiComponentLabel(x, 12, translatedTitle);
		
		lblCredits = new GuiComponentLabel(27, 40, 300, 300, translatedCredits);
		lblCredits.setScale(0.5f);
		lblCredits.setAdditionalLineHeight(2);
		
		addComponent(lblTitle);
		addComponent(lblCredits);
	}
	
}
