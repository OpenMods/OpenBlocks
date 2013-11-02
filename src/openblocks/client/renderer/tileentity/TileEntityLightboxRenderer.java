package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityLightbox;
import openblocks.utils.BlockUtils;

import org.lwjgl.opengl.GL11;

public class TileEntityLightboxRenderer extends TileEntitySpecialRenderer {

	RenderBlocks renderBlocks = new RenderBlocks();

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {

		GL11.glPushMatrix();
		bindTexture(TextureMap.locationBlocksTexture);

		// move to the middle of the block
		GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);

		// rotate to the correct rotation
		TileEntityLightbox lightbox = (TileEntityLightbox)tileentity;
		ForgeDirection surface = lightbox.getSurface();
		ForgeDirection rotation = lightbox.getRotation();
		if (surface == ForgeDirection.UP || surface == ForgeDirection.DOWN) {
			GL11.glRotatef(BlockUtils.getRotationFromDirection(surface.getOpposite()), 1, 0, 0);
			GL11.glRotatef(-BlockUtils.getRotationFromDirection(rotation.getOpposite()), 0, 0, 1);
		} else {
			GL11.glRotatef(BlockUtils.getRotationFromDirection(surface.getOpposite()), 0, 1, 0);
		}

		// render a cube
		GL11.glDisable(GL11.GL_LIGHTING);
		OpenRenderHelper.renderCube(-0.5, -0.5, 0.3, 0.5, 0.5, 0.5, OpenBlocks.Blocks.lightbox, null);
		GL11.glEnable(GL11.GL_LIGHTING);
		// render the map
		ItemStack mapStack = lightbox.getStackInSlot(0);
		if (mapStack != null && mapStack.getItem().isMap()) {
			MapData mapdata = Item.map.getMapData(mapStack, tileentity.worldObj);
			if (mapdata != null) {
				GL11.glTranslatef(0.5f, 0.5F, 0.299f);
				GL11.glScaled(1.0 / 128, 1.0 / 128, 1.0 / 128);
				GL11.glRotatef(180, 0, 0, 1);

				RenderHelper.disableStandardItemLighting();
				mapdata.playersVisibleOnMap.clear();
				RenderManager.instance.itemRenderer.mapItemRenderer.renderMap((EntityPlayer)null, RenderManager.instance.renderEngine, mapdata);
				RenderHelper.enableStandardItemLighting();
			}
		}

		GL11.glPopMatrix();
	}

}
