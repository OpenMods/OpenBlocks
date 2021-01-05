package openblocks.common.tileentity;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.oredict.OreDictionary;
import openblocks.client.gui.GuiAutoEnchantmentTable;
import openblocks.common.FluidXpUtils;
import openblocks.common.FluidXpUtils.IFluidXpConverter;
import openblocks.common.container.ContainerAutoEnchantmentTable;
import openblocks.common.tileentity.TileEntityAutoEnchantmentTable.AutoSlots;
import openblocks.rpc.ILevelChanger;
import openmods.api.INeighbourAwareTile;
import openmods.api.IValueProvider;
import openmods.api.IValueReceiver;
import openmods.fixers.GenericInventoryTeFixerWalker;
import openmods.fixers.RegisterFixer;
import openmods.gui.misc.IConfigurableGuiSlots;
import openmods.inventory.GenericInventory;
import openmods.inventory.ISidedInventoryDelegate;
import openmods.inventory.ItemMover;
import openmods.inventory.TileEntityInventory;
import openmods.liquids.SidedFluidCapabilityWrapper;
import openmods.sync.SyncMap;
import openmods.sync.SyncableEnum;
import openmods.sync.SyncableFlags;
import openmods.sync.SyncableInt;
import openmods.sync.SyncableSides;
import openmods.sync.SyncableTank;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.EnchantmentUtils;
import openmods.utils.MiscUtils;
import openmods.utils.SidedInventoryAdapter;
import openmods.utils.SidedItemHandlerAdapter;
import openmods.utils.VanillaEnchantLogic;
import openmods.utils.VanillaEnchantLogic.Level;
import openmods.utils.bitmap.BitMapUtils;
import openmods.utils.bitmap.IRpcDirectionBitMap;
import openmods.utils.bitmap.IRpcIntBitMap;
import openmods.utils.bitmap.IWriteableBitMap;

@RegisterFixer(GenericInventoryTeFixerWalker.class)
public class TileEntityAutoEnchantmentTable extends SyncedTileEntity implements ISidedInventoryDelegate, IHasGui, IConfigurableGuiSlots<AutoSlots>, ILevelChanger, INeighbourAwareTile, ITickable {

	private static final String TAG_SEED = "Seed";

	public static final int MAX_STORED_LEVELS = 30;
	public static final int TANK_CAPACITY = FluidXpUtils.getMaxPossibleFluidForLevel(MAX_STORED_LEVELS);

	public enum Slots {
		tool,
		output,
		lapis
	}

	public enum AutoSlots {
		toolInput,
		lapisInput,
		output,
		xp
	}

	private SyncableTank tank;
	private SyncableSides inputSides;
	private SyncableSides lapisSides;
	private SyncableSides outputSides;
	private SyncableSides xpSides;
	private SyncableFlags automaticSlots;

	private SyncableInt powerLimit;
	private SyncableInt availablePower;
	private SyncableEnum<VanillaEnchantLogic.Level> selectedLevel;

	private long seed;

	private static final int POWER_CHECK_PERIOD = 20;

	private int powerCheckCountdown = 0;

	private boolean needsTankUpdate;

	private final GenericInventory inventory = new TileEntityInventory(this, "autoenchant", true, 3) {
		final List<ItemStack> lapis = OreDictionary.getOres("gemLapis");

		@Override
		public boolean isItemValidForSlot(int slot, @Nonnull ItemStack itemstack) {
			if (slot == Slots.tool.ordinal()) return itemstack.isItemEnchantable();
			if (slot == Slots.lapis.ordinal()) {
				for (ItemStack ore : lapis)
					if (OreDictionary.itemMatches(ore, itemstack, false)) return true;

				return false;
			}
			return false;
		}
	};

	private final SidedInventoryAdapter slotSides = new SidedInventoryAdapter(inventory);

	private final SidedFluidCapabilityWrapper tankCapability = SidedFluidCapabilityWrapper.wrap(tank, xpSides, false, true);

	private final SidedItemHandlerAdapter itemHandlerCapability = new SidedItemHandlerAdapter(inventory.getHandler());

	private static final Random bookRand = new Random();

	private static final Random seedGenerator = new Random();

	/**
	 * grotesque book turning stuff taken from the main enchantment table
	 */
	public class BookState {

		public int tickCount;
		public float pageFlip;
		public float pageFlipPrev;
		public float flipT;
		public float flipA;
		public float bookSpread;
		public float bookSpreadPrev;
		public float bookRotation;
		public float bookRotationPrev;
		public float tRot;

