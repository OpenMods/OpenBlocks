package openblocks.client;

import javax.swing.Icon;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.MathHelper;
import openmods.Log;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Preconditions;

public class Icons {
	public final static int ICON_TYPE_BLOCK = 0;
	public final static int ICON_TYPE_ITEM = 1;

	private static void bindIconSheet(TextureManager tex, int type) {
		switch (type) {
			case ICON_TYPE_BLOCK:
				tex.bindTexture(TextureMap.locationBlocksTexture);
				break;
			case ICON_TYPE_ITEM:
				tex.bindTexture(TextureMap.locationItemsTexture);
				break;
			default:
				Log.warn("Unknown icon sheet: %d", type);
				break;
		}
	}

	public static void renderQuad(Tessellator tes, double scale, Icon icon) {
		tes.addVertexWithUV(scale, scale, 0, icon.getMinU(), icon.getMinV());
		tes.addVertexWithUV(scale, -scale, 0, icon.getMinU(), icon.getMaxV());
		tes.addVertexWithUV(-scale, -scale, 0, icon.getMaxU(), icon.getMaxV());
		tes.addVertexWithUV(-scale, scale, 0, icon.getMaxU(), icon.getMinV());
	}

	public interface IDrawableIcon {
		void draw(TextureManager tex, double alpha, double scale);

		void registerIcons(int type, IconRegister registry);
	}

	public static class SingleIcon implements IDrawableIcon {
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
		public void draw(TextureManager tex, double alpha, double scale) {
			Preconditions.checkNotNull(icon);
			bindIconSheet(tex, type);
			final Tessellator tes = Tessellator.instance;
			tes.startDrawingQuads();
			tes.setTranslation(0, 0, 0);
			tes.setColorRGBA_I(color, MathHelper.floor_double(255 * alpha));
			renderQuad(tes, scale, icon);
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
		public void registerIcons(int type, IconRegister registry) {
			front.registerIcons(type, registry);
			back.registerIcons(type, registry);
		}
	}

	public static class DisplayListWrapper implements IDrawableIcon {

		private final IDrawableIcon wrappedIcon;
		private Integer displayList;

		public DisplayListWrapper(IDrawableIcon wrappedIcon) {
			this.wrappedIcon = wrappedIcon;
		}

		@Override
		public void draw(TextureManager tex, double alpha, double scale) {
			if (displayList == null) {
				displayList = GL11.glGenLists(1);
				GL11.glNewList(displayList, GL11.GL_COMPILE);
				wrappedIcon.draw(tex, alpha, scale);
				GL11.glEndList();
			}

			GL11.glCallList(displayList);
		}

		@Override
		public void registerIcons(int type, IconRegister registry) {
			wrappedIcon.registerIcons(type, registry);
		}

		@Override
		protected void finalize() throws Throwable {
			if (displayList != null) GL11.glDeleteLists(displayList, 1);
		}
	}

	public static IDrawableIcon itemIcon(String iconId, int color) {
		return new LoadableSingleIcon(iconId, color, ICON_TYPE_ITEM);
	}

	public static IDrawableIcon itemIcon(String iconId) {
		return itemIcon(iconId, 0xFFFFFF);
	}

	public static IDrawableIcon blockIcon(String iconId, int color) {
		return new LoadableSingleIcon(iconId, color, ICON_TYPE_BLOCK);
	}

	public static IDrawableIcon blockIcon(String iconId) {
		return blockIcon(iconId, 0xFFFFFF);
	}
}
