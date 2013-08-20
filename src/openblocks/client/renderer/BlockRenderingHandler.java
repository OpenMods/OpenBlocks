package openblocks.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import openblocks.OpenBlocks;
import openblocks.client.renderer.tileentity.OpenRenderHelper;
import openblocks.common.TrophyHandler.Trophy;
import openblocks.common.tileentity.TileEntityBearTrap;
import openblocks.common.tileentity.TileEntityFlag;
import openblocks.common.tileentity.TileEntityGrave;
import openblocks.common.tileentity.TileEntityGuide;
import openblocks.common.tileentity.TileEntityLightbox;
import openblocks.common.tileentity.TileEntityTarget;
import openblocks.common.tileentity.TileEntityTrophy;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockRenderingHandler implements ISimpleBlockRenderingHandler {

	private TileEntityGuide teGuide = new TileEntityGuide();
	private TileEntityLightbox teLightbox = new TileEntityLightbox();
	private TileEntityTarget teTarget = new TileEntityTarget();
	private TileEntityGrave teGrave = new TileEntityGrave();
	private TileEntityFlag teFlag = new TileEntityFlag();
	private TileEntityTrophy teTrophy = new TileEntityTrophy();
	private TileEntityBearTrap teBearTrap = new TileEntityBearTrap();

	public BlockRenderingHandler() {
		teTarget.setPowered(true);
	}

	@Override
	public int getRenderId() {
		return OpenBlocks.renderId;
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {

		TileEntity te = null;
		if (block == OpenBlocks.Blocks.lightbox) {
			te = teLightbox;
		} else if (block == OpenBlocks.Blocks.target) {
			te = teTarget;
		} else if (block == OpenBlocks.Blocks.grave) {
			te = teGrave;
		} else if (block == OpenBlocks.Blocks.flag) {
			te = teFlag;
			teFlag.setColorIndex(metadata);
		} else if (block == OpenBlocks.Blocks.trophy) {
			if (metadata < Trophy.values().length) {
				te = teTrophy;
				teTrophy.trophyType = Trophy.values()[metadata];
			}
		} else if (block == OpenBlocks.Blocks.bearTrap) {
			te = teBearTrap;
			teBearTrap.setOpen();
		}
		try {
			if (Minecraft.getMinecraft().theWorld != null) {
				GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
				if (te != null) {
					te.worldObj = Minecraft.getMinecraft().theWorld;
					GL11.glTranslated(-0.5, -0.5, -0.5);
					TileEntityRenderer.instance.renderTileEntityAt(te, 0.0D, 0.0D, 0.0D, 0.0F);
				} else {
					OpenRenderHelper.renderCube(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5, block, null);
				}
				GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			}
		} catch (Exception e) {

		}
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

}
