package openblocks.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import openblocks.Config;
import openblocks.client.gui.GuiBlockPlacer;
import openblocks.common.block.BlockBlockManpulatorBase;
import openblocks.common.block.BlockBlockPlacer;
import openblocks.common.container.ContainerBlockPlacer;
import openmods.api.IInventoryCallback;
import openmods.fakeplayer.FakePlayerPool;
import openmods.fakeplayer.UseItemAction;
import openmods.fixers.GenericInventoryTeFixerWalker;
import openmods.fixers.RegisterFixer;
import openmods.inventory.GenericInventory;
import openmods.inventory.IInventoryDelegate;
import openmods.inventory.TileEntityInventory;
import openmods.utils.OptionalInt;

@RegisterFixer(GenericInventoryTeFixerWalker.class)
public class TileEntityBlockPlacer extends TileEntityBlockManipulator implements IHasGui, IInventoryDelegate {

	static final int BUFFER_SIZE = 9;

	private boolean skipActionOnInventoryUpdate = false;

	private final GenericInventory inventory = new TileEntityInventory(this, "blockPlacer", false, BUFFER_SIZE)
			.addCallback(new IInventoryCallback() {
				@Override
				public void onInventoryChanged(IInventory inventory, OptionalInt slotNumber) {
					markUpdated();
					if (!skipActionOnInventoryUpdate && !world.isRemote && isUpdateTriggering(inventory, slotNumber)) {
						final BlockState blockState = world.getBlockState(getPos());
						if (blockState.getBlock() instanceof BlockBlockPlacer &&
								blockState.getValue(BlockBlockManpulatorBase.POWERED))
							triggerBlockAction(blockState);
					}
				}

				private boolean isUpdateTriggering(IInventory inventory, OptionalInt maybeSlotNumber) {
					if (!maybeSlotNumber.isPresent()) return true; // full update, trigger everything
					final int slotNumber = maybeSlotNumber.get();
					return !inventory.getStackInSlot(slotNumber).isEmpty();
				}
			});

	@Override
	protected boolean canWork(BlockState targetState, BlockPos target, Direction direction) {
		if (inventory.isEmpty()) return false;

		final Block block = targetState.getBlock();
		return block.isAir(targetState, world, target) || block.isReplaceable(world, target);
	}

	@Override
	protected void doWork(BlockState targetState, BlockPos target, Direction direction) {
		ItemStack stack = ItemStack.EMPTY;
		int slotId;

		for (slotId = 0; slotId < inventory.getSizeInventory(); slotId++) {
			stack = inventory.getStackInSlot(slotId);
			if (!stack.isEmpty()) break;
		}

		if (stack.isEmpty()) return;

		// this logic is tuned for vanilla blocks (like pistons), which places blocks with front facing player
		// so to place object pointing in the same direction as placer, we need configuration player-target-placer
		// * 2, since some blocks may take into account player height, so distance must be greater than that
		final BlockPos playerPos = target.offset(direction, 2);

		final ItemStack result = FakePlayerPool.instance.executeOnPlayer((ServerWorld)world, new UseItemAction(
				stack,
				new Vec3d(playerPos),
				new Vec3d(target),
				new Vec3d(target).addVector(0.5, 0.5, 0.5),
				direction.getOpposite(),
				Hand.MAIN_HAND));

		if (!ItemStack.areItemStacksEqual(result, stack)) {
			skipActionOnInventoryUpdate = true;
			try {
				inventory.setInventorySlotContents(slotId, result);
			} finally {
				skipActionOnInventoryUpdate = false;
			}
		}
	}

	@Override
	public Object getServerGui(PlayerEntity player) {
		return new ContainerBlockPlacer(player.inventory, this);
	}

	@Override
	public Object getClientGui(PlayerEntity player) {
		return new GuiBlockPlacer(new ContainerBlockPlacer(player.inventory, this));
	}

	@Override
	public boolean canOpenGui(PlayerEntity player) {
		return true;
	}

	@Override
	public IInventory getInventory() {
		return inventory;
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT tag) {
		tag = super.writeToNBT(tag);
		inventory.writeToNBT(tag);
		return tag;
	}

	@Override
	public void readFromNBT(CompoundNBT tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, Direction facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, Direction facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T)inventory.getHandler();

		return super.getCapability(capability, facing);
	}

	@Override
	protected int getActionLimit() {
		return Config.blockPlacerActionLimit;
	}
}
