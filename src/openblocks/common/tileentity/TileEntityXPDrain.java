package openblocks.common.tileentity;

import java.lang.ref.WeakReference;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import openblocks.OpenBlocks;
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
					for (EntityPlayer player : getPlayersOnGrid()) {
						FluidStack xpStack = OpenBlocks.XP_FLUID.copy();
						int experience = EnchantmentUtils.getPlayerXP(player);
						int xpToDrain = Math.min(4, experience);
						xpStack.amount = EnchantmentUtils.XPToLiquidRatio(xpToDrain);
						/*
						 * We should simulate and then check the amount of XP to
						 * be drained,
						 * if one is zero and the other is not, we don't apply
						 * the draining.
						 */

						int filled = tank.fill(ForgeDirection.UP, xpStack, false);
						int theoreticalDrain = EnchantmentUtils.liquidToXPRatio(filled);
						if (theoreticalDrain <= 0 && filled > 0 || filled <= 0 && theoreticalDrain > 0) {
							// Regardless of ratio, this will protect against
							// infini-loops caused by
							// rounding.
							// ALERT: There is a return here, if code is added
							// under this for-loop
							// In the future, it could have unexpected outcomes.
							// Don't change the code
							// :P - NC
							return;
						}
						// Limit the stack to what we got last time. Keeps
						// things all sync'ed.
						// I realize that the update loop is single threaded,
						// but I'm paranoid.
						// What're you going to do.
						xpStack.amount = filled;
						filled = tank.fill(ForgeDirection.UP, xpStack, true);
						if (filled > 0) {
							if (OpenMods.proxy.getTicks(worldObj) % 4 == 0) {
								worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "random.orb", 0.1F, 0.5F * ((worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.7F + 1.8F));
							}
							int xpDrained = EnchantmentUtils.liquidToXPRatio(filled);
							EnchantmentUtils.drainPlayerXP(player, xpDrained);
						}
					}
				}
			}
		}
	}

	public void searchForTank() {
		targetTank = null;
		for (int y = yCoord - 1; y > 0; y--) {
			boolean isAir = worldObj.isAirBlock(xCoord, y, zCoord);
			if (!isAir) {
				TileEntity te = worldObj.getBlockTileEntity(xCoord, y, zCoord);
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
		AxisAlignedBB bb = AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
		return worldObj.getEntitiesWithinAABB(EntityPlayer.class, bb);
	}

}
