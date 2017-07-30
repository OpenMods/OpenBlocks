package openblocks.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import openblocks.common.block.BlockBlockManpulatorBase;
import openmods.api.INeighbourAwareTile;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.BlockNotifyFlags;

public abstract class TileEntityBlockManipulator extends OpenTileEntity implements INeighbourAwareTile {

	private static final int EVENT_ACTIVATE = 3;

	public TileEntityBlockManipulator() {}

	@Override
	public void onNeighbourChanged(Block block) {
		if (!worldObj.isRemote) {
			final IBlockState state = worldObj.getBlockState(getPos());
			if (state.getBlock() == getBlockType()) {
				final boolean isPowered = worldObj.isBlockIndirectlyGettingPowered(pos) > 0;

				final IBlockState newState = state.withProperty(BlockBlockManpulatorBase.POWERED, isPowered);
				if (newState != state) {
					worldObj.setBlockState(getPos(), newState, BlockNotifyFlags.SEND_TO_CLIENTS);
					playSoundAtBlock(isPowered? SoundEvents.BLOCK_PISTON_EXTEND : SoundEvents.BLOCK_PISTON_CONTRACT, 0.5F, worldObj.rand.nextFloat() * 0.15F + 0.6F);
				}

				if (isPowered)
					triggerBlockAction(newState);
			}
		}
	}

	protected void triggerBlockAction() {
		final IBlockState state = worldObj.getBlockState(getPos());
		triggerBlockAction(state);
	}

	private void triggerBlockAction(IBlockState state) {
		final EnumFacing direction = getFront(state);
		final BlockPos target = pos.offset(direction);

		if (worldObj.isBlockLoaded(target)) {
			final IBlockState targetState = worldObj.getBlockState(target);
			if (canWork(targetState, target, direction)) sendBlockEvent(EVENT_ACTIVATE, 0);
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
		if (worldObj instanceof WorldServer) {
			final EnumFacing direction = getFront();
			final BlockPos target = pos.offset(direction);

			if (worldObj.isBlockLoaded(target)) {
				final IBlockState targetState = worldObj.getBlockState(target);
				if (canWork(targetState, target, direction))
					doWork(targetState, target, direction);
			}
		}
	}

	protected abstract boolean canWork(IBlockState targetState, BlockPos target, EnumFacing direction);

	protected abstract void doWork(IBlockState targetState, BlockPos target, EnumFacing direction);

}
