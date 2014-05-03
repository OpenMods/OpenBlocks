package openblocks.client.renderer.item;

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.IItemRenderer;
import openblocks.common.tileentity.TileEntityTank;
import openmods.sync.SyncableTank;

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
		final SyncableTank tank = (SyncableTank)teTank.getTank();
		NBTTagCompound tag = item.getTagCompound();
		if (tag != null && tag.hasKey("tank")) {
			tank.readFromNBT(tag.getCompoundTag("tank"));
		} else {
			tank.setFluid(null);
		}

		TileEntityRenderer.instance.renderTileEntityAt(teTank, 0.0D, -0.1D, 0.0D, 0.0F);
	}

}
