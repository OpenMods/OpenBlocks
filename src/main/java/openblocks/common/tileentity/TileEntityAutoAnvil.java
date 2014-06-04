package openblocks.common.tileentity;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiAutoAnvil;
import openblocks.common.container.ContainerAutoAnvil;
import openmods.GenericInventory;
import openmods.IInventoryProvider;
import openmods.api.IHasGui;
import openmods.include.IExtendable;
import openmods.include.IncludeInterface;
import openmods.include.IncludeOverride;
import openmods.liquids.SidedFluidHandler;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableFlags;
import openmods.sync.SyncableTank;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.EnchantmentUtils;
import openmods.utils.InventoryUtils;
import openmods.utils.SidedInventoryAdapter;

public class TileEntityAutoAnvil extends SyncedTileEntity implements IHasGui, IInventoryProvider, IExtendable {

	protected static final int TOTAL_COOLDOWN = 40;
	protected static final int TANK_CAPACITY = EnchantmentUtils.getLiquidForLevel(45);

	protected int cooldown = 0;

	/**
	 * The 3 slots in the inventory
	 */
	public enum Slots {
		tool,
		modifier,
		output
	}

	/**
	 * The keys of the things that can be auto injected/extracted
	 */
	public enum AutoSlots {
		tool,
		modifier,
		output,
		xp
	}

	/**
	 * The shared/syncable objects
	 */
	private SyncableFlags toolSides;
	private SyncableFlags modifierSides;
	private SyncableFlags outputSides;
	private SyncableFlags xpSides;
	private SyncableTank tank;
	private SyncableFlags automaticSlots;

