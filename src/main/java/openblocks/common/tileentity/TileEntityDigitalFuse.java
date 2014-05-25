package openblocks.common.tileentity;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.Config;
import openblocks.client.gui.GuiDigitalFuse;
import openblocks.common.container.ContainerDigitalFuse;
import openmods.OpenMods;
import openmods.api.IHasGui;
import openmods.api.INeighbourAwareTile;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableFlags;
import openmods.sync.SyncableInt;
import openmods.tileentity.SyncedTileEntity;

public class TileEntityDigitalFuse extends SyncedTileEntity implements INeighbourAwareTile, IHasGui {

	private static byte STATE_RESET = 1;
	private static byte STATE_POWERED = 2;
	private static byte STATE_OUTPUT = 4;

	private SyncableInt timeLeft;
	private SyncableInt resetTime;
	private SyncableFlags stateFlags;

	private long lastTime;

	public TileEntityDigitalFuse() {
		lastTime = System.currentTimeMillis();
	}

	@Override
	protected void createSyncedFields() {
		timeLeft = new SyncableInt(500);
		resetTime = new SyncableInt(500);
		stateFlags = new SyncableFlags();
	}

	@Override
	protected void initialize() {
		if (worldObj.isRemote) return;
		onNeighbourChanged(0);
	}

	@Override
	public void updateEntity() {

		super.updateEntity();

		long currentTime = System.currentTimeMillis();

		if (stateFlags.get(STATE_POWERED)) {

			long timeDifference = currentTime - lastTime;

			if (timeDifference > 1000) {
				timeDifference -= 1000;

				if (timeLeft.getValue() > 0) {
					timeLeft.modify(-1);

					if (timeLeft.getValue() == 0) {

					} else if (shouldSync()) {
						sync();
					}
				}

				lastTime = currentTime + timeDifference;
			}

			if (worldObj.isRemote) {
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}

		} else {
			lastTime = System.currentTimeMillis();
		}

		stateFlags.set(STATE_OUTPUT, timeLeft.getValue() == 0);

		if (!worldObj.isRemote) sync();

		if (stateFlags.ticksSinceSet(OpenMods.proxy, worldObj, STATE_OUTPUT) == 0 ||
				stateFlags.ticksSinceUnset(OpenMods.proxy, worldObj, STATE_OUTPUT) == 0) {
			updateOutputBlock();
		}
	}

	private void updateOutputBlock() {
		ForgeDirection rotate = getRotation();
		worldObj.notifyBlockOfNeighborChange(
				xCoord + rotate.offsetX,
				yCoord + rotate.offsetY,
				zCoord + rotate.offsetZ,
				Config.blockDigitalFuseId);
	}

	public SyncableInt getTimeLeft() {
		return timeLeft;
	}

	public SyncableInt getResetTime() {
		return resetTime;
	}

	public boolean isOutputtingPower() {
		return stateFlags.get(STATE_OUTPUT);
	}

	private boolean shouldSync() {
		long ticks = OpenMods.proxy.getTicks(worldObj);
		int left = timeLeft.getValue();
		int interval = left > 0 && left < 10? 2 : 10;
		return !worldObj.isRemote &&
				ticks % interval == 0;
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {}

	@Override
	public void onNeighbourChanged(int blockId) {

		if (!worldObj.isRemote) {

			ForgeDirection rotation = getRotation();

			stateFlags.set(STATE_POWERED, isPoweredOnSide(rotation.getRotation(ForgeDirection.UP)));
			stateFlags.set(STATE_RESET, isPoweredOnSide(rotation.getRotation(ForgeDirection.DOWN)));

			if (stateFlags.get(STATE_RESET)) {
				timeLeft.setValue(resetTime.getValue());
			}

			sync();
		}
	}

	private boolean isPoweredOnSide(ForgeDirection side) {

		int pX = xCoord + side.offsetX;
		int pY = yCoord + side.offsetY;
		int pZ = zCoord + side.offsetZ;

		if (worldObj.isAirBlock(pX, pY, pZ)) { return false; }

		int blockId = worldObj.getBlockId(pX, pY, pZ);
		Block block = Block.blocksList[blockId];

		if (block == Block.redstoneWire) {
			return worldObj.getBlockMetadata(pX, pY, pZ) > 0;
		} else if (block.hasComparatorInputOverride()) {
			return block.getComparatorInputOverride(worldObj, pX, pY, pZ, side.getOpposite().ordinal()) > 0;
		} else if (block.canProvidePower()) { return Math.max(
				block.isProvidingStrongPower(worldObj, pX, pY, pZ, side.getOpposite().ordinal()),
				block.isProvidingWeakPower(worldObj, pX, pY, pZ, side.getOpposite().ordinal())) > 0; }

		return false;
	}

	@Override
	public Object getServerGui(EntityPlayer player) {
		return new ContainerDigitalFuse(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiDigitalFuse((ContainerDigitalFuse)getServerGui(player));
	}

	@Override
	public boolean canOpenGui(EntityPlayer player) {
		return true;
	}

	public int getSignalFlags() {
		int flags = 0;
		for (int i : stateFlags.getActiveSlots()) {
			flags |= i;
		}
		return flags;
	}

}
