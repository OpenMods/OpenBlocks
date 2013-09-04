package openblocks.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.client.renderer.tileentity.OpenRenderHelper;
import openblocks.common.TrophyHandler.Trophy;
import openblocks.common.tileentity.OpenTileEntity;
import openblocks.common.tileentity.TileEntityBearTrap;
import openblocks.common.tileentity.TileEntityBigButton;
import openblocks.common.tileentity.TileEntityCannon;
import openblocks.common.tileentity.TileEntityFlag;
import openblocks.common.tileentity.TileEntityGrave;
import openblocks.common.tileentity.TileEntityGuide;
import openblocks.common.tileentity.TileEntityLightbox;
import openblocks.common.tileentity.TileEntitySprinkler;
import openblocks.common.tileentity.TileEntityTarget;
import openblocks.common.tileentity.TileEntityTrophy;
import openblocks.common.tileentity.TileEntityVacuumHopper;

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
	private TileEntitySprinkler teSprinkler = new TileEntitySprinkler();
	private TileEntityVacuumHopper teHopper = new TileEntityVacuumHopper();
	private TileEntityCannon teCannon = new TileEntityCannon();
	private TileEntityBigButton teButton = new TileEntityBigButton();

	public BlockRenderingHandler() {
		teTarget.setEnabled(true);
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
			teTarget.setRotation(ForgeDirection.WEST);
		} else if (block == OpenBlocks.Blocks.grave) {
			te = teGrave;
		} else if (block == OpenBlocks.Blocks.flag) {
			te = teFlag;
			teFlag.setColorIndex(metadata);
			teFlag.setFlag1(true);
		} else if (block == OpenBlocks.Blocks.trophy) {
			if (metadata < Trophy.values().length) {
				te = teTrophy;
				teTrophy.trophyType = Trophy.values()[metadata];
			}
		} else if (block == OpenBlocks.Blocks.bearTrap) {
			te = teBearTrap;
			teBearTrap.setOpen();
		} else if (block == OpenBlocks.Blocks.sprinkler) {
			te = teSprinkler;
		} else if (block == OpenBlocks.Blocks.vacuumHopper) {
			te = teHopper;
		} else if (block == OpenBlocks.Blocks.cannon) {
			te = teCannon;
			teCannon.disableLineRender();
		}else if (block == OpenBlocks.Blocks.bigButton) {
			te = teButton;
			GL11.glTranslated(-0.5, 0, 0);
		}
		if (te instanceof OpenTileEntity) {
			((OpenTileEntity)te).setUsedForClientInventoryRendering(true);
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
