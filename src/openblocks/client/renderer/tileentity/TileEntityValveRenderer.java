package openblocks.client.renderer.tileentity;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraftforge.liquids.LiquidStack;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityValve;

import org.lwjgl.opengl.GL11;

public class TileEntityValveRenderer extends TileEntitySpecialRenderer {

	RenderBlocks renderBlocks = new RenderBlocks();
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y,
			double z, float f) {
		TileEntityValve valve = (TileEntityValve) tileentity;
		renderValve(x, y, z);
		if (valve != null && valve.worldObj != null) {
			int[] coords = valve.getLinkedCoords();
			HashMap<Integer, Double> spread = valve.getSpread();
			if (coords == null) {
				return;
			}
			for (int i = 0; i < coords.length; i += 3) {
				int _x = coords[i];
				int _y = coords[i+1];
				int _z = coords[i+2];
				double yLevel = 0;
				if (spread != null && spread.containsKey(_y)) {
					yLevel = spread.get(_y);
				}
				renderAt(_x + x, _y + y, _z + z, yLevel, valve.getLiquid());
			}
		}
	}


	private void renderValve(double x, double y, double z) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y, (float) z + 0.5F);
		GL11.glPushMatrix();
		GL11.glDisable(2896);

		Tessellator t = Tessellator.instance;
		renderBlocks.setRenderBounds(0.0D, 0, 0.0D, 1.0, 1.0, 1.0);
		t.startDrawingQuads();
		t.setColorRGBA(0, 0, 0, 255);
		t.setBrightness(200);
		this.bindTextureByName("/mods/openblocks/textures/blocks/guide.png");
		Icon renderingIcon = OpenBlocks.Blocks.guide.getBlockTextureFromSide(0);
		renderBlocks.renderFaceXNeg(OpenBlocks.Blocks.guide, -0.5D, 0.0D,
				-0.5D, renderingIcon);
		renderBlocks.renderFaceXPos(OpenBlocks.Blocks.guide, -0.5D, 0.0D,
				-0.5D, renderingIcon);
		renderBlocks.renderFaceYNeg(OpenBlocks.Blocks.guide, -0.5D, 0.0D,
				-0.5D, renderingIcon);
		renderBlocks.renderFaceYPos(OpenBlocks.Blocks.guide, -0.5D, 0.0D,
				-0.5D, renderingIcon);
		renderBlocks.renderFaceZNeg(OpenBlocks.Blocks.guide, -0.5D, 0.0D,
				-0.5D, renderingIcon);
		renderBlocks.renderFaceZPos(OpenBlocks.Blocks.guide, -0.5D, 0.0D,
				-0.5D, renderingIcon);
		t.draw();

		GL11.glEnable(2896);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}


	private void renderAt(double x, double y, double z, double yLevel, LiquidStack liquid) {
		if (yLevel == 0) {
			return;
		}
		if (liquid == null) {
			//System.out.println("no liquid");
			return;
		}
		liquid = liquid.canonical();
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y, (float) z + 0.5F);
		GL11.glPushMatrix();
		GL11.glDisable(2896);
		bindTextureByName("/terrain.png");
		Tessellator t = Tessellator.instance;
		renderBlocks.setRenderBounds(0.0D, 0, 0.0D, 1.0, yLevel, 1.0);
		t.startDrawingQuads();
		t.setColorRGBA(255, 255, 255, 255);
		Block block = null;
		Icon texture = null;
		try {
			if (liquid.itemID < Block.blocksList.length && Block.blocksList[liquid.itemID] != null) {
				block = Block.blocksList[liquid.itemID];
				texture = getLiquidTexture(liquid);
			} else if (Item.itemsList[liquid.itemID] != null) {
				block = Block.waterStill;
				texture = getLiquidTexture(liquid);
			} else {
				return;
			}
		}catch(Exception e) {
			return;
		}
		
		renderBlocks.renderFaceXNeg(OpenBlocks.Blocks.guide, -0.5D, 0.0D, -0.5D, texture != null ? texture : block.getBlockTextureFromSide(0));
		renderBlocks.renderFaceXPos(OpenBlocks.Blocks.guide, -0.5D, 0.0D, -0.5D, texture != null ? texture : block.getBlockTextureFromSide(0));
		renderBlocks.renderFaceYNeg(OpenBlocks.Blocks.guide, -0.5D, 0.0D, -0.5D, texture != null ? texture : block.getBlockTextureFromSide(0));
		renderBlocks.renderFaceYPos(OpenBlocks.Blocks.guide, -0.5D, 0.0D, -0.5D, texture != null ? texture : block.getBlockTextureFromSide(0));
		renderBlocks.renderFaceZNeg(OpenBlocks.Blocks.guide, -0.5D, 0.0D, -0.5D, texture != null ? texture : block.getBlockTextureFromSide(0));
		renderBlocks.renderFaceZPos(OpenBlocks.Blocks.guide, -0.5D, 0.0D, -0.5D, texture != null ? texture : block.getBlockTextureFromSide(0));
		t.draw();

		GL11.glEnable(2896);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
	
	public static Icon getLiquidTexture(LiquidStack liquid) throws Exception {
		if (liquid == null || liquid.itemID <= 0) {
			return null;
		}
		LiquidStack canon = liquid.canonical();
		if (canon == null) {
			throw new Exception();
		}
		Icon icon = canon.getRenderingIcon();
		if (icon == null) {
			throw new Exception();
		}
		return icon;
	}

}
