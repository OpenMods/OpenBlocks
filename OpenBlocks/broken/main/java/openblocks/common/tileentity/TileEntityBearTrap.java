package openblocks.common.tileentity;

import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import openblocks.OpenBlocks;
import openmods.api.IActivateAwareTile;
import openmods.api.IAddAwareTile;
import openmods.api.INeighbourAwareTile;
import openmods.api.ISurfaceAttachment;
import openmods.model.eval.EvalModelState;
import openmods.sync.SyncMap;
import openmods.sync.SyncableBoolean;
import openmods.sync.SyncableFlags;
import openmods.sync.SyncableUUID;
import openmods.tileentity.SyncedTileEntity;

public class TileEntityBearTrap extends SyncedTileEntity implements IActivateAwareTile, ISurfaceAttachment, INeighbourAwareTile, IAddAwareTile, ITickable {

	private static final float FULLY_OPEN = 1 - 0.001f; // epsilon required in 1.10, check later versions
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

	public TileEntityBearTrap() {}

	@Override
	protected void onSyncMapCreate(SyncMap syncMap) {
		syncMap.addUpdateListener(changes -> {
			if (changes.contains(flags) && !isShut()) tickSinceOpened = 0;
		});
	}

	private Entity getEntity() {
		UUID uuid = trappedEntity.getValue();
		if (uuid == null) return null;

		if (cachedEntity != null && uuid.equals(cachedEntity.getUniqueID())) return cachedEntity;

		for (Entity entity : world.loadedEntityList)
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
	public void update() {
		tickSinceOpened++;
		if (!world.isRemote) {
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

		final int x = pos.getX();
		final int y = pos.getY();
		final int z = pos.getZ();

		trappedEntity.distanceWalkedOnStepModified = 0.0f;
		trappedEntity.distanceWalkedModified = 0.0f;

		trappedEntity.posX = 0.5 + x;
		trappedEntity.posZ = 0.5 + z;
		trappedEntity.posY = y;

		trappedEntity.prevPosX = 0.5 + x;
		trappedEntity.prevPosZ = 0.5 + z;
		trappedEntity.prevPosY = y;

		trappedEntity.lastTickPosX = 0.5 + x;
		trappedEntity.lastTickPosZ = 0.5 + z;
		trappedEntity.lastTickPosY = y;

		trappedEntity.motionX = 0;
		trappedEntity.motionY = 0;
		trappedEntity.motionZ = 0;
	}

	public void onEntityCollided(Entity entity) {
		if (!world.isRemote) {
			if (entity instanceof CreatureEntity && !isLocked.get() && tickSinceOpened > OPENING_ANIMATION_TIME) {
				close(entity);
			}
		}
	}

	public boolean isShut() {
		return flags.get(Flags.isShut);
	}

	public int getComparatorLevel() {
		final Entity e = getEntity();
		return e != null? MathHelper.ceil(e.getEntityBoundingBox().getAverageEdgeLength() / 16.0) : 0;
	}

	public int ticksSinceOpened() {
		return tickSinceOpened;
	}

	@Override
	public boolean onBlockActivated(PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) open();
		return true;
	}

	private void close(Entity trapped) {
		if (!flags.get(Flags.isShut)) {
			flags.on(Flags.isShut);
			trappedEntity.setValue(trapped.getUniqueID());
			playSoundAtBlock(OpenBlocks.Sounds.BLOCK_BEARTRAP_CLOSE, 0.5F, 1.0F);
			world.notifyNeighborsOfStateChange(pos, getBlockType(), true);
		}
	}

	private void open() {
		if (flags.get(Flags.isShut)) {
			flags.off(Flags.isShut);
			trappedEntity.clear();
			playSoundAtBlock(OpenBlocks.Sounds.BLOCK_BEARTRAP_OPEN, 0.5F, 1.0F);
			world.notifyNeighborsOfStateChange(pos, getBlockType(), true);
		}
	}

	@Override
	public Direction getSurfaceDirection() {
		return Direction.DOWN;
	}

	@Override
	public void onNeighbourChanged(BlockPos neighbourPos, Block neighbourBlock) {
		updateRedstone();
	}

	@Override
	public void onAdded() {
		updateRedstone();
	}

	private void updateRedstone() {
		if (!world.isRemote) {
			boolean isLocked = world.isBlockIndirectlyGettingPowered(pos) > 0;
			this.isLocked.set(isLocked);
			if (isLocked) open();
		}
	}

	public EvalModelState getRenderState(float partialTick) {
		if (isShut()) {
			return EvalModelState.create().withArg("progress", FULLY_OPEN, false);
		} else {
			final float openingProgress = (ticksSinceOpened() + partialTick) / TileEntityBearTrap.OPENING_ANIMATION_TIME;
			if (openingProgress < FULLY_OPEN) {
				return EvalModelState.create().withArg("progress", FULLY_OPEN - openingProgress, true);
			} else {
				return EvalModelState.create().withArg("progress", 0, false);
			}
		}
	}
}
