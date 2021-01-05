package openblocks.common.tileentity;

import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiXPBottler;
import openblocks.common.FluidXpUtils;
import openblocks.common.FluidXpUtils.IFluidXpConverter;
import openblocks.common.container.ContainerXPBottler;
import openblocks.common.tileentity.TileEntityXPBottler.AutoSlots;
import openmods.api.INeighbourAwareTile;
import openmods.api.INeighbourTeAwareTile;
import openmods.api.IValueProvider;
import openmods.api.IValueReceiver;
import openmods.fixers.GenericInventoryTeFixerWalker;
import openmods.fixers.RegisterFixer;
import openmods.gamelogic.WorkerLogic;
import openmods.gui.misc.IConfigurableGuiSlots;
import openmods.inventory.GenericInventory;
import openmods.inventory.ISidedInventoryDelegate;
import openmods.inventory.ItemMover;
import openmods.inventory.TileEntityInventory;
import openmods.liquids.SidedFluidCapabilityWrapper;
import openmods.sync.SyncMap;
import openmods.sync.SyncableFlags;
import openmods.sync.SyncableInt;
import openmods.sync.SyncableSides;
import openmods.sync.SyncableTank;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.MiscUtils;
import openmods.utils.SidedInventoryAdapter;
import openmods.utils.SidedItemHandlerAdapter;
import openmods.utils.bitmap.BitMapUtils;
import openmods.utils.bitmap.IRpcDirectionBitMap;
import openmods.utils.bitmap.IRpcIntBitMap;
import openmods.utils.bitmap.IWriteableBitMap;

@RegisterFixer(GenericInventoryTeFixerWalker.class)
public class TileEntityXPBottler extends SyncedTileEntity implements ISidedInventoryDelegate, IHasGui, IConfigurableGuiSlots<AutoSlots>, INeighbourAwareTile, INeighbourTeAwareTile, ITickable {

	public static final int TANK_CAPACITY = FluidXpUtils.getMaxPossibleFluidForXp(FluidXpUtils.XP_PER_BOTTLE);
	public static final int PROGRESS_TICKS = 40;

	protected static final ItemStack GLASS_BOTTLE = new ItemStack(Items.GLASS_BOTTLE, 1);
	protected static final ItemStack XP_BOTTLE = new ItemStack(Items.EXPERIENCE_BOTTLE, 1);

	private boolean needsTankUpdate;

	public enum Slots {
		input,
		output
	}

	public enum AutoSlots {
		input,
		output,
		xp
	}

	private final GenericInventory inventory = registerInventoryCallback(new TileEntityInventory(this, "xpbottler", true, 2) {
		@Override
		public boolean isItemValidForSlot(int slot, @Nonnull ItemStack itemstack) {
			if (slot != Slots.input.ordinal()) return false;
			return itemstack.getItem() == Items.GLASS_BOTTLE;
		}
	});

	private final SidedInventoryAdapter sided = new SidedInventoryAdapter(inventory);

	private final SidedItemHandlerAdapter itemHandlerCapability = new SidedItemHandlerAdapter(inventory.getHandler());

	private SyncableInt progress;
	private SyncableSides glassSides;
	private SyncableSides xpBottleSides;
	private SyncableSides xpSides;
	private SyncableFlags automaticSlots;
	private SyncableTank tank;

	private final WorkerLogic logic = new WorkerLogic(progress, PROGRESS_TICKS);

	private final SidedFluidCapabilityWrapper tankCapability = SidedFluidCapabilityWrapper.wrap(tank, xpSides, false, true);

	@Override
	protected void createSyncedFields() {
		progress = new SyncableInt();
		glassSides = new SyncableSides();
		xpBottleSides = new SyncableSides();
		xpSides = new SyncableSides();
		automaticSlots = SyncableFlags.create(AutoSlots.values().length);
		tank = new SyncableTank(TANK_CAPACITY, FluidXpUtils.getAcceptedFluids());
	}

	@Override
	protected void onSyncMapCreate(SyncMap syncMap) {
		syncMap.addSyncListener(itemHandlerCapability.createSyncListener());
	}

	public TileEntityXPBottler() {
		sided.registerSlot(Slots.input, glassSides, true, false);
		sided.registerSlot(Slots.output, xpBottleSides, false, true);

		itemHandlerCapability.registerSlot(Slots.input, glassSides, true, false);
		itemHandlerCapability.registerSlot(Slots.output, xpBottleSides, false, true);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, Direction facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return tankCapability.hasHandler(facing);

		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return itemHandlerCapability.hasHandler(facing);

		return super.hasCapability(capability, facing);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, Direction facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return (T)tankCapability.getHandler(facing);

		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T)itemHandlerCapability.getHandler(facing);

