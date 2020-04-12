package openblocks.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.common.CraneRegistry;
import openblocks.common.entity.EntityMagnet;
import openblocks.common.item.ItemCraneBackpack;
import org.lwjgl.opengl.GL11;

public class ModelCraneBackpack extends ModelBiped {

	public static final ModelCraneBackpack instance = new ModelCraneBackpack();

	private static final ResourceLocation texture = new ResourceLocation(ItemCraneBackpack.TEXTURE_CRANE);

	private static final float DEG_TO_RAD = (float)Math.PI / 180;

	private final ModelRenderer arm;

	public ModelCraneBackpack() {
		textureWidth = 128;
		textureHeight = 64;

		bipedBody = new ModelRenderer(this, 0, 0);
		bipedBody.setTextureSize(textureWidth, textureHeight);
		bipedBody.setRotationPoint(0, 0, 0);

		// main body
		bipedBody.addBox(-4, 0, -2, 8, 12, 8);

		// support
		bipedBody.setTextureOffset(32, 0);
		bipedBody.addBox(-1, -16, 6, 2, 24, 2);

		// arm
		arm = new ModelRenderer(this, 0, 0);
		arm.setTextureSize(textureWidth, textureHeight);
		arm.setRotationPoint(0, -16, 7);
		arm.addBox(-1, 0, 1, 2, 2, 42);
	}

	@Override
	public void setRotationAngles(float swingTime, float swingAmpl, float rightArmAngle, float headAngleX, float headAngleY, float scale, Entity entity) {
		super.setRotationAngles(swingTime, swingAmpl, rightArmAngle, headAngleX, headAngleY, scale, entity);

		if (isSneak) {
			arm.rotationPointZ = -0.15f; // good enough values
			arm.offsetY = -0.125f;
		} else {
			arm.rotationPointZ = 7;
			arm.offsetY = 0;
		}
	}

	@Override
	public void render(Entity entity, float swingTime, float swingAmpl, float rightArmAngle, float headAngleX, float headAngleY, float scale) {
		isSneak = entity != null && entity.isSneaking();
		setRotationAngles(swingTime, swingAmpl, rightArmAngle, headAngleX, headAngleY, scale, entity);
		bipedBody.render(scale);

		arm.rotateAngleY = (float)Math.PI + bipedHead.rotateAngleY;
		arm.render(scale);
	}

	private static float interpolateAngle(float current, float prev, float partialTickTime) {
		float interpolated = prev + partialTickTime * (current - prev);
		return (90 + interpolated) * DEG_TO_RAD;
	}

	private static double interpolatePos(double current, double prev, float partialTickTime) {
		return prev + partialTickTime * (current - prev);
	}

	@SubscribeEvent
	public void renderLines(RenderPlayerEvent.Pre evt) {
		final PlayerEntity player = evt.getEntityPlayer();

		if (!ItemCraneBackpack.isWearingCrane(player)) return;

		final EntityMagnet magnet = CraneRegistry.instance.getMagnetForPlayer(player);

		if (magnet == null) return;

		final float partialRenderTick = evt.getPartialRenderTick();
		double playerX = interpolatePos(player.posX, player.lastTickPosX, partialRenderTick)
				- TileEntityRendererDispatcher.staticPlayerX;
		double playerY = interpolatePos(player.posY, player.lastTickPosY, partialRenderTick)
				- TileEntityRendererDispatcher.staticPlayerY;
		double playerZ = interpolatePos(player.posZ, player.lastTickPosZ, partialRenderTick)
				- TileEntityRendererDispatcher.staticPlayerZ;

		playerY += player.getEyeHeight();

		final float offset = interpolateAngle(player.renderYawOffset, player.prevRenderYawOffset, partialRenderTick);
		final float head = interpolateAngle(player.rotationYawHead, player.prevRotationYawHead, partialRenderTick);

		double armX = playerX;
		double armY = playerY;
		double armZ = playerZ;

		double armLength;

		if (player.isSneaking()) {
			armY += 0.70;
			armLength = 2;
		} else {
			armX += -0.45 * MathHelper.cos(offset);
			armY += 0.65;
			armZ += -0.45 * MathHelper.sin(offset);
			armLength = 2.4;
		}

		armX += armLength * MathHelper.cos(head);
		armZ += armLength * MathHelper.sin(head);

		final double magnetX = interpolatePos(magnet.posX, magnet.lastTickPosX, partialRenderTick) - TileEntityRendererDispatcher.staticPlayerX;
		final double magnetY = interpolatePos(magnet.posY, magnet.lastTickPosY, partialRenderTick) - TileEntityRendererDispatcher.staticPlayerY + magnet.height - 0.1;
		final double magnetZ = interpolatePos(magnet.posZ, magnet.lastTickPosZ, partialRenderTick) - TileEntityRendererDispatcher.staticPlayerZ;

		GlStateManager.glLineWidth(2);
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GL11.glEnable(GL11.GL_LINE_STIPPLE);
		GlStateManager.color(1, 1, 0);
		GL11.glLineStipple(3, (short)0x0555);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(armX, armY, armZ);
		GL11.glVertex3d(magnetX, magnetY, magnetZ);
		GL11.glEnd();
		GL11.glDisable(GL11.GL_LINE_STIPPLE);
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.glLineWidth(1);
		GlStateManager.color(1, 1, 1);

		drawLine(magnetX, magnetY, magnetZ, armX, armY, armZ);
	}

