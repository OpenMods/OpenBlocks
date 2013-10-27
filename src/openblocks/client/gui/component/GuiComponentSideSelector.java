package openblocks.client.gui.component;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import openblocks.sync.SyncableFlags;
import openblocks.utils.SidePicker;
import openblocks.utils.SidePicker.HitCoord;
import openblocks.utils.SidePicker.Side;
import openblocks.utils.Trackball.TrackballWrapper;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class GuiComponentSideSelector extends BaseComponent {

	RenderBlocks blockRender = new RenderBlocks();

	private TrackballWrapper trackball = new TrackballWrapper(1, 40);

	public double scale;

	private ISideSelectionCallback callback;

	private ForgeDirection lastSideHovered;
	private int movedTicks = 0;
	public SyncableFlags enabledDirections;
	private Block block;
	private boolean isInitialized;
	private int meta = 0;
	private TileEntity te;
	private boolean highlightSelectedSides = false;

	public GuiComponentSideSelector(int x, int y, double scale, TileEntity te, int meta, Block block, SyncableFlags directions, boolean highlightSelectedSides, ISideSelectionCallback iSideSelectionCallback) {
		super(x, y);
		this.scale = scale;
		this.callback = iSideSelectionCallback;
		this.enabledDirections = directions;
		this.block = block;
		this.meta = meta;
		this.te = te;
		this.highlightSelectedSides = highlightSelectedSides;
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		if (isInitialized == false || Mouse.isButtonDown(2)) {
			double yaw = Math.toRadians(minecraft.renderViewEntity.rotationYaw - 180);
			double pitch = Math.toRadians(minecraft.renderViewEntity.rotationPitch);

			Matrix4f initial = new Matrix4f();
			initial.rotate((float)pitch, new Vector3f(1, 0, 0));
			initial.rotate((float)yaw, new Vector3f(0, 1, 0));
			trackball.setTransform(initial);

			isInitialized = true;
		}

		GL11.glPushMatrix();
		Tessellator t = Tessellator.instance;
		GL11.glTranslated(offsetX + x + (scale / 2), offsetY + y + (scale / 2), scale);
		GL11.glScaled(scale, -scale, scale);
		// TODO: replace with proper width,height
		trackball.update(mouseX - 50, -(mouseY - 50));
		if (te != null) TileEntityRenderer.instance.renderTileEntityAt(te, -0.5, -0.5, -0.5, 0.0F);
		else drawBlock(minecraft.renderEngine, t);

		SidePicker picker = new SidePicker(0.5);

		HitCoord coord = picker.getNearestHit();

		if (coord != null) drawHighlight(t, coord.side, 0x444444);
		
		if (highlightSelectedSides) {
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				if (enabledDirections.get(dir.ordinal())) {
					drawHighlight(t, Side.fromForgeDirection(dir), 0xCC0000);
				}
			}
		}

		lastSideHovered = coord == null? ForgeDirection.UNKNOWN : coord.side.toForgeDirection();

		GL11.glPopMatrix();
	}

	private static void drawHighlight(Tessellator t, SidePicker.Side side, int color) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		t.startDrawingQuads();
		t.setColorRGBA_I(color, 64);
		switch (side) {
			case XPos:
				t.addVertex(0.5, -0.5, -0.5);
				t.addVertex(0.5, 0.5, -0.5);
				t.addVertex(0.5, 0.5, 0.5);
				t.addVertex(0.5, -0.5, 0.5);
				break;
			case YPos:
				t.addVertex(-0.5, 0.5, -0.5);
				t.addVertex(-0.5, 0.5, 0.5);
				t.addVertex(0.5, 0.5, 0.5);
				t.addVertex(0.5, 0.5, -0.5);
				break;
			case ZPos:
				t.addVertex(-0.5, -0.5, 0.5);
				t.addVertex(0.5, -0.5, 0.5);
				t.addVertex(0.5, 0.5, 0.5);
				t.addVertex(-0.5, 0.5, 0.5);
				break;
			case XNeg:
				t.addVertex(-0.5, -0.5, -0.5);
				t.addVertex(-0.5, -0.5, 0.5);
				t.addVertex(-0.5, 0.5, 0.5);
				t.addVertex(-0.5, 0.5, -0.5);
				break;
			case YNeg:
				t.addVertex(-0.5, -0.5, -0.5);
				t.addVertex(0.5, -0.5, -0.5);
				t.addVertex(0.5, -0.5, 0.5);
				t.addVertex(-0.5, -0.5, 0.5);
				break;
			case ZNeg:
				t.addVertex(-0.5, -0.5, -0.5);
				t.addVertex(-0.5, 0.5, -0.5);
				t.addVertex(0.5, 0.5, -0.5);
				t.addVertex(0.5, -0.5, -0.5);
				break;
		}
		t.draw();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}

	private void drawBlock(TextureManager manager, Tessellator t) {
		GL11.glColor4f(1, 1, 1, 1);
		manager.bindTexture(TextureMap.locationBlocksTexture);
		blockRender.setRenderBounds(0, 0, 0, 1, 1, 1);
		t.startDrawingQuads();

		blockRender.renderFaceXNeg(Block.stone, -0.5, -0.5, -0.5, block.getIcon(4, meta));

		blockRender.renderFaceXPos(Block.stone, -0.5, -0.5, -0.5, block.getIcon(5, meta));

		blockRender.renderFaceYPos(Block.stone, -0.5, -0.5, -0.5, block.getIcon(1, meta));

		blockRender.renderFaceYNeg(Block.stone, -0.5, -0.5, -0.5, block.getIcon(0, meta));

		blockRender.renderFaceZNeg(Block.stone, -0.5, -0.5, -0.5, block.getIcon(2, meta));

		blockRender.renderFaceZPos(Block.stone, -0.5, -0.5, -0.5, block.getIcon(3, meta));

		t.draw();
	}

	private void setFaceColor(ForgeDirection dir) {
		if (enabledDirections.get(dir)) {
			Tessellator.instance.setColorOpaque_F(1, 0, 0);
		} else {
			Tessellator.instance.setColorOpaque_F(1, 1, 1);
		}
	}

	@Override
	public void mouseClickMove(int mouseX, int mouseY, int button, long time) {
		super.mouseClickMove(mouseX, mouseY, button, time);
		movedTicks++;
	}

	@Override
	public void mouseMovedOrUp(int mouseX, int mouseY, int button) {
		super.mouseMovedOrUp(mouseX, mouseY, button);
		if (button == 0 &&
				movedTicks < 5 &&
				lastSideHovered != null &&
				lastSideHovered != ForgeDirection.UNKNOWN &&
				callback != null) {
			callback.onSideSelected(lastSideHovered);
			movedTicks = 5;
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		movedTicks = 0;
		lastSideHovered = null;
	}
}
