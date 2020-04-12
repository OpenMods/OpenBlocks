package openblocks.client;

import com.google.common.base.Preconditions;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import openmods.colors.RGB;
import org.lwjgl.opengl.GL11;

public class Icons {

	private static void addVertexWithUV(BufferBuilder wr, double x, double y, double u, double v) {
		wr.pos(x, y, 0).tex(u, v).endVertex();
	}

	public interface IDrawableIcon {
		void draw(double alpha, double scale);

		void registerIcons(AtlasTexture registry);
	}

	public static class SingleIcon implements IDrawableIcon {
		protected TextureAtlasSprite icon;
		public final float r;
		public final float g;
		public final float b;

		protected SingleIcon(int color) {
			RGB rgb = new RGB(color);
			this.r = rgb.getR();
			this.g = rgb.getG();
			this.b = rgb.getB();
		}

		public SingleIcon(TextureAtlasSprite icon, int color) {
			this(color);
			this.icon = icon;
		}

		@Override
		public void draw(double alpha, double scale) {
			Preconditions.checkNotNull(icon);
			GlStateManager.color(r, g, b, MathHelper.floor(255 * alpha));
			final Tessellator tes = Tessellator.getInstance();
			final BufferBuilder wr = tes.getBuffer();
			wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			addVertexWithUV(wr, scale, scale, icon.getMinU(), icon.getMinV());
			addVertexWithUV(wr, scale, -scale, icon.getMinU(), icon.getMaxV());
			addVertexWithUV(wr, -scale, -scale, icon.getMaxU(), icon.getMaxV());
			addVertexWithUV(wr, -scale, scale, icon.getMaxU(), icon.getMinV());
			tes.draw();
		}

		@Override
		public void registerIcons(AtlasTexture registry) {}
	}

	private static class LoadableSingleIcon extends SingleIcon {
		private final ResourceLocation iconId;

		private LoadableSingleIcon(ResourceLocation iconId, int color) {
			super(color);
			this.iconId = iconId;
		}

		@Override
		public void registerIcons(AtlasTexture registry) {
			icon = registry.registerSprite(iconId);
		}
	}

	public static class ComposedIcon implements IDrawableIcon {
		private final IDrawableIcon front;
		private final IDrawableIcon back;
		private final double scaleRatio;
		private final double distance;

		public ComposedIcon(IDrawableIcon front, IDrawableIcon back, double scaleRatio, double distance) {
			this.front = front;
			this.back = back;
			this.scaleRatio = scaleRatio;
			this.distance = distance;
		}

		@Override
		public void draw(double alpha, double scale) {
			back.draw(alpha, scale * scaleRatio);
			GL11.glTranslated(0, 0, -distance);
			front.draw(alpha, scale);
		}

		@Override
		public void registerIcons(AtlasTexture registry) {
			front.registerIcons(registry);
			back.registerIcons(registry);
		}
	}

	public static IDrawableIcon createIcon(ResourceLocation iconId, int color) {
		return new LoadableSingleIcon(iconId, color);
	}

	public static IDrawableIcon createIcon(ResourceLocation iconId) {
		return createIcon(iconId, 0xFFFFFF);
	}

}
