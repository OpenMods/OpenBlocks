package openblocks.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;

import org.lwjgl.opengl.GL11;

public class ModelCartographer extends ModelBase {
	private static final float SCALE = 1.0f / 16.0f;
	private final ModelRenderer body;
	private final ModelRenderer base;

	private Integer eyeList;

	public ModelCartographer() {
		textureWidth = 32;
		textureHeight = 32;

		body = new ModelRenderer(this, 0, 0);
		body.addBox(-2.5f, -1.5f, -2.5f, 5, 2, 5);

		base = new ModelRenderer(this);
		body.addChild(base);

		base.setTextureOffset(0, 7);
		base.addBox(-1.5f, 0.5f, -1.5f, 3, 1, 3);

		base.setTextureOffset(0, 11);
		base.addBox(-0.5f, 0.5f, -2.5f, 1, 4, 1);

		base.setTextureOffset(4, 11);
		base.addBox(-0.5f, 0.5f, 1.5f, 1, 4, 1);
	}

	public void renderEye(float baseRotation, float eyeRotation) {

		if (eyeList == null) {
			eyeList = GL11.glGenLists(1);
			GL11.glNewList(eyeList, GL11.GL_COMPILE);
			GL11.glColor3d(1, 1, 1);
			GL11.glScalef(SCALE * 3, SCALE * 3, SCALE);

			Tessellator tes = new Tessellator();
			tes.xOffset = -0.5;
			tes.zOffset = 0.25f;
			tes.yOffset = -0.5;

			final Icon icon = Item.eyeOfEnder.getIconFromDamage(0);
			ItemRenderer.renderItemIn2D(
					tes,
					icon.getInterpolatedU(15), icon.getInterpolatedV(2),
					icon.getInterpolatedU(2), icon.getInterpolatedV(15),
					13, 13,
					0.5f
					);
			GL11.glEndList();
		}

		GL11.glPushMatrix();
		GL11.glTranslatef(0, 0.25f, 0);
		GL11.glRotated(Math.toDegrees(baseRotation) + 90, 0, 1, 0);
		GL11.glRotated(Math.toDegrees(eyeRotation), 1, 0, 0);
		GL11.glCallList(eyeList);
		GL11.glPopMatrix();
	}

	@Override
	protected void finalize() {
		if (eyeList != null) GL11.glDeleteLists(eyeList, 1);
	}

	public void renderBase(float baseRotation) {
		base.rotateAngleY = baseRotation;
		body.render(SCALE);
	}
}
