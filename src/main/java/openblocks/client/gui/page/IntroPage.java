package openblocks.client.gui.page;

import openblocks.OpenBlocks;
import openmods.gui.IComponentParent;
import openmods.gui.Icon;
import openmods.gui.component.GuiComponentSprite;
import openmods.gui.component.page.PageBase;

public class IntroPage extends PageBase {
	public static Icon iconImage = new Icon(OpenBlocks.location("textures/gui/bookimage.png"), 0, 0.7421875f, 0, 0.546875f, 95, 70);

	public IntroPage(IComponentParent parent) {
		super(parent);
		addComponent(new GuiComponentSprite(parent,
				(getWidth() - iconImage.width) / 2,
				(getHeight() - iconImage.height) / 2,
				iconImage));
	}
}
