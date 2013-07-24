package openblocks.client;

import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityGuide;
import openblocks.common.tileentity.TileEntityLightbox;
import openblocks.common.tileentity.TileEntityTarget;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockRenderingHandler implements ISimpleBlockRenderingHandler {

	private TileEntityGuide teGuide = new TileEntityGuide();
	private TileEntityLightbox teLightbox = new TileEntityLightbox();
	private TileEntityTarget teTarget = new TileEntityTarget();

	public BlockRenderingHandler() {
		teTarget.setPowered(true);
	}

	@Override
	public int getRenderId() {
		return OpenBlocks.renderId;
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks renderer) {

		TileEntity te = null;
		if (block == OpenBlocks.Blocks.guide) {
			te = teGuide;
		} else if (block == OpenBlocks.Blocks.lightbox) {
			te = teLightbox;
		} else if (block == OpenBlocks.Blocks.target) {
			te = teTarget;
		}
		if (te != null) {
			GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			TileEntityRenderer.instance.renderTileEntityAt(te, 0.0D, 0.0D,
					0.0D, 0.0F);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		}
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

}
