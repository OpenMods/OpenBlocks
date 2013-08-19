package openblocks.common.tileentity;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import openblocks.api.IAwareTile;
import openblocks.common.entity.EntityLuggage;
import openblocks.sync.ISyncHandler;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncMap;
import openblocks.sync.SyncMapTile;
import openblocks.sync.SyncableFlags;
import openblocks.sync.SyncableInt;

public class TileEntityBearTrap extends OpenTileEntity implements ISyncHandler,
		IAwareTile {

	public enum Keys {
		flags, trappedEntityId
	}

	public enum Flags {
		isShut
	}

	private SyncableFlags flags = new SyncableFlags();

	private boolean hasBeenSnapped = false;

	private SyncableInt trappedEntityId = new SyncableInt();

	private SyncMapTile syncMap = new SyncMapTile();

	public TileEntityBearTrap() {
		syncMap.put(Keys.flags, flags);
		flags.on(Flags.isShut);
		syncMap.put(Keys.trappedEntityId, trappedEntityId);
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
				trappedEntity.posY = (double)yCoord;
				trappedEntity.prevPosX = 0.5 + xCoord;
				trappedEntity.prevPosZ = 0.5 + zCoord;
				trappedEntity.prevPosY = (double)yCoord;
				trappedEntity.lastTickPosX = 0.5 + xCoord;
				trappedEntity.lastTickPosZ = 0.5 + zCoord;
				trappedEntity.lastTickPosY = (double)yCoord;
				trappedEntity.motionX = 0;
				trappedEntity.motionY = 0;
				trappedEntity.motionZ = 0;
			}
		}
		syncMap.sync(worldObj, this, (double)xCoord, (double)yCoord, (double)zCoord, 2);
	}

	public void onEntityCollided(Entity entity) {
		if (!worldObj.isRemote) {
			if (!flags.get(Flags.isShut) && entity instanceof EntityCreature) {
				trappedEntityId.setValue(entity.entityId);
				entity.worldObj.playSoundAtEntity(entity, worldObj.rand.nextBoolean()? "openblocks.beartrapclose" : "openblocks.beartrapcloseb", 0.5F, 1.0F);
				flags.set(Flags.isShut, true);
			}
		}
	}

	public boolean isShut() {
		return flags.get(Flags.isShut);
	}

	@Override
	public SyncMap getSyncMap() {
		return syncMap;
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {
		if (changes.contains(flags)) {
			hasBeenSnapped = true;
		}
	}

	@Override
	public void writeIdentifier(DataOutputStream dos) throws IOException {
		dos.writeInt(xCoord);
		dos.writeInt(yCoord);
		dos.writeInt(zCoord);
	}

	@Override
	public void onBlockBroken() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBlockAdded() {
		// TODO Auto-generated method stub

	}

	public int tickSinceOpened() {
		return flags.ticksSinceChange(Flags.isShut);
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {

		if (!worldObj.isRemote) {
			if (flags.get(Flags.isShut)) {
				flags.off(Flags.isShut);
				player.worldObj.playSoundAtEntity(player, "openblocks.beartrapopen", 0.5F, 1.0F);
			}
		}
		return true;
	}

	@Override
	public void onNeighbourChanged(int blockId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onBlockEventReceived(int eventId, int eventParam) {
		return false;
	}

	public void setOpen() {
		flags.set(Flags.isShut, false);
	}
}