	private final GenericInventory inventory = new GenericInventory("autoanvil", true, 3) {
		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			if (i == 0 && (!itemstack.getItem().isItemTool(itemstack) && itemstack.getItem() != Items.enchanted_book)) { return false; }
			if (i == 2) { return false; }
			return super.isItemValidForSlot(i, itemstack);
		}
	};

	@IncludeInterface(ISidedInventory.class)
	private final SidedInventoryAdapter slotSides = new SidedInventoryAdapter(inventory);

	@IncludeInterface
	private final IFluidHandler tankWrapper = new SidedFluidHandler.Drain(xpSides, tank);

	public TileEntityAutoAnvil() {
		slotSides.registerSlot(Slots.tool, toolSides, true, false);
		slotSides.registerSlot(Slots.modifier, modifierSides, true, false);
		slotSides.registerSlot(Slots.output, outputSides, false, true);
	}

	@Override
	protected void createSyncedFields() {
		toolSides = new SyncableFlags();
		modifierSides = new SyncableFlags();
		outputSides = new SyncableFlags();
		xpSides = new SyncableFlags();
		tank = new SyncableTank(TANK_CAPACITY, OpenBlocks.XP_FLUID);
		automaticSlots = new SyncableFlags();
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (!worldObj.isRemote) {
			// if we should auto-drink liquid, do it!
			if (automaticSlots.get(AutoSlots.xp)) {
				tank.fillFromSides(100, worldObj, getPosition(), xpSides);
			}

			if (shouldAutoOutput() && hasOutput()) {
				InventoryUtils.moveItemsToOneOfSides(this, Slots.output, 1, outputSides);
			}

			// if we should auto input the tool and we don't currently have one
			if (shouldAutoInputTool() && !hasTool()) {
				InventoryUtils.moveItemsFromOneOfSides(this, null, 1, Slots.tool, toolSides);
			}

			// if we should auto input the modifier
			if (shouldAutoInputModifier()) {
				InventoryUtils.moveItemsFromOneOfSides(this, null, 1, Slots.modifier, modifierSides);
			}

			if (cooldown == 0) {
				int liquidRequired = updateRepairOutput(false);
				if (liquidRequired > 0
						&& tank.getFluidAmount() >= liquidRequired) {
					liquidRequired = updateRepairOutput(true);
					worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "random.anvil_use", 0.3f, 1f);
					cooldown = TOTAL_COOLDOWN;
				}
			} else if (cooldown > 0) {
				cooldown--;
			}
		}
	}

	@Override
	public boolean canOpenGui(EntityPlayer player) {
		return true;
	}

	@Override
	public Object getServerGui(EntityPlayer player) {
		return new ContainerAutoAnvil(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiAutoAnvil(new ContainerAutoAnvil(player.inventory, this));
	}

	/**
	 * Get the sides that tools and be inserted from
	 * 
	 * @return
	 */
	public SyncableFlags getToolSides() {
		return toolSides;
	}

	/**
	 * Get the sides that modifiers can be inserted from
	 * 
	 * @return
	 */
	public SyncableFlags getModifierSides() {
		return modifierSides;
	}

	/**
	 * Get the sides that the final product can be extracted from
	 * 
	 * @return
	 */
	public SyncableFlags getOutputSides() {
		return outputSides;
	}

	/**
	 * Get the sides that the XP can be injected from
	 */
	public SyncableFlags getXPSides() {
		return xpSides;
	}

	/**
	 * Get the sides that can be auto extracted/injected/inserted.
	 * This is a syncableFlag and it uses AutoSides as the enum key
	 * 
	 * @return
	 */
	public SyncableFlags getAutomaticSlots() {
		return automaticSlots;
	}

	/**
	 * Returns true if we should auto-pull the modifier
	 * 
	 * @return
	 */
	private boolean shouldAutoInputModifier() {
		return automaticSlots.get(AutoSlots.modifier);
	}

	/**
	 * Should the anvil auto output the resulting item?
	 * 
	 * @return
	 */
	public boolean shouldAutoOutput() {
		return automaticSlots.get(AutoSlots.output);
	}

	/**
	 * Checks if there is a stack in the input slot
	 * 
	 * @return
	 */
	private boolean hasTool() {
		return inventory.getStackInSlot(0) != null;
	}

	/**
	 * Should the anvil auto input the tool into slot 0?
	 * 
	 * @return
	 */
	private boolean shouldAutoInputTool() {
		return automaticSlots.get(AutoSlots.tool);
	}

	/**
	 * Does the anvil have something in slot [2]?
	 * 
	 * @return
	 */
	private boolean hasOutput() {
		return inventory.getStackInSlot(2) != null;
	}

	public int updateRepairOutput(boolean doIt) {
		ItemStack modifierStack = inventory.getStackInSlot(1);
		ItemStack inputStack = inventory.getStackInSlot(0);
		if (inputStack == null || modifierStack == null) return 0;

		Item inputItem = inputStack.getItem();
		Item modifierItem = modifierStack.getItem();

		if (modifierItem == null || inputItem == null) return 0;

		int maximumCost = 0;
		int i = 0;
		byte b0 = 0;

		ItemStack inputStackCopy = inputStack.copy();

		@SuppressWarnings("unchecked")
		Map<Integer, Integer> inputStackEnchantments = EnchantmentHelper.getEnchantments(inputStackCopy);
		int k = b0 + inputStack.getRepairCost() + modifierStack.getRepairCost();
		int stackSizeToBeUsedInRepair = 0;

		final boolean isEnchantedBook = modifierStack.getItem() == Items.enchanted_book
				&& Items.enchanted_book.func_92110_g(modifierStack).tagCount() > 0;

		if (inputStackCopy.isItemStackDamageable()
				&& inputItem.getIsRepairable(inputStack, modifierStack)) {
			int l = Math.min(inputStackCopy.getItemDamageForDisplay(), inputStackCopy.getMaxDamage() / 4);

			if (l <= 0) { return 0; }

			int i1;
			for (i1 = 0; l > 0 && i1 < modifierStack.stackSize; ++i1) {
				int j1 = inputStackCopy.getItemDamageForDisplay() - l;
				inputStackCopy.setItemDamage(j1);
				i += Math.max(1, l / 100) + inputStackEnchantments.size();
				l = Math.min(inputStackCopy.getItemDamageForDisplay(), inputStackCopy.getMaxDamage() / 4);
			}

			stackSizeToBeUsedInRepair = i1;
		} else {
			if (!isEnchantedBook
					&& (inputItem != modifierItem || !inputStackCopy.isItemStackDamageable())) { return 0; }

			if (inputStackCopy.isItemStackDamageable() && !isEnchantedBook) {
				int l = inputStack.getMaxDamage() - inputStack.getItemDamageForDisplay();
				int i1 = modifierStack.getMaxDamage() - modifierStack.getItemDamageForDisplay();
				int j1 = i1 + inputStackCopy.getMaxDamage() * 12 / 100;
				int i2 = l + j1;
				int k1 = inputStackCopy.getMaxDamage() - i2;

				if (k1 < 0) k1 = 0;

				if (k1 < inputStackCopy.getItemDamage()) {
					inputStackCopy.setItemDamage(k1);
					i += Math.max(1, j1 / 100);
				}
			}

			@SuppressWarnings("unchecked")
			Map<Integer, Integer> stackEnch = EnchantmentHelper.getEnchantments(modifierStack);

			for (Map.Entry<Integer, Integer> e : stackEnch.entrySet()) {
				int enchId = e.getKey();
				int enchLevel = e.getValue();
				Enchantment enchantment = Enchantment.enchantmentsList[enchId];
				int k1 = inputStackEnchantments.containsKey(enchId)? inputStackEnchantments.get(enchId) : 0;
				int j2;

				if (k1 == enchLevel) {
					++enchLevel;
					j2 = enchLevel;
				} else {
					j2 = Math.max(enchLevel, k1);
				}

				int l1 = j2;
				int k2 = l1 - k1;
				boolean flag1 = enchantment.canApply(inputStack);

				Iterator<Integer> iterator1 = inputStackEnchantments.keySet().iterator();

				while (iterator1.hasNext()) {
					int l2 = iterator1.next().intValue();

					if (l2 != enchId
							&& !enchantment.canApplyTogether(Enchantment.enchantmentsList[l2])) {
						flag1 = false;
						i += k2;
					}
				}

				if (flag1) {
					if (l1 > enchantment.getMaxLevel()) {
						l1 = enchantment.getMaxLevel();
					}

					inputStackEnchantments.put(enchId, Integer.valueOf(l1));
					int i3 = 0;

					switch (enchantment.getWeight()) {
						case 1:
							i3 = 8;
							break;
						case 2:
							i3 = 4;
						case 3:
						case 4:
						case 6:
						case 7:
						case 8:
						case 9:
						default:
							break;
						case 5:
							i3 = 2;
							break;
						case 10:
							i3 = 1;
					}

					if (isEnchantedBook) {
						i3 = Math.max(1, i3 / 2);
					}

					i += i3 * k2;
				}
			}
		}

		int l = 0;

		for (Map.Entry<Integer, Integer> e : inputStackEnchantments.entrySet()) {
			int enchId = e.getKey();
			int enchLevel = e.getValue();
			Enchantment enchantment = Enchantment.enchantmentsList[enchId];
			int extra = 0;
			++l;

			switch (enchantment.getWeight()) {
				case 1:
					extra = 8;
					break;
				case 2:
					extra = 4;
				case 3:
				case 4:
				case 6:
				case 7:
				case 8:
				case 9:
				default:
					break;
				case 5:
					extra = 2;
					break;
				case 10:
					extra = 1;
			}

			if (isEnchantedBook) extra = Math.max(1, extra / 2);

			k += l + enchLevel * extra;
		}

		if (isEnchantedBook) {
			k = Math.max(1, k / 2);
		}

		if (isEnchantedBook && !inputItem.isBookEnchantable(inputStackCopy, modifierStack)) {
			inputStackCopy = null;
		}

		maximumCost = k + i;

		if (i <= 0) {
			inputStackCopy = null;
		}

		if (inputStackCopy != null) {
			int cost = Math.max(inputStackCopy.getRepairCost(), modifierStack.getRepairCost());
			if (inputStackCopy.hasDisplayName()) cost -= 9;

			if (cost < 0) cost = 0;
			cost += 2;
			inputStackCopy.setRepairCost(cost);

			EnchantmentHelper.setEnchantments(inputStackEnchantments, inputStackCopy);

			int requiredXP = EnchantmentUtils.getExperienceForLevel(maximumCost);
			int requiredLiquid = EnchantmentUtils.XPToLiquidRatio(requiredXP);
			if (tank.getFluidAmount() >= requiredLiquid && doIt) {
				tank.drain(requiredLiquid, true);
				inventory.setInventorySlotContents(0, null);
				if (isEnchantedBook) {
					stackSizeToBeUsedInRepair = 1;
				}
				inventory.decrStackSize(1, Math.max(1, stackSizeToBeUsedInRepair));
				inventory.setInventorySlotContents(2, inputStackCopy);
				return 0;
			}
			return requiredLiquid;
		}

		return 0;

	}

	@IncludeOverride
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {}

	public IFluidTank getTank() {
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
}
