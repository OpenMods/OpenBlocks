package openblocks.common.tileentity;

import java.util.Random;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiAutoEnchantmentTable;
import openblocks.common.container.ContainerAutoEnchantmentTable;
import openblocks.common.tileentity.TileEntityAutoEnchantmentTable.AutoSlots;
import openmods.GenericInventory;
import openmods.IInventoryProvider;
import openmods.api.IHasGui;
import openmods.api.IValueProvider;
import openmods.api.IValueReceiver;
import openmods.gui.misc.IConfigurableGuiSlots;
import openmods.include.IExtendable;
import openmods.include.IncludeInterface;
import openmods.include.IncludeOverride;
import openmods.liquids.SidedFluidHandler;
import openmods.sync.*;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.*;
import openmods.utils.bitmap.*;

public class TileEntityAutoEnchantmentTable extends SyncedTileEntity implements IInventoryProvider, IHasGui, IExtendable, IConfigurableGuiSlots<AutoSlots> {

	public static final int TANK_CAPACITY = EnchantmentUtils.getLiquidForLevel(30);

	public static enum Slots {
		input,
		output
	}

	public static enum AutoSlots {
		input,
		output,
		xp
	}

	private SyncableTank tank;
	private SyncableDirs inputSides;
	private SyncableDirs outputSides;
	private SyncableDirs xpSides;
	private SyncableInt targetLevel;
	private SyncableFlags automaticSlots;

