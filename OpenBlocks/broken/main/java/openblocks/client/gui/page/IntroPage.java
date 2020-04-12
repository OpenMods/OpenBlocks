package openblocks.client.gui.page;

import openblocks.OpenBlocks;
import openmods.gui.Icon;
import openmods.gui.component.GuiComponentHCenter;
import openmods.gui.component.GuiComponentSprite;
import openmods.gui.component.GuiComponentVCenter;
import openmods.gui.component.page.PageBase;

public class IntroPage extends PageBase {
	public static final Icon iconImage = new Icon(OpenBlocks.location("textures/gui/bookimage.png"), 0, 0.7421875f, 0, 0.546875f, 95, 70);

	public IntroPage() {
		addComponent(GuiComponentHCenter.wrap(0, 0, getWidth(),
				GuiComponentVCenter.wrap(0, 0, getHeight(),
						new GuiComponentSprite(0, 0, iconImage))));
	}
}
