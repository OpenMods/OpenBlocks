package openblocks.common;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import openblocks.OpenBlocks;
import openblocks.common.block.BlockGuide;
import openblocks.common.block.BlockHeal;
import openblocks.common.block.BlockLadder;
import openblocks.common.block.BlockDrop;

public class CommonProxy {

	public void init() {

		OpenBlocks.Blocks.ladder = new BlockLadder();
		CraftingManager.getInstance().getRecipeList().add(
				new ShapelessOreRecipe(new ItemStack(OpenBlocks.Blocks.ladder),
						new ItemStack(Block.ladder), new ItemStack(Block.trapdoor)));
		

		OpenBlocks.Blocks.guide = new BlockGuide();
		OpenBlocks.Blocks.drop = new BlockDrop();
		OpenBlocks.Blocks.heal = new BlockHeal();
	}

	public void registerRenderInformation() {

	}
	

}
