package openblocks.client.renderer;

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import openblocks.common.tileentity.TileEntityTank;
import openmods.sync.SyncableTank;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ItemRendererTank implements IItemRenderer {

	private TileEntityTank teTank = new TileEntityTank();

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		if (item.hasTagCompound() && item.getTagCompound().hasKey("tank")) {
			((SyncableTank)teTank.getTank()).readFromNBT(item.getTagCompound().getCompoundTag("tank"));
		} else {
			((SyncableTank)teTank.getTank()).setFluid(null);
		}

		TileEntityRenderer.instance.renderTileEntityAt(teTank, 0.0D, 0.0D, 0.0D, 0.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	}

}
