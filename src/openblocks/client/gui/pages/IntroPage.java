package openblocks.client.gui.pages;

import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import openmods.gui.component.BaseComponent;
import openmods.gui.component.GuiComponentSprite;
import openmods.utils.render.FakeIcon;

public class IntroPage extends BlankPage {

	private GuiComponentSprite image;
	private static final ResourceLocation texture = new ResourceLocation("openblocks:textures/gui/bookimage.png");
	public static Icon iconImage = new FakeIcon(0, 0.7421875f, 0, 0.546875f, 95, 70);

	public IntroPage() {
		image = new GuiComponentSprite(52, 48, iconImage, texture);
		addComponent(image);
	}
	
}
