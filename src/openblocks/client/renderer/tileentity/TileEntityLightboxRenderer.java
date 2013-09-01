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
import net.minecraft.util.ResourceLocation;
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
		func_110628_a(TextureMap.field_110575_b);

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
		int meta = 0;
		if (lightbox.worldObj != null) {
			meta = lightbox.worldObj.getBlockMetadata(tileentity.xCoord, tileentity.yCoord, tileentity.zCoord);
		}

		// render a cube
		OpenRenderHelper.renderCube(-0.5, -0.5, 0.3, 0.5, 0.5, 0.5, OpenBlocks.Blocks.lightbox, null);

		// render the map
		ItemStack mapStack = lightbox.getStackInSlot(0);
		if (mapStack != null) {
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
