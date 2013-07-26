package openblocks.client;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import openblocks.common.tileentity.TileEntityGrave;
import openblocks.utils.BlockUtils;

import org.lwjgl.opengl.GL11;

public class TileEntityGraveRenderer extends TileEntitySpecialRenderer {

	private ModelGrave model = new ModelGrave();

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y,
			double z, float f) {

		TileEntityGrave target = (TileEntityGrave) tileentity;

		float sixteenth = 1/16f;
		float fontSize = 2f;
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x +  0.5F , (float) y + 1.0f, (float) z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glPushMatrix();
		GL11.glRotatef(
				 -BlockUtils.getRotationFromDirection(target.getRotation()), 0,
				1, 0);
		this.bindTextureByName("/mods/openblocks/textures/models/grave.png");
		model.render(tileentity, f);
		
        float textScale = sixteenth * fontSize * sixteenth;
		FontRenderer renderer = getFontRenderer();
		if (renderer != null) {
			
			/* So here is what I think, Pop the matrix so we at the top of the block,
			 * Then translate to the bottom of the stone, apply the same rotation as the stone,
			 * then translate up by stone height * percent height. That should land our text
			 * dead on target. - NeverCast
			 * 
			 * The text isn't rendering in the correct place. not sure if you've implemented that
			 * yet or not, but maybe lets ditch block rotation completely? If you die, it doesnt
			 * really matter what way it's facing. You're never meant to actually 'place' the block..
			 * - Mikee
			 */
			GL11.glPopMatrix();
	        GL11.glTranslatef(0F, 15F * sixteenth, 6F * sixteenth); // Rotation point, minor adjustment to lay on top
	        //-0.0743572F, 0F, 0.0371786F);
	        GL11.glRotatef((float) Math.toDegrees(-0.0743572F), 1f, 0f, 0f);
	        GL11.glRotatef((float) Math.toDegrees(0.0371786F), 0f, 0f, 1f);
	        GL11.glTranslatef(-4f * sixteenth, -12f * sixteenth, -sixteenth); // This will now shift the text around the slab
	        GL11.glScalef( textScale, textScale, textScale);
	       // GL11.glNormal3f(0.0F, 0.0F, -1.0F * f2);
	        GL11.glDepthMask(false);
	        int maxChars = 10;
	        float maxOffset = 58;
	        float offPerChar = maxOffset / maxChars;
	        String s = target.getUsername();
	        int offset = (int) (((int)(maxChars - s.length()) / 2 * offPerChar) + (offPerChar * 0.5f));
			renderer.drawString(s, offset, 0, 0);
			GL11.glDepthMask(true);
		}
		GL11.glPopMatrix();


	}

}
