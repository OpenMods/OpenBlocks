package openblocks.common.tileentity;

import java.lang.ref.WeakReference;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import openblocks.OpenBlocks;
import openblocks.common.LiquidXpUtils;
import openmods.OpenMods;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.EnchantmentUtils;

public class TileEntityXPDrain extends OpenTileEntity {

	private WeakReference<TileEntity> targetTank;

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (OpenMods.proxy.getTicks(worldObj) % 100 == 0) {
			searchForTank();
		}

		if (targetTank != null) {
			TileEntity tile = targetTank.get();
			if (!(tile instanceof IFluidHandler) || tile.isInvalid()) {
				targetTank = null;
			} else {

				if (!worldObj.isRemote) {
					IFluidHandler tank = (IFluidHandler)tile;

					for (EntityXPOrb orb : getXPOrbsOnGrid())
						tryConsumeOrb(tank, orb);

					for (EntityPlayer player : getPlayersOnGrid())
						tryDrainPlayer(tank, player);
				}
			}
		}
	}

	protected void tryDrainPlayer(IFluidHandler tank, EntityPlayer player) {
		int playerXP = EnchantmentUtils.getPlayerXP(player);
		if (playerXP <= 0) return;

		int maxDrainedXp = Math.min(4, playerXP);

		FluidStack liquid = OpenBlocks.XP_FLUID.copy();
		liquid.amount = LiquidXpUtils.xpToLiquidRatio(maxDrainedXp);
		int maxAcceptedLiquid = tank.fill(ForgeDirection.UP, liquid, false);

		// rounding down, so we only use as much as we can
		int acceptedXP = LiquidXpUtils.liquidToXpRatio(maxAcceptedLiquid);
		int acceptedLiquid = LiquidXpUtils.xpToLiquidRatio(acceptedXP);

		liquid.amount = acceptedLiquid;
		int finallyAcceptedLiquid = tank.fill(ForgeDirection.UP, liquid, true);

		if (finallyAcceptedLiquid <= 0) return;

		if (OpenMods.proxy.getTicks(worldObj) % 4 == 0) {
			worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "random.orb", 0.1F, 0.5F * ((worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.7F + 1.8F));
		}

		EnchantmentUtils.addPlayerXP(player, -acceptedXP);
	}

	protected void tryConsumeOrb(IFluidHandler tank, EntityXPOrb orb) {
		if (!orb.isDead) {
			FluidStack xpStack = OpenBlocks.XP_FLUID.copy();
			xpStack.amount = LiquidXpUtils.xpToLiquidRatio(orb.getXpValue());
			int filled = tank.fill(ForgeDirection.UP, xpStack, false);
			if (filled == xpStack.amount) {
				tank.fill(ForgeDirection.UP, xpStack, true);
				orb.setDead();
			}
		}
	}

	public void searchForTank() {
		targetTank = null;
		for (int y = yCoord - 1; y > 0; y--) {
			boolean isAir = worldObj.isAirBlock(xCoord, y, zCoord);
			if (!isAir) {
				TileEntity te = worldObj.getTileEntity(xCoord, y, zCoord);
				if (!(te instanceof IFluidHandler) && te != null) {
					Block block = te.getBlockType();
					if (block.isOpaqueCube()) { return; }
				} else {
					targetTank = new WeakReference<TileEntity>(te);
					return;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected List<EntityPlayer> getPlayersOnGrid() {
		AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
		return worldObj.getEntitiesWithinAABB(EntityPlayer.class, bb);
	}

	@SuppressWarnings("unchecked")
	protected List<EntityXPOrb> getXPOrbsOnGrid() {
		AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 0.3, zCoord + 1);
		return worldObj.getEntitiesWithinAABB(EntityXPOrb.class, bb);
	}

}
