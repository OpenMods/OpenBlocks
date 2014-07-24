package openblocks.common.tileentity;

import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;
import openmods.api.IActivateAwareTile;
import openmods.api.INeighbourAwareTile;
import openmods.api.ISurfaceAttachment;
import openmods.sync.*;
import openmods.tileentity.SyncedTileEntity;

public class TileEntityBearTrap extends SyncedTileEntity implements IActivateAwareTile, ISurfaceAttachment, INeighbourAwareTile {

	public static final int OPENING_ANIMATION_TIME = 15;

	public enum Flags {
		isShut
	}

	private SyncableFlags flags;
	// can't be added as new flag, since animation depends on it
	private SyncableBoolean isLocked;
	private SyncableInt trappedEntityId;
	private int ticksSinceChange;

	public TileEntityBearTrap() {
		syncMap.addUpdateListener(new ISyncListener() {
			@Override
			public void onSync(Set<ISyncableObject> changes) {
				ticksSinceChange = 0;
			}
		});
	}

	@Override
	protected void createSyncedFields() {
		flags = SyncableFlags.create(Flags.values().length);
		trappedEntityId = new SyncableInt();
		isLocked = new SyncableBoolean();
		flags.on(Flags.isShut);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		ticksSinceChange++;
		final int entityId = trappedEntityId.get();
		if (entityId != 0) immobilizeEntity(entityId);
		if (!worldObj.isRemote) sync();
	}

	private void immobilizeEntity(int entityId) {
		Entity trappedEntity = worldObj.getEntityByID(entityId);
		if (trappedEntity != null) {
			trappedEntity.distanceWalkedOnStepModified = 0.0f;
			trappedEntity.distanceWalkedModified = 0.0f;
			trappedEntity.posX = 0.5 + xCoord;
			trappedEntity.posZ = 0.5 + zCoord;
			trappedEntity.posY = yCoord;
			trappedEntity.prevPosX = 0.5 + xCoord;
			trappedEntity.prevPosZ = 0.5 + zCoord;
			trappedEntity.prevPosY = yCoord;
			trappedEntity.lastTickPosX = 0.5 + xCoord;
			trappedEntity.lastTickPosZ = 0.5 + zCoord;
			trappedEntity.lastTickPosY = yCoord;
			trappedEntity.motionX = 0;
			trappedEntity.motionY = 0;
			trappedEntity.motionZ = 0;
		}
	}

	public void onEntityCollided(Entity entity) {
		if (!worldObj.isRemote) {
			if (entity instanceof EntityCreature && !isLocked.get() && ticksSinceChange > OPENING_ANIMATION_TIME) {
				close(entity);
			}
		}
	}

	public boolean isShut() {
		return flags.get(Flags.isShut);
	}

	public int getComparatorLevel() {
		int entityId = trappedEntityId.get();
		if (entityId == 0) return 0;
		Entity e = worldObj.getEntityByID(entityId);
		if (e == null) return 0;

		return e.myEntitySize.ordinal() + 1;
	}

	public int tickSinceChange() {
		return ticksSinceChange;
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!worldObj.isRemote) open();
		return true;
	}

	private void close(Entity trapped) {
		if (!flags.get(Flags.isShut)) {
			flags.on(Flags.isShut);
			trappedEntityId.set(trapped.getEntityId());
			worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "openblocks:beartrap.close", 0.5F, 1.0F);
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
		}
	}

	private void open() {
		if (flags.get(Flags.isShut)) {
			flags.off(Flags.isShut);
			trappedEntityId.set(0);
			worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "openblocks:beartrap.open", 0.5F, 1.0F);
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
		}
	}

	@Override
	public ForgeDirection getSurfaceDirection() {
		return ForgeDirection.DOWN;
	}

	@Override
	public void onNeighbourChanged() {
		if (!worldObj.isRemote) {
			int redstoneLevel = worldObj.getStrongestIndirectPower(xCoord, yCoord, zCoord);
			boolean isLocked = redstoneLevel > 0;
			this.isLocked.set(isLocked);
			if (isLocked) open();
		}
	}

}
