package openblocks.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import openblocks.OpenBlocks;
import openblocks.common.entity.EntityMagnet;
import org.lwjgl.opengl.GL11;

public class EntityMagnetRenderer extends EntityRenderer<EntityMagnet> {

	private final static ResourceLocation texture = OpenBlocks.location("textures/models/magnet.png");

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

	public EntityMagnetRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityMagnet magnet, double x, double y, double z, float yaw, float partialTick) {
		bindEntityTexture(magnet);

		GlStateManager.color(1, 1, 1);
		GL11.glPushMatrix();
		GL11.glRotatef(yaw, 0, 1, 0);
		GL11.glTranslated(x, y + magnet.height - 0.4f, z);
		model.render(magnet, 0, 0, 0, 0, 0, 1.0f / 8.0f);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMagnet entity) {
		return texture;
	}

}
