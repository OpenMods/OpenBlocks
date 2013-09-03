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
import openblocks.OpenBlocks;
import openblocks.OpenBlocks.Config;
import openblocks.OpenBlocks.Gui;
import openblocks.client.fx.FXLiquidSpray;
import openblocks.client.gui.GuiLightbox;
import openblocks.client.gui.GuiLuggage;
import openblocks.client.gui.GuiSprinkler;
import openblocks.client.gui.GuiVacuumHopper;
import openblocks.client.renderer.BlockRenderingHandler;
import openblocks.client.renderer.ItemRendererHangGlider;
import openblocks.client.renderer.ItemRendererLuggage;
import openblocks.client.renderer.ItemRendererTank;
import openblocks.client.renderer.entity.EntityGhostRenderer;
import openblocks.client.renderer.entity.EntityHangGliderRenderer;
import openblocks.client.renderer.entity.EntityLuggageRenderer;
import openblocks.client.renderer.entity.EntityPlayerRenderer;
import openblocks.client.renderer.tileentity.TileEntityBearTrapRenderer;
import openblocks.client.renderer.tileentity.TileEntityCannonRenderer;
import openblocks.client.renderer.tileentity.TileEntityFlagRenderer;
import openblocks.client.renderer.tileentity.TileEntityGraveRenderer;
import openblocks.client.renderer.tileentity.TileEntityGuideRenderer;
import openblocks.client.renderer.tileentity.TileEntityLightboxRenderer;
import openblocks.client.renderer.tileentity.TileEntitySprinklerRenderer;
import openblocks.client.renderer.tileentity.TileEntityTankRenderer;
import openblocks.client.renderer.tileentity.TileEntityTargetRenderer;
import openblocks.client.renderer.tileentity.TileEntityTrophyRenderer;
import openblocks.client.renderer.tileentity.TileEntityVacuumHopperRenderer;
import openblocks.common.CommonProxy;
import openblocks.common.container.ContainerLightbox;
import openblocks.common.container.ContainerLuggage;
import openblocks.common.container.ContainerSprinkler;
import openblocks.common.container.ContainerVacuumHopper;
import openblocks.common.entity.EntityGhost;
import openblocks.common.entity.EntityHangGlider;
import openblocks.common.entity.EntityLuggage;
import openblocks.common.tileentity.TileEntityBearTrap;
import openblocks.common.tileentity.TileEntityCannon;
import openblocks.common.tileentity.TileEntityFlag;
import openblocks.common.tileentity.TileEntityGrave;
import openblocks.common.tileentity.TileEntityGuide;
import openblocks.common.tileentity.TileEntityLightbox;
import openblocks.common.tileentity.TileEntitySprinkler;
import openblocks.common.tileentity.TileEntityTank;
import openblocks.common.tileentity.TileEntityTarget;
import openblocks.common.tileentity.TileEntityTrophy;
import openblocks.common.tileentity.TileEntityVacuumHopper;
import openblocks.sync.SyncableManager;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {

	private ItemRendererHangGlider hangGliderRenderer;

	public ClientProxy() {
		OpenBlocks.syncableManager = new SyncableManager();
		MinecraftForge.EVENT_BUS.register(new SoundLoader());
	}
	
	@Override
	public void postInit() {
		SoundEventsManager.instance.init();
	}

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

		MinecraftForgeClient.registerItemRenderer(OpenBlocks.Config.blockTankId, new ItemRendererTank());

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

	public void assertItemHangGliderRenderer() {
		if (hangGliderRenderer == null) hangGliderRenderer = new ItemRendererHangGlider();
		if (MinecraftForgeClient.getItemRenderer(new ItemStack(OpenBlocks.Items.hangGlider), ItemRenderType.EQUIPPED) == null) {
			MinecraftForgeClient.registerItemRenderer(OpenBlocks.Items.hangGlider.itemID, hangGliderRenderer);
		}
	}

	private void attachPlayerRenderer() {
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
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if ((world instanceof WorldClient)) {
			if (ID == Gui.Luggage.ordinal()) { return new GuiLuggage(new ContainerLuggage(player.inventory, (EntityLuggage)world.getEntityByID(x))); }
			TileEntity tile = world.getBlockTileEntity(x, y, z);
			if (ID == Gui.Lightbox.ordinal()) { return new GuiLightbox(new ContainerLightbox(player.inventory, (TileEntityLightbox)tile)); }
			if (ID == Gui.Sprinkler.ordinal()) { return new GuiSprinkler(new ContainerSprinkler(player.inventory, (TileEntitySprinkler)tile)); }
			if (ID == Gui.VacuumHopper.ordinal()) { return new GuiVacuumHopper(new ContainerVacuumHopper(player.inventory, (TileEntityVacuumHopper)tile)); }
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
	public boolean isServer() {
		return false;
	}

	/**
	 * Is this the client
	 * 
	 * @return true if this is the client
	 */
	public boolean isClient() {
		return true;
	}

	@Override
	public void spawnLiquidSpray(World worldObj, FluidStack water, double x, double y, double z, ForgeDirection direction, float angleRadians, float spread) {
		FXLiquidSpray spray = new FXLiquidSpray(worldObj, water, x, y, z, direction, angleRadians, spread);
		Minecraft.getMinecraft().effectRenderer.addEffect(spray);
	}
}
