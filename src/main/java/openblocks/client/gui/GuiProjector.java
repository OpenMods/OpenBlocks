package openblocks.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import openblocks.client.renderer.HeightMapRenderer;
import openblocks.common.HeightMapData;
import openblocks.common.MapDataManager;
import openblocks.common.container.ContainerProjector;
import openblocks.rpc.IRotatable;
import openmods.gui.BaseGuiContainer;
import openmods.gui.component.*;
import openmods.gui.listener.IMouseDownListener;
import openmods.gui.misc.Trackball.TrackballWrapper;
import openmods.utils.MathUtils;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiProjector extends BaseGuiContainer<ContainerProjector> {

	private static final ResourceLocation texture = new ResourceLocation("openblocks:textures/gui/projector.png");

	private static final int VIEW_HEIGHT = 138;
	private static final int VIEW_WIDTH = 160;
	private TrackballWrapper trackball = new TrackballWrapper(1, 150);

	private static IMouseDownListener createRotationListener(final IRotatable proxy, final int direction) {
		return new IMouseDownListener() {
			@Override
			public void componentMouseDown(BaseComponent component, int x, int y, int button) {
				proxy.rotate(direction);
			}
		};
	}

	public GuiProjector(ContainerProjector container) {
		super(container, 176, 234, "");
		IRotatable proxy = getContainer().getOwner().createClientRpcProxy(IRotatable.class);

		GuiComponentIconButton buttonLeft = new GuiComponentIconButton(7, 130, 0xFFFFFF, FakeIcon.createSheetIcon(176, 0, 13, 13), texture);
		buttonLeft.setListener(createRotationListener(proxy, -1));
		root.addComponent(buttonLeft);

		GuiComponentIconButton buttonRight = new GuiComponentIconButton(152, 130, 0xFFFFFF, FakeIcon.createSheetIcon(176 + 13, 0, -13, 13), texture);
		buttonRight.setListener(createRotationListener(proxy, +1));
		root.addComponent(buttonRight);
	}

	@Override
	protected BaseComposite createRoot() {
		return new EmptyComposite(0, 0, xSize, ySize);
	}

	private boolean isInitialized;
	private int scale = 90;
	private double mapHeight = 2;

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
		if (isInitialized == false || Mouse.isButtonDown(2)) {
			trackball.setTransform(MathUtils.createEntityRotateMatrix(Minecraft.getMinecraft().renderViewEntity));
			isInitialized = true;
		}

		mapHeight += Mouse.getDWheel() / 1000.0;
		if (mapHeight > 10) mapHeight = 10;
		if (mapHeight < -5) mapHeight = -5;

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;

		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glPushMatrix();
		GL11.glColorMask(false, false, false, false);
		GL11.glTranslatef(0, 0, +999);
		// raise whole screen ...
		drawRect(0, 0, width, height, 0xFF000000);
		GL11.glColorMask(true, true, true, true);
		GL11.glTranslatef(0, 0, -999 * 2);

		GL11.glDepthFunc(GL11.GL_GREATER);
		// ... and dig hole for map
		drawRect(left + 8, top + 8, left + 8 + VIEW_WIDTH, top + 8 + VIEW_HEIGHT, 0xFF000000);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		ContainerProjector container = getContainer();
		Integer mapId = container.getMapId();

		if (mapId != null) {
			World world = container.getOwner().getWorldObj();
			HeightMapData data = MapDataManager.getMapData(world, mapId);
			if (data.isValid()) {
				GL11.glPushMatrix();
				GL11.glColor4f(1, 1, 1, 1);
				int viewMiddleX = left + 8 + VIEW_WIDTH / 2;
				int viewMiddleY = top + 8 + VIEW_HEIGHT / 2;
				GL11.glTranslatef(viewMiddleX, viewMiddleY, 50);

				GL11.glScalef(scale, -scale, scale);
				trackball.update(mouseX - viewMiddleX, -(mouseY - viewMiddleY));

				GL11.glRotated(90 * container.rotation(), 0, 1, 0);
				GL11.glTranslated(-0.5, -mapHeight, -0.5);
				HeightMapRenderer.instance.render(mapId, data);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				drawLevels();
				GL11.glPopMatrix();
			}
		}

		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(texture);

		GL11.glPushMatrix();
		GL11.glTranslated(guiLeft, guiTop, 0);
		drawTexturedModalRect(0, 0, 0, 0, xSize, ySize);

		root.render(mc, 0, 0, mouseX - guiLeft, mouseY - guiTop);
		GL11.glPopMatrix();
	}

	private void drawLevels() {
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		GL11.glBegin(GL11.GL_QUADS);

		GL11.glColor3d(0, 1, 0);

		for (int i = 0; i <= 4; i++) {
			GL11.glVertex3d(0, i, 0);
			GL11.glVertex3d(0, i, 1);
			GL11.glVertex3d(1, i, 1);
			GL11.glVertex3d(1, i, 0);
		}

		GL11.glColor3d(1, 0, 0);
		GL11.glVertex3d(0, mapHeight, 0);
		GL11.glVertex3d(0, mapHeight, 1);
		GL11.glVertex3d(1, mapHeight, 1);
		GL11.glVertex3d(1, mapHeight, 0);

		GL11.glEnd();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		GL11.glEnable(GL11.GL_CULL_FACE);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {}
}