	private static void drawLineFPP(PlayerEntity player, float partialTickTime) {
		EntityMagnet magnet = CraneRegistry.instance.getMagnetForPlayer(player);

		if (magnet == null) return;

		final float yaw = interpolateAngle(player.rotationYaw, player.prevRotationYaw, partialTickTime);

		final double posX = 1.9 * MathHelper.cos(yaw);
		final double posZ = 1.9 * MathHelper.sin(yaw);

		final double centerX = interpolatePos(player.posX, player.lastTickPosX, partialTickTime);
		final double centerY = interpolatePos(player.posY, player.lastTickPosY, partialTickTime);
		final double centerZ = interpolatePos(player.posZ, player.lastTickPosZ, partialTickTime);

		final double magnetX = interpolatePos(magnet.posX, magnet.lastTickPosX, partialTickTime) - centerX;
		final double magnetY = interpolatePos(magnet.posY, magnet.lastTickPosY, partialTickTime) - centerY + magnet.height - 0.05;
		final double magnetZ = interpolatePos(magnet.posZ, magnet.lastTickPosZ, partialTickTime) - centerZ;

		drawLine(magnetX, magnetY, magnetZ, posX, player.getEyeHeight() + 0.6, posZ);
	}

	private static void drawLine(double x1, double y1, double z1, double x2, double y2, double z2) {
		GlStateManager.glLineWidth(2);
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GL11.glEnable(GL11.GL_LINE_STIPPLE);

		GlStateManager.color(0, 0, 0);
		GL11.glLineStipple(5, (short)0x5555);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(x1, y1, z1);
		GL11.glVertex3d(x2, y2, z2);
		GL11.glEnd();

		GlStateManager.color(1, 1, 0);
		GL11.glLineStipple(5, (short)0xAAAA);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(x1, y1, z1);
		GL11.glVertex3d(x2, y2, z2);
		GL11.glEnd();

		GlStateManager.color(1, 1, 1);

		GL11.glDisable(GL11.GL_LINE_STIPPLE);
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.glLineWidth(1);
	}

	private void drawArm(RenderWorldLastEvent evt, final PlayerEntity player) {
		final TextureManager tex = Minecraft.getMinecraft().getTextureManager();
		tex.bindTexture(texture);

		GlStateManager.color(1, 1, 1);
		GlStateManager.disableLighting();
		GL11.glPushMatrix();

		// values adjusted to roughly match TPP crane position
		GL11.glRotated(-player.rotationYaw, 0, 1, 0);
		GL11.glTranslatef(0, player.getEyeHeight() + 1.6f, -1f);

		arm.rotateAngleY = 0;
		arm.render(1.0f / 16.0f);
		GL11.glPopMatrix();
		GlStateManager.enableLighting();
	}

	@SubscribeEvent
	public void renderFppArm(RenderWorldLastEvent evt) {
		final Minecraft mc = Minecraft.getMinecraft();

		if (mc.gameSettings.thirdPersonView != 0) return;

		final Entity rve = mc.getRenderViewEntity();
		if (!(rve instanceof PlayerEntity)) return;

		final PlayerEntity player = (PlayerEntity)rve;
		if (!ItemCraneBackpack.isWearingCrane(player)) return;

		drawArm(evt, player);
		drawLineFPP(player, evt.getPartialTicks());
	}

	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
	}
}
