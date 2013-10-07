package openblocks.client.gui.component;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.ForgeDirection;
import openblocks.sync.SyncableDirectionSet;
import openblocks.utils.SidePicker;
import openblocks.utils.SidePicker.HitCoord;
import openblocks.utils.Trackball.TrackballWrapper;

import org.lwjgl.opengl.GL11;

public class GuiComponentSideSelector extends BaseComponent {

	RenderBlocks blockRender = new RenderBlocks();

	private TrackballWrapper trackball = new TrackballWrapper(1, 40);

	public double scale;

	private ISideSelectionCallback callback;

	private ForgeDirection lastSideHovered;

	private int movedTicks = 0;

	private SyncableDirectionSet enabledDirections;

	public GuiComponentSideSelector(int x, int y, double scale, SyncableDirectionSet directions, ISideSelectionCallback iSideSelectionCallback) {
		super(x, y);
		this.scale = scale;
		this.callback = iSideSelectionCallback;
		this.enabledDirections = directions;
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		System.out.println(enabledDirections);
		GL11.glPushMatrix();
		Tessellator t = Tessellator.instance;
		GL11.glTranslated(offsetX + x + (scale / 2), offsetY + y + (scale / 2), scale);
		GL11.glScaled(scale, scale, scale);
		trackball.update(mouseX - 50, mouseY - 50); // TODO: replace with proper
													// width,height
		GL11.glColor4f(1, 1, 1, 1);
		minecraft.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		blockRender.setRenderBounds(0, 0, 0, 1, 1, 1);
		t.startDrawingQuads();
		GL11.glDisable(GL11.GL_CULL_FACE);

		setFaceColor(ForgeDirection.WEST);
		blockRender.renderFaceXNeg(Block.stone, -0.5, -0.5, -0.5, Block.blockIron.getIcon(0, 0));

		setFaceColor(ForgeDirection.EAST);
		blockRender.renderFaceXPos(Block.stone, -0.5, -0.5, -0.5, Block.blockIron.getIcon(0, 0));

		setFaceColor(ForgeDirection.UP);
		blockRender.renderFaceYNeg(Block.stone, -0.5, -0.5, -0.5, Block.blockIron.getIcon(0, 0));

		setFaceColor(ForgeDirection.DOWN);
		blockRender.renderFaceYPos(Block.stone, -0.5, -0.5, -0.5, Block.blockIron.getIcon(0, 0));

		setFaceColor(ForgeDirection.NORTH);
		blockRender.renderFaceZNeg(Block.stone, -0.5, -0.5, -0.5, Block.blockIron.getIcon(0, 0));

		setFaceColor(ForgeDirection.SOUTH);
		blockRender.renderFaceZPos(Block.stone, -0.5, -0.5, -0.5, Block.blockIron.getIcon(0, 0));
		t.draw();

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

		GL11.glEnable(GL11.GL_CULL_FACE);

		GL11.glPopMatrix();
	}

	private void setFaceColor(ForgeDirection dir) {
		if (enabledDirections.getValue().contains(dir)) {
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
		if (movedTicks < 5 && lastSideHovered != null && callback != null) {
			callback.onSideSelected(lastSideHovered);
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		movedTicks = 0;
		lastSideHovered = null;
	}
}
