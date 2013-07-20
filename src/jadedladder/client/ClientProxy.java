package jadedladder.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import jadedladder.JadedLadder;
import jadedladder.common.CommonProxy;
import jadedladder.common.tileentity.TileEntityGuide;

public class ClientProxy extends CommonProxy {

	public void registerRenderInformation() {

		JadedLadder.renderId = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(new BlockRenderingHandler());

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGuide.class, new TileEntityGuideRenderer());
		
	}
}
