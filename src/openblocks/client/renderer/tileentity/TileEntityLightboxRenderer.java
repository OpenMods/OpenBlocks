package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityLightbox;
import openblocks.utils.BlockUtils;

import org.lwjgl.opengl.GL11;

public class TileEntityLightboxRenderer extends TileEntitySpecialRenderer {

	RenderBlocks renderBlocks = new RenderBlocks();

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y,
			double z, float f) {

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5f, (float) z + 0.5F);
		TileEntityLightbox lightbox = (TileEntityLightbox) tileentity;
		ForgeDirection surface = lightbox.getSurface();
		ForgeDirection rotation = lightbox.getRotation();

		RenderHelper.disableStandardItemLighting();
		if (surface == ForgeDirection.UP || surface == ForgeDirection.DOWN) {
			GL11.glRotatef(
					BlockUtils.getRotationFromDirection(surface.getOpposite()),
					1, 0, 0);
			GL11.glRotatef(-BlockUtils.getRotationFromDirection(rotation
					.getOpposite()), 0, 0, 1);
		} else {
			GL11.glRotatef(
					BlockUtils.getRotationFromDirection(surface.getOpposite()),
					0, 1, 0);
		}

		GL11.glPushMatrix();

		Tessellator t = Tessellator.instance;
		renderBlocks.setRenderBounds(0D, 0.0D, 0.8D, 1D, 1D, 1d);
		t.startDrawingQuads();

		t.setColorRGBA(200, 200, 200, 255);
		t.setBrightness(255);
		if (lightbox.worldObj != null) {
			int metadata = lightbox.worldObj.getBlockMetadata(lightbox.xCoord,
					lightbox.yCoord, lightbox.zCoord);
			if (metadata > 0) {
				t.setColorRGBA(255, 255, 255, 255);
			}
		}
		this.bindTextureByName("/mods/openblocks/textures/blocks/guide.png");
		Icon renderingIcon = OpenBlocks.Blocks.lightbox
				.getBlockTextureFromSide(0);
		renderBlocks.renderFaceXNeg(OpenBlocks.Blocks.lightbox, -0.5D, -0.5D,
				-0.5D, renderingIcon);
		renderBlocks.renderFaceXPos(OpenBlocks.Blocks.lightbox, -0.5D, -0.5D,
				-0.5D, renderingIcon);
		renderBlocks.renderFaceYNeg(OpenBlocks.Blocks.lightbox, -0.5D, -0.5D,
				-0.5D, renderingIcon);
		renderBlocks.renderFaceYPos(OpenBlocks.Blocks.lightbox, -0.5D, -0.5D,
				-0.5D, renderingIcon);
		renderBlocks.renderFaceZNeg(OpenBlocks.Blocks.lightbox, -0.5D, -0.5D,
				-0.5D, renderingIcon);
		renderBlocks.renderFaceZPos(OpenBlocks.Blocks.lightbox, -0.5D, -0.5D,
				-0.5D, renderingIcon);

		t.draw();

		ItemStack mapStack = lightbox.getStackInSlot(0);
		if (mapStack != null) {
			MapData mapdata = Item.map
					.getMapData(mapStack, tileentity.worldObj);
			GL11.glTranslatef(0.5f, 0.5F, 0.299f);
			GL11.glScaled(0.0078125, 0.0078125, 0.0078125);
			GL11.glRotatef(180, 0, 0, 1);
			if (mapdata != null) {
				mapdata.playersVisibleOnMap.clear();
				RenderManager.instance.itemRenderer.mapItemRenderer.renderMap(
						(EntityPlayer) null,
						RenderManager.instance.renderEngine, mapdata);
			}
		}

		GL11.glPopMatrix();

		RenderHelper.enableStandardItemLighting();
		GL11.glPopMatrix();
	}

}
