package openblocks.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import openblocks.common.CraneRegistry;
import openblocks.common.entity.EntityMagnet;
import openblocks.common.item.ItemCraneBackpack;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

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
		final EntityPlayer player = evt.entityPlayer;

		if (!ItemCraneBackpack.isWearingCrane(player)) return;

		final EntityMagnet magnet = CraneRegistry.instance.getMagnetForPlayer(player);

		if (magnet == null) return;

		double playerX = interpolatePos(player.posX, player.lastTickPosX, evt.partialRenderTick)
				- RenderManager.renderPosX;
		double playerY = interpolatePos(player.posY, player.lastTickPosY, evt.partialRenderTick)
				- RenderManager.renderPosY;
		double playerZ = interpolatePos(player.posZ, player.lastTickPosZ, evt.partialRenderTick)
				- RenderManager.renderPosZ;

		if (player instanceof EntityOtherPlayerMP) playerY += 1.62;

		final float offset = interpolateAngle(player.renderYawOffset, player.prevRenderYawOffset, evt.partialRenderTick);
		final float head = interpolateAngle(player.rotationYawHead, player.prevRotationYawHead, evt.partialRenderTick);

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

		final double magnetX = interpolatePos(magnet.posX, magnet.lastTickPosX, evt.partialRenderTick) - RenderManager.renderPosX;
		final double magnetY = interpolatePos(magnet.posY, magnet.lastTickPosY, evt.partialRenderTick) - RenderManager.renderPosY + magnet.height - 0.1;
		final double magnetZ = interpolatePos(magnet.posZ, magnet.lastTickPosZ, evt.partialRenderTick) - RenderManager.renderPosZ;

		GL11.glLineWidth(2);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_LINE_STIPPLE);
		GL11.glColor3f(1, 1, 0);
		GL11.glLineStipple(3, (short)0x0555);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(armX, armY, armZ);
		GL11.glVertex3d(magnetX, magnetY, magnetZ);
		GL11.glEnd();
		GL11.glDisable(GL11.GL_LINE_STIPPLE);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		drawLine(magnetX, magnetY, magnetZ, armX, armY, armZ);
	}

	private static void drawLineFPP(EntityPlayer player, float partialTickTime) {
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

		drawLine(magnetX, magnetY, magnetZ, posX, 0.6, posZ);
	}

	private static void drawLine(double x1, double y1, double z1, double x2, double y2, double z2) {
		GL11.glLineWidth(2);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_LINE_STIPPLE);

		GL11.glColor3f(0, 0, 0);
		GL11.glLineStipple(5, (short)0x5555);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(x1, y1, z1);
		GL11.glVertex3d(x2, y2, z2);
		GL11.glEnd();

		GL11.glColor3f(1, 1, 0);
		GL11.glLineStipple(5, (short)0xAAAA);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(x1, y1, z1);
		GL11.glVertex3d(x2, y2, z2);
		GL11.glEnd();

		GL11.glDisable(GL11.GL_LINE_STIPPLE);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	private void drawArm(RenderWorldLastEvent evt, final EntityPlayer player) {
		final TextureManager tex = Minecraft.getMinecraft().getTextureManager();
		tex.bindTexture(texture);

		GL11.glColor3f(1, 1, 1);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glPushMatrix();

		// values adjusted to roughly match TPP crane position
		GL11.glRotated(-player.rotationYaw, 0, 1, 0);
		GL11.glTranslatef(0, 1.6f, -1f);

		arm.rotateAngleY = 0;
		arm.render(1.0f / 16.0f);
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@SubscribeEvent
	public void renderFppArm(RenderWorldLastEvent evt) {
		final Minecraft mc = Minecraft.getMinecraft();

		if (mc.gameSettings.thirdPersonView != 0) return;

		final Entity rve = mc.renderViewEntity;
		if (!(rve instanceof EntityPlayer)) return;

		final EntityPlayer player = (EntityPlayer)rve;
		if (!ItemCraneBackpack.isWearingCrane(player)) return;

		drawArm(evt, player);
		drawLineFPP(player, evt.partialTicks);
	}

	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
	}
}
