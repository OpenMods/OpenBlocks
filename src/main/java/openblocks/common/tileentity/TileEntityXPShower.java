package openblocks.common.tileentity;

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraftforge.fluids.FluidContainerRegistry;
import openblocks.OpenBlocks;
import openblocks.common.entity.EntityXPOrbNoFly;
import openmods.OpenMods;
import openmods.api.INeighbourAwareTile;
import openmods.liquids.GenericTank;
import openmods.sync.SyncableBoolean;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.EnchantmentUtils;

public class TileEntityXPShower extends SyncedTileEntity implements INeighbourAwareTile {

	private static final int DRAIN_PER_CYCLE = 50;

	private GenericTank bufferTank = new GenericTank(
			FluidContainerRegistry.BUCKET_VOLUME,
			OpenBlocks.XP_FLUID
			);

	private int drainedCountdown = 0;
	private SyncableBoolean isOn;
	private boolean isPowered = false;

	@Override
	protected void createSyncedFields() {
		isOn = new SyncableBoolean();
	}

	@Override
	public void updateEntity() {

		super.updateEntity();

		if (!worldObj.isRemote) {

			if (!isPowered && OpenMods.proxy.getTicks(worldObj) % 3 == 0) {

				bufferTank.fillFromSides(DRAIN_PER_CYCLE, worldObj, getPosition(), EnumSet.of(getRotation()));

				int amountInTank = bufferTank.getFluidAmount();

				if (amountInTank > 0) {

					int xpInTank = EnchantmentUtils.liquidToXPRatio(amountInTank);
					int drainable = EnchantmentUtils.XPToLiquidRatio(xpInTank);

					if (drainable > 0) {

						bufferTank.drain(drainable, true);

						drainedCountdown = 10;

						while (xpInTank > 0) {
							int xpAmount = EntityXPOrb.getXPSplit(xpInTank);
							xpInTank -= xpAmount;
							worldObj.spawnEntityInWorld(new EntityXPOrbNoFly(worldObj, xCoord + 0.5D, yCoord, zCoord + 0.5D, xpAmount));
						}
					}
				}
			}

			isOn.set(drainedCountdown-- > 0 && !isPowered);
			sync();

		} else if (isOn.get()) {

			Vec3 vec = Vec3.createVectorHelper(
					(worldObj.rand.nextDouble() - 0.5) * 0.05,
					0,
					(worldObj.rand.nextDouble() - 0.5) * 0.05);
			OpenBlocks.proxy.spawnLiquidSpray(worldObj, OpenBlocks.XP_FLUID, xCoord + 0.5d, yCoord + 0.4d, zCoord + 0.5d, 0.4f, 0.7f, vec);

		}

	}

	@Override
	protected void initialize() {
		if (!worldObj.isRemote) updateState();
	}

	@Override
	public void onNeighbourChanged(Block block) {
		if (!worldObj.isRemote) updateState();
	}

	public void updateState() {
		isPowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		bufferTank.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		bufferTank.writeToNBT(nbt);
	}

}
