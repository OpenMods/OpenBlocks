package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import openblocks.client.model.ModelTarget;
import openblocks.client.model.ModelValve;
import openblocks.common.tileentity.TileEntityTarget;
import openblocks.common.tileentity.tank.TileEntityTankValve;
import openblocks.utils.BlockUtils;

import org.lwjgl.opengl.GL11;

public class TileEntityValveRenderer extends TileEntitySpecialRenderer {

	private ModelValve model = new ModelValve();

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {

		TileEntityTankValve valve = (TileEntityTankValve)tileentity;

		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5f, (float)y + 0.5f, (float)z + 0.5f);
		//GL11.glRotatef(180, 0, 0, 1);
		//GL11.glPushMatrix();
			ForgeDirection rotation = valve.getRotation().getOpposite();
			if (rotation == ForgeDirection.UP || rotation == ForgeDirection.DOWN) {
				GL11.glRotatef(BlockUtils.getRotationFromDirection(rotation), 1, 0, 0);
			}else {
				GL11.glRotatef(BlockUtils.getRotationFromDirection(rotation), 0, 1, 0);
			}
		
			GL11.glTranslatef(0, -0.5f, 0f);
			this.bindTextureByName("/mods/openblocks/textures/models/valve.png");
			model.render(tileentity, f);
			
		//GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

}
