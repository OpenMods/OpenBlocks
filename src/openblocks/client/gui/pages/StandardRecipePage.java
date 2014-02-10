package openblocks.client.gui.pages;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import openmods.gui.component.BaseComponent;
import openmods.gui.component.GuiComponentLabel;
import openmods.gui.component.GuiComponentSprite;
import openmods.utils.render.FakeIcon;

public class StandardRecipePage extends BaseComponent {

	private static final ResourceLocation texture = new ResourceLocation("openblocks:textures/gui/book.png");

	public static Icon iconCraftingGrid = FakeIcon.createSheetIcon(0, 180, 56, 56);
	public static Icon iconArrow = FakeIcon.createSheetIcon(60, 198, 48, 15);
	
	private GuiComponentCraftingGrid craftingGrid;
	private GuiComponentSprite arrow;
	private GuiComponentLabel lblDescription;
	private GuiComponentLabel lblTitle;

	public StandardRecipePage(String title, String description, String videoLink, ItemStack resultingItem) {
		super(0, 0);

		String translatedTitle = StatCollector.translateToLocal(title);
		String translatedDescription = StatCollector.translateToLocal(description).replaceAll("\\\\n", "\n");
		String translatedLink = StatCollector.translateToLocal(videoLink);

		if (videoLink != "" && !videoLink.equals(translatedLink)) {
			addComponent(new GuiComponentYouTube(28, 150, translatedLink));
		}
		
		lblTitle = new GuiComponentLabel((getWidth() - Minecraft.getMinecraft().fontRenderer.getStringWidth(translatedTitle)) / 2, 12, translatedTitle);
		lblDescription = new GuiComponentLabel(27, 100, 340, 1000, translatedDescription);
		arrow = new GuiComponentSprite(90, 50, iconArrow, texture);
		craftingGrid = new GuiComponentCraftingGrid(25, 30, getFirstRecipeForItem(resultingItem), iconCraftingGrid, texture);
		
		lblDescription.setScale(0.5f);
		lblDescription.setAdditionalLineHeight(4);
		
		addComponent(lblDescription);
		addComponent(lblTitle);
		addComponent(arrow);
		addComponent(craftingGrid);
		
	}
	
	private ItemStack[] getFirstRecipeForItem(ItemStack resultingItem) {
		ItemStack[] recipeItems = new ItemStack[9];
		for (IRecipe recipe : (List<IRecipe>)CraftingManager.getInstance().getRecipeList()) {
			if (recipe != null) {
				ItemStack result = recipe.getRecipeOutput();
				if (result != null && result.isItemEqual(resultingItem)) {
					Object[] input = null;
					if (recipe instanceof ShapelessOreRecipe) {
						input = ((ShapelessOreRecipe)recipe).getInput().toArray();
					} else if (recipe instanceof ShapedOreRecipe) {
						input = ((ShapedOreRecipe)recipe).getInput();
					}
					if (input != null) {
						for (int i = 0; i < input.length; i++) {
							Object obj = input[i];
							if (obj instanceof ItemStack) {
								recipeItems[i] = (ItemStack)obj;
							} else if (obj instanceof ArrayList) {
								ArrayList list = (ArrayList) obj;
								if (list.size() > 0) {
									recipeItems[i] = (ItemStack) list.get(0);
								}
							} else {
							}
						}
					}
					break;
				}
			}
		}
		for (int i = 0; i < 9; i++) {
			if (recipeItems[i] instanceof ItemStack) {
				if (recipeItems[i].getItemDamage() == OreDictionary.WILDCARD_VALUE) {
					recipeItems[i].setItemDamage(0);
				}
			}
		}
		return recipeItems;
	}

	@Override
	public int getWidth() {
		return 220;
	}

	@Override
	public int getHeight() {
		return 200;
	}

}
