package openblocks.client.renderer.entity;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.client.model.ModelCartographer;
import openblocks.client.renderer.entity.EntitySelectionHandler.ISelectionRenderer;
import openblocks.common.entity.EntityCartographer;
import openblocks.common.entity.EntityCartographer.MapJobs;
import openmods.utils.BlockUtils;
import openmods.utils.render.RenderUtils;

import org.lwjgl.opengl.GL11;

public class EntityCartographerRenderer extends Render {

	private final static ResourceLocation texture = new ResourceLocation("openblocks:textures/models/cartographer.png");

	private static ModelCartographer model = new ModelCartographer();

	public static class Selection implements ISelectionRenderer<EntityCartographer> {

		private static final double Z_FIGHTER = 0.0001;

		private static final double CONE_START = 0.075;
		private static final double CONE_END = 0.3;
		private static final double BASE_SIZE = 0.125;
		private static final double MAP_SIZE = 0.08;

		private static final double INTERSECTION_DIST = 2.5 / 16.0;
		private static final double INTERSECTION_SIZE = BASE_SIZE * (INTERSECTION_DIST - CONE_START) / (CONE_END - CONE_START);

		private static final Random RANDOM = new Random();

		private Integer coneDisplay;
		private DynamicTexture mapTextureData;
		private ResourceLocation mapTextureLocation;

		@Override
		public void render(EntityCartographer e, EntityPlayer player, RenderGlobal context, float partialTickTime) {
			if (RANDOM.nextFloat() < 0.1f) return;
			GL11.glPushMatrix();

			RenderUtils.translateToPlayer(e, partialTickTime);

			ForgeDirection side = BlockUtils.get2dOrientation(player).getOpposite();

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
			compileCone();

			GL11.glColor4f(1, 1, 1, 1);
			
			final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
			
			if (e.isMapping.getValue()) {
				GL11.glTranslated(+BASE_SIZE, +BASE_SIZE, -CONE_END);
				bindMapTexture(textureManager);
				renderProgressMap(e.jobs);
			} else {
				textureManager.bindTexture(texture);
				drawBase();
				GL11.glTranslated(+BASE_SIZE, +BASE_SIZE, -CONE_END - Z_FIGHTER);
				renderText(e, context);
			}
			GL11.glPopMatrix();
		}

		private static void drawBase() {
			GL11.glDisable(GL11.GL_CULL_FACE);

			GL11.glBegin(GL11.GL_QUADS);
			GL11.glColor4d(1, 1, 1, 1);
			GL11.glTexCoord2d(0.0, 1.0);
			GL11.glVertex3d(-BASE_SIZE, -BASE_SIZE, -CONE_END);
			GL11.glTexCoord2d(0.5, 1.0);
			GL11.glVertex3d(+BASE_SIZE, -BASE_SIZE, -CONE_END);
			GL11.glTexCoord2d(0.5, 0.5);
			GL11.glVertex3d(+BASE_SIZE, +BASE_SIZE, -CONE_END);
			GL11.glTexCoord2d(0.0, 0.5);
			GL11.glVertex3d(-BASE_SIZE, +BASE_SIZE, -CONE_END);
			GL11.glEnd();

			GL11.glEnable(GL11.GL_CULL_FACE);
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

		@Override
		protected void finalize() throws Throwable {
			if (coneDisplay != null) GL11.glDeleteLists(coneDisplay, 1);
		}

		private void compileCone() {
			if (coneDisplay == null) {
				coneDisplay = GL11.glGenLists(1);
				GL11.glNewList(coneDisplay, GL11.GL_COMPILE);
				renderCone();
				GL11.glEndList();
			}

			GL11.glCallList(coneDisplay);
		}

		private static void renderCone() {
			GL11.glDisable(GL11.GL_TEXTURE_2D);

			GL11.glBegin(GL11.GL_QUADS);
			GL11.glColor4d(1, 1, 1, 1);
			GL11.glVertex3d(-INTERSECTION_SIZE, -INTERSECTION_SIZE, -INTERSECTION_DIST - Z_FIGHTER);
			GL11.glVertex3d(-INTERSECTION_SIZE, +INTERSECTION_SIZE, -INTERSECTION_DIST - Z_FIGHTER);
			GL11.glVertex3d(+INTERSECTION_SIZE, +INTERSECTION_SIZE, -INTERSECTION_DIST - Z_FIGHTER);
			GL11.glVertex3d(+INTERSECTION_SIZE, -INTERSECTION_SIZE, -INTERSECTION_DIST - Z_FIGHTER);
			GL11.glEnd();

			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_CULL_FACE);

			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glColor4d(0, 1, 1, 0.125);
			GL11.glVertex3d(0, 0, -CONE_START);
			GL11.glVertex3d(-BASE_SIZE, -BASE_SIZE, -CONE_END);
			GL11.glVertex3d(+BASE_SIZE, -BASE_SIZE, -CONE_END);
			GL11.glVertex3d(+BASE_SIZE, +BASE_SIZE, -CONE_END);
			GL11.glVertex3d(-BASE_SIZE, +BASE_SIZE, -CONE_END);
			GL11.glVertex3d(-BASE_SIZE, -BASE_SIZE, -CONE_END);
			GL11.glEnd();

			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float partialTickTime, float scale) {
		EntityCartographer cartographer = (EntityCartographer)entity;
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glColor3f(1, 1, 1);
		bindTexture(texture);
		model.renderBase(cartographer.eyeYaw);

		bindTexture(TextureMap.locationItemsTexture);
		model.renderEye(cartographer.eyeYaw, cartographer.eyePitch);
		cartographer.updateEye();

		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}
}
