package openblocks.client.renderer.block;

import static openblocks.client.renderer.tileentity.tank.INeighbourMap.*;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import openblocks.client.renderer.tileentity.tank.INeighbourMap;
import openblocks.common.block.BlockTank;
import openblocks.common.tileentity.TileEntityTank;
import openmods.renderer.DisplayListWrapper;
import openmods.renderer.IBlockRenderer;
import openmods.utils.render.RenderUtils;

import org.lwjgl.opengl.GL11;

public class BlockTankRenderer implements IBlockRenderer<BlockTank> {

	private static final double H = 0.01;

	public static final DisplayListWrapper EMPTY_FRAME = new DisplayListWrapper() {

		@Override
		public void compile() {
			GL11.glColor3f(1, 1, 1);
			GL11.glDisable(GL11.GL_TEXTURE_2D);

			final Tessellator tes = new Tessellator();
			tes.startDrawingQuads();
			tes.setColorOpaque(0, 0, 0);
			tes.setTextureUV(0, 0);
			BlockTankRenderer.render(tes, INeighbourMap.NO_NEIGHBOURS, 0, 0, 0);
			tes.draw();

			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
	};

	// Can I interest you in Karnaugh maps? -B
	// This whole logic stuff would be really hard to document here. Sorry.
	// Symmetry of equations is broken by special casing for blocks on diagonal
	// (to prevent edge from being drawn twice)

	private static boolean shouldRenderEdgeT(INeighbourMap connections, int d1, int d2) {
		final boolean n1 = connections.hasDirectNeighbour(d1);
		final boolean n2 = connections.hasDirectNeighbour(d2);
		final boolean n12 = connections.hasDiagonalNeighbour(d1, d2);
		return (n1 == n2) & !n12;
	}

	private static boolean shouldRenderEdgeB(INeighbourMap connections, int d1, int d2) {
		final boolean n1 = connections.hasDirectNeighbour(d1);
		final boolean n2 = connections.hasDirectNeighbour(d2);
		final boolean n12 = connections.hasDiagonalNeighbour(d1, d2);
		return (!n2 & !n1) | (n2 & !n12 & n1);
	}

	private static void render(Tessellator tes, INeighbourMap connections, double x, double y, double z) {
		if (shouldRenderEdgeT(connections, DIR_SOUTH, DIR_DOWN)) RenderUtils.renderCube(tes, x - H + 0, y - H + 0, z - H + 1, x + H + 1, y + H + 0, z + H + 1);
		if (shouldRenderEdgeT(connections, DIR_NORTH, DIR_DOWN)) RenderUtils.renderCube(tes, x - H + 0, y - H + 0, z - H + 0, x + H + 1, y + H + 0, z + H + 0);
		if (shouldRenderEdgeB(connections, DIR_SOUTH, DIR_UP)) RenderUtils.renderCube(tes, x - H + 0, y - H + 1, z - H + 1, x + H + 1, y + H + 1, z + H + 1);
		if (shouldRenderEdgeB(connections, DIR_NORTH, DIR_UP)) RenderUtils.renderCube(tes, x - H + 0, y - H + 1, z - H + 0, x + H + 1, y + H + 1, z + H + 0);

		if (shouldRenderEdgeT(connections, DIR_EAST, DIR_NORTH)) RenderUtils.renderCube(tes, x - H + 1, y - H + 0, z - H + 0, x + H + 1, y + H + 1, z + H + 0);
		if (shouldRenderEdgeT(connections, DIR_WEST, DIR_NORTH)) RenderUtils.renderCube(tes, x - H + 0, y - H + 0, z - H + 0, x + H + 0, y + H + 1, z + H + 0);
		if (shouldRenderEdgeB(connections, DIR_EAST, DIR_SOUTH)) RenderUtils.renderCube(tes, x - H + 1, y - H + 0, z - H + 1, x + H + 1, y + H + 1, z + H + 1);
		if (shouldRenderEdgeB(connections, DIR_WEST, DIR_SOUTH)) RenderUtils.renderCube(tes, x - H + 0, y - H + 0, z - H + 1, x + H + 0, y + H + 1, z + H + 1);

		if (shouldRenderEdgeT(connections, DIR_EAST, DIR_DOWN)) RenderUtils.renderCube(tes, x - H + 1, y - H + 0, z - H + 0, x + H + 1, y + H + 0, z + H + 1);
		if (shouldRenderEdgeT(connections, DIR_WEST, DIR_DOWN)) RenderUtils.renderCube(tes, x - H + 0, y - H + 0, z - H + 0, x + H + 0, y + H + 0, z + H + 1);
		if (shouldRenderEdgeB(connections, DIR_EAST, DIR_UP)) RenderUtils.renderCube(tes, x - H + 1, y - H + 1, z - H + 0, x + H + 1, y + H + 1, z + H + 1);
		if (shouldRenderEdgeB(connections, DIR_WEST, DIR_UP)) RenderUtils.renderCube(tes, x - H + 0, y - H + 1, z - H + 0, x + H + 0, y + H + 1, z + H + 1);
	}

	@Override
	public void renderInventoryBlock(BlockTank block, int metadata, int modelID, RenderBlocks renderer) {
		EMPTY_FRAME.render(); // not actually used, since there is custom item renderer
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, BlockTank block, int modelId, RenderBlocks renderer) {
		if (renderer.hasOverrideBlockTexture()) {
			// breaking animation handling
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderStandardBlock(block, x, y, z);
			return true;
		}

		TileEntity te = world.getTileEntity(x, y, z);
		INeighbourMap connections = (te instanceof TileEntityTank)? ((TileEntityTank)te).getRenderNeigbourMap() : INeighbourMap.NO_NEIGHBOURS;
		final IIcon icon = block.getIcon();
		Tessellator.instance.setTextureUV(icon.getMinU(), icon.getMinV());
		Tessellator.instance.setColorOpaque(255, 255, 255);
		render(Tessellator.instance, connections, x, y, z);
		return true;
	}

}
