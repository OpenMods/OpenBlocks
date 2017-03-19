package openblocks.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.common.MinecraftForge;
import openmods.renderer.DisplayListWrapper;
import org.lwjgl.opengl.GL11;

public class ModelCartographer extends ModelBase {
	private static final float SCALE = 1.0f / 16.0f;
	private final ModelRenderer body;
	private final ModelRenderer base;

	private final DisplayListWrapper eyeList = new DisplayListWrapper() {

		@Override
		public void compile() {
			// TODO somehow render eye?
		}
	};

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

		MinecraftForge.EVENT_BUS.register(this);
	}

	public void renderEye(float baseRotation, float eyeRotation) {
		GL11.glPushMatrix();
		GL11.glTranslatef(0, 0.25f, 0);
		GL11.glRotated(Math.toDegrees(baseRotation) + 90, 0, 1, 0);
		GL11.glRotated(Math.toDegrees(eyeRotation), 1, 0, 0);
		eyeList.render();
		GL11.glPopMatrix();
	}

	public void renderBase(float baseRotation) {
		base.rotateAngleY = baseRotation;
		body.render(SCALE);
	}
}
