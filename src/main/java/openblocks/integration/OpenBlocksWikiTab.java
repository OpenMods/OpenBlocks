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

/**
 * The wiki tab implementation for OpenBlocks.
 * 
 * @author TheSilkMiner
 * 
 * @since 1.4.5
 *
 */
public final class OpenBlocksWikiTab implements IWikiTab {
	
	/**
	 * A String used to identify splitters inside the Wiki pages
	 * list.
	 * 
	 * @since 1.4.5
	 */
	private static final String SPLITTER = "~SPLIT~";

	/**
	 * The renderer used to render the item in the upper part of the
	 * wiki tab.
	 * 
	 * @since 1.4.5
	 */
	private static RenderItem renderer = new RenderItem();
	
	/**
	 * The ItemStack which needs to be drawn.
	 * 
	 * @since 1.4.5
	 */
	private static ItemStack toDraw;
	
	/**
	 * The list of pages to be shown on top of the blocks and
	 * items in the wiki tab.
	 * 
	 * @since 1.4.5
	 */
	private List<String> pages = Lists.newArrayList();
	
	/**
	 * Static initializer.
	 * 
	 * <p>Does this even require Javadoc?</p>
	 * 
	 * @since 1.4.5
	 */
	static {
		
		renderer.setRenderManager(RenderManager.instance);
	}
	
	/**
	 * Creates an instance of the wiki tab, adding the required pages.
	 * 
	 * <p><b>WARNING!</b></p>
	 * 
	 * <p>The addition of pages inside this constructor is highly risky:
	 * the implementation isn't written for extensibility and, as such,
	 * every page addition must be tested in-game for compatibility with
	 * the current design.</p>
	 * 
	 * <p>The addition of pages most likely requires the editing of the
	 * search bar's position and the amount of items shown in the wiki
	 * tab. These edits have to be performed in the methods
	 * {@link #getPages(int[])} (where you need to edit the amount of
	 * items shown), {@link #pagesPerTab()} (where you need to edit the
	 * amount of items shown, usually diminishing by 2 every time) and
	 * {@link #getReservedSpaces()} (where the y of the located texture
	 * must be edited to allow for a design-compatible number).</p>
	 * 
	 * @since 1.4.5
	 */
	public OpenBlocksWikiTab() {
		
		pages.add("credits");
		
		// MERGED INTO obUtils page
		//pages.add("easyEdit");
		//pages.add("invBackup");
		
		pages.add("obUtils");
		pages.add("bKey");
		pages.add("enchantments");
		pages.add("changelogs");
		
		// REMOVED: useless since tutorials can be added
		//          to other wiki pages.
		//pages.add(SPLITTER);
		//pages.add("tutorials");
	}
	
	/**
	 * Checks if the given block is available and returns an ItemStack based on
	 * the following conditions:
	 * 
	 * <ul>
	 * 	<li>with the block specified if the block is available</li>
	 * 	<li>with the fallback specified if the original block isn't available.</li>
	 * </ul>
	 * 
	 * @param blockToCheck
	 * 		The block which availability needs to be checked.
	 * @param fallback
	 * 		The block to return in case the {@code blockToCheck} isn't available.
	 * @return
	 * 		An ItemStack.
	 * 
	 * @since 1.4.5
	 */
	public static ItemStack withFallBack(Block blockToCheck, Block fallback) {
		
		if (blockToCheck == null) return new ItemStack(fallback);
		
		return new ItemStack(blockToCheck);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.4.5 in OpenBlocks
	 */
	@Override
	public String getName() {
		
		return "wiki.openblocks.tab";
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.4.5 in OpenBlocks
	 */
	@Override
	public ItemStack renderTabIcon(GuiWiki gui) {
		
		return withFallBack(OpenBlocks.Blocks.flag, Blocks.sponge);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.4.5 in OpenBlocks
	 */
	@Override
	public List<IReservedSpace> getReservedSpaces() {
		
		List<IReservedSpace> reservedSpaces = Lists.newArrayList();
		ResourceLocation textureLocation = new ResourceLocation("openblocks",
				"textures/gui/wiki/shorterItemGrid.png");
		reservedSpaces.add(new LocatedTexture(textureLocation, 40, 110, 36, 108));
		return reservedSpaces;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.4.5 in OpenBlocks
	 */
	@Override
	public List<IPageLink> getPages(int[] pageIndexes) {
		
		List<ItemStack> itemStacks = WikiRegistry.getItemAndBlockPageEntries();
		List<ItemStack> obItemStacks = Lists.newArrayList();
		
		for(ItemStack stack : itemStacks) {
			
			if (stack.getUnlocalizedName().contains("openblocks")) obItemStacks.add(stack);
		}
		
        List<IPageLink> pages = Lists.newArrayList();
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

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.4.5 in OpenBlocks
	 */
	@Override
	public int getSearchBarAndScrollStartY() {
		
		return 96;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.4.5 in OpenBlocks
	 */
	@Override
	public int pagesPerTab() {
		
		return 12;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.4.5 in OpenBlocks
	 */
	@Override
	public int pagesPerScroll() {
		
		return 2;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.4.5 in OpenBlocks
	 */
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

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.4.5 in OpenBlocks
	 */
	@Override
	public void renderBackground(GuiWiki gui, int mouseX, int mouseY) { }

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.4.5 in OpenBlocks
	 */
	@Override
	public void onMouseClick(GuiWiki gui, int mouseX, int mouseY, int mouseKey) { }

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.4.5 in OpenBlocks
	 */
	@Override
	public void onPageChange(GuiWiki gui, String pageName, Object... metadata) {
		
		if (metadata.length > 0 && metadata[0] instanceof ItemStack) {
			
			 toDraw = (ItemStack) metadata[0];
		}
		else if (metadata.length == 0 && pageName.contains("openblocks")) {
			 
			 toDraw = withFallBack(OpenBlocks.Blocks.flag, Blocks.sponge);
		}
	}
	
	/**
	 * Gets the formatted name of the page you input.
	 * 
	 * @param page
	 * 		The page's name.
	 * @return
	 * 		The formatted name.
	 * 
	 * @since 1.4.5
	 */
    private String getPageName(String page){
    	
    	if (page == SPLITTER) return "";
    	
        return StatCollector.translateToLocal("wiki.openblocks.page." + page);
    }

    /**
     * Gets the location where the {@code .txt} file with the page
     * contents is located.
     * 
     * @param page
     * 		The page's name.
     * @return
     * 		The location.
     * 
     * @since 1.4.5
     */
    private String getPageLocation(String page){
    	
        return "openblocks:wikitab/" + page;
    }
}
