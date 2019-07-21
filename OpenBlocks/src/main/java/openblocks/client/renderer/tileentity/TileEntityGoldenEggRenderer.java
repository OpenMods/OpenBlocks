package openblocks.client.renderer.tileentity;

import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import openblocks.common.tileentity.TileEntityGoldenEgg;
import openblocks.common.tileentity.TileEntityGoldenEgg.State;
import org.lwjgl.opengl.GL11;

public class TileEntityGoldenEggRenderer extends TileEntitySpecialRenderer<TileEntityGoldenEgg> {

	private static final float PHANTOM_SCALE = 1.5f;

	private static final Random RANDOM = new Random(432L);

	private static void renderEgg(IBakedModel model, BlockState state) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexBuffer = tessellator.getBuffer();

		vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
		for (Direction enumfacing : Direction.values())
			renderQuads(vertexBuffer, model.getQuads(state, enumfacing, 0L));

		renderQuads(vertexBuffer, model.getQuads(state, null, 0L));
		GL11.glPushMatrix();
		GL11.glTranslated(-0.5, 0, -0.5);
		tessellator.draw();
		GL11.glPopMatrix();
	}

	private static void renderQuads(BufferBuilder vertexBuffer, List<BakedQuad> quads) {
		for (BakedQuad quad : quads)
			vertexBuffer.addVertexData(quad.getVertexData());
	}

	protected static BlockRendererDispatcher blockRenderer;

	@Override
	public void render(TileEntityGoldenEgg egg, double x, double y, double z, float partialTickTime, int destroyProgress, float alpha) {
		if (egg == null) return;

		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y, z + 0.5);

		float rotation = egg.getRotation(partialTickTime);
		float progress = egg.getProgress(partialTickTime);
		float offset = egg.getOffset(partialTickTime);

		GL11.glTranslatef(0, offset, 0);
		GL11.glRotatef(rotation, 0, 1, 0);

		bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

		if (blockRenderer == null) blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
		BlockPos pos = egg.getPos();
		IBlockAccess world = MinecraftForgeClient.getRegionRenderCache(egg.getWorld(), pos);
		BlockState blockState = world.getBlockState(pos);

		IBakedModel model = blockRenderer.getBlockModelShapes().getModelForState(blockState);

		renderEgg(model, blockState);

		final State state = egg.getState();
		if (state.specialEffects) {
			renderPhantom(model, blockState, rotation, progress, partialTickTime);
			renderStar(rotation, progress, Tessellator.getInstance(), partialTickTime);
		}

		GL11.glPopMatrix();
	}

	private static void renderPhantom(IBakedModel model, BlockState state, float rotation, float progress, float partialTicks) {
		RenderHelper.disableStandardItemLighting();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		GlStateManager.color(1f, 1f, 1f, 0.2f + 0.8f * progress);
		float scale = PHANTOM_SCALE * (0.2f + progress * 0.8f);

		GL11.glTranslatef(0, -0.1f * progress, 0);
		GL11.glScalef(scale, scale, scale);
		renderEgg(model, state);

		GlStateManager.disableBlend();
		RenderHelper.enableStandardItemLighting();
	}

	private static void renderStar(float rotation, float progress, Tessellator tes, float partialTicks) {
		/* Shift down a bit */
		GL11.glTranslatef(0f, 0.5f, 0);
		/* Rotate opposite direction at 20% speed */
		GL11.glRotatef(rotation * -0.2f % 360, 0.5f, 1, 0.5f);

		/* Configuration tweaks */
		float BEAM_START_DISTANCE = 2F;
		float BEAM_END_DISTANCE = 10f;
		float MAX_OPACITY = 192f;

		RenderHelper.disableStandardItemLighting();
		float f2 = 0.0F;

		if (progress > 0.8F) {
			f2 = (progress - 0.8F) / 0.2F;
		}

		GlStateManager.disableTexture2D();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GlStateManager.disableAlpha();
		GlStateManager.enableCull();
		GlStateManager.depthMask(false);

		RANDOM.setSeed(432L);

		BufferBuilder wr = tes.getBuffer();

		final int alpha = (int)(MAX_OPACITY * (1.0F - f2));

		for (int i = 0; i < (progress + progress * progress) / 2.0F * 60.0F; ++i) {
			GL11.glRotatef(RANDOM.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(RANDOM.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(RANDOM.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(RANDOM.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(RANDOM.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(RANDOM.nextFloat() * 360.0F + progress * 90.0F, 0.0F, 0.0F, 1.0F);

			final float f3 = RANDOM.nextFloat() * BEAM_END_DISTANCE + 5.0F + f2 * 10.0F;
			final float f4 = RANDOM.nextFloat() * BEAM_START_DISTANCE + 1.0F + f2 * 2.0F;

			wr.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);

			wr.pos(0.0, 0.0, 0.0).color(255, 255, 255, alpha).endVertex();

			wr.pos(0.0D, 0.0D, 0.0D).color(255, 255, 255, alpha).endVertex();
			wr.pos(-0.866D * f4, f3, (-0.5F * f4)).color(255, 0, 255, 0).endVertex();
			wr.pos(0.866D * f4, f3, (-0.5F * f4)).color(255, 0, 255, 0).endVertex();
			wr.pos(0.0D, f3, (1.0F * f4)).color(255, 0, 255, 0).endVertex();
			wr.pos(-0.866D * f4, f3, (-0.5F * f4)).color(255, 0, 255, 0).endVertex();
			tes.draw();
		}

		GlStateManager.depthMask(true);
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableTexture2D();
		GlStateManager.enableAlpha();

		RenderHelper.enableStandardItemLighting();
	}
}
