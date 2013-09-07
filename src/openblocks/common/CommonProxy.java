	package openblocks.common;

import java.io.File;
import java.util.List;
import java.util.WeakHashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import openblocks.OpenBlocks;
import openblocks.OpenBlocks.Config;
import openblocks.common.block.BlockBearTrap;
import openblocks.common.block.BlockBigButton;
import openblocks.common.block.BlockCannon;
import openblocks.common.block.BlockElevator;
import openblocks.common.block.BlockFlag;
import openblocks.common.block.BlockGrave;
import openblocks.common.block.BlockGuide;
import openblocks.common.block.BlockHeal;
import openblocks.common.block.BlockLadder;
import openblocks.common.block.BlockLightbox;
import openblocks.common.block.BlockSponge;
import openblocks.common.block.BlockSprinkler;
import openblocks.common.block.BlockTank;
import openblocks.common.block.BlockTarget;
import openblocks.common.block.BlockTrophy;
import openblocks.common.block.BlockVacuumHopper;
import openblocks.common.container.ContainerBigButton;
import openblocks.common.container.ContainerLightbox;
import openblocks.common.container.ContainerLuggage;
import openblocks.common.container.ContainerSprinkler;
import openblocks.common.container.ContainerVacuumHopper;
import openblocks.common.entity.EntityCannon;
import openblocks.common.entity.EntityGhost;
import openblocks.common.entity.EntityHangGlider;
import openblocks.common.entity.EntityLuggage;
import openblocks.common.item.ItemGeneric;
import openblocks.common.item.ItemHangGlider;
import openblocks.common.item.ItemLuggage;
import openblocks.common.item.ItemSonicGlasses;
import openblocks.common.tileentity.TileEntityBigButton;
import openblocks.common.tileentity.TileEntityLightbox;
import openblocks.common.tileentity.TileEntitySprinkler;
import openblocks.common.tileentity.TileEntityVacuumHopper;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;

public class CommonProxy implements IGuiHandler {

	public WeakHashMap<EntityPlayer, EntityHangGlider> gliderMap = new WeakHashMap<EntityPlayer, EntityHangGlider>();
	public WeakHashMap<EntityPlayer, EntityHangGlider> gliderClientMap = new WeakHashMap<EntityPlayer, EntityHangGlider>();

