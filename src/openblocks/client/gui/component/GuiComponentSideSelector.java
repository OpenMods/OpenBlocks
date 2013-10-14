package openblocks.client.gui.component;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.ForgeDirection;
import openblocks.sync.SyncableFlags;
import openblocks.utils.SidePicker;
import openblocks.utils.SidePicker.HitCoord;
import openblocks.utils.Trackball.TrackballWrapper;

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
	
	public GuiComponentSideSelector(int x, int y, double scale, TileEntity te, int meta, Block block, SyncableFlags directions, ISideSelectionCallback iSideSelectionCallback) {
		super(x, y);
		this.scale = scale;
		this.callback = iSideSelectionCallback;
		this.enabledDirections = directions;
		this.block = block;
		this.meta = meta;
		this.te = te;
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		if (isInitialized == false) {
			double yaw = Math.toRadians(minecraft.renderViewEntity.rotationYaw - 180);
			double pitch = Math.toRadians(minecraft.renderViewEntity.rotationPitch);
			
			Matrix4f initial = new Matrix4f();
			initial.rotate((float)pitch, new Vector3f(1, 0, 0));
			initial.rotate((float)yaw, new Vector3f(0, 1, 0));
			trackball.setTransform(initial);
			
			isInitialized = true;
		}
		
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		Tessellator t = Tessellator.instance;
		GL11.glTranslated(offsetX + x + (scale / 2), offsetY + y + (scale / 2), scale);
		GL11.glScaled(scale, -scale, scale);
		trackball.update(mouseX - 50, -(mouseY - 50)); // TODO: replace with proper
													// width,height
		if (te != null) {
			TileEntityRenderer.instance.renderTileEntityAt(te, -0.5, -0.5, -0.5, 0.0F);
		}else {
			GL11.glColor4f(1, 1, 1, 1);
			minecraft.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
			blockRender.setRenderBounds(0, 0, 0, 1, 1, 1);
			t.startDrawingQuads();
	
			setFaceColor(ForgeDirection.WEST);
			blockRender.renderFaceXNeg(Block.stone, -0.5, -0.5, -0.5, block.getIcon(4, meta));
	
			setFaceColor(ForgeDirection.EAST);
			blockRender.renderFaceXPos(Block.stone, -0.5, -0.5, -0.5, block.getIcon(5, meta));
	
			setFaceColor(ForgeDirection.UP);
			blockRender.renderFaceYPos(Block.stone, -0.5, -0.5, -0.5, block.getIcon(1, meta));
	
			setFaceColor(ForgeDirection.DOWN);
			blockRender.renderFaceYNeg(Block.stone, -0.5, -0.5, -0.5, block.getIcon(0, meta));
	
			setFaceColor(ForgeDirection.NORTH);
			blockRender.renderFaceZNeg(Block.stone, -0.5, -0.5, -0.5, block.getIcon(2, meta));
	
			setFaceColor(ForgeDirection.SOUTH);
			blockRender.renderFaceZPos(Block.stone, -0.5, -0.5, -0.5, block.getIcon(3, meta));
			
			t.draw();

		}
		GL11.glPointSize(10);
		SidePicker picker = new SidePicker(0.5);
		Map<SidePicker.Side, Vec3> hits = picker.calculateMouseHits();

		if (!hits.isEmpty()) {
			GL11.glBegin(GL11.GL_POINTS);
			for (Map.Entry<SidePicker.Side, Vec3> e : hits.entrySet()) {
				switch (e.getKey()) {
					case XPos:
						GL11.glColor3f(1, 0, 0);
						break;
					case YPos:
						GL11.glColor3f(0, 1, 0);
						break;
					case ZPos:
						GL11.glColor3f(0, 0, 1);
						break;
					case XNeg:
						GL11.glColor3f(0, 1, 1);
						break;
					case YNeg:
						GL11.glColor3f(1, 0, 1);
						break;
					case ZNeg:
						GL11.glColor3f(1, 1, 0);
						break;
				}
				Vec3 hit = e.getValue();
				GL11.glVertex3d(hit.xCoord, hit.yCoord, hit.zCoord);
			}
			GL11.glEnd();
		}

		HitCoord coord = picker.getNearestHit();
		lastSideHovered = coord == null? ForgeDirection.UNKNOWN : coord.side.toForgeDirection();

		GL11.glPopMatrix();
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
		if (movedTicks < 5 && lastSideHovered != null && lastSideHovered != ForgeDirection.UNKNOWN && callback != null) {
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
