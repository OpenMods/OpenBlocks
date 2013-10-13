package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.common.api.IAwareTile;
import openblocks.common.api.ISurfaceAttachment;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableFlags;
import openblocks.sync.SyncableInt;
import openblocks.utils.BlockUtils;

public class TileEntityBearTrap extends NetworkedTileEntity implements
		IAwareTile, ISurfaceAttachment {

	public enum Keys {
		flags, trappedEntityId
	}

	public enum Flags {
		isShut
	}

	private SyncableFlags flags = new SyncableFlags();
	private SyncableInt trappedEntityId = new SyncableInt();

	public TileEntityBearTrap() {
		addSyncedObject(Keys.flags, flags);
		addSyncedObject(Keys.trappedEntityId, trappedEntityId);
		flags.on(Flags.isShut);
	}

	@Override
	protected void initialize() {

	}

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

		if (OpenBlocks.proxy.getTicks(worldObj) % 4 == 0) {
			sync(false);
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

	@Override
	public void onBlockBroken() {}

	@Override
	public void onBlockAdded() {}

	public int tickSinceOpened() {
		return flags.ticksSinceChange(Flags.isShut);
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

	@Override
	public void onNeighbourChanged(int blockId) {}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		setRotation(BlockUtils.get2dOrientation(player));
		sync();
	}

	@Override
	public boolean onBlockEventReceived(int eventId, int eventParam) {
		return false;
	}

	public void setOpen() {
		flags.set(Flags.isShut, false);
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {}

	@Override
	public ForgeDirection getSurfaceDirection() {
		return ForgeDirection.DOWN;
	}

}
