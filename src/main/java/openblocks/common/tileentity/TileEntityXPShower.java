package openblocks.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraftforge.fluids.FluidContainerRegistry;
import openblocks.OpenBlocks;
import openblocks.common.LiquidXpUtils;
import openblocks.common.entity.EntityXPOrbNoFly;
import openmods.OpenMods;
import openmods.api.IAddAwareTile;
import openmods.api.INeighbourAwareTile;
import openmods.liquids.GenericTank;
import openmods.sync.SyncableBoolean;
import openmods.tileentity.SyncedTileEntity;

public class TileEntityXPShower extends SyncedTileEntity implements INeighbourAwareTile, IAddAwareTile {

	private static final int DRAIN_PER_CYCLE = 50;

	private GenericTank bufferTank = new GenericTank(FluidContainerRegistry.BUCKET_VOLUME, OpenBlocks.Fluids.xpJuice);

	private SyncableBoolean isOn;
	private SyncableBoolean particleSpawnerActive;
	private int particleSpawnTimer = 0;

	@Override
	protected void createSyncedFields() {
		isOn = new SyncableBoolean();
		particleSpawnerActive = new SyncableBoolean();
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (!worldObj.isRemote) {
			trySpawnXpOrbs();
		} else {
			trySpawnParticles();
		}
	}

	private void trySpawnXpOrbs() {
		boolean hasSpawnedParticle = false;
		if (isOn.get() && OpenMods.proxy.getTicks(worldObj) % 3 == 0) {
			bufferTank.fillFromSide(DRAIN_PER_CYCLE, worldObj, getPosition(), getOrientation().north());

			int amountInTank = bufferTank.getFluidAmount();

			if (amountInTank > 0) {
				int xpInTank = LiquidXpUtils.liquidToXpRatio(amountInTank);
				int drainable = LiquidXpUtils.xpToLiquidRatio(xpInTank);

				if (drainable > 0) {
					bufferTank.drain(drainable, true);
					while (xpInTank > 0) {
						hasSpawnedParticle = true;
						int xpAmount = EntityXPOrb.getXPSplit(xpInTank);
						xpInTank -= xpAmount;
						worldObj.spawnEntityInWorld(new EntityXPOrbNoFly(worldObj, xCoord + 0.5D, yCoord, zCoord + 0.5D, xpAmount));
					}
				}
			}
		}

		particleSpawnerActive.set(hasSpawnedParticle);
		sync();
	}

	private void trySpawnParticles() {
		final int particleLevel = OpenBlocks.proxy.getParticleSettings();

		if (particleLevel == 0 || (particleLevel == 1 && worldObj.rand.nextInt(3) == 0)) {
			particleSpawnTimer = particleSpawnerActive.get()? 10 : particleSpawnTimer - 1;

			if (particleSpawnTimer > 0) {
				Vec3 vec = Vec3.createVectorHelper(
						(worldObj.rand.nextDouble() - 0.5) * 0.05,
						0,
						(worldObj.rand.nextDouble() - 0.5) * 0.05);
				OpenBlocks.proxy.spawnLiquidSpray(worldObj, OpenBlocks.Fluids.xpJuice, xCoord + 0.5d, yCoord + 0.4d, zCoord + 0.5d, 0.4f, 0.7f, vec);
			}
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
		final boolean isPowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
		isOn.set(!isPowered);
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
