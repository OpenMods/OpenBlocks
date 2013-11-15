package openblocks.client.renderer.tileentity;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityRopeLadder;
import openblocks.utils.BlockUtils;

import org.lwjgl.opengl.GL11;

public class TileEntityRopeLadderRenderer extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {

		TileEntityRopeLadder rope = (TileEntityRopeLadder)tileentity;
		GL11.glPushMatrix();
		bindTexture(TextureMap.locationBlocksTexture);
		long ticks = OpenBlocks.proxy.getTicks(rope.worldObj);
		double offset = 0.0;
		if (rope.shouldAnimate()) {
			offset = (MathHelper.sin((float)(rope.yCoord + ((double)ticks / 5))) / 50);
		}
		ForgeDirection rot = rope.getRotation();
		GL11.glTranslated(x + 0.5 + (offset * rot.offsetX), y + 0.5, z + 0.5
				+ (offset * rot.offsetZ));
		GL11.glRotatef(BlockUtils.getRotationFromDirection(rot), 0, 1, 0);
		GL11.glDisable(GL11.GL_LIGHTING);

		Block b = OpenBlocks.Blocks.ropeLadder;
		Tessellator t = Tessellator.instance;
		GL11.glColor4f(1, 1, 1, 1);
		OpenRenderHelper.renderBlocks.setRenderBounds(-0.5, -0.5, -0.5, 0.5, 0.5, -0.4375);
		t.startDrawingQuads();

		OpenRenderHelper.renderBlocks.renderFaceZNeg(b, 0, 0, 0, b.getBlockTextureFromSide(0));
		OpenRenderHelper.renderBlocks.renderFaceZPos(b, 0, 0, 0, b.getBlockTextureFromSide(0));

		t.draw();

		GL11.glEnable(GL11.GL_LIGHTING);

		GL11.glPopMatrix();
	}

}
