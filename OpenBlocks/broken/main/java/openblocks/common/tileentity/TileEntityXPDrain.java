package openblocks.common.tileentity;

import java.util.List;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import openblocks.OpenBlocks;
import openblocks.common.FluidXpUtils;
import openmods.OpenMods;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.BlockUtils;
import openmods.utils.CompatibilityUtils;
import openmods.utils.EnchantmentUtils;

public class TileEntityXPDrain extends OpenTileEntity implements ITickable {

	@Override
	public void update() {
		if (!world.isRemote) {
			final List<ExperienceOrbEntity> xpOrbsOnGrid = getXPOrbsOnGrid();
			final List<PlayerEntity> playersOnGrid = getPlayersOnGrid();

			if (!xpOrbsOnGrid.isEmpty() || !playersOnGrid.isEmpty()) {
				final BlockPos down = getPos().down();

				if (world.isBlockLoaded(down)) {
					final TileEntity te = world.getTileEntity(down);

					if (te != null && !te.isInvalid()) {
						final IFluidHandler maybeHandler = CompatibilityUtils.getFluidHandler(te, Direction.UP);

						if (maybeHandler != null) {
							for (ExperienceOrbEntity orb : xpOrbsOnGrid)
								tryConsumeOrb(maybeHandler, orb);

							for (PlayerEntity player : playersOnGrid)
								tryDrainPlayer(maybeHandler, player);
						}
					}
				}
			}
		}
	}

	protected void tryDrainPlayer(IFluidHandler tank, PlayerEntity player) {
		int playerXP = EnchantmentUtils.getPlayerXP(player);
		if (playerXP <= 0) return;

		int maxDrainedXp = Math.min(4, playerXP);

		int xpAmount = FluidXpUtils.xpJuiceConverter.xpToFluid(maxDrainedXp);
		FluidStack xpStack = new FluidStack(OpenBlocks.Fluids.xpJuice, xpAmount);

		int maxAcceptedLiquid = tank.fill(xpStack, false);

		// rounding down, so we only use as much as we can
		int acceptedXP = FluidXpUtils.xpJuiceConverter.fluidToXp(maxAcceptedLiquid);
		int acceptedLiquid = FluidXpUtils.xpJuiceConverter.xpToFluid(acceptedXP);

		xpStack.amount = acceptedLiquid;
		int finallyAcceptedLiquid = tank.fill(xpStack, true);

		if (finallyAcceptedLiquid <= 0) return;

		if (OpenMods.proxy.getTicks(world) % 4 == 0) {
			playSoundAtBlock(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1F, 0.5F * ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.8F));
		}

		EnchantmentUtils.addPlayerXP(player, -acceptedXP);
	}

	protected void tryConsumeOrb(IFluidHandler tank, ExperienceOrbEntity orb) {
		if (!orb.isDead) {
			int xpAmount = FluidXpUtils.xpJuiceConverter.xpToFluid(orb.getXpValue());
			FluidStack xpStack = new FluidStack(OpenBlocks.Fluids.xpJuice, xpAmount);
			int filled = tank.fill(xpStack, false);
			if (filled == xpStack.amount) {
				tank.fill(xpStack, true);
				orb.setDead();
			}
		}
	}

	protected List<PlayerEntity> getPlayersOnGrid() {
		return world.getEntitiesWithinAABB(PlayerEntity.class, BlockUtils.singleBlock(pos));
	}

	protected List<ExperienceOrbEntity> getXPOrbsOnGrid() {
		return world.getEntitiesWithinAABB(ExperienceOrbEntity.class, BlockUtils.aabbOffset(pos, 0, 0, 0, 1, 0.3, 1));
	}

}