	public void init() {
		@SuppressWarnings("unchecked")
		final List<IRecipe> recipeList = CraftingManager.getInstance().getRecipeList();

		if (canRegisterBlock(Config.blockLadderId)) {
			OpenBlocks.Blocks.ladder = new BlockLadder();
			recipeList.add(new ShapelessOreRecipe(new ItemStack(OpenBlocks.Blocks.ladder), new ItemStack(Block.ladder), new ItemStack(Block.trapdoor)));
		}
		if (canRegisterBlock(Config.blockGuideId)) {
			OpenBlocks.Blocks.guide = new BlockGuide();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.guide), new Object[] { "ggg", "gtg", "ggg", 'g', new ItemStack(Block.glass), 't', new ItemStack(Block.torchWood) }));
		}
		if (canRegisterBlock(Config.blockElevatorId)) {
			OpenBlocks.Blocks.elevator = new BlockElevator();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.elevator), new Object[] { "www", "wgw", "www", 'w', new ItemStack(Block.cloth, 1, Short.MAX_VALUE), 'g', new ItemStack(Item.ingotGold) }));
		}
		if (canRegisterBlock(Config.blockHealId)) {
			OpenBlocks.Blocks.heal = new BlockHeal();
		}
		if (canRegisterBlock(Config.blockLightboxId)) {
			OpenBlocks.Blocks.lightbox = new BlockLightbox();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.lightbox), new Object[] { "igi", "iti", "iii", 'i', new ItemStack(Item.ingotIron), 'g', new ItemStack(Block.thinGlass), 't', new ItemStack(Block.torchWood) }));
		}
		if (canRegisterBlock(Config.blockTargetId)) {
			OpenBlocks.Blocks.target = new BlockTarget();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.target), new Object[] { "www", "www", "s s", 'w', new ItemStack(Block.cloth, 1, Short.MAX_VALUE), 's', "stickWood" }));
		}
		if (canRegisterBlock(Config.blockGraveId)) {
			OpenBlocks.Blocks.grave = new BlockGrave();
		}
		if (canRegisterBlock(Config.blockFlagId)) {
			OpenBlocks.Blocks.flag = new BlockFlag();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.flag), new Object[] { "sw ", "sww", "s  ", 'w', new ItemStack(Block.cloth, 1, Short.MAX_VALUE), 's', "stickWood" }));
		}
		if (canRegisterBlock(Config.blockTankId)) {
			OpenBlocks.Blocks.tank = new BlockTank();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.tank, 2), new Object[] { "sgs", "ggg", "sgs", 'g', new ItemStack(Block.thinGlass), 's', new ItemStack(Block.obsidian) }));
		}
		if (canRegisterBlock(Config.blockTrophyId)) {
			OpenBlocks.Blocks.trophy = new BlockTrophy();
			MinecraftForge.EVENT_BUS.register(new TrophyHandler());
		}
		if (canRegisterBlock(Config.blockBearTrapId)) {
			OpenBlocks.Blocks.bearTrap = new BlockBearTrap();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.bearTrap), new Object[] { "bib", "bib", "bib", 'b', new ItemStack(Block.fenceIron), 'i', new ItemStack(Item.ingotIron) }));
		}

		if (canRegisterBlock(Config.blockSprinklerId)) {
			OpenBlocks.Blocks.sprinkler = new BlockSprinkler();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.sprinkler, 1), new Object[] { "igi", "iri", "igi", 'i', new ItemStack(Item.ingotIron), 'r', new ItemStack(Block.torchRedstoneActive), 'g', new ItemStack(Block.fenceIron) }));
		}

		if (canRegisterBlock(Config.blockCannonId)) {
			OpenBlocks.Blocks.cannon = new BlockCannon();
			EntityRegistry.registerModEntity(EntityCannon.class, "Cannon", Integer.MAX_VALUE, OpenBlocks.instance, Integer.MAX_VALUE, 8, false);
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.cannon), new Object[] { " d ", " f ", "iri", 'd', new ItemStack(Block.dispenser), 'f', new ItemStack(Block.fenceIron), 'i', new ItemStack(Item.ingotIron), 'r', new ItemStack(Block.blockRedstone) }));
		}

		if (canRegisterBlock(Config.blockVacuumHopperId)) {
			OpenBlocks.Blocks.vacuumHopper = new BlockVacuumHopper();
			recipeList.add(new ShapelessOreRecipe(new ItemStack(OpenBlocks.Blocks.vacuumHopper), new ItemStack(Block.hopperBlock), new ItemStack(Block.obsidian), new ItemStack(Item.enderPearl)));
		}

		if (canRegisterBlock(Config.blockSpongeId)) {
			OpenBlocks.Blocks.sponge = new BlockSponge();
			recipeList.add(new ShapelessOreRecipe(new ItemStack(OpenBlocks.Blocks.sponge), new ItemStack(Block.cloth, 1, Short.MAX_VALUE), new ItemStack(Item.slimeBall)));
		}
		if (canRegisterBlock(Config.blockBigButton)) {
			OpenBlocks.Blocks.bigButton = new BlockBigButton();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.bigButton), new Object[] { "bb", "bb", 'b', new ItemStack(Block.stoneButton) }));
		}

		// There is no fail checking here because if the Generic item fails, then I doubt anyone wants this to be silent.
		// Too many items would suffer from this. - NC
		OpenBlocks.Items.generic = new ItemGeneric();
		if (Config.itemHangGliderId > 0) {
			OpenBlocks.Items.hangGlider = new ItemHangGlider();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Items.hangGlider), new Object[] { "wsw", 'w', ItemGeneric.Metas.gliderWing.newItemStack(), 's', "stickWood" }));
		}

		if (Config.itemLuggageId > 0) {
			OpenBlocks.Items.luggage = new ItemLuggage();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Items.luggage), new Object[] { "sds", "scs", "sss", 's', "stickWood", 'd', new ItemStack(Item.diamond), 'c', new ItemStack(Block.chest) }));
		}

		if (Config.itemSonicGlassesId > 0) {
			OpenBlocks.Items.sonicGlasses = new ItemSonicGlasses();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Items.sonicGlasses), new Object[] { "ihi", "oso", "   ", 's', "stickWood", 'h', new ItemStack(Item.helmetIron), 'o', new ItemStack(Block.obsidian), 'i',  new ItemStack(Item.ingotIron)}));
		}

		// GameRegistry.addRecipe(new TorchBowRecipe());
		NetworkRegistry.instance().registerGuiHandler(OpenBlocks.instance, this);

		// MinecraftForge.EVENT_BUS.register(new BowEventHandler());
		if (OpenBlocks.Config.enableGraves) {
			MinecraftForge.EVENT_BUS.register(new PlayerDeathHandler());
		}

		if (OpenBlocks.Config.enableGraves) {
			EntityRegistry.registerModEntity(EntityGhost.class, "Ghost", 700, OpenBlocks.instance, 64, 1, true);
		}
		if (OpenBlocks.Config.itemLuggageId > 0) {
			EntityRegistry.registerModEntity(EntityLuggage.class, "Luggage", 702, OpenBlocks.instance, 64, 1, true);
		}
		EntityRegistry.registerModEntity(EntityHangGlider.class, "Hang Glider", 701, OpenBlocks.instance, 64, 1, true);

		OpenBlocks.Items.generic.initRecipes();
	}

	public void postInit() {}

	private static boolean canRegisterBlock(int blockId) {
		if(blockId > 0) {
			if(Block.blocksList[blockId] != null) {
				if(!Config.failIdsQuietly) {
					throw new RuntimeException("OpenBlocks tried to register a block for ID: " + blockId + " but it was in use. failIdsQuietly is false so I'm yelling at you now.");
				}else {
					System.out.println("[OpenBlocksMonitor] Block ID " + blockId + " in use. This block will *NOT* be loaded.");
					return false;
				}
			}
			return true;
		}else {
			return false; // Block disabled, fail silently
		}
	}

	public void assertItemHangGliderRenderer() {

	}

	public void registerRenderInformation() {

	}

	public String getModId() {
		return OpenBlocks.class.getAnnotation(Mod.class).modid();
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == OpenBlocks.Gui.Luggage.ordinal()) { return new ContainerLuggage(player.inventory, (EntityLuggage)world.getEntityByID(x)); }

		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (ID == OpenBlocks.Gui.Lightbox.ordinal()) { return new ContainerLightbox(player.inventory, (TileEntityLightbox)tile); }
		if (ID == OpenBlocks.Gui.Sprinkler.ordinal()) { return new ContainerSprinkler(player.inventory, (TileEntitySprinkler)tile); }
		if (ID == OpenBlocks.Gui.VacuumHopper.ordinal()) { return new ContainerVacuumHopper(player.inventory, (TileEntityVacuumHopper)tile); }
		if (ID == OpenBlocks.Gui.BigButton.ordinal()) { return new ContainerBigButton(player.inventory, (TileEntityBigButton)tile); }

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	public File getWorldDir(World world) {
		return new File(OpenBlocks.getBaseDir(), DimensionManager.getWorld(0).getSaveHandler().getWorldDirectoryName());
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

	public void spawnLiquidSpray(World worldObj, FluidStack water, double x, double y, double z, ForgeDirection sprayDirection, float angleRadians, float spread) {}
}
