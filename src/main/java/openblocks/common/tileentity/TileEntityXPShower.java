package openblocks.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fluids.Fluid;
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

	private static final FluidStack XP_FLUID = new FluidStack(OpenBlocks.Fluids.xpJuice, 1);

	private static final int DRAIN_PER_CYCLE = 50;

	private GenericTank bufferTank = new GenericTank(Fluid.BUCKET_VOLUME, OpenBlocks.Fluids.xpJuice);

	private SyncableBoolean isOn;
	private SyncableBoolean particleSpawnerActive;
	private int particleSpawnTimer = 0;

	@Override
	protected void createSyncedFields() {
		isOn = new SyncableBoolean();
		particleSpawnerActive = new SyncableBoolean();
	}

	@Override
	public void update() {
		if (!worldObj.isRemote) {
			trySpawnXpOrbs();
		} else {
			trySpawnParticles();
		}
	}

	private void trySpawnXpOrbs() {
		boolean hasSpawnedParticle = false;
		if (isOn.get() && OpenMods.proxy.getTicks(worldObj) % 3 == 0) {
			bufferTank.fillFromSide(DRAIN_PER_CYCLE, worldObj, pos, getOrientation().north());

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
						final BlockPos p = getPos();
						worldObj.spawnEntityInWorld(new EntityXPOrbNoFly(worldObj, p.getX() + 0.5D, p.getY(), p.getZ() + 0.5D, xpAmount));
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
				final BlockPos p = getPos();
				Vec3d vec = new Vec3d(
						(worldObj.rand.nextDouble() - 0.5) * 0.05,
						0,
						(worldObj.rand.nextDouble() - 0.5) * 0.05);
				OpenBlocks.proxy.spawnLiquidSpray(worldObj, XP_FLUID, p.getX() + 0.5d, p.getY() + 0.4d, p.getZ() + 0.5d, 0.4f, 0.7f, vec);
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
		final int power = worldObj.isBlockIndirectlyGettingPowered(getPos()); // TODO 1.10 verify
		isOn.set(power > 0);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		bufferTank.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);
		bufferTank.writeToNBT(tag);
		return tag;
	}
}
