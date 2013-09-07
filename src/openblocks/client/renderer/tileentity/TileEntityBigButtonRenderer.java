package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;
import openblocks.client.model.ModelBigButton;
import openblocks.common.tileentity.TileEntityBigButton;
import openblocks.utils.BlockUtils;

import org.lwjgl.opengl.GL11;

public class TileEntityBigButtonRenderer  extends TileEntitySpecialRenderer {

	private ModelBigButton model = new ModelBigButton();

	private static final ResourceLocation texture = new ResourceLocation("openblocks", "textures/models/bigbutton.png");

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		GL11.glPushMatrix();
		func_110628_a(texture);
		TileEntityBigButton button = (TileEntityBigButton)tileentity;
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.0F, (float)z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		ForgeDirection surface = button.getRotation();
		GL11.glRotatef(-BlockUtils.getRotationFromDirection(surface), 0, 1, 0);
		model.render(button, f);
		GL11.glPopMatrix();
	}
}