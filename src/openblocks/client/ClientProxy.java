package openblocks.client;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import openblocks.OpenBlocks;
import openblocks.OpenBlocks.Gui;
import openblocks.client.gui.GuiLightbox;
import openblocks.client.renderer.BlockRenderingHandler;
import openblocks.client.renderer.entity.EntityGhostRenderer;
import openblocks.client.renderer.tileentity.TileEntityGraveRenderer;
import openblocks.client.renderer.tileentity.TileEntityGuideRenderer;
import openblocks.client.renderer.tileentity.TileEntityLightboxRenderer;
import openblocks.client.renderer.tileentity.TileEntityTargetRenderer;
import openblocks.client.renderer.tileentity.TileEntityValveRenderer;
import openblocks.common.CommonProxy;
import openblocks.common.container.ContainerLightbox;
import openblocks.common.entity.EntityGhost;
import openblocks.common.tileentity.TileEntityFlag;
import openblocks.common.tileentity.TileEntityGrave;
import openblocks.common.tileentity.TileEntityGuide;
import openblocks.common.tileentity.TileEntityLightbox;
import openblocks.common.tileentity.TileEntityTarget;
import openblocks.common.tileentity.TileEntityValve;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

	public ClientProxy() {
		MinecraftForge.EVENT_BUS.register(new SoundLoader());
	}

	public void registerRenderInformation() {

		OpenBlocks.renderId = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(new BlockRenderingHandler());

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGuide.class,
				new TileEntityGuideRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLightbox.class,
				new TileEntityLightboxRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTarget.class,
				new TileEntityTargetRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGrave.class,
				new TileEntityGraveRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFlag.class,
				new TileEntityFlagRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityValve.class,
				new TileEntityValveRenderer());
		
		RenderingRegistry.registerEntityRenderingHandler(EntityGhost.class, new EntityGhostRenderer());
		
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		if ((world instanceof WorldClient)) {
			TileEntity tile = world.getBlockTileEntity(x, y, z);
			if (ID == Gui.Lightbox.ordinal()) {
				return new GuiLightbox(new ContainerLightbox(player.inventory,
						(TileEntityLightbox) tile));
			}
		}
		return null;
	}
}
