package openblocks.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorld;
import openblocks.common.block.BlockBlockManpulatorBase;
import openmods.api.INeighbourAwareTile;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.BlockNotifyFlags;

public abstract class TileEntityBlockManipulator extends OpenTileEntity implements INeighbourAwareTile, ITickable {

	private static final int EVENT_ACTIVATE = 3;

	private int actionCount = 0;

	public TileEntityBlockManipulator() {}

	protected abstract int getActionLimit();

	@Override
	public void update() {
		final boolean checkForWork = actionCount > 0;
		actionCount = 0;
		if (checkForWork) {
			final BlockState blockState = world.getBlockState(getPos());
			if (blockState.getBlock() == getBlockType() &&
					blockState.getValue(BlockBlockManpulatorBase.POWERED))
				triggerBlockAction(blockState);
		}
	}

	@Override
	public void onNeighbourChanged(BlockPos neighbourPos, Block neighbourBlock) {
		if (actionCount > getActionLimit())
			return;

		if (!world.isRemote) {
			final BlockState state = world.getBlockState(getPos());
			if (state.getBlock() == getBlockType()) {
				final boolean isPowered = world.isBlockIndirectlyGettingPowered(getPos()) > 0;

				final BlockState newState = state.withProperty(BlockBlockManpulatorBase.POWERED, isPowered);
				if (newState != state) {
					world.setBlockState(getPos(), newState, BlockNotifyFlags.SEND_TO_CLIENTS);
					playSoundAtBlock(isPowered? SoundEvents.BLOCK_PISTON_EXTEND : SoundEvents.BLOCK_PISTON_CONTRACT, 0.5F, world.rand.nextFloat() * 0.15F + 0.6F);
				}

				if (isPowered)
					triggerBlockAction(newState);
			}
		}
	}

	protected void triggerBlockAction() {
		final BlockState state = world.getBlockState(getPos());
		triggerBlockAction(state);
	}

	protected void triggerBlockAction(BlockState state) {
		final Direction direction = getFront(state);
		final BlockPos target = pos.offset(direction);

		if (world.isBlockLoaded(target)) {
			final BlockState targetState = world.getBlockState(target);
			if (canWork(targetState, target, direction)) {
				sendBlockEvent(EVENT_ACTIVATE, 0);
				actionCount++;
			}
		}
	}

	@Override
	public boolean receiveClientEvent(int event, int param) {
		if (event == EVENT_ACTIVATE) {
			doWork();
			return true;
		}

		return false;
	}

	private void doWork() {
		if (world instanceof ServerWorld) {
			final Direction direction = getFront();
			final BlockPos target = pos.offset(direction);

			if (world.isBlockLoaded(target)) {
				final BlockState targetState = world.getBlockState(target);
				if (canWork(targetState, target, direction))
					doWork(targetState, target, direction);
			}
		}
	}

	protected abstract boolean canWork(BlockState targetState, BlockPos target, Direction direction);

	protected abstract void doWork(BlockState targetState, BlockPos target, Direction direction);

}
