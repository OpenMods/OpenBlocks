package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;
import openblocks.client.model.ModelVacuumHopper;
import openblocks.common.tileentity.TileEntityVacuumHopper;
import openblocks.utils.BlockUtils;

import org.lwjgl.opengl.GL11;

public class TileEntityVacuumHopperRenderer extends TileEntitySpecialRenderer {

	private ModelVacuumHopper model = new ModelVacuumHopper();

	private static final ResourceLocation texture = new ResourceLocation("openblocks", "textures/models/vacuumhopper.png");

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		GL11.glPushMatrix();
		func_110628_a(texture);
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.0F, (float)z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		TileEntityVacuumHopper hopper = (TileEntityVacuumHopper)tileentity;
		ForgeDirection surface = hopper.getSurface();
		if (surface == ForgeDirection.UP || surface == ForgeDirection.DOWN) {
			GL11.glTranslated(0, 0.5, 0.5);
			if (surface == ForgeDirection.UP){
				GL11.glTranslated(0, 0, -1.0);
			}
			GL11.glRotatef(BlockUtils.getRotationFromDirection(surface), 1, 0, 0);	
		}else {
			GL11.glRotatef(-BlockUtils.getRotationFromDirection(surface), 0, 1, 0);
		}
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		model.render(hopper, f);
		GL11.glPopMatrix();
	}

}
