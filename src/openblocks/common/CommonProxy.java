package openblocks.common;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import openblocks.OpenBlocks;
import openblocks.OpenBlocks.Config;
import openblocks.common.block.BlockGuide;
import openblocks.common.block.BlockHeal;
import openblocks.common.block.BlockLadder;
import openblocks.common.block.BlockDrop;
import openblocks.common.block.BlockLightbox;
import openblocks.common.block.BlockTarget;
import openblocks.common.block.OpenBlock;
import openblocks.common.container.ContainerLightbox;
import openblocks.common.tileentity.TileEntityHealBlock;
import openblocks.common.tileentity.TileEntityLightbox;

public class CommonProxy implements IGuiHandler {

	public void init() {

		if (Config.blockLadderId > -1) {
			OpenBlocks.Blocks.ladder = new BlockLadder();
			CraftingManager.getInstance().getRecipeList().add(
					new ShapelessOreRecipe(new ItemStack(OpenBlocks.Blocks.ladder),
							new ItemStack(Block.ladder), new ItemStack(Block.trapdoor)));
		}
		
		if (Config.blockGuideId > -1) {
			OpenBlocks.Blocks.guide = new BlockGuide();
		}
		if (Config.blockDropId > -1) {
			OpenBlocks.Blocks.drop = new BlockDrop();
		}
		if (Config.blockHealId > -1) {
			OpenBlocks.Blocks.heal = new BlockHeal();
		}
		if (Config.blockLightboxId > -1) {
			OpenBlocks.Blocks.lightbox = new BlockLightbox();
		}
		
		if (Config.blockTargetId > -1) {
			OpenBlocks.Blocks.target = new BlockTarget();
		}
		

	    GameRegistry.addRecipe(new TorchBowRecipe());
		NetworkRegistry.instance().registerGuiHandler(OpenBlocks.instance, this);
		MinecraftForge.EVENT_BUS.register(new BowEventHandler());
	}

	public void registerRenderInformation() {

	}
	
	public String getModId(){
		return OpenBlocks.class.getAnnotation(Mod.class).modid();
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (ID == OpenBlocks.Gui.Lightbox.ordinal()) {
			return new ContainerLightbox(player.inventory, (TileEntityLightbox)tile);
		} 
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		return null;
	}

}
