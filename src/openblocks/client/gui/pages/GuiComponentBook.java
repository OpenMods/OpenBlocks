package openblocks.client.gui.pages;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import openmods.gui.component.BaseComponent;
import openmods.gui.component.BaseComponent.IComponentListener;
import openmods.gui.component.GuiComponentSprite;
import openmods.utils.render.FakeIcon;

import com.google.common.collect.Lists;

public class GuiComponentBook extends BaseComponent implements IComponentListener {

	private GuiComponentSprite imgLeftBackground;
	private GuiComponentSprite imgRightBackground;
	private GuiComponentSprite imgPrev;
	private GuiComponentSprite imgNext;

	public static Icon iconPageLeft = FakeIcon.createSheetIcon(-45, 0, -211, 180);
	public static Icon iconPageRight = FakeIcon.createSheetIcon(0, 0, 211, 180);
	public static Icon iconPrev = FakeIcon.createSheetIcon(0, 0, 20, 20);
	public static Icon iconNext = FakeIcon.createSheetIcon(0, 0, 20, 20);

	private static final ResourceLocation texture = new ResourceLocation("openblocks:textures/gui/book.png");

	public List<BaseComponent> pages;

	private int index = 0;

	public GuiComponentBook(GuiScreen screen) {
		super(0, 0);

		imgLeftBackground = new GuiComponentSprite(0, 0, iconPageLeft, texture);
		imgRightBackground = new GuiComponentSprite(0, 0, iconPageRight, texture);

		imgPrev = new GuiComponentSprite(0, 140, iconPrev, texture);
		imgPrev.addListener(this);
		imgNext = new GuiComponentSprite(400, 140, iconNext, texture);
		imgNext.addListener(this);

		addComponent(imgLeftBackground);
		addComponent(imgRightBackground);
		addComponent(imgPrev);
		addComponent(imgNext);

		pages = Lists.newArrayList();

	}

	@Override
	public int getWidth() {
		return iconPageRight.getIconHeight() * 2;
	}

	@Override
	public int getHeight() {
		return iconPageRight.getIconHeight();
	}

	public void addPage(BaseComponent page) {
		addComponent(page);
		page.setEnabled(false);
		pages.add(page);
	}

	public boolean addStandardRecipePage(String modId, String name, Object item) {
		ItemStack stack = null;
		String type = "";
		if (item instanceof ItemStack) {
			stack = (ItemStack)item;
			type = (stack.getItem() instanceof ItemBlock)? "tile" : "item";
		}
		if (item instanceof Item) {
			stack = new ItemStack((Item)item);
			type = "item";
		} else if (item instanceof Block) {
			stack = new ItemStack((Block)item);
			type = "tile";
		}
		if (stack != null) {
			String fullName = String.format("%s.%s.%s.name", type, modId, name);
			String description = String.format("%s.%s.%s.description", type, modId, name);
			String video = String.format("%s.%s.%s.video", type, modId, name);
			addPage(new StandardRecipePage(fullName, description, video, stack));
			return true;
		}
		return false;
	}

	public void enablePages() {
		int i = 0;
		for (BaseComponent page : pages) {
			page.setEnabled(i == index || i == index + 1);
			i++;
		}
		imgNext.setEnabled(index < pages.size() - 2);
		imgPrev.setEnabled(index > 0);
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		imgRightBackground.setX(iconPageRight.getIconWidth());
		if (index + 1 < pages.size()) {
			pages.get(index + 1).setX(iconPageRight.getIconWidth());
		}
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
	}

	@Override
	public void componentMouseDown(BaseComponent component, int offsetX, int offsetY, int button) {
		if (component == imgPrev) {
			if (index > 0) {
				index -= 2;
			}
		}
		if (component == imgNext) {
			if (index < pages.size() - 2) {
				index += 2;
			}
		}
		enablePages();
	}

	@Override
	public void componentMouseDrag(BaseComponent component, int offsetX, int offsetY, int button, long time) {}

	@Override
	public void componentMouseMove(BaseComponent component, int offsetX, int offsetY) {}

	@Override
	public void componentMouseUp(BaseComponent component, int offsetX, int offsetY, int button) {}

	@Override
	public void componentKeyTyped(BaseComponent component, char par1, int par2) {}
}
