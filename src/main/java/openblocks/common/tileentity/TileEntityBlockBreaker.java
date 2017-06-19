package openblocks.common.tileentity;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import openblocks.OpenBlocks;
import openblocks.common.block.BlockBlockBreaker;
import openmods.api.INeighbourAwareTile;
import openmods.fakeplayer.BreakBlockAction;
import openmods.fakeplayer.FakePlayerPool;
import openmods.include.IncludeInterface;
import openmods.inventory.GenericInventory;
import openmods.inventory.legacy.ItemDistribution;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.BlockNotifyFlags;

public class TileEntityBlockBreaker extends OpenTileEntity implements INeighbourAwareTile {

	private static final int EVENT_ACTIVATE = 3;

	// DON'T remove this object, even though it seems unused. Without it Builcraft pipes won't connect. -B
	@IncludeInterface(IInventory.class)
	private final GenericInventory inventory = registerInventoryCallback(new GenericInventory("blockbreaker", true, 1) {
		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			return false;
		}
	});

	public TileEntityBlockBreaker() {}

	@Override
	public void onNeighbourChanged(Block block) {
		if (!worldObj.isRemote) {
			final IBlockState state = worldObj.getBlockState(getPos());
			if (state.getBlock() instanceof BlockBlockBreaker) {
				final boolean isPowered = worldObj.isBlockIndirectlyGettingPowered(pos) > 0;

				final IBlockState newState = state.withProperty(BlockBlockBreaker.POWERED, isPowered);
				if (newState != state) {
					worldObj.setBlockState(getPos(), newState, BlockNotifyFlags.SEND_TO_CLIENTS);
					playSoundAtBlock(isPowered? SoundEvents.BLOCK_PISTON_EXTEND : SoundEvents.BLOCK_PISTON_CONTRACT, 0.5F, worldObj.rand.nextFloat() * 0.15F + 0.6F);
				}

				if (isPowered)
					triggerBreakBlock(newState);
			}
		}
	}

	private boolean canBreakBlock(IBlockState state, BlockPos pos) {
		final Block block = state.getBlock();
		return !block.isAir(state, worldObj, pos) && block != Blocks.BEDROCK && state.getBlockHardness(worldObj, pos) > -1.0F;
	}

	private void triggerBreakBlock(IBlockState newState) {
		final EnumFacing direction = OpenBlocks.Blocks.blockBreaker.getFront(newState);
		final BlockPos target = pos.offset(direction);

		if (worldObj.isBlockLoaded(target)) {
			final IBlockState state = worldObj.getBlockState(target);
			if (canBreakBlock(state, target)) sendBlockEvent(EVENT_ACTIVATE, 0);
		}
	}

	@Override
	public boolean receiveClientEvent(int event, int param) {
		if (event == EVENT_ACTIVATE) {
			breakBlock();
			return true;
		}

		return false;
	}

	private void breakBlock() {
		if (!(worldObj instanceof WorldServer)) return;

		final IBlockState state = worldObj.getBlockState(getPos());
		if (!(state.getBlock() instanceof BlockBlockBreaker)) return;
		final EnumFacing direction = OpenBlocks.Blocks.blockBreaker.getFront(state);
		final BlockPos target = pos.offset(direction);

		if (!worldObj.isBlockLoaded(target)) return;

		final IBlockState blockState = worldObj.getBlockState(target);
		if (!canBreakBlock(blockState, target)) return;

		final List<EntityItem> drops = FakePlayerPool.instance.executeOnPlayer((WorldServer)worldObj, new BreakBlockAction(worldObj, target));
		tryInjectItems(drops, direction.getOpposite());
	}

	private void tryInjectItems(List<EntityItem> drops, EnumFacing direction) {
		TileEntity targetInventory = getTileInDirection(direction);
		if (targetInventory == null) return;

		for (EntityItem drop : drops) {
			ItemStack stack = drop.getEntityItem();
			ItemDistribution.insertItemInto(stack, targetInventory, direction, true);

			if (stack.stackSize <= 0) drop.setDead();
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);
		inventory.writeToNBT(tag);

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag);

	}
}
