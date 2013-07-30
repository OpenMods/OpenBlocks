package openblocks.common;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import openblocks.Log;
import openblocks.OpenBlocks;
import openblocks.OpenBlocks.Config;
import openblocks.common.block.BlockElevator;
import openblocks.common.block.BlockFlag;
import openblocks.common.block.BlockGrave;
import openblocks.common.block.BlockGuide;
import openblocks.common.block.BlockHeal;
import openblocks.common.block.BlockLadder;
import openblocks.common.block.BlockLightbox;
import openblocks.common.block.BlockTank;
import openblocks.common.block.BlockTarget;
import openblocks.common.block.BlockValve;
import openblocks.common.container.ContainerLightbox;
import openblocks.common.entity.EntityGhost;
import openblocks.common.recipe.TorchBowRecipe;
import openblocks.common.tileentity.TileEntityLightbox;
import openblocks.utils.LanguageUtils;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy implements IGuiHandler {

	public void init() {

		if (Config.blockLadderId > -1) {
			OpenBlocks.Blocks.ladder = new BlockLadder();
			CraftingManager.getInstance().getRecipeList().add(new ShapelessOreRecipe(new ItemStack(OpenBlocks.Blocks.ladder), new ItemStack(Block.ladder), new ItemStack(Block.trapdoor)));
		}

		if (Config.blockGuideId > -1) {
			OpenBlocks.Blocks.guide = new BlockGuide();
		}
		if (Config.blockElevatorId > -1) {
			OpenBlocks.Blocks.elevator = new BlockElevator();
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
		if (Config.blockGraveId > -1) {
			OpenBlocks.Blocks.grave = new BlockGrave();
		}
		if (Config.blockFlagId > -1) {
			OpenBlocks.Blocks.flag = new BlockFlag();
		}
		if (Config.blockTankId > -1) {
			OpenBlocks.Blocks.tank = new BlockTank();
		}
		if (Config.blockValveId > -1) {
			OpenBlocks.Blocks.valve = new BlockValve();
		}

		GameRegistry.addRecipe(new TorchBowRecipe());
		NetworkRegistry.instance().registerGuiHandler(OpenBlocks.instance, this);

		MinecraftForge.EVENT_BUS.register(new BowEventHandler());
		MinecraftForge.EVENT_BUS.register(new PlayerDeathHandler());

		EntityRegistry.registerModEntity(EntityGhost.class, "Ghost", 700, OpenBlocks.instance, 64, 1, true);

		LanguageUtils.setupLanguages();
	}

	public void registerRenderInformation() {

	}

	public String getModId() {
		return OpenBlocks.class.getAnnotation(Mod.class).modid();
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (ID == OpenBlocks.Gui.Lightbox.ordinal()) { return new ContainerLightbox(player.inventory, (TileEntityLightbox) tile); }
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	/**
	 * Is this the server
	 * 
	 * @return true if this is the server
	 */
	public boolean isServer() {
		return true; // Why have this method? If the checking method changes in
						// the future we fix it in one place.
	}

	/**
	 * Is this the client
	 * 
	 * @return true if this is the client
	 */
	public boolean isClient() {
		return false;
	}

	/**
	 * Checks if this game is SinglePlayer
	 * 
	 * @return true if this is single player
	 */
	public boolean isSinglePlayer() {
		// Yeah I know it doesn't matter now but why not have it :P
		MinecraftServer serverInstance = MinecraftServer.getServer();
		if (serverInstance == null) return false;
		return serverInstance.isSinglePlayer();
	}

}
