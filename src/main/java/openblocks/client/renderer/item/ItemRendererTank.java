package openblocks.client.renderer.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import openblocks.client.renderer.block.BlockTankRenderer;
import openblocks.client.renderer.tileentity.TileEntityTankRenderer;
import openblocks.client.renderer.tileentity.tank.ITankRenderFluidData;
import openblocks.common.tileentity.TileEntityTank;
import openmods.utils.Diagonal;
import org.lwjgl.opengl.GL11;

public class ItemRendererTank implements IItemRenderer {

	private FluidTank tank = new FluidTank(TileEntityTank.getTankCapacity());

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
		GL11.glPushMatrix();
		if (type == ItemRenderType.ENTITY) GL11.glTranslated(-0.5, -0.5, -0.5);
		else if (type == ItemRenderType.INVENTORY) GL11.glTranslated(0, -0.1, 0);

		BlockTankRenderer.EMPTY_FRAME.render();

		NBTTagCompound tag = item.getTagCompound();
		if (tag != null && tag.hasKey("tank")) {
			final FluidStack stack = readFluid(tag);
			if (stack != null) {
				final float height = (float)tank.getFluidAmount() / tank.getCapacity();
				TileEntityTankRenderer.renderFluid(new ITankRenderFluidData() {
					@Override
					public boolean shouldRenderFluidWall(ForgeDirection side) {
						return true;
					}

					@Override
					public boolean hasFluid() {
						return true;
					}

					@Override
					public FluidStack getFluid() {
						return stack;
					}

					@Override
					public float getCornerFluidLevel(Diagonal diagonal, float time) {
						return height;
					}

					@Override
					public float getCenterFluidLevel(float time) {
						return height;
					}
				}, 0);
			}
		}

		GL11.glPopMatrix();
	}

	private FluidStack readFluid(NBTTagCompound tag) {
		synchronized (tank) {
			tank.setFluid(null);
			tank.readFromNBT(tag.getCompoundTag("tank"));
			return tank.getFluid();
		}
	}
}
