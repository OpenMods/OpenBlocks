package openblocks.client.renderer;

import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openmods.Log;
import openmods.block.OpenBlock;
import openmods.block.OpenBlock.BlockRotationMode;
import openmods.renderer.IBlockRenderer;
import openmods.tileentity.OpenTileEntity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.common.collect.Maps;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockRenderingHandler implements ISimpleBlockRenderingHandler {

	private final Map<Block, TileEntity> inventoryTileEntities;
	private final Map<Block, IBlockRenderer> blockRenderers;

	public BlockRenderingHandler() {
		inventoryTileEntities = Maps.newIdentityHashMap();
		blockRenderers = Maps.newIdentityHashMap();
		blockRenderers.put(OpenBlocks.Blocks.path, new BlockPathRenderer());
		BlockCanvasRenderer canvasRenderer = new BlockCanvasRenderer();
		blockRenderers.put(OpenBlocks.Blocks.canvas, canvasRenderer);
		blockRenderers.put(OpenBlocks.Blocks.canvasGlass, canvasRenderer);
		blockRenderers.put(OpenBlocks.Blocks.paintCan, new BlockPaintCanRenderer());
	}

	public TileEntity getTileEntityForBlock(Block block) {
		TileEntity te = inventoryTileEntities.get(block);
		if (te == null) {
			te = block.createTileEntity(Minecraft.getMinecraft().theWorld, 0);
			inventoryTileEntities.put(block, te);
		}
		return te;
	}

	@Override
	public int getRenderId() {
		return OpenBlocks.renderId;
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		OpenBlock openBlock = null;
		if (block instanceof OpenBlock) {
			openBlock = (OpenBlock)block;
		}
		/**
		 * Deal with special block rendering handlers
		 */
		if (blockRenderers.containsKey(block)) {
			blockRenderers.get(block).renderInventoryBlock(block, metadata, modelID, renderer);
			return;
		}
		TileEntity te = null;
		// if it's an openblock
		if (openBlock != null && openBlock.useTESRForInventory()) {
			// get the TE class for this block
			Class<? extends TileEntity> teClass = openBlock.getTileClass();
			// if we've got a special renderer for it
			if (teClass != null && TileEntityRenderer.instance.specialRendererMap.containsKey(teClass)) {
				// get the cached copy
				te = getTileEntityForBlock(block);
				// if it's an opentileentity, prepare it for inventory rendering
				if (te instanceof OpenTileEntity) {
					((OpenTileEntity)te).prepareForInventoryRender(block, metadata);
				}
			}
		}

		try {
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
			if (te != null) {
				GL11.glPushAttrib(GL11.GL_TEXTURE_BIT);
				GL11.glPushMatrix();
				GL11.glTranslated(-0.5, -0.5, -0.5);
				TileEntityRenderer.instance.renderTileEntityAt(te, 0.0D, 0.0D, 0.0D, 0.0F);
				GL11.glPopMatrix();
				GL11.glPopAttrib();
			}
			if (openBlock == null || openBlock.shouldRenderBlock()) {
				ForgeDirection rotation = ForgeDirection.EAST;
				if (block instanceof OpenBlock) {
					rotation = openBlock.getInventoryRenderRotation();
					openBlock.setBoundsBasedOnRotation(rotation);
					rotateFacesOnRenderer(openBlock, rotation, renderer);
				}
				renderInventoryBlock(renderer, block, rotation);
				resetFacesOnRenderer(renderer);
			}
		} catch (Exception e) {
			Log.severe(e, "Error during block '%s' rendering", block.getUnlocalizedName());
		}
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

		OpenBlock openBlock = null;
		if (block instanceof OpenBlock) {
			openBlock = (OpenBlock)block;
		}
		/* deal with custom block renderers */
		if (blockRenderers.containsKey(block)) {
			return blockRenderers.get(block).renderWorldBlock(world, x, y, z, block, modelId, renderer);
		} else if (openBlock == null || openBlock.shouldRenderBlock()) {
			if (openBlock != null) {
				int metadata = world.getBlockMetadata(x, y, z);
				ForgeDirection rotation = ForgeDirection.getOrientation(metadata);
				rotateFacesOnRenderer((OpenBlock)block, rotation, renderer);
			}
			renderer.renderStandardBlock(block, x, y, z);
			resetFacesOnRenderer(renderer);
		}
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	public static void rotateFacesOnRenderer(OpenBlock block, ForgeDirection rotation, RenderBlocks renderer) {
		BlockRotationMode mode = block.getRotationMode();
		switch (mode) {
			case SIX_DIRECTIONS:
				switch (rotation) {
					case DOWN:
						renderer.uvRotateSouth = 3;
						renderer.uvRotateNorth = 3;
						renderer.uvRotateEast = 3;
						renderer.uvRotateWest = 3;
						break;
					case EAST:
						renderer.uvRotateTop = 1;
						renderer.uvRotateBottom = 2;
						renderer.uvRotateWest = 1;
						renderer.uvRotateEast = 2;
						break;
					case NORTH:
						renderer.uvRotateNorth = 2;
						renderer.uvRotateSouth = 1;
						break;
					case SOUTH:
						renderer.uvRotateTop = 3;
						renderer.uvRotateBottom = 3;
						renderer.uvRotateNorth = 1;
						renderer.uvRotateSouth = 2;
						break;
					case UNKNOWN:
						break;
					case UP:
						break;
					case WEST:
						renderer.uvRotateTop = 2;
						renderer.uvRotateBottom = 1;
						renderer.uvRotateWest = 2;
						renderer.uvRotateEast = 1;
						break;
					default:
						break;

				}
				break;
			case FOUR_DIRECTIONS:
				switch (rotation) {
					case EAST:
						renderer.uvRotateTop = 1;
						break;
					case WEST:
						renderer.uvRotateTop = 2;
						break;
					case SOUTH:
						renderer.uvRotateTop = 3;
						break;
					default:
						break;
				}
				break;
			default:
				break;

		}

	}

	public static void resetFacesOnRenderer(RenderBlocks renderer) {
		renderer.uvRotateTop = 0;
		renderer.uvRotateBottom = 0;
		renderer.uvRotateEast = 0;
		renderer.uvRotateNorth = 0;
		renderer.uvRotateSouth = 0;
		renderer.uvRotateTop = 0;
		renderer.uvRotateWest = 0;
		renderer.flipTexture = false;
	}

	public static void renderInventoryBlock(RenderBlocks renderer, Block block, ForgeDirection rotation) {
		renderInventoryBlock(renderer, block, rotation, -1);
	}

	public static void renderInventoryBlock(RenderBlocks renderer, Block block, ForgeDirection rotation, int colorMultiplier) {
		renderInventoryBlock(renderer, block, rotation, colorMultiplier, null);
	}

	public static void renderInventoryBlock(RenderBlocks renderer, Block block, ForgeDirection rotation, int colorMultiplier, Set<ForgeDirection> enabledSides) {
		Tessellator tessellator = Tessellator.instance;
		block.setBlockBoundsForItemRender();
		renderer.setRenderBoundsFromBlock(block);

		float r;
		float g;
		float b;
		if (colorMultiplier > -1)
		{
			r = (colorMultiplier >> 16 & 255) / 255.0F;
			g = (colorMultiplier >> 8 & 255) / 255.0F;
			b = (colorMultiplier & 255) / 255.0F;
			GL11.glColor4f(r, g, b, 1.0F);
		}
		// Learn to matrix, please push and pop :D -- NC
		GL11.glPushMatrix();
		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		int metadata = rotation.ordinal();
		if (enabledSides == null || enabledSides.contains(ForgeDirection.DOWN)) {
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, -1.0F, 0.0F);
			renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, metadata));
			tessellator.draw();
		}
		if (enabledSides == null || enabledSides.contains(ForgeDirection.UP)) {
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 1.0F, 0.0F);
			renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, metadata));
			tessellator.draw();
		}
		if (enabledSides == null || enabledSides.contains(ForgeDirection.SOUTH)) {
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, -1.0F);
			renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, metadata));
			tessellator.draw();
		}
		if (enabledSides == null || enabledSides.contains(ForgeDirection.NORTH)) {
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, 1.0F);
			renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, metadata));
			tessellator.draw();
		}
		if (enabledSides == null || enabledSides.contains(ForgeDirection.WEST)) {
			tessellator.startDrawingQuads();
			tessellator.setNormal(-1.0F, 0.0F, 0.0F);
			renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, metadata));
			tessellator.draw();
		}
		if (enabledSides == null || enabledSides.contains(ForgeDirection.EAST)) {
			tessellator.startDrawingQuads();
			tessellator.setNormal(1.0F, 0.0F, 0.0F);
			renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, metadata));
			tessellator.draw();
		}
		GL11.glPopMatrix();
	}
}