		public void handleBookRotation() {
			this.bookSpreadPrev = this.bookSpread;
			this.bookRotationPrev = this.bookRotation;
			PlayerEntity entityplayer = world.getClosestPlayer(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 3.0D, false);

			if (entityplayer != null) {
				double d0 = entityplayer.posX - (pos.getX() + 0.5F);
				double d1 = entityplayer.posZ - (pos.getZ() + 0.5F);
				this.tRot = (float)MathHelper.atan2(d1, d0);
				this.bookSpread += 0.1F;

				if (this.bookSpread < 0.5F || bookRand.nextInt(40) == 0) {
					float f1 = this.flipT;

					while (true) {
						this.flipT += bookRand.nextInt(4) - bookRand.nextInt(4);

						if (f1 != this.flipT) {
							break;
						}
					}
				}
			} else {
				this.tRot += 0.02F;
				this.bookSpread -= 0.1F;
			}

			while (this.bookRotation >= (float)Math.PI) {
				this.bookRotation -= ((float)Math.PI * 2F);
			}

			while (this.bookRotation < -(float)Math.PI) {
				this.bookRotation += ((float)Math.PI * 2F);
			}

			while (this.tRot >= (float)Math.PI) {
				this.tRot -= ((float)Math.PI * 2F);
			}

			while (this.tRot < -(float)Math.PI) {
				this.tRot += ((float)Math.PI * 2F);
			}

			float f2 = this.tRot - this.bookRotation;

			while (f2 >= (float)Math.PI) {
				f2 -= ((float)Math.PI * 2F);
			}

			while (f2 < -(float)Math.PI) {
				f2 += ((float)Math.PI * 2F);
			}

			this.bookRotation += f2 * 0.4F;
			this.bookSpread = MathHelper.clamp(this.bookSpread, 0.0F, 1.0F);
			++this.tickCount;
			this.pageFlipPrev = this.pageFlip;
			float f = (this.flipT - this.pageFlip) * 0.4F;
			f = MathHelper.clamp(f, -0.2F, 0.2F);
			this.flipA += (f - this.flipA) * 0.9F;
			this.pageFlip += this.flipA;
		}
	}

	public final BookState bookState = new BookState();

