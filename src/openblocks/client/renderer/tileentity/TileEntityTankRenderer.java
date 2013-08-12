package openblocks.client.renderer.tileentity;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.tank.TileEntityTank;
import openblocks.sync.SyncableTank;

import org.lwjgl.opengl.GL11;

public class TileEntityTankRenderer extends TileEntitySpecialRenderer {

	RenderBlocks renderBlocks = new RenderBlocks();
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		bindTextureByName("/terrain.png");
		TileEntityTank tankTile = (TileEntityTank) tileentity;
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
		GL11.glPushMatrix();

		boolean tankInEast = tankTile.getTankInDirection(ForgeDirection.EAST) != null;
		boolean tankInWest = tankTile.getTankInDirection(ForgeDirection.WEST) != null;
		boolean tankInNorth = tankTile.getTankInDirection(ForgeDirection.NORTH) != null;
		boolean tankInSouth = tankTile.getTankInDirection(ForgeDirection.SOUTH) != null;
		boolean tankUp = tankTile.getTankInDirection(ForgeDirection.UP) != null;
		boolean tankDown = tankTile.getTankInDirection(ForgeDirection.DOWN) != null;
		
		if (!tankInEast){
			if (!tankInNorth){
				// north east
				OpenRenderHelper.renderCube(0.475, -0.501, -0.501, 0.501, 0.501, -0.475, OpenBlocks.Blocks.tank, null);
			}
			if (!tankInSouth) {
				// south east
				OpenRenderHelper.renderCube(0.475, -0.501, 0.475, 0.501, 0.501, 0.501, OpenBlocks.Blocks.tank, null);
			}
			
			if (!tankDown) {
				// bottom east
				OpenRenderHelper.renderCube(0.475, -0.501, -0.501, 0.501, -0.475, 0.501, OpenBlocks.Blocks.tank, null);
			}
			
			if (!tankUp) {
				// top east
				OpenRenderHelper.renderCube(0.475, 0.475, -0.501, 0.501, 0.501, 0.501, OpenBlocks.Blocks.tank, null);
			}
		}
		if (!tankInWest) {
			if (!tankInNorth) {
				// north west
				OpenRenderHelper.renderCube(-0.501, -0.501, -0.501, -0.475, 0.501, -0.475, OpenBlocks.Blocks.tank, null);
			}
			if (!tankInSouth) {
				// south west
				OpenRenderHelper.renderCube(-0.501, -0.501, 0.475, -0.475, 0.501, 0.501, OpenBlocks.Blocks.tank, null);
			}
			if (!tankDown) {
				// bottom west
				OpenRenderHelper.renderCube(-0.501, -0.501, -0.501, -0.475, -0.475, 0.501, OpenBlocks.Blocks.tank, null);
			}
			if (!tankUp) {
				// top west
				OpenRenderHelper.renderCube(-0.501, 0.475, -0.501, -0.475, 0.501, 0.501, OpenBlocks.Blocks.tank, null);
			}
		}
		if (!tankInNorth) {
			if (!tankUp) {
				// top north
				OpenRenderHelper.renderCube(-0.501, 0.475, -0.501, 0.501, 0.501, -0.475, OpenBlocks.Blocks.tank, null);
			}
			if (!tankDown) {
				// bottom north
				OpenRenderHelper.renderCube(-0.501, -0.501, -0.501, 0.501, -0.475, -0.475, OpenBlocks.Blocks.tank, null);
			}
		}

		if (!tankInSouth) {

			if (!tankUp) {
				// top south
				OpenRenderHelper.renderCube(-0.501, 0.475, 0.475, 0.501, 0.501, 0.501, OpenBlocks.Blocks.tank, null);
			}
			if (!tankDown) {
				// bottom south
				OpenRenderHelper.renderCube(-0.501, -0.501, 0.475, 0.501, -0.475, 0.501, OpenBlocks.Blocks.tank, null);
			}
		}
		
		GL11.glEnable(2896);
		
		LiquidTank internalTank = tankTile.getInternalTank();
		
		LiquidStack liquid = internalTank.getLiquid();
		
		bindTextureByName("/terrain.png");
		
