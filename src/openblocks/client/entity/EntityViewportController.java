package openblocks.client.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityViewportController extends EntityLiving {

	private int ticks = 0;

	public EntityViewportController(World world) {
		super(world);
	}

	public boolean isAIEnabled() {
		return false;
	}

	@Override
	public void onEntityUpdate() {

	}

	public void onUpdate() {

	}

	public void onLivingUpdate() {

	}

	public void updateCamera(EntityLiving entity) {
		ticks += 4;
		if (entity == null || entity.isDead) { return; }

		double yawRad = ((double)(ticks % 360) / 180) * Math.PI;
		// System.out.println(yawRad);
		double x = Math.cos(yawRad);
		double z = Math.sin(yawRad);
		double mag = Math.sqrt(x * x + z * z);
		double cx = (x / mag) * 2;
		double cz = (z / mag) * 2;
		posX = lastTickPosX = entity.posX + cx;
		posZ = lastTickPosZ = entity.posZ + cz;
		posY = lastTickPosY = entity.posY;

		double distanceX = entity.posX - posX;
		double distanceY = entity.posY - posY;
		double distanceZ = entity.posZ - posZ;

		double d3 = (double)MathHelper.sqrt_double(distanceX * distanceX
				+ distanceZ * distanceZ);
		float f2 = (float)(Math.atan2(distanceZ, distanceX) * 180.0D / Math.PI) - 90.0F;
		float f3 = (float)(-(Math.atan2(distanceY, d3) * 180.0D / Math.PI));

		this.rotationPitch = this.prevRotationPitch = this.cameraPitch = this.prevCameraPitch = f3 + 20;
		this.rotationYaw = this.rotationYawHead = this.prevRotationYaw = this.prevRotationYawHead = renderYawOffset = f2;

	}

	@Override
	public int getMaxHealth() {
		return 10;
	}

}
