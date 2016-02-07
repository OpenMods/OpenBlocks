package openblocks.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.Vec3;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import openblocks.OpenBlocks;
import openblocks.common.LiquidXpUtils;
import openblocks.common.entity.EntityXPOrbNoFly;
import openmods.OpenMods;
import openmods.api.IAddAwareTile;
import openmods.api.INeighbourAwareTile;
import openmods.liquids.GenericTank;
import openmods.sync.SyncableBoolean;
import openmods.tileentity.SyncedTileEntity;

public class TileEntityXPShower extends SyncedTileEntity implements INeighbourAwareTile, IAddAwareTile, ITickable {

	private static final int DRAIN_PER_CYCLE = 50;

	private GenericTank bufferTank = new GenericTank(FluidContainerRegistry.BUCKET_VOLUME, OpenBlocks.Fluids.xpJuice);

	private int drainedCountdown = 0;
	private SyncableBoolean isOn;
	private boolean isPowered = false;

	@Override
	protected void createSyncedFields() {
		isOn = new SyncableBoolean();
	}

	@Override
	public void update() {
		if (!worldObj.isRemote) {

			if (!isPowered && OpenMods.proxy.getTicks(worldObj) % 3 == 0) {
				bufferTank.fillFromSide(DRAIN_PER_CYCLE, worldObj, pos, getOrientation().north());

				int amountInTank = bufferTank.getFluidAmount();

				if (amountInTank > 0) {

					int xpInTank = LiquidXpUtils.liquidToXpRatio(amountInTank);
					int drainable = LiquidXpUtils.xpToLiquidRatio(xpInTank);

					if (drainable > 0) {

						bufferTank.drain(drainable, true);

						drainedCountdown = 10;

						while (xpInTank > 0) {
							int xpAmount = EntityXPOrb.getXPSplit(xpInTank);
							xpInTank -= xpAmount;
							worldObj.spawnEntityInWorld(new EntityXPOrbNoFly(worldObj, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, xpAmount));
						}
					}
				}
			}

			isOn.set(drainedCountdown-- > 0 && !isPowered);
			sync();

		} else if (isOn.get()) {
			Vec3 vec = new Vec3(
					(worldObj.rand.nextDouble() - 0.5) * 0.05,
					0,
					(worldObj.rand.nextDouble() - 0.5) * 0.05);
			OpenBlocks.proxy.spawnLiquidSpray(worldObj, new FluidStack(OpenBlocks.Fluids.xpJuice, 1000), pos.getX() + 0.5, pos.getY() + 0.4, pos.getZ() + 0.5, 0.4f, 0.7f, vec);

		}

	}

	@Override
	public void onAdded() {
		if (!worldObj.isRemote) updateState();
	}

	@Override
	public void onNeighbourChanged(Block block) {
		if (!worldObj.isRemote) updateState();
	}

	public void updateState() {
		isPowered = worldObj.isBlockIndirectlyGettingPowered(pos) > 0;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		bufferTank.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		bufferTank.writeToNBT(nbt);
	}
}
