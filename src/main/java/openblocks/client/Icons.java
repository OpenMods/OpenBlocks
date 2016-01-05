package openblocks.client;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.MathHelper;
import openmods.utils.TextureUtils;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Preconditions;

public class Icons {

	public static void renderQuad(Tessellator tes, double scale, IIcon icon) {
		tes.addVertexWithUV(scale, scale, 0, icon.getMinU(), icon.getMinV());
		tes.addVertexWithUV(scale, -scale, 0, icon.getMinU(), icon.getMaxV());
		tes.addVertexWithUV(-scale, -scale, 0, icon.getMaxU(), icon.getMaxV());
		tes.addVertexWithUV(-scale, scale, 0, icon.getMaxU(), icon.getMinV());
	}

	public interface IDrawableIcon {
		void draw(TextureManager tex, double alpha, double scale);

		void registerIcons(int type, IIconRegister registry);
	}

	public static class SingleIcon implements IDrawableIcon {
		protected IIcon icon;
		public final int color;
		public final int type;

		protected SingleIcon(int color, int type) {
			this.color = color;
			this.type = type;
		}

		public SingleIcon(IIcon icon, int color, int type) {
			this(color, type);
			this.icon = icon;
		}

		@Override
		public void draw(TextureManager tex, double alpha, double scale) {
			Preconditions.checkNotNull(icon);
			TextureUtils.bindIconSheet(tex, type);
			final Tessellator tes = Tessellator.instance;
			tes.startDrawingQuads();
			tes.setTranslation(0, 0, 0);
			tes.setColorRGBA_I(color, MathHelper.floor_double(255 * alpha));
			renderQuad(tes, scale, icon);
			tes.draw();
		}

		@Override
		public void registerIcons(int type, IIconRegister registry) {}
	}

	private static class LoadableSingleIcon extends SingleIcon {
		private final String iconId;

		private LoadableSingleIcon(String iconId, int color, int type) {
			super(color, type);
			this.iconId = iconId;
		}

		@Override
		public void registerIcons(int type, IIconRegister registry) {
			if (this.type == type) icon = registry.registerIcon(iconId);
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
		public void draw(TextureManager tex, double alpha, double scale) {
			back.draw(tex, alpha, scale * scaleRatio);
			GL11.glTranslated(0, 0, -distance);
			front.draw(tex, alpha, scale);
		}

		@Override
		public void registerIcons(int type, IIconRegister registry) {
			front.registerIcons(type, registry);
			back.registerIcons(type, registry);
		}
	}

	public static IDrawableIcon itemIcon(String iconId, int color) {
		return new LoadableSingleIcon(iconId, color, TextureUtils.TEXTURE_MAP_ITEMS);
	}

	public static IDrawableIcon itemIcon(String iconId) {
		return itemIcon(iconId, 0xFFFFFF);
	}

	public static IDrawableIcon blockIcon(String iconId, int color) {
		return new LoadableSingleIcon(iconId, color, TextureUtils.TEXTURE_MAP_BLOCKS);
	}

	public static IDrawableIcon blockIcon(String iconId) {
		return blockIcon(iconId, 0xFFFFFF);
	}
}
