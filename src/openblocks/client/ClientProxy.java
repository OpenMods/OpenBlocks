package openblocks.client;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import openblocks.Config;
import openblocks.IProxy;
import openblocks.OpenBlocks;
import openblocks.OpenBlocks.Gui;
import openblocks.client.fx.FXLiquidSpray;
import openblocks.client.gui.*;
import openblocks.client.renderer.*;
import openblocks.client.renderer.entity.*;
import openblocks.client.renderer.tileentity.*;
import openblocks.common.container.*;
import openblocks.common.entity.EntityGhost;
import openblocks.common.entity.EntityHangGlider;
import openblocks.common.entity.EntityLuggage;
import openblocks.common.tileentity.*;
import openblocks.sync.SyncableManager;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy implements IProxy {

	private ItemRendererHangGlider hangGliderRenderer;

	public ClientProxy() {
		OpenBlocks.syncableManager = new SyncableManager();
		MinecraftForge.EVENT_BUS.register(new SoundLoader());
	}

	@Override
	public void init() {}
	
	@Override
	public void postInit() {
		SoundEventsManager.instance.init();
	}

	@Override
	public void registerRenderInformation() {

		OpenBlocks.renderId = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(new BlockRenderingHandler());

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGuide.class, new TileEntityGuideRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLightbox.class, new TileEntityLightboxRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTarget.class, new TileEntityTargetRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGrave.class, new TileEntityGraveRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFlag.class, new TileEntityFlagRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTank.class, new TileEntityTankRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTrophy.class, new TileEntityTrophyRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBearTrap.class, new TileEntityBearTrapRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySprinkler.class, new TileEntitySprinklerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCannon.class, new TileEntityCannonRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVacuumHopper.class, new TileEntityVacuumHopperRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBigButton.class, new TileEntityBigButtonRenderer());

		MinecraftForgeClient.registerItemRenderer(Config.blockTankId, new ItemRendererTank());

		assertItemHangGliderRenderer();
		MinecraftForge.EVENT_BUS.register(hangGliderRenderer);

		RenderingRegistry.registerEntityRenderingHandler(EntityGhost.class, new EntityGhostRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityHangGlider.class, new EntityHangGliderRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityLuggage.class, new EntityLuggageRenderer());
		MinecraftForgeClient.registerItemRenderer(OpenBlocks.Items.luggage.itemID, new ItemRendererLuggage());
		TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
		attachPlayerRenderer();

		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
		MinecraftForge.EVENT_BUS.register(SoundEventsManager.instance);
	}

	private void assertItemHangGliderRenderer() {
		if (hangGliderRenderer == null) hangGliderRenderer = new ItemRendererHangGlider();
		if (MinecraftForgeClient.getItemRenderer(new ItemStack(OpenBlocks.Items.hangGlider), ItemRenderType.EQUIPPED) == null) {
			MinecraftForgeClient.registerItemRenderer(OpenBlocks.Items.hangGlider.itemID, hangGliderRenderer);
		}
	}

	@SuppressWarnings("unchecked")
	private static void attachPlayerRenderer() {
		if (Config.tryHookPlayerRenderer) {
			// Get current renderer and check that it's Mojangs
			Render render = (Render)RenderManager.instance.entityRenderMap.get(EntityPlayer.class);
			if (render.getClass().equals(net.minecraft.client.renderer.entity.RenderPlayer.class)) {
				EntityPlayerRenderer playerRenderer = new EntityPlayerRenderer();
				playerRenderer.setRenderManager(RenderManager.instance);
				RenderManager.instance.entityRenderMap.put(EntityPlayer.class, playerRenderer);
			}
		}
	}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if ((world instanceof WorldClient)) {
			if (ID == Gui.Luggage.ordinal()) { return new GuiLuggage(new ContainerLuggage(player.inventory, (EntityLuggage)world.getEntityByID(x))); }
			TileEntity tile = world.getBlockTileEntity(x, y, z);
			if (ID == Gui.Lightbox.ordinal()) { return new GuiLightbox(new ContainerLightbox(player.inventory, (TileEntityLightbox)tile)); }
			if (ID == Gui.Sprinkler.ordinal()) { return new GuiSprinkler(new ContainerSprinkler(player.inventory, (TileEntitySprinkler)tile)); }
			if (ID == Gui.VacuumHopper.ordinal()) { return new GuiVacuumHopper(new ContainerVacuumHopper(player.inventory, (TileEntityVacuumHopper)tile)); }
			if (ID == Gui.BigButton.ordinal()) { return new GuiBigButton(new ContainerBigButton(player.inventory, (TileEntityBigButton)tile)); }

		}
		return null;
	}

	@Override
	public File getWorldDir(World world) {
		return new File(OpenBlocks.getBaseDir(), "saves/"
				+ world.getSaveHandler().getWorldDirectoryName());
	}

	/**
	 * Is this the server
	 *
	 * @return true if this is the server
	 */
	@Override
	public boolean isServer() {
		return false;
	}

	/**
	 * Is this the client
	 *
	 * @return true if this is the client
	 */
	@Override
	public boolean isClient() {
		return true;
	}

	@Override
	public void spawnLiquidSpray(World worldObj, FluidStack water, double x, double y, double z, ForgeDirection direction, float angleRadians, float spread) {
		FXLiquidSpray spray = new FXLiquidSpray(worldObj, water, x, y, z, direction, angleRadians, spread);
		Minecraft.getMinecraft().effectRenderer.addEffect(spray);
	}
}
