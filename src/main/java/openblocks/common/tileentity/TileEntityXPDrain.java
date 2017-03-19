package openblocks.common.tileentity;

import java.lang.ref.WeakReference;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import openblocks.OpenBlocks;
import openblocks.common.LiquidXpUtils;
import openmods.OpenMods;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.BlockUtils;
import openmods.utils.EnchantmentUtils;

public class TileEntityXPDrain extends OpenTileEntity implements ITickable {

	private WeakReference<TileEntity> targetTank;

	@Override
	public void update() {
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

		int xpAmount = LiquidXpUtils.xpToLiquidRatio(maxDrainedXp);
		FluidStack xpStack = new FluidStack(OpenBlocks.Fluids.xpJuice, xpAmount);

		int maxAcceptedLiquid = tank.fill(EnumFacing.UP, xpStack, false);

		// rounding down, so we only use as much as we can
		int acceptedXP = LiquidXpUtils.liquidToXpRatio(maxAcceptedLiquid);
		int acceptedLiquid = LiquidXpUtils.xpToLiquidRatio(acceptedXP);

		xpStack.amount = acceptedLiquid;
		int finallyAcceptedLiquid = tank.fill(EnumFacing.UP, xpStack, true);

		if (finallyAcceptedLiquid <= 0) return;

		if (OpenMods.proxy.getTicks(worldObj) % 4 == 0) {
			playSoundAtBlock(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1F, 0.5F * ((worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.7F + 1.8F));
		}

		EnchantmentUtils.addPlayerXP(player, -acceptedXP);
	}

	protected void tryConsumeOrb(IFluidHandler tank, EntityXPOrb orb) {
		if (!orb.isDead) {
			int xpAmount = LiquidXpUtils.xpToLiquidRatio(orb.getXpValue());
			FluidStack xpStack = new FluidStack(OpenBlocks.Fluids.xpJuice, xpAmount);
			int filled = tank.fill(EnumFacing.UP, xpStack, false);
			if (filled == xpStack.amount) {
				tank.fill(EnumFacing.UP, xpStack, true);
				orb.setDead();
			}
		}
	}

	public void searchForTank() {
		targetTank = null;
		BlockPos target = pos.down();
		while (target.getY() >= 0) {
			boolean isAir = worldObj.isAirBlock(target);
			if (!isAir) {
				TileEntity te = worldObj.getTileEntity(target);
				if (!(te instanceof IFluidHandler) && te != null) {
					final IBlockState blockState = worldObj.getBlockState(pos);
					if (blockState.isOpaqueCube()) { return; }
				} else {
					targetTank = new WeakReference<TileEntity>(te);
					return;
				}
			}
			target = target.down();
		}
	}

	protected List<EntityPlayer> getPlayersOnGrid() {
		return worldObj.getEntitiesWithinAABB(EntityPlayer.class, BlockUtils.singleBlock(pos));
	}

	protected List<EntityXPOrb> getXPOrbsOnGrid() {
		return worldObj.getEntitiesWithinAABB(EntityXPOrb.class, BlockUtils.aabbOffset(pos, 0, 0, 0, 1, 0.3, 1));
	}

}
