package openblocks.client.renderer.entity;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import openblocks.OpenBlocks;
import openblocks.client.model.ModelCartographer;
import openblocks.client.renderer.entity.EntitySelectionHandler.ISelectionRenderer;
import openblocks.common.entity.EntityCartographer;
import openblocks.common.entity.EntityCartographer.MapJobs;
import openmods.renderer.DisplayListWrapper;
import openmods.utils.render.RenderUtils;
import org.lwjgl.opengl.GL11;

public class EntityCartographerRenderer extends EntityRenderer<EntityCartographer> {

	private static final double Z_FIGHTER = 0.0001;

	private static final double CONE_START = 0.075;
	private static final double CONE_END = 0.3;
	private static final double BASE_SIZE = 0.125;
	private static final double MAP_SIZE = 0.08;

	private static final double INTERSECTION_DIST = 2.5 / 16.0;
	private static final double INTERSECTION_SIZE = BASE_SIZE * (INTERSECTION_DIST - CONE_START) / (CONE_END - CONE_START);

	private final static ResourceLocation TEXTURE = OpenBlocks.location("textures/models/cartographer.png");

	private static final ModelCartographer MODEL = new ModelCartographer();

	private static final DisplayListWrapper CONE_DISPLAY = new DisplayListWrapper() {
		@Override
		public void compile() {
			GlStateManager.disableLighting();
			GlStateManager.disableTexture2D();

			GL11.glBegin(GL11.GL_QUADS);
			GlStateManager.color(1, 1, 1, 1);
			GL11.glVertex3d(-INTERSECTION_SIZE, -INTERSECTION_SIZE, -INTERSECTION_DIST - Z_FIGHTER);
			GL11.glVertex3d(-INTERSECTION_SIZE, +INTERSECTION_SIZE, -INTERSECTION_DIST - Z_FIGHTER);
			GL11.glVertex3d(+INTERSECTION_SIZE, +INTERSECTION_SIZE, -INTERSECTION_DIST - Z_FIGHTER);
			GL11.glVertex3d(+INTERSECTION_SIZE, -INTERSECTION_SIZE, -INTERSECTION_DIST - Z_FIGHTER);
			GL11.glEnd();

			GlStateManager.enableBlend();
			GlStateManager.disableCull();

			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GlStateManager.color(0f, 1f, 1f, 0.125f);
			GL11.glVertex3d(0, 0, -CONE_START);
			GL11.glVertex3d(-BASE_SIZE, -BASE_SIZE, -CONE_END);
			GL11.glVertex3d(+BASE_SIZE, -BASE_SIZE, -CONE_END);
			GL11.glVertex3d(+BASE_SIZE, +BASE_SIZE, -CONE_END);
			GL11.glVertex3d(-BASE_SIZE, +BASE_SIZE, -CONE_END);
			GL11.glVertex3d(-BASE_SIZE, -BASE_SIZE, -CONE_END);
			GL11.glEnd();

			GlStateManager.enableCull();
			GlStateManager.disableBlend();
			GlStateManager.enableTexture2D();
		}
	};

	public static class Selection implements ISelectionRenderer<EntityCartographer> {

		private static final Random RANDOM = new Random();

		private DynamicTexture mapTextureData;
		private ResourceLocation mapTextureLocation;

		@Override
		public void render(EntityCartographer e, PlayerEntity player, RenderGlobal context, float partialTickTime) {
			if (RANDOM.nextFloat() < 0.1f) return;
			GL11.glPushMatrix();

			RenderUtils.translateToPlayer(e, partialTickTime);

			Direction side = player.getHorizontalFacing().getOpposite();

			switch (side) {
				case EAST:
					GL11.glRotated(-90, 0, 1, 0);
					break;
				case WEST:
					GL11.glRotated(90, 0, 1, 0);
					break;
				case NORTH:
					GL11.glRotated(0, 0, 1, 0);
					break;
				case SOUTH:
					GL11.glRotated(180, 0, 1, 0);
					break;
				default:
					break;
			}

			GL11.glTranslated(0, -0.03, 0);
			CONE_DISPLAY.render();

			GlStateManager.color(1, 1, 1, 1);

			final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

			if (e.isMapping.get()) {
				GL11.glTranslated(+BASE_SIZE, +BASE_SIZE, -CONE_END);
				bindMapTexture(textureManager);
				renderProgressMap(e.jobs);
			} else {
				textureManager.bindTexture(TEXTURE);
				drawBase();
				GL11.glTranslated(+BASE_SIZE, +BASE_SIZE, -CONE_END - Z_FIGHTER);
				renderText(e, context);
			}
			GL11.glPopMatrix();
		}

