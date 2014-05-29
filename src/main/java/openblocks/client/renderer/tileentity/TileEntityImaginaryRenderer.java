package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import openblocks.Config;
import openblocks.OpenBlocks.Blocks;
import openblocks.common.tileentity.*;
import openblocks.common.tileentity.TileEntityImaginary.ICollisionData;
import openblocks.common.tileentity.TileEntityImaginary.PanelData;
import openblocks.common.tileentity.TileEntityImaginary.Property;
import openblocks.common.tileentity.TileEntityImaginary.StairsData;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TileEntityImaginaryRenderer extends TileEntitySpecialRenderer {

	private abstract static class ElementDisplay {
		public Integer pencilDisplayList;
		public Integer crayonDisplayList;

		@Override
		public void finalize() {
			clear();
		}

		private void clear() {
			if (crayonDisplayList != null) {
				GL11.glDeleteLists(crayonDisplayList, 1);
				crayonDisplayList = null;
			}

			if (pencilDisplayList != null) {
				GL11.glDeleteLists(pencilDisplayList, 1);
				pencilDisplayList = null;
			}
		}

		public Integer getDisplayList(boolean isPencil) {
			return isPencil? getPencilDisplayList() : getCrayonDisplayList();
		}

		private Integer getCrayonDisplayList() {
			if (crayonDisplayList == null) crayonDisplayList = compileList(getCrayonTexture());

			return crayonDisplayList;
		}

		private Integer getPencilDisplayList() {
			if (pencilDisplayList == null) pencilDisplayList = compileList(getPencilTexture());

			return pencilDisplayList;
		}

		private int compileList(IIcon icon) {
			int displayList = GL11.glGenLists(1);

			GL11.glNewList(displayList, GL11.GL_COMPILE);

			GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			Tessellator tes = new Tessellator();
			tes.startDrawingQuads();
			addQuads(tes, icon);
			tes.draw();

			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);

			GL11.glEndList();

			return displayList;
		}

		protected abstract void addQuads(Tessellator tes, IIcon icon);

		protected abstract IIcon getPencilTexture();

		protected abstract IIcon getCrayonTexture();
	}

	private final static ElementDisplay blockDisplay = new ElementDisplay() {
		@Override
		public void addQuads(Tessellator tes, IIcon icon) {
			final double delta0 = 0.001;
			final double delta1 = 1 - 0.001;

			tes.addVertexWithUV(0, 0, delta0, icon.getMinU(), icon.getMinV());
			tes.addVertexWithUV(0, 1, delta0, icon.getMinU(), icon.getMaxV());
			tes.addVertexWithUV(1, 1, delta0, icon.getMaxU(), icon.getMaxV());
			tes.addVertexWithUV(1, 0, delta0, icon.getMaxU(), icon.getMinV());

			tes.addVertexWithUV(0, 0, delta1, icon.getMinU(), icon.getMinV());
			tes.addVertexWithUV(1, 0, delta1, icon.getMinU(), icon.getMaxV());
			tes.addVertexWithUV(1, 1, delta1, icon.getMaxU(), icon.getMaxV());
			tes.addVertexWithUV(0, 1, delta1, icon.getMaxU(), icon.getMinV());

			tes.addVertexWithUV(delta1, 0, 0, icon.getMinU(), icon.getMinV());
			tes.addVertexWithUV(delta1, 1, 0, icon.getMinU(), icon.getMaxV());
			tes.addVertexWithUV(delta1, 1, 1, icon.getMaxU(), icon.getMaxV());
			tes.addVertexWithUV(delta1, 0, 1, icon.getMaxU(), icon.getMinV());

			tes.addVertexWithUV(delta0, 0, 0, icon.getMinU(), icon.getMinV());
			tes.addVertexWithUV(delta0, 0, 1, icon.getMinU(), icon.getMaxV());
			tes.addVertexWithUV(delta0, 1, 1, icon.getMaxU(), icon.getMaxV());
			tes.addVertexWithUV(delta0, 1, 0, icon.getMaxU(), icon.getMinV());

			tes.addVertexWithUV(0, delta1, 0, icon.getMinU(), icon.getMinV());
			tes.addVertexWithUV(0, delta1, 1, icon.getMinU(), icon.getMaxV());
			tes.addVertexWithUV(1, delta1, 1, icon.getMaxU(), icon.getMaxV());
			tes.addVertexWithUV(1, delta1, 0, icon.getMaxU(), icon.getMinV());

			tes.addVertexWithUV(0, delta0, 0, icon.getMinU(), icon.getMinV());
			tes.addVertexWithUV(1, delta0, 0, icon.getMinU(), icon.getMaxV());
			tes.addVertexWithUV(1, delta0, 1, icon.getMaxU(), icon.getMaxV());
			tes.addVertexWithUV(0, delta0, 1, icon.getMaxU(), icon.getMinV());
		}

		@Override
		protected IIcon getPencilTexture() {
			return Blocks.imaginary.texturePencilBlock;
		}

		@Override
		protected IIcon getCrayonTexture() {
			return Blocks.imaginary.textureCrayonBlock;
		}
	};

	private final static ElementDisplay panelDisplay = new ElementDisplay() {
		@Override
		public void addQuads(Tessellator tes, IIcon icon) {
			tes.addVertexWithUV(0, 0, 0, icon.getMinU(), icon.getMinV());
			tes.addVertexWithUV(0, 0, 1, icon.getMinU(), icon.getMaxV());
			tes.addVertexWithUV(1, 0, 1, icon.getMaxU(), icon.getMaxV());
			tes.addVertexWithUV(1, 0, 0, icon.getMaxU(), icon.getMinV());
		}

		@Override
		protected IIcon getPencilTexture() {
			return Blocks.imaginary.texturePencilPanel;
		}

		@Override
		protected IIcon getCrayonTexture() {
			return Blocks.imaginary.textureCrayonPanel;
		}
	};

	private final static ElementDisplay halfPanelDisplay = new ElementDisplay() {
		@Override
		public void addQuads(Tessellator tes, IIcon icon) {
			tes.addVertexWithUV(-0.5, 0, -0.5, icon.getMinU(), icon.getMaxV());
			tes.addVertexWithUV(-0.5, 0, +0.5, icon.getMinU(), icon.getMinV());
			tes.addVertexWithUV(+0.5, 0, +0.5, icon.getMaxU(), icon.getMinV());
			tes.addVertexWithUV(+0.5, 0, -0.5, icon.getMaxU(), icon.getMaxV());
		}

		@Override
		protected IIcon getPencilTexture() {
			return Blocks.imaginary.texturePencilHalfPanel;
		}

		@Override
		protected IIcon getCrayonTexture() {
			return Blocks.imaginary.textureCrayonHalfPanel;
		}
	};

	public TileEntityImaginaryRenderer() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float partialTicks) {
		final TileEntityImaginary te = (TileEntityImaginary)tileentity;

		boolean isVisible = te.is(Property.VISIBLE);

		if (isVisible && te.visibility < 1) te.visibility = Math.min(te.visibility
				+ Config.imaginaryFadingSpeed, 1);
		else if (!isVisible && te.visibility > 0) te.visibility = Math.max(te.visibility
				- Config.imaginaryFadingSpeed, 0);

		if (te.visibility <= 0) return;

		bindTexture(TextureMap.locationBlocksTexture);

		if (!te.isPencil()) {
			byte red = (byte)(te.color >> 16);
			byte green = (byte)(te.color >> 8);
			byte blue = (byte)(te.color >> 0);
			GL11.glColor4ub(red, green, blue, (byte)(255 * te.visibility));
		} else {
			GL11.glColor4ub((byte)255, (byte)255, (byte)255, (byte)(255 * te.visibility));
		}

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);

		final ICollisionData data = te.collisionData;

		if (data instanceof PanelData) {
			PanelData pd = (PanelData)data;

			GL11.glTranslated(0, pd.height, 0);
			int displayList = panelDisplay.getDisplayList(te.isPencil());
			GL11.glCallList(displayList);
		} else if (data instanceof StairsData) {
			StairsData sd = (StairsData)data;

			GL11.glTranslated(0.5, 0, 0.5);

			switch (sd.orientation) {
				case NORTH:
					break;
				case EAST:
					GL11.glRotatef(-90, 0, 1, 0);
					break;
				case SOUTH:
					GL11.glRotatef(180, 0, 1, 0);
					break;
				case WEST:
					GL11.glRotatef(90, 0, 1, 0);
					break;
				default:
					break;
			}

			int displayList = halfPanelDisplay.getDisplayList(te.isPencil());
			GL11.glTranslated(0, sd.lowerPanelHeight, 0);
			GL11.glCallList(displayList);

			GL11.glTranslated(0, sd.upperPanelHeight - sd.lowerPanelHeight, -0.5);
			GL11.glCallList(displayList);
		} else {
			int displayList = blockDisplay.getDisplayList(te.isPencil());
			GL11.glCallList(displayList);
		}

		GL11.glPopMatrix();
	}

	/**
	 * @param evt
	 */
	@SubscribeEvent
	public void onTextureReload(TextureStitchEvent.Pre evt) {
		blockDisplay.clear();
		panelDisplay.clear();
		halfPanelDisplay.clear();
	}
}