	public TileEntityAutoEnchantmentTable() {
		slotSides.registerSlot(Slots.tool, inputSides, true, false);
		slotSides.registerSlot(Slots.lapis, lapisSides, true, false);
		slotSides.registerSlot(Slots.output, outputSides, false, true);

		itemHandlerCapability.registerSlot(Slots.tool, inputSides, true, false);
		itemHandlerCapability.registerSlot(Slots.lapis, lapisSides, true, false);
		itemHandlerCapability.registerSlot(Slots.output, outputSides, false, true);

		this.seed = seedGenerator.nextLong();
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
	protected void createSyncedFields() {
		tank = new SyncableTank(TANK_CAPACITY, FluidXpUtils.getAcceptedFluids());
		inputSides = new SyncableSides();
		outputSides = new SyncableSides();
		xpSides = new SyncableSides();
		lapisSides = new SyncableSides();
		powerLimit = new SyncableInt(1);
		availablePower = new SyncableInt();
		selectedLevel = new SyncableEnum<>(VanillaEnchantLogic.Level.L1);
		automaticSlots = SyncableFlags.create(AutoSlots.values().length);
	}

	@Override
	protected void onSyncMapCreate(SyncMap syncMap) {
		syncMap.addSyncListener(itemHandlerCapability.createSyncListener());
	}

	@Override
	public void update() {
		bookState.handleBookRotation();

		if (!world.isRemote) {
			if (automaticSlots.get(AutoSlots.xp)) {
				if (needsTankUpdate) {
					tank.updateNeighbours(world, pos);
					needsTankUpdate = false;
				}

				tank.fillFromSides(80, world, pos, xpSides.getValue());
			}

			if (powerCheckCountdown-- <= 0) {
				powerCheckCountdown = POWER_CHECK_PERIOD;
				final int power = (int)EnchantmentUtils.getPower(world, getPos());
				availablePower.set(power);
			}

			final ItemMover mover = new ItemMover(world, pos).breakAfterFirstTry().randomizeSides().setMaxSize(1);

			if (shouldAutoOutput() && hasStack(Slots.output)) {
				mover.setSides(outputSides.getValue()).pushFromSlot(inventory.getHandler(), Slots.output.ordinal());
			}

			if (shouldAutoInputTool() && hasSpace(Slots.tool)) {
				mover.setSides(inputSides.getValue()).pullToSlot(inventory.getHandler(), Slots.tool.ordinal());
			}

			if (shouldAutoInputLapis() && hasSpace(Slots.lapis)) {
				mover.setSides(lapisSides.getValue()).pullToSlot(inventory.getHandler(), Slots.lapis.ordinal());
			}

			tryEnchantItem();

			sync();
		}
	}

	private void tryEnchantItem() {
		final ItemStack tool = getStack(Slots.tool);
		if (tool.isEmpty() || !tool.isItemEnchantable()) return;

		final ItemStack lapis = getStack(Slots.lapis);
		if (lapis.isEmpty()) return;

		if (hasStack(Slots.output)) return;

		final int power = Math.min(availablePower.get(), powerLimit.get());
		if (power <= 0) return;

		final VanillaEnchantLogic logic = new VanillaEnchantLogic(seed);
		if (!logic.setup(tool, selectedLevel.get(), power)) return;

		if (lapis.getCount() < logic.getLapisCost()) return;

		final int levelsRequirement = logic.getLevelRequirement();
		final FluidStack contents = tank.getFluid();
		final Optional<IFluidXpConverter> maybeConverter = FluidXpUtils.getConverter(contents);
		if (!maybeConverter.isPresent()) return;

		final IFluidXpConverter converter = maybeConverter.get();
		final int availableXp = converter.fluidToXp(contents.amount);
		final int availableLevels = EnchantmentUtils.getLevelForExperience(availableXp);
		if (availableLevels < levelsRequirement) return;

		final int xpCost = EnchantmentUtils.getExperienceForLevel(levelsRequirement) - EnchantmentUtils.getExperienceForLevel(levelsRequirement - logic.getLevelCost());
		final int liquidXpCost = converter.xpToFluid(xpCost);
		final FluidStack drainedXp = tank.drain(liquidXpCost, false);
		if (drainedXp == null || drainedXp.amount < xpCost) return;

		setStack(Slots.output, logic.enchant());

		setStack(Slots.tool, ItemStack.EMPTY);
		decrementStack(Slots.lapis, logic.getLapisCost());
		tank.drain(liquidXpCost, true);

		this.seed = seedGenerator.nextLong();
	}

	private boolean shouldAutoInputLapis() {
		return automaticSlots.get(AutoSlots.lapisInput);
	}

	private boolean shouldAutoInputTool() {
		return automaticSlots.get(AutoSlots.toolInput);
	}

	private boolean shouldAutoOutput() {
		return automaticSlots.get(AutoSlots.output);
	}

	private boolean hasStack(Slots slot) {
		return !getStack(slot).isEmpty();
	}

	private boolean hasSpace(Slots slot) {
		final ItemStack stackInSlot = getStack(slot);
		return stackInSlot.getCount() < stackInSlot.getMaxStackSize();
	}

	public void setStack(Slots slot, @Nonnull ItemStack stack) {
		inventory.setInventorySlotContents(slot.ordinal(), stack);
	}

	private void decrementStack(Slots slot, int amount) {
		ItemStack stack = getStack(slot);
		stack.shrink(amount);
		markDirty();
	}

	@Nonnull
	private ItemStack getStack(Slots slot) {
		return inventory.getStackInSlot(slot);
	}

	@Override
	public Object getServerGui(PlayerEntity player) {
		return new ContainerAutoEnchantmentTable(player.inventory, this);
	}

	@Override
	public Object getClientGui(PlayerEntity player) {
		return new GuiAutoEnchantmentTable(new ContainerAutoEnchantmentTable(player.inventory, this));
	}

	@Override
	public boolean canOpenGui(PlayerEntity player) {
		return true;
	}

	public IValueProvider<FluidStack> getFluidProvider() {
		return tank;
	}

	@Override
	public ISidedInventory getInventory() {
		return slotSides;
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT tag) {
		tag = super.writeToNBT(tag);
		inventory.writeToNBT(tag);
		tag.setLong(TAG_SEED, seed);
		return tag;
	}

	@Override
	public void readFromNBT(CompoundNBT tag) {
		super.readFromNBT(tag);
		seed = tag.getLong(TAG_SEED);
		inventory.readFromNBT(tag, false);
	}

	private SyncableSides selectSlotMap(AutoSlots slot) {
		switch (slot) {
			case toolInput:
				return inputSides;
			case output:
				return outputSides;
			case xp:
				return xpSides;
			case lapisInput:
				return lapisSides;
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
	public void changePowerLimit(int powerLimit) {
		this.powerLimit.set(powerLimit);
		sync();
	}

	@Override
	public void changeLevel(Level level) {
		this.selectedLevel.set(level);
		sync();
	}

	public IValueProvider<Integer> getLevelProvider() {
		return powerLimit;
	}

	public IValueProvider<Integer> getAvailablePowerProvider() {
		return availablePower;
	}

	public IValueProvider<Level> getSelectedLevelProvider() {
		return selectedLevel;
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

}
