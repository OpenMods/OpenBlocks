package openblocks.integration;

import java.util.List;

import org.lwjgl.opengl.GL11;

import openblocks.OpenBlocks;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import igwmod.api.WikiRegistry;
import igwmod.gui.GuiWiki;
import igwmod.gui.IPageLink;
import igwmod.gui.IReservedSpace;
import igwmod.gui.LocatedStack;
import igwmod.gui.LocatedString;
import igwmod.gui.LocatedTexture;
import igwmod.gui.tabs.IWikiTab;

public final class OpenBlocksWikiTab implements IWikiTab {

	private static final String SPLITTER = "~SPLIT~";

	private static RenderItem renderer = new RenderItem();

	private static ItemStack toDraw;

	private List<String> pages = Lists.newArrayList();

	static {

		renderer.setRenderManager(RenderManager.instance);
	}

	/*
	 * WARNING!
	 * The addition of pages inside this constructor is highly risky:
	 * the implementation isn't written for extensibility and, as such,
	 * every page addition must be tested in-game for compatibility with
	 * the current design.
	 * The addition of pages most likely requires the editing of the
	 * search bar's position and the amount of items shown in the wiki
	 * tab. These edits have to be performed in the methods:
	 * getPages(int[]) (where you need to edit the amount of
	 * items shown), pagesPerTab() (where you need to edit the
	 * amount of items shown, usually diminishing by 2 every time) and
	 * getReservedSpaces() (where the y of the located texture
	 * must be edited to allow for a design-compatible number).
	 */
	public OpenBlocksWikiTab() {

		pages.add("credits");
		pages.add("obUtils");
		pages.add("bKey");
		pages.add("enchantments");
		pages.add("changelogs");
	}

	public static ItemStack withFallBack(Block blockToCheck, Block fallback) {

		if (blockToCheck == null) return new ItemStack(fallback);

		return new ItemStack(blockToCheck);
	}

	@Override
	public String getName() {

		return "wiki.openblocks.tab";
	}

	@Override
	public ItemStack renderTabIcon(GuiWiki gui) {

		return withFallBack(OpenBlocks.Blocks.flag, Blocks.sponge);
	}

	@Override
	public List<IReservedSpace> getReservedSpaces() {

		final List<IReservedSpace> reservedSpaces = Lists.newArrayList();
		final ResourceLocation textureLocation = new ResourceLocation("openblocks",
				"textures/gui/wiki/shorterItemGrid.png");
		reservedSpaces.add(new LocatedTexture(textureLocation, 40, 110, 36, 108));
		return reservedSpaces;
	}

	@Override
	public List<IPageLink> getPages(int[] pageIndexes) {

		final List<ItemStack> itemStacks = WikiRegistry.getItemAndBlockPageEntries();
		final List<ItemStack> obItemStacks = Lists.newArrayList();

		for(ItemStack stack : itemStacks) {

			if (stack.getUnlocalizedName().contains("openblocks")) obItemStacks.add(stack);
		}

		final List<IPageLink> pages = Lists.newArrayList();
		if (pageIndexes == null) {

			for(int i = 0; i < obItemStacks.size(); i++) {

				pages.add(new LocatedStack(obItemStacks.get(i),
						41 + i % 2 * 18,
						111 + i / 2 * 18));
			}
		}
		else {

			for(int i = 0; i < pageIndexes.length; i++) {

				if(pageIndexes[i] >= obItemStacks.size()) break;

				pages.add(new LocatedStack(obItemStacks.get(pageIndexes[i]),
						41 + i % 2 * 18,
						111 + i / 2 * 18));
			}
		}

		for(int i = 0; i < this.pages.size(); i++) {

			if (this.pages.get(i).equals(SPLITTER)) {

				pages.add(new LocatedString("", 80, 125 + 11 * i, 0, false));
				continue;
			}

			pages.add(new LocatedString(this.getPageName(this.pages.get(i)),
					80,
					125 + 11 * i,
					false,
					this.getPageLocation(this.pages.get(i))));
		}

		return pages;
	}

	@Override
	public int getSearchBarAndScrollStartY() {

		return 96;
	}

	@Override
	public int pagesPerTab() {

		return 12;
	}

	@Override
	public int pagesPerScroll() {

		return 2;
	}

	@Override
	public void renderForeground(GuiWiki gui, int mouseX, int mouseY) {

		if (toDraw != null) {

			if (toDraw.getItem() instanceof ItemBlock) {

				gui.renderRotatingBlockIntoGUI(gui, toDraw, 55, 33, 2.8F);
				return;
			}

			GL11.glPushMatrix();
			GL11.glTranslated(49, 20, 0);
			GL11.glScaled(2.2, 2.2, 2.2);
			renderer.renderItemAndEffectIntoGUI(gui.getFontRenderer(), gui.mc.getTextureManager(), toDraw, 0, 0);
			GL11.glPopMatrix();
		}
	}

	@Override
	public void renderBackground(GuiWiki gui, int mouseX, int mouseY) { }

	@Override
	public void onMouseClick(GuiWiki gui, int mouseX, int mouseY, int mouseKey) { }

	@Override
	public void onPageChange(GuiWiki gui, String pageName, Object... metadata) {

		if (metadata.length > 0 && metadata[0] instanceof ItemStack) {

			toDraw = (ItemStack) metadata[0];
		}
		else if (metadata.length == 0 && pageName.contains("openblocks")) {

			toDraw = withFallBack(OpenBlocks.Blocks.flag, Blocks.sponge);
		}
	}

	private String getPageName(String page){

		if (page == SPLITTER) return "";

		return StatCollector.translateToLocal("wiki.openblocks.page." + page);
	}

	private String getPageLocation(String page){

		return "openblocks:tab/" + page;
	}
}
