package openblocks.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import openblocks.common.entity.EntityMagnet;

import org.lwjgl.opengl.GL11;

public class EntityMagnetRenderer extends Render {

	private final static ResourceLocation texture = new ResourceLocation("openblocks:textures/models/magnet.png");

	private final ModelBase model = new ModelBase() {
		private final ModelRenderer renderer;

		{
			textureHeight = 32;
			textureWidth = 32;
			renderer = new ModelRenderer(this);
			renderer.mirror = true;

			renderer.setTextureOffset(0, 0);
			renderer.addBox(-3, 0, -3, 6, 1, 6);

			renderer.setTextureOffset(0, 7);
			renderer.addBox(-2, 1, -2, 4, 1, 4);

			renderer.setTextureOffset(0, 12);
			renderer.addBox(-1, 2, -1, 2, 1, 2);
		}

		@Override
		public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float scale) {
			renderer.render(scale);
		}
	};

	public EntityMagnetRenderer() {}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTick) {
		EntityMagnet magnet = (EntityMagnet)entity;
		bindEntityTexture(magnet);

		GL11.glColor3f(1, 1, 1);
		GL11.glPushMatrix();
		GL11.glRotatef(yaw, 0, 1, 0);
		GL11.glTranslated(x, y, z);
		model.render(magnet, 0, 0, 0, 0, 0, 1.0f / 8.0f);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}

}
