package openblocks.client.gui.page;

import net.minecraft.util.ResourceLocation;
import openmods.gui.component.GuiComponentSprite;
import openmods.gui.component.page.PageBase;

public class IntroPage extends PageBase {
	private GuiComponentSprite image;

	private static final ResourceLocation texture = new ResourceLocation("openblocks:textures/gui/bookimage.png");
	public static IIcon iconImage = new FakeIcon(0, 0.7421875f, 0, 0.546875f, 95, 70);

	public IntroPage() {
		image = new GuiComponentSprite((getWidth() - iconImage.getIconWidth()) / 2, (getHeight() - iconImage.getIconHeight()) / 2, iconImage, texture);
		addComponent(image);
	}

}