		private static void drawBase() {
			GlStateManager.disableCull();

			GL11.glBegin(GL11.GL_QUADS);
			GlStateManager.color(1, 1, 1, 1);
			GL11.glTexCoord2d(0.0, 1.0);
			GL11.glVertex3d(-BASE_SIZE, -BASE_SIZE, -CONE_END);
			GL11.glTexCoord2d(0.5, 1.0);
			GL11.glVertex3d(+BASE_SIZE, -BASE_SIZE, -CONE_END);
			GL11.glTexCoord2d(0.5, 0.5);
			GL11.glVertex3d(+BASE_SIZE, +BASE_SIZE, -CONE_END);
			GL11.glTexCoord2d(0.0, 0.5);
			GL11.glVertex3d(-BASE_SIZE, +BASE_SIZE, -CONE_END);
			GL11.glEnd();

			GlStateManager.enableCull();
		}

		private static void renderText(EntityCartographer e, RenderGlobal context) {
			GL11.glScaled(2 * BASE_SIZE / 16.0, 2 * BASE_SIZE / 16.0, 1);
			FontRenderer fonts = Minecraft.getMinecraft().fontRenderer;
			String coords = String.format("%d,%d", e.getNewMapCenterX(), e.getNewMapCenterZ());
			int len = fonts.getStringWidth(coords);
			double scaleV = 4.0 / 8.0;
			int margin = 2;
			double available = 16 - 2 * margin;
			double scaleH = available / len;
			double scale = Math.min(scaleV, scaleH);

			GL11.glTranslated(-margin, -2, 0);
			GL11.glScaled(-scale, -scale, 1);
			fonts.drawString(coords, 0, 0, 0);
		}

		private void renderProgressMap(MapJobs segments) {
			final int[] mapColors = mapTextureData.getTextureData();

			int bit = 0;
			final int mapSize = segments.size();

			for (int row = 0; row < mapSize; row++)
				for (int column = 0; column < mapSize; column++)
					mapColors[row * 64 + column] = segments.test(bit++)? 0xFF00FF19 : 0xFF00600B;

			mapTextureData.updateDynamicTexture();

			float maxTex = mapSize / 64.0f;
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3d(-BASE_SIZE + MAP_SIZE, -BASE_SIZE + MAP_SIZE, 0);

			GL11.glTexCoord2f(0, maxTex);
			GL11.glVertex3d(-BASE_SIZE + MAP_SIZE, -BASE_SIZE - MAP_SIZE, 0);

			GL11.glTexCoord2f(maxTex, maxTex);
			GL11.glVertex3d(-BASE_SIZE - MAP_SIZE, -BASE_SIZE - MAP_SIZE, 0);

			GL11.glTexCoord2f(maxTex, 0);
			GL11.glVertex3d(-BASE_SIZE - MAP_SIZE, -BASE_SIZE + MAP_SIZE, 0);
			GL11.glEnd();
		}

		private void bindMapTexture(TextureManager manager) {
			if (mapTextureLocation == null) {
				mapTextureData = new DynamicTexture(64, 64);
				mapTextureLocation = manager.getDynamicTextureLocation("selection_", mapTextureData);
			}

			manager.bindTexture(mapTextureLocation);
		}
	}

	public EntityCartographerRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityCartographer cartographer, double x, double y, double z, float scale, float partialTickTime) {
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GlStateManager.color(1, 1, 1);
		bindTexture(TEXTURE);
		MODEL.renderBase(cartographer.eyeYaw);

		bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
		MODEL.renderEye(cartographer.eyeYaw, cartographer.eyePitch);
		cartographer.updateEye();

		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityCartographer entity) {
		return TEXTURE;
	}

	public static void registerListener() {
		MinecraftForge.EVENT_BUS.register(MODEL);
	}
}
