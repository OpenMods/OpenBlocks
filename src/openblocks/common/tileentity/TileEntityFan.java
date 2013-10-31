package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.ForgeDirection;
import openblocks.common.api.IAwareTile;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableFloat;

public class TileEntityFan extends NetworkedTileEntity implements IAwareTile {

	private SyncableFloat angle = new SyncableFloat(0.0f);

	public enum Keys {
		angle
	}

	public TileEntityFan() {
		addSyncedObject(angle);
	}

	@Override
	public void updateEntity() {
		@SuppressWarnings("unchecked")
		List<Entity> entities = worldObj.getEntitiesWithinAABB(Entity.class, getEntitySearchBoundingBox());
		Vec3 blockPos = getBlockPosition();
		for (Entity entity : entities) {
			Vec3 entityPos = getEntityPosition(entity);
			Vec3 basePos = getConeBaseCenter();
			double dX = entityPos.xCoord - blockPos.xCoord;
			double dY = entityPos.yCoord - blockPos.yCoord;
			double dZ = entityPos.zCoord - blockPos.zCoord;
			double dist = MathHelper.sqrt_double(dX * dX + dZ * dZ);
			if (isLyingInCone(entityPos, blockPos, basePos, 1.2f)) {
				double yaw = Math.atan2(dZ, dX) - (Math.PI / 2);
				float pitch = (float)(-(Math.atan2(dY, dist)));
				double f1 = MathHelper.cos((float)-yaw);
				double f2 = MathHelper.sin((float)-yaw);
				double f3 = -MathHelper.cos(-pitch);
				double f4 = MathHelper.sin(-pitch);
				Vec3 directionVec = worldObj.getWorldVec3Pool().getVecFromPool(f2
						* f3, f4, f1 * f3);
				double force = 1.0 - (dist / 10.0);
				force = Math.max(0, force);
				entity.motionX -= force * directionVec.xCoord * 0.05;
				entity.motionZ -= force * directionVec.zCoord * 0.05;
			}
		}
	}

	public Vec3 getEntityPosition(Entity entity) {
		return worldObj.getWorldVec3Pool().getVecFromPool(entity.posX, entity.posY, entity.posZ);
	}

	public Vec3 getConeBaseCenter() {
		double angle = Math.toRadians(getAngle() - 90);
		return worldObj.getWorldVec3Pool().getVecFromPool(xCoord + 0.5
				+ (Math.cos(angle) * 10), yCoord + 0.5, zCoord + 0.5
				+ (Math.sin(angle) * 10));
	}

	public Vec3 getBlockPosition() {
		return worldObj.getWorldVec3Pool().getVecFromPool(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
	}

	public AxisAlignedBB getEntitySearchBoundingBox() {
		AxisAlignedBB boundingBox = AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord - 2, zCoord, xCoord + 1, yCoord + 3, zCoord + 1);
		return boundingBox.expand(10.0, 10.0, 10.0);
	}

	public boolean isLyingInCone(Vec3 point, Vec3 t, Vec3 b, float aperture) {

		float halfAperture = aperture / 2.f;

		Vec3 apexToXVect = dif(t, point);
		Vec3 axisVect = dif(t, b);

		boolean isInInfiniteCone = apexToXVect.dotProduct(axisVect)
				/ apexToXVect.lengthVector() / axisVect.lengthVector() > Math.cos(halfAperture);

		if (!isInInfiniteCone) return false;

		boolean isUnderRoundCap = apexToXVect.dotProduct(axisVect)
				/ axisVect.lengthVector() < axisVect.lengthVector();
		return isUnderRoundCap;
	}

	static public Vec3 dif(Vec3 a, Vec3 b) {
		return Vec3.createVectorHelper(a.xCoord - b.xCoord, a.yCoord - b.yCoord, a.zCoord
				- b.zCoord);
	}

	@Override
	public void onBlockBroken() {}

	@Override
	public void onBlockAdded() {}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return false;
	}

	@Override
	public void onNeighbourChanged(int blockId) {}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		angle.setValue(player.rotationYawHead);
		sync();
	}

	@Override
	public boolean onBlockEventReceived(int eventId, int eventParam) {
		return false;
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {}

	public float getAngle() {
		return angle.getValue();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		angle.writeToNBT(nbt, "angle");
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		angle.readFromNBT(nbt, "angle");
	}
}