		return super.getCapability(capability, facing);
	}

	@Override
	public void update() {
		if (!world.isRemote) {

			if (automaticSlots.get(AutoSlots.xp)) {
				if (needsTankUpdate) {
					tank.updateNeighbours(world, pos);
					needsTankUpdate = false;
				}

				tank.fillFromSides(10, world, pos, xpSides.getValue());
			}

			final ItemMover mover = new ItemMover(world, pos).breakAfterFirstTry().randomizeSides().setMaxSize(1);

			if (shouldAutoOutput() && hasOutputStack()) {
				mover.setSides(xpBottleSides.getValue()).pushFromSlot(inventory.getHandler(), Slots.output.ordinal());
			}

			if (shouldAutoInput() && !hasGlassInInput()) {
				mover.setSides(glassSides.getValue()).pullToSlot(inventory.getHandler(), Slots.input.ordinal());
			}

			logic.checkWorkCondition(hasSpaceInOutput() && hasGlassInInput() && hasEnoughFluid());

			if (logic.update()) {
				playSoundAtBlock(OpenBlocks.Sounds.BLOCK_XPBOTTLER_DONE, 0.5f, 0.8f);
				inventory.decrStackSize(Slots.input.ordinal(), 1);

				final FluidStack tankContents = tank.getFluid();
				final Optional<IFluidXpConverter> maybeConverter = FluidXpUtils.getConverter(tankContents);
				if (maybeConverter.isPresent()) {
					final IFluidXpConverter converter = maybeConverter.get();
					tank.drain(converter.xpToFluid(FluidXpUtils.XP_PER_BOTTLE), true);
				}

				ItemStack outputStack = inventory.getStackInSlot(Slots.output.ordinal());

				if (outputStack.isEmpty()) {
					inventory.setInventorySlotContents(Slots.output.ordinal(), XP_BOTTLE.copy());
				} else {
					outputStack.grow(1);
				}

				inventory.onInventoryChanged(Slots.output.ordinal());
			}

			sync();
		}
	}

	@Override
	public Object getServerGui(PlayerEntity player) {
		return new ContainerXPBottler(player.inventory, this);
	}

	@Override
	public Object getClientGui(PlayerEntity player) {
		return new GuiXPBottler(new ContainerXPBottler(player.inventory, this));
	}

	@Override
	public boolean canOpenGui(PlayerEntity player) {
		return true;
	}

	public IValueProvider<Integer> getProgress() {
		return progress;
	}

	public boolean hasOutputStack() {
		ItemStack outputStack = inventory.getStackInSlot(1);
		return !outputStack.isEmpty();
	}

	public boolean shouldAutoInput() {
		return automaticSlots.get(AutoSlots.input);
	}

	public boolean shouldAutoOutput() {
		return automaticSlots.get(AutoSlots.output);
	}

	public boolean hasGlassInInput() {
		ItemStack inputStack = inventory.getStackInSlot(Slots.input.ordinal());
		return !inputStack.isEmpty() && inputStack.isItemEqual(GLASS_BOTTLE);
	}

	public boolean hasSpaceInOutput() {
		ItemStack outputStack = inventory.getStackInSlot(Slots.output.ordinal());
		return outputStack.isEmpty() || (outputStack.isItemEqual(XP_BOTTLE) && outputStack.getCount() < outputStack.getMaxStackSize());
	}

	private boolean hasEnoughFluid() {
		final FluidStack contents = tank.getFluid();
		final Optional<IFluidXpConverter> converter = FluidXpUtils.getConverter(contents);
		return converter.isPresent() && converter.get().fluidToXp(contents.amount) >= FluidXpUtils.XP_PER_BOTTLE;
	}

	public IValueProvider<FluidStack> getFluidProvider() {
		return tank;
	}

	@Override
	public ISidedInventory getInventory() {
		return sided;
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

	private SyncableSides selectSlotMap(AutoSlots slot) {
		switch (slot) {
			case input:
				return glassSides;
			case output:
				return xpBottleSides;
			case xp:
				return xpSides;
			default:
				throw MiscUtils.unhandledEnum(slot);
		}
	}

	@Override
	public IValueProvider<Set<Direction>> createAllowedDirectionsProvider(AutoSlots slot) {
		return selectSlotMap(slot);
	}

	@Override
	public IWriteableBitMap<Direction> createAllowedDirectionsReceiver(AutoSlots slot) {
		SyncableSides dirs = selectSlotMap(slot);
		return BitMapUtils.createRpcAdapter(createRpcProxy(dirs, IRpcDirectionBitMap.class));
	}

	@Override
	public IValueProvider<Boolean> createAutoFlagProvider(AutoSlots slot) {
		return BitMapUtils.singleBitProvider(automaticSlots, slot.ordinal());
	}

	@Override
	public IValueReceiver<Boolean> createAutoSlotReceiver(AutoSlots slot) {
		IRpcIntBitMap bits = createRpcProxy(automaticSlots, IRpcIntBitMap.class);
		return BitMapUtils.singleBitReceiver(bits, slot.ordinal());
	}

	@Override
	public void validate() {
		super.validate();
		this.needsTankUpdate = true;
	}

	@Override
	public void onNeighbourChanged(BlockPos neighbourPos, Block neighbourBlock) {
		this.needsTankUpdate = true;
	}

	@Override
	public void onNeighbourTeChanged(BlockPos pos) {
		this.needsTankUpdate = true;
	}
}
