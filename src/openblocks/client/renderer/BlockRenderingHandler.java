package openblocks.client.renderer;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Log;
import openblocks.OpenBlocks;
import openblocks.client.renderer.tileentity.OpenRenderHelper;
import openblocks.common.tileentity.*;

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

		TileEntityLightbox teLightbox = new TileEntityLightbox();
		inventoryTileEntities.put(OpenBlocks.Blocks.lightbox, teLightbox);

		TileEntityTarget teTarget = new TileEntityTarget();
		teTarget.setEnabled(true);
		teTarget.setRotation(ForgeDirection.WEST);
		inventoryTileEntities.put(OpenBlocks.Blocks.target, teTarget);

		TileEntityGrave teGrave = new TileEntityGrave();
		inventoryTileEntities.put(OpenBlocks.Blocks.grave, teGrave);

		TileEntityFlag teFlag = new TileEntityFlag();
		teFlag.setFlag1(true);
		inventoryTileEntities.put(OpenBlocks.Blocks.flag, teFlag);

		TileEntityTrophy teTrophy = new TileEntityTrophy();
		inventoryTileEntities.put(OpenBlocks.Blocks.trophy, teTrophy);

		TileEntityBearTrap teBearTrap = new TileEntityBearTrap();
		inventoryTileEntities.put(OpenBlocks.Blocks.bearTrap, teBearTrap);

		TileEntitySprinkler teSprinkler = new TileEntitySprinkler();
		inventoryTileEntities.put(OpenBlocks.Blocks.sprinkler, teSprinkler);

		TileEntityVacuumHopper teHopper = new TileEntityVacuumHopper();
		inventoryTileEntities.put(OpenBlocks.Blocks.vacuumHopper, teHopper);

		TileEntityCannon teCannon = new TileEntityCannon();
		teCannon.disableLineRender();
		inventoryTileEntities.put(OpenBlocks.Blocks.cannon, teCannon);

		TileEntityBigButton teButton = new TileEntityBigButton();
		inventoryTileEntities.put(OpenBlocks.Blocks.bigButton, teButton);

		TileEntityFan teFan = new TileEntityFan();
		inventoryTileEntities.put(OpenBlocks.Blocks.fan, teFan);

		TileEntityVillageHighlighter teVillageHighlighter = new TileEntityVillageHighlighter();
		inventoryTileEntities.put(OpenBlocks.Blocks.villageHighlighter, teVillageHighlighter);

		TileEntityAutoAnvil teAutoAnvil = new TileEntityAutoAnvil();
		inventoryTileEntities.put(OpenBlocks.Blocks.autoAnvil, teAutoAnvil);
		
		TileEntityAutoEnchantmentTable teAutoTable = new TileEntityAutoEnchantmentTable();
		inventoryTileEntities.put(OpenBlocks.Blocks.autoEnchantmentTable, teAutoTable);
	}

	@Override
	public int getRenderId() {
		return OpenBlocks.renderId;
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {

		if (blockRenderers.containsKey(block)) {
			blockRenderers.get(block).renderInventoryBlock(block, metadata, modelID, renderer);
			return;
		}

		TileEntity te = inventoryTileEntities.get(block);

		if (te instanceof OpenTileEntity) {
			((OpenTileEntity)te).prepareForInventoryRender(block, metadata);
		}

		try {
			final World world = Minecraft.getMinecraft().theWorld;
			if (world != null) {
				GL11.glEnable(GL12.GL_RESCALE_NORMAL);
				GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
				if (te != null) {
					te.worldObj = world;
					GL11.glTranslated(-0.5, -0.5, -0.5);
					TileEntityRenderer.instance.renderTileEntityAt(te, 0.0D, 0.0D, 0.0D, 0.0F);
				} else {
					OpenRenderHelper.renderCube(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5, block, null);
				}
			}
		} catch (Exception e) {
			Log.severe(e, "Error during block '%s' rendering", block.getUnlocalizedName());
		}
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		if (blockRenderers.containsKey(block)) { return blockRenderers.get(block).renderWorldBlock(world, x, y, z, block, modelId, renderer); }
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

}
