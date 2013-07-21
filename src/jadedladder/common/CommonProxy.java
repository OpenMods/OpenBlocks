package jadedladder.common;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import jadedladder.JadedLadder;
import jadedladder.common.block.BlockGuide;
import jadedladder.common.block.BlockLadder;

public class CommonProxy {

	public void init() {

		JadedLadder.Blocks.ladder = new BlockLadder();
		CraftingManager.getInstance().getRecipeList().add(
				new ShapelessOreRecipe(new ItemStack(JadedLadder.Blocks.ladder),
						new ItemStack(Block.ladder), new ItemStack(Block.trapdoor)));
		

		JadedLadder.Blocks.guide = new BlockGuide();
	}

	public void registerRenderInformation() {

	}
	
	
	/* Helpers */

    public static final double lengthSq(double x, double y, double z) {
        return (x * x) + (y * y) + (z * z);
    }

    public static final double lengthSq(double x, double z) {
        return (x * x) + (z * z);
    }

}
