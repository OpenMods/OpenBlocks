package openblocks.client.renderer.tileentity;

import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityTarget;
import openblocks.common.tileentity.tank.TileEntityTank;
import openblocks.common.tileentity.tank.TileEntityTankBase;
import openblocks.sync.SyncableTank;
import openblocks.utils.BlockUtils;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidStack;

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
		
		SyncableTank internalTank = tankTile.getInternalTank();
		
		LiquidStack liquid = internalTank.getLiquid();
		bindTextureByName("/terrain.png");
		if (liquid != null && liquid.amount > 100) {

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
				}
				
				GL11.glPushMatrix();
				GL11.glDisable(2896);
				Tessellator t = Tessellator.instance;
				
				double ySouthEast = tankTile.getLiquidHeightForSide(ForgeDirection.SOUTH, ForgeDirection.EAST);
				double yNorthEast = tankTile.getLiquidHeightForSide(ForgeDirection.NORTH, ForgeDirection.EAST);
				double ySouthWest = tankTile.getLiquidHeightForSide(ForgeDirection.SOUTH, ForgeDirection.WEST);
				double yNorthWest = tankTile.getLiquidHeightForSide(ForgeDirection.NORTH, ForgeDirection.WEST);
				
				// north side
				t.startDrawingQuads();
				t.addVertex( 0.5,-0.5,-0.5); // bottom 
				t.addVertex(-0.5,-0.5,-0.5 ); // bottom
				t.addVertex(-0.5, -0.5 + yNorthWest,-0.5 ); // top north/west
				t.addVertex( 0.5, -0.5 + yNorthEast,-0.5 ); // top north/east
				t.draw();
			
				// south side
				t.startDrawingQuads();
				t.addVertex( 0.5,-0.5, 0.5);
				t.addVertex( 0.5, -0.5 + ySouthEast, 0.5); // top south east
				t.addVertex(-0.5, -0.5 + ySouthWest, 0.5); // top south west
				t.addVertex(-0.5,-0.5, 0.5);
				t.draw();
			
				// east side
				t.startDrawingQuads();
				t.addVertex( 0.5, -0.5, -0.5 );
				t.addVertex( 0.5, -0.5 + yNorthEast, -0.5); // top north/east
				t.addVertex(0.5,  -0.5 + ySouthEast,  0.5); // top south/east
				t.addVertex(0.5, -0.5,  0.5 );
				t.draw();
			
				// west side
				t.startDrawingQuads();
				t.addVertex( -0.5, -0.5,  0.5 );
				t.addVertex( -0.5, -0.5 + ySouthWest,  0.5); // top south/west
				t.addVertex(-0.5, -0.5 + yNorthWest, -0.5 ); // top north/west
				t.addVertex(-0.5, -0.5, -0.5 );
				t.draw();
			
				// top
				t.startDrawingQuads();
				t.addVertex( 0.5,  -0.5 + ySouthEast,  0.5 ); // south east
				t.addVertex(0.5,  -0.5 + yNorthEast, -0.5); // north east
				t.addVertex(-0.5,  -0.5 + yNorthWest, -0.5 ); // north west
				t.addVertex(-0.5,  -0.5 + ySouthWest,  0.5 ); // south west
				t.draw();
			
				// bottom
				t.startDrawingQuads();
				t.addVertex( 0.5, -0.5, -0.5 );
				t.addVertex(0.5, -0.5,  0.5 );
				t.addVertex(-0.5, -0.5,  0.5 );
				t.addVertex( -0.5, -0.5, -0.5 );
				t.draw();
			
				GL11.glEnable(2896);
				GL11.glPopMatrix();
				
			}catch(Exception e) {
			}
			
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
}
