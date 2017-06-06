package openblocks.common.tileentity;

import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiXPBottler;
import openblocks.common.LiquidXpUtils;
import openblocks.common.container.ContainerXPBottler;
import openblocks.common.tileentity.TileEntityXPBottler.AutoSlots;
import openmods.api.IHasGui;
import openmods.api.INeighbourAwareTile;
import openmods.api.INeighbourTeAwareTile;
import openmods.api.IValueProvider;
import openmods.api.IValueReceiver;
import openmods.gamelogic.WorkerLogic;
import openmods.gui.misc.IConfigurableGuiSlots;
import openmods.include.IncludeInterface;
import openmods.inventory.GenericInventory;
import openmods.inventory.IInventoryProvider;
import openmods.inventory.TileEntityInventory;
import openmods.inventory.legacy.ItemDistribution;
import openmods.liquids.SidedFluidCapabilityWrapper;
import openmods.sync.SyncableFlags;
import openmods.sync.SyncableInt;
import openmods.sync.SyncableSides;
import openmods.sync.SyncableTank;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.MiscUtils;
import openmods.utils.SidedInventoryAdapter;
import openmods.utils.bitmap.BitMapUtils;
import openmods.utils.bitmap.IRpcDirectionBitMap;
import openmods.utils.bitmap.IRpcIntBitMap;
import openmods.utils.bitmap.IWriteableBitMap;

public class TileEntityXPBottler extends SyncedTileEntity implements IInventoryProvider, IHasGui, IConfigurableGuiSlots<AutoSlots>, INeighbourAwareTile, INeighbourTeAwareTile, ITickable {

	public static final int TANK_CAPACITY = LiquidXpUtils.xpToLiquidRatio(LiquidXpUtils.XP_PER_BOTTLE);
	public static final int PROGRESS_TICKS = 40;

	protected static final ItemStack GLASS_BOTTLE = new ItemStack(Items.GLASS_BOTTLE, 1);
	protected static final ItemStack XP_BOTTLE = new ItemStack(Items.EXPERIENCE_BOTTLE, 1);

	private boolean needsTankUpdate;

	public static enum Slots {
		input,
		output
	}

	public static enum AutoSlots {
		input,
		output,
		xp
	}

	private final GenericInventory inventory = registerInventoryCallback(new TileEntityInventory(this, "xpbottler", true, 2) {
		@Override
		public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
			if (slot != Slots.input.ordinal()) return false;
			return itemstack.getItem() == Items.GLASS_BOTTLE;
		}
	});

	@IncludeInterface(ISidedInventory.class)
	private final SidedInventoryAdapter sided = new SidedInventoryAdapter(inventory);

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
		tank = new SyncableTank(TANK_CAPACITY, OpenBlocks.Fluids.xpJuice);
	}

	public TileEntityXPBottler() {
		sided.registerSlot(Slots.input, glassSides, true, false);
		sided.registerSlot(Slots.output, xpBottleSides, false, true);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return tankCapability.hasHandler(facing);

		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return (T)tankCapability.getHandler(facing);

		return null;
	}

	@Override
	public void update() {
		if (!worldObj.isRemote) {

			if (automaticSlots.get(AutoSlots.xp)) {
				if (needsTankUpdate) {
					tank.updateNeighbours(worldObj, pos);
					needsTankUpdate = false;
				}

				tank.fillFromSides(10, worldObj, pos, xpSides.getValue());
			}

			if (shouldAutoOutput() && hasOutputStack()) {
				ItemDistribution.moveItemsToOneOfSides(this, inventory, Slots.output.ordinal(), 1, xpBottleSides.getValue(), true);
			}

			if (shouldAutoInput() && !hasGlassInInput()) {
				ItemDistribution.moveItemsFromOneOfSides(this, inventory, GLASS_BOTTLE, 1, Slots.input.ordinal(), glassSides.getValue(), true);
			}

			logic.checkWorkCondition(hasSpaceInOutput() && hasGlassInInput() && isTankFull());

			if (logic.update()) {
				playSoundAtBlock(OpenBlocks.Sounds.BLOCK_XPBOTTLER_DONE, 0.5f, 0.8f);
				inventory.decrStackSize(Slots.input.ordinal(), 1);
				tank.setFluid(null);

				ItemStack outputStack = inventory.getStackInSlot(Slots.output.ordinal());

				if (outputStack == null) {
					inventory.setInventorySlotContents(Slots.output.ordinal(), XP_BOTTLE.copy());
				} else {
					outputStack.stackSize++;
				}

				inventory.onInventoryChanged(Slots.output.ordinal());
			}

			sync();
		}
	}

	@Override
	public Object getServerGui(EntityPlayer player) {
		return new ContainerXPBottler(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiXPBottler(new ContainerXPBottler(player.inventory, this));
	}

	@Override
	public boolean canOpenGui(EntityPlayer player) {
		return true;
	}

	public IValueProvider<Integer> getProgress() {
		return progress;
	}

	public boolean hasOutputStack() {
		ItemStack outputStack = inventory.getStackInSlot(1);
		return outputStack != null && outputStack.stackSize > 0;
	}

	public boolean shouldAutoInput() {
		return automaticSlots.get(AutoSlots.input);
	}

	public boolean shouldAutoOutput() {
		return automaticSlots.get(AutoSlots.output);
	}

	public boolean hasGlassInInput() {
		ItemStack inputStack = inventory.getStackInSlot(Slots.input.ordinal());
		return inputStack != null && inputStack.isItemEqual(GLASS_BOTTLE);
	}

	public boolean hasSpaceInOutput() {
		ItemStack outputStack = inventory.getStackInSlot(Slots.output.ordinal());
		return outputStack == null
				|| (outputStack.isItemEqual(XP_BOTTLE) && outputStack.stackSize < outputStack.getMaxStackSize());
	}

	public boolean isTankFull() {
		return tank.getFluidAmount() == tank.getCapacity();
	}

	public IValueProvider<FluidStack> getFluidProvider() {
		return tank;
	}

	@Override
	public IInventory getInventory() {
		return inventory;
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
	public IValueProvider<Set<EnumFacing>> createAllowedDirectionsProvider(AutoSlots slot) {
		return selectSlotMap(slot);
	}

	@Override
	public IWriteableBitMap<EnumFacing> createAllowedDirectionsReceiver(AutoSlots slot) {
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
	public void onNeighbourChanged(Block block) {
		this.needsTankUpdate = true;
	}

	@Override
	public void onNeighbourTeChanged(BlockPos pos) {
		this.needsTankUpdate = true;
	}
}
