package openblocks.client;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import openblocks.Config;
import openblocks.IProxy;
import openblocks.OpenBlocks;
import openblocks.client.fx.FXLiquidSpray;
import openblocks.client.renderer.BlockRenderingHandler;
import openblocks.client.renderer.ItemRendererHangGlider;
import openblocks.client.renderer.ItemRendererLuggage;
import openblocks.client.renderer.ItemRendererTank;
import openblocks.client.renderer.entity.EntityGhostRenderer;
import openblocks.client.renderer.entity.EntityHangGliderRenderer;
import openblocks.client.renderer.entity.EntityLuggageRenderer;
import openblocks.client.renderer.entity.EntityPlayerRenderer;
import openblocks.client.renderer.tileentity.TileEntityBearTrapRenderer;
import openblocks.client.renderer.tileentity.TileEntityBigButtonRenderer;
import openblocks.client.renderer.tileentity.TileEntityCannonRenderer;
import openblocks.client.renderer.tileentity.TileEntityFanRenderer;
import openblocks.client.renderer.tileentity.TileEntityFlagRenderer;
import openblocks.client.renderer.tileentity.TileEntityGraveRenderer;
import openblocks.client.renderer.tileentity.TileEntityGuideRenderer;
import openblocks.client.renderer.tileentity.TileEntityImaginaryRenderer;
import openblocks.client.renderer.tileentity.TileEntityLightboxRenderer;
import openblocks.client.renderer.tileentity.TileEntitySprinklerRenderer;
import openblocks.client.renderer.tileentity.TileEntityTankRenderer;
import openblocks.client.renderer.tileentity.TileEntityTargetRenderer;
import openblocks.client.renderer.tileentity.TileEntityTrophyRenderer;
import openblocks.client.renderer.tileentity.TileEntityVacuumHopperRenderer;
import openblocks.common.entity.EntityGhost;
import openblocks.common.entity.EntityHangGlider;
import openblocks.common.entity.EntityLuggage;
import openblocks.common.tileentity.TileEntityBearTrap;
import openblocks.common.tileentity.TileEntityBigButton;
import openblocks.common.tileentity.TileEntityCannon;
import openblocks.common.tileentity.TileEntityFan;
import openblocks.common.tileentity.TileEntityFlag;
import openblocks.common.tileentity.TileEntityGrave;
import openblocks.common.tileentity.TileEntityGuide;
import openblocks.common.tileentity.TileEntityImaginary;
import openblocks.common.tileentity.TileEntityLightbox;
import openblocks.common.tileentity.TileEntitySprinkler;
import openblocks.common.tileentity.TileEntityTank;
import openblocks.common.tileentity.TileEntityTarget;
import openblocks.common.tileentity.TileEntityTrophy;
import openblocks.common.tileentity.TileEntityVacuumHopper;
import openblocks.sync.SyncableManager;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy implements IProxy {
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
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityImaginary.class, new TileEntityImaginaryRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFan.class, new TileEntityFanRenderer());

		if (OpenBlocks.Blocks.tank != null) MinecraftForgeClient.registerItemRenderer(OpenBlocks.Blocks.tank.blockID, new ItemRendererTank());

		RenderingRegistry.registerEntityRenderingHandler(EntityGhost.class, new EntityGhostRenderer());

		if (OpenBlocks.Items.luggage != null) {
			MinecraftForgeClient.registerItemRenderer(OpenBlocks.Items.luggage.itemID, new ItemRendererLuggage());
			RenderingRegistry.registerEntityRenderingHandler(EntityLuggage.class, new EntityLuggageRenderer());
		}

		TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);

		if (OpenBlocks.Items.hangGlider != null) {
			RenderingRegistry.registerEntityRenderingHandler(EntityHangGlider.class, new EntityHangGliderRenderer());

			ItemRendererHangGlider hangGliderRenderer = new ItemRendererHangGlider();
			MinecraftForgeClient.registerItemRenderer(OpenBlocks.Items.hangGlider.itemID, hangGliderRenderer);
			MinecraftForge.EVENT_BUS.register(hangGliderRenderer);

			attachPlayerRenderer();
		}

		if (OpenBlocks.Items.sonicGlasses != null) {
			MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
			MinecraftForge.EVENT_BUS.register(SoundEventsManager.instance);
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

	@Override
	public EntityPlayer getThePlayer() {
		return FMLClientHandler.instance().getClient().thePlayer;
	}

	@Override
	public IGuiHandler createGuiHandler() {
		return new ClientGuiHandler();
	}

	@Override
	public long getTicks(World worldObj) {
		return ClientTickHandler.getTicks();
	}
}
