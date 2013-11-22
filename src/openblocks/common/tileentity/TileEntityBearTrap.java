package openblocks.common.tileentity;

import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openmods.common.api.IActivateAwareTile;
import openmods.common.api.ISurfaceAttachment;
import openmods.common.tileentity.SyncedTileEntity;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableFlags;
import openmods.sync.SyncableInt;

public class TileEntityBearTrap extends SyncedTileEntity implements
		IActivateAwareTile, ISurfaceAttachment {

	public enum Flags {
		isShut
	}

	private SyncableFlags flags;
	private SyncableInt trappedEntityId;

	public TileEntityBearTrap() {}

	@Override
	protected void createSyncedFields() {
		flags = new SyncableFlags();
		trappedEntityId = new SyncableInt();
		flags.on(Flags.isShut);
	}

	@Override
	protected void initialize() {}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (trappedEntityId.getValue() != 0) {
			Entity trappedEntity = worldObj.getEntityByID(trappedEntityId.getValue());
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
		if (!worldObj.isRemote) {
			sync();
		}
	}

	public void onEntityCollided(Entity entity) {
		if (!worldObj.isRemote) {
			if (!flags.get(Flags.isShut) && tickSinceOpened() > 20
					&& entity instanceof EntityCreature) {
				trappedEntityId.setValue(entity.entityId);
				entity.worldObj.playSoundAtEntity(entity, worldObj.rand.nextBoolean()? "openblocks:beartrapclose" : "openblocks:beartrapcloseb", 0.5F, 1.0F);
				flags.set(Flags.isShut, true);
			}
		}
	}

	public boolean isShut() {
		return flags.get(Flags.isShut);
	}

	public int tickSinceOpened() {
		return flags.getTicksSinceChange(OpenBlocks.instance.proxy, worldObj);
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!worldObj.isRemote) {
			if (flags.get(Flags.isShut)) {
				flags.off(Flags.isShut);
				trappedEntityId.setValue(0);
				player.worldObj.playSoundAtEntity(player, "openblocks:beartrapopen", 0.5F, 1.0F);
			}
		}
		return true;
	}

	public void setOpen() {
		flags.set(Flags.isShut, false);
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {}

	@Override
	public ForgeDirection getSurfaceDirection() {
		return ForgeDirection.DOWN;
	}

}