	private final GenericInventory inventory = new GenericInventory("autoenchant", true, 2) {
		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			if (i == Slots.input.ordinal()) { return !itemstack.isItemEnchanted(); }
			return i == Slots.input.ordinal();
		}
	};

	@IncludeInterface(ISidedInventory.class)
	private final SidedInventoryAdapter slotSides = new SidedInventoryAdapter(inventory);

	@IncludeInterface
	private final IFluidHandler tankWrapper = new SidedFluidHandler.Drain(xpSides, tank);

	/**
	 * grotesque book turning stuff taken from the main enchantment table
	 */
	public int tickCount;
	public float pageFlip;
	public float pageFlipPrev;
	public float field_70373_d;
	public float field_70374_e;
	public float bookSpread;
	public float bookSpreadPrev;
	public float bookRotation2;
	public float bookRotationPrev;
	public float bookRotation;
	private static Random rand = new Random();

	public TileEntityAutoEnchantmentTable() {
		slotSides.registerSlot(Slots.input, inputSides, true, false);
		slotSides.registerSlot(Slots.output, outputSides, false, true);
	}

	@Override
	protected void createSyncedFields() {
		tank = new SyncableTank(TANK_CAPACITY, OpenBlocks.XP_FLUID);
		inputSides = new SyncableDirs();
		outputSides = new SyncableDirs();
		xpSides = new SyncableDirs();
		targetLevel = new SyncableInt(1);
		automaticSlots = SyncableFlags.create(AutoSlots.values().length);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		handleBookRotation();
		if (!worldObj.isRemote) {

			if (automaticSlots.get(AutoSlots.xp)) {
				tank.fillFromSides(80, worldObj, getPosition(), xpSides.getValue());
			}

			if (shouldAutoOutput() && hasStack(Slots.output)) {
				InventoryUtils.moveItemsToOneOfSides(this, inventory, Slots.output.ordinal(), 1, outputSides.getValue());
			}

			// if we should auto input the tool and we don't currently have one
			if (shouldAutoInput() && !hasStack(Slots.input)) {
				InventoryUtils.moveItemsFromOneOfSides(this, inventory, 1, Slots.input.ordinal(), inputSides.getValue());
			}

			if (hasStack(Slots.input)
					&& inventory.isItemValidForSlot(Slots.input.ordinal(), getStack(Slots.input))
					&& !hasStack(Slots.output)) {
				int xpRequired = EnchantmentUtils.getLiquidForLevel(targetLevel.get());
				if (xpRequired > 0 && tank.getFluidAmount() >= xpRequired) {
					double power = EnchantmentUtils.getPower(worldObj, xCoord, yCoord, zCoord);
					int enchantability = EnchantmentUtils.calcEnchantability(getStack(Slots.input), (int)power, true);
					if (enchantability >= targetLevel.get()) {
						ItemStack inputStack = getStack(Slots.input);
						if (inputStack == null) return;
						ItemStack resultingStack = inputStack.copy();
						resultingStack.stackSize = 1;
						if (EnchantmentUtils.enchantItem(resultingStack, targetLevel.get(), worldObj.rand)) {
							tank.drain(xpRequired, true);
							inputStack.stackSize--;
							if (inputStack.stackSize < 1) {
								setStack(Slots.input, null);
							}
							setStack(Slots.output, resultingStack);
						}
					}
				}
			}
		}
	}

	private void handleBookRotation() {
		this.bookSpreadPrev = this.bookSpread;
		this.bookRotationPrev = this.bookRotation2;
		EntityPlayer entityplayer = this.worldObj.getClosestPlayer(this.xCoord + 0.5F, this.yCoord + 0.5F, this.zCoord + 0.5F, 3.0D);

		if (entityplayer != null) {
			double d0 = entityplayer.posX - (this.xCoord + 0.5F);
			double d1 = entityplayer.posZ - (this.zCoord + 0.5F);
			this.bookRotation = (float)Math.atan2(d1, d0);
			this.bookSpread += 0.1F;

			if (this.bookSpread < 0.5F || rand.nextInt(40) == 0) {
				float f = this.field_70373_d;

				do {
					this.field_70373_d += rand.nextInt(4) - rand.nextInt(4);
				} while (f == this.field_70373_d);
			}
		} else {
			this.bookRotation += 0.02F;
			this.bookSpread -= 0.1F;
		}

		while (this.bookRotation2 >= (float)Math.PI) {
			this.bookRotation2 -= ((float)Math.PI * 2F);
		}

		while (this.bookRotation2 < -(float)Math.PI) {
			this.bookRotation2 += ((float)Math.PI * 2F);
		}

		while (this.bookRotation >= (float)Math.PI) {
			this.bookRotation -= ((float)Math.PI * 2F);
		}

		while (this.bookRotation < -(float)Math.PI) {
			this.bookRotation += ((float)Math.PI * 2F);
		}

		float f1 = this.bookRotation - this.bookRotation2;

		while (f1 >= (float)Math.PI)
			f1 -= ((float)Math.PI * 2F);

		while (f1 < -(float)Math.PI) {
			f1 += ((float)Math.PI * 2F);
		}

		this.bookRotation2 += f1 * 0.4F;

		if (this.bookSpread < 0.0F) {
			this.bookSpread = 0.0F;
		}

		if (this.bookSpread > 1.0F) {
			this.bookSpread = 1.0F;
		}

		++this.tickCount;
		this.pageFlipPrev = this.pageFlip;
		float f2 = (this.field_70373_d - this.pageFlip) * 0.4F;
		float f3 = 0.2F;

		if (f2 < -f3) {
			f2 = -f3;
		}

		if (f2 > f3) {
			f2 = f3;
		}

		this.field_70374_e += (f2 - this.field_70374_e) * 0.9F;
		this.pageFlip += this.field_70374_e;
	}

	private boolean shouldAutoInput() {
		return automaticSlots.get(AutoSlots.input);
	}

	private boolean shouldAutoOutput() {
		return automaticSlots.get(AutoSlots.output);
	}

	public boolean hasStack(Enum<?> slot) {
		return getStack(slot) != null;
	}

	public SyncableInt getTargetLevel() {
		return targetLevel;
	}

	public void setStack(Enum<?> slot, ItemStack stack) {
		inventory.setInventorySlotContents(slot.ordinal(), stack);
	}

	public ItemStack getStack(Enum<?> slot) {
		return inventory.getStackInSlot(slot);
	}

	@Override
	public Object getServerGui(EntityPlayer player) {
		return new ContainerAutoEnchantmentTable(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiAutoEnchantmentTable(new ContainerAutoEnchantmentTable(player.inventory, this));
	}

	@Override
	public boolean canOpenGui(EntityPlayer player) {
		return true;
	}

	@IncludeOverride
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	public IValueProvider<FluidStack> getFluidProvider() {
		return tank;
	}

	@Override
	public IInventory getInventory() {
		return slotSides;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		inventory.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag);
	}

	private SyncableDirs selectSlotMap(AutoSlots slot) {
		switch (slot) {
			case input:
				return inputSides;
			case output:
				return outputSides;
			case xp:
				return xpSides;
			default:
				throw MiscUtils.unhandledEnum(slot);
		}
	}

	@Override
	public IValueProvider<Set<ForgeDirection>> createAllowedDirectionsProvider(AutoSlots slot) {
		return selectSlotMap(slot);
	}

	@Override
	public IWriteableBitMap<ForgeDirection> createAllowedDirectionsReceiver(AutoSlots slot) {
		SyncableDirs dirs = selectSlotMap(slot);
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

}