		if (liquid != null && liquid.amount > 100) {

			Block block = null;
			Icon texture = null;

			GL11.glPushMatrix();
			GL11.glDisable(2896);
			try {
				if (liquid.itemID < Block.blocksList.length && Block.blocksList[liquid.itemID] != null) {
					block = Block.blocksList[liquid.itemID];
					texture = getLiquidTexture(liquid);
				} else if (Item.itemsList[liquid.itemID] != null) {
					block = Block.waterStill;
					texture = getLiquidTexture(liquid);
				} else {
				}
				
				bindTextureByName(getLiquidSheet(liquid));
				
				Tessellator t = Tessellator.instance;
				
				double ySouthEast = tankTile.getLiquidHeightForSide(ForgeDirection.SOUTH, ForgeDirection.EAST);
				double yNorthEast = tankTile.getLiquidHeightForSide(ForgeDirection.NORTH, ForgeDirection.EAST);
				double ySouthWest = tankTile.getLiquidHeightForSide(ForgeDirection.SOUTH, ForgeDirection.WEST);
				double yNorthWest = tankTile.getLiquidHeightForSide(ForgeDirection.NORTH, ForgeDirection.WEST);
				
		        double uMin = (double)texture.getInterpolatedU(0.0);
		        double uMax = (double)texture.getInterpolatedU(16.0);
		        double vMin = (double)texture.getInterpolatedV(0.0);
		        double vMax = (double)texture.getInterpolatedV(16.0);

		        double vHeight = vMax - vMin;
		        
				
				// north side
				t.startDrawingQuads();
				t.addVertexWithUV( 0.5,-0.5,-0.5, uMax, vMin); // bottom 
				t.addVertexWithUV(-0.5,-0.5,-0.5, uMin, vMin); // bottom
				t.addVertexWithUV(-0.5, -0.5 + yNorthWest,-0.5, uMin, vMin + (vHeight * yNorthWest)); // top north/west
				t.addVertexWithUV( 0.5, -0.5 + yNorthEast,-0.5, uMax, vMin + (vHeight * yNorthEast)); // top north/east
				t.draw();
			
				// south side
				t.startDrawingQuads();
				t.addVertexWithUV( 0.5,-0.5, 0.5, 				uMin, vMin);
				t.addVertexWithUV( 0.5, -0.5 + ySouthEast, 0.5, uMin, vMin + (vHeight * ySouthEast)); // top south east
				t.addVertexWithUV(-0.5, -0.5 + ySouthWest, 0.5, uMax, vMin + (vHeight * ySouthWest)); // top south west
				t.addVertexWithUV(-0.5,-0.5, 0.5, 				uMax, vMin);
				t.draw();
			
				// east side
				t.startDrawingQuads();
				t.addVertexWithUV( 0.5, -0.5, -0.5, 			uMin, vMin);
				t.addVertexWithUV( 0.5, -0.5 + yNorthEast, -0.5,uMin, vMin + (vHeight * yNorthEast)); // top north/east
				t.addVertexWithUV(0.5,  -0.5 + ySouthEast,  0.5,uMax, vMin + (vHeight * ySouthEast)); // top south/east
				t.addVertexWithUV(0.5, -0.5,  0.5, 				uMax, vMin );
				t.draw();
			
				// west side
				t.startDrawingQuads();
				t.addVertexWithUV( -0.5, -0.5,  0.5, 			uMin, vMin);
				t.addVertexWithUV( -0.5, -0.5 + ySouthWest, 0.5,uMin, vMin + (vHeight * ySouthWest)); // top south/west
				t.addVertexWithUV(-0.5, -0.5 + yNorthWest, -0.5,uMax, vMin + (vHeight * yNorthWest)); // top north/west
				t.addVertexWithUV(-0.5, -0.5, -0.5,				uMax, vMin);
				t.draw();
			
				// top
				t.startDrawingQuads();
				t.addVertexWithUV( 0.5,  -0.5 + ySouthEast,  0.5,uMax, vMin); // south east
				t.addVertexWithUV(0.5,  -0.5 + yNorthEast, -0.5, uMin, vMin); // north east
				t.addVertexWithUV(-0.5,  -0.5 + yNorthWest, -0.5,uMin, vMax); // north west
				t.addVertexWithUV(-0.5,  -0.5 + ySouthWest,  0.5,uMax, vMax); // south west
				t.draw();
			
				// bottom
				t.startDrawingQuads();
				t.addVertexWithUV( 0.5, -0.5, -0.5, uMax, vMin);
				t.addVertexWithUV(0.5, -0.5,  0.5, 	uMin, vMin);
				t.addVertexWithUV(-0.5, -0.5,  0.5, uMin, vMax);
				t.addVertexWithUV( -0.5, -0.5, -0.5,uMax, vMax);
				t.draw();
			
				
			}catch(Exception e) {
			}
			GL11.glEnable(2896);
			GL11.glPopMatrix();
			
		}
		
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
	
	public static String getLiquidSheet(LiquidStack liquid) {
		if (liquid == null || liquid.itemID <= 0) {
			return "/terrain.png";
		}
		LiquidStack canon = liquid.canonical();
		if (canon == null) {
			return "/terrain.png";
		}
		return canon.getTextureSheet();
	}
}
