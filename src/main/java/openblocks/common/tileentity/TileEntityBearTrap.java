package openblocks.common.tileentity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import openmods.api.*;
import openmods.sync.*;
import openmods.tileentity.SyncedTileEntity;

public class TileEntityBearTrap extends SyncedTileEntity implements IActivateAwareTile, ISurfaceAttachment, INeighbourAwareTile, IAddAwareTile {

	public static final int OPENING_ANIMATION_TIME = 15;

	public enum Flags {
		isShut
	}

	private SyncableFlags flags;
	// can't be added as new flag, since animation depends on it
	private SyncableBoolean isLocked;
	private SyncableUUID trappedEntity;
	private int tickSinceOpened;
	private Entity cachedEntity;

	public TileEntityBearTrap() {
		syncMap.addUpdateListener(new ISyncListener() {
			@Override
			public void onSync(Set<ISyncableObject> changes) {
				if (changes.contains(flags) && !isShut()) tickSinceOpened = 0;
			}
		});
	}

	private Entity getEntity() {
		UUID uuid = trappedEntity.getValue();
		if (uuid == null) return null;

		if (cachedEntity != null && uuid.equals(cachedEntity.getUniqueID())) return cachedEntity;

		@SuppressWarnings("unchecked")
		final List<Entity> entityList = worldObj.loadedEntityList;

		for (Entity entity : entityList)
			if (uuid.equals(entity.getUniqueID())) {
				cachedEntity = entity;
				return entity;
			}

		return null;

	}

	@Override
	protected void createSyncedFields() {
		flags = SyncableFlags.create(Flags.values().length);
		trappedEntity = new SyncableUUID();
		isLocked = new SyncableBoolean();
		flags.on(Flags.isShut);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		tickSinceOpened++;
		if (!worldObj.isRemote) {
			immobilizeEntity();
			sync();
		}
	}

	private void immobilizeEntity() {
		final Entity trappedEntity = getEntity();
		if (trappedEntity == null || trappedEntity.isDead) {
			open();
			return;
		}

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

	public void onEntityCollided(Entity entity) {
		if (!worldObj.isRemote) {
			if (entity instanceof EntityCreature && !isLocked.get() && tickSinceOpened > OPENING_ANIMATION_TIME) {
				close(entity);
			}
		}
	}

	public boolean isShut() {
		return flags.get(Flags.isShut);
	}

	public int getComparatorLevel() {
		final Entity e = getEntity();
		return e != null? e.myEntitySize.ordinal() + 1 : 0;
	}

	public int ticksSinceOpened() {
		return tickSinceOpened;
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!worldObj.isRemote) open();
		return true;
	}

	private void close(Entity trapped) {
		if (!flags.get(Flags.isShut)) {
			flags.on(Flags.isShut);
			trappedEntity.setValue(trapped.getUniqueID());
			worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "openblocks:beartrap.close", 0.5F, 1.0F);
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
		}
	}

	private void open() {
		if (flags.get(Flags.isShut)) {
			flags.off(Flags.isShut);
			trappedEntity.clear();
			worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "openblocks:beartrap.open", 0.5F, 1.0F);
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
		}
	}

	@Override
	public ForgeDirection getSurfaceDirection() {
		return ForgeDirection.DOWN;
	}

	@Override
	public void onNeighbourChanged(Block block) {
		updateRedstone();
	}

	@Override
	public void onAdded() {
		updateRedstone();
	}

	private void updateRedstone() {
		if (!worldObj.isRemote) {
			boolean isLocked = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
			this.isLocked.set(isLocked);
			if (isLocked) open();
		}
	}
}
