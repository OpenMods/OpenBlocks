package openblocks.client;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import openblocks.Log;

import com.google.common.base.Preconditions;

public class Icons {
	public final static int ICON_TYPE_BLOCK = 0;
	public final static int ICON_TYPE_ITEM = 1;

	private static void bindIconSheet(TextureManager tex, int type) {
		switch (type) {
			case ICON_TYPE_BLOCK:
				tex.func_110577_a(TextureMap.field_110575_b);
				break;
			case ICON_TYPE_ITEM:
				tex.func_110577_a(TextureMap.field_110576_c);
				break;
			default:
				Log.warn("Unknown icon sheet: %d", type);
				break;
		}
	}

	public static void renderFacingQuad(Tessellator tes, double scale, Icon icon) {
		double arX = ActiveRenderInfo.rotationX * scale;
		double arZ = ActiveRenderInfo.rotationZ * scale;
		double arYZ = ActiveRenderInfo.rotationYZ * scale;
		double arXY = ActiveRenderInfo.rotationXY * scale;
		double arXZ = ActiveRenderInfo.rotationXZ * scale;

		tes.addVertexWithUV(-arX - arYZ, -arXZ, -arZ - arXY, icon.getMaxU(), icon.getMaxV());
		tes.addVertexWithUV(-arX + arYZ, arXZ, -arZ + arXY, icon.getMaxU(), icon.getMinV());
		tes.addVertexWithUV(arX + arYZ, arXZ, arZ + arXY, icon.getMinU(), icon.getMinV());
		tes.addVertexWithUV(arX - arYZ, -arXZ, arZ - arXY, icon.getMinU(), icon.getMaxV());
	}

	public interface DrawableIcon {
		void draw(TextureManager tex, Tessellator tes, double x, double y, double z, double time, double scale);

		void registerIcons(int type, IconRegister registry);
	}

	public static class SingleIcon implements DrawableIcon {
		protected Icon icon;
		public final int color;
		public final int type;

		protected SingleIcon(int color, int type) {
			this.color = color;
			this.type = type;
		}

		public SingleIcon(Icon icon, int color, int type) {
			this(color, type);
			this.icon = icon;
		}

		@Override
		public void draw(TextureManager tex, Tessellator tes, double x, double y, double z, double time, double scale) {
			Preconditions.checkNotNull(icon);
			bindIconSheet(tex, type);

			tes.startDrawingQuads();
			tes.setColorRGBA_I(color, MathHelper.floor_double(255 * time));

			tes.setTranslation(x, y, z);
			renderFacingQuad(tes, scale, icon);
			tes.draw();
		}

		@Override
		public void registerIcons(int type, IconRegister registry) {}
	}

	private static class LoadableSingleIcon extends SingleIcon {
		private final String iconId;

		private LoadableSingleIcon(String iconId, int color, int type) {
			super(color, type);
			this.iconId = iconId;
		}

		@Override
		public void registerIcons(int type, IconRegister registry) {
			if (this.type == type) icon = registry.registerIcon(iconId);
		}
	}

	private static class ComposedIcon implements DrawableIcon {
		private final DrawableIcon first;
		private final DrawableIcon second;
		private final double scaleRatio;

		public ComposedIcon(DrawableIcon first, DrawableIcon second, double scaleRatio) {
			this.first = first;
			this.second = second;
			this.scaleRatio = scaleRatio;
		}

		@Override
		public void draw(TextureManager tex, Tessellator tes, double x, double y, double z, double time, double scale) {
			first.draw(tex, tes, x, y, z, time, scale);
			second.draw(tex, tes, x, y, z, time, scale * scaleRatio);
		}

		@Override
		public void registerIcons(int type, IconRegister registry) {
			first.registerIcons(type, registry);
			second.registerIcons(type, registry);
		}

	}

	public static DrawableIcon itemIcon(String iconId, int color) {
		return new LoadableSingleIcon(iconId, color, ICON_TYPE_ITEM);
	}

	public static DrawableIcon itemIcon(String iconId) {
		return itemIcon(iconId, 0xFFFFFF);
	}

	public static DrawableIcon blockIcon(String iconId, int color) {
		return new LoadableSingleIcon(iconId, color, ICON_TYPE_BLOCK);
	}

	public static DrawableIcon blockIcon(String iconId) {
		return blockIcon(iconId, 0xFFFFFF);
	}

	public static DrawableIcon framedIcon(DrawableIcon frame, DrawableIcon inner, double ratio) {
		return new Icons.ComposedIcon(frame, inner, ratio);
	}
}
