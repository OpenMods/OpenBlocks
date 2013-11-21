package openblocks.common.tileentity;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.*;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiAutoAnvil;
import openblocks.common.GenericInventory;
import openblocks.common.container.ContainerAutoAnvil;
import openblocks.utils.EnchantmentUtils;
import openblocks.utils.InventoryUtils;
import openblocks.utils.SlotSideHelper;
import openmods.common.api.IActivateAwareTile;
import openmods.common.api.IHasGui;
import openmods.common.tileentity.SyncedTileEntity;
import openmods.network.sync.ISyncableObject;
import openmods.network.sync.SyncableFlags;
import openmods.network.sync.SyncableTank;

public class TileEntityAutoAnvil extends SyncedTileEntity implements
		IActivateAwareTile, ISidedInventory, IFluidHandler, IHasGui {

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

	private SlotSideHelper slotSides = new SlotSideHelper();

	public TileEntityAutoAnvil() {
		setInventory(new GenericInventory("autoanvil", true, 3));

		slotSides.addMapping(Slots.tool, toolSides);
		slotSides.addMapping(Slots.modifier, modifierSides);
		slotSides.addMapping(Slots.output, outputSides);
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
				tank.autoFillFromSides(100, this, xpSides);
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
		ItemStack inputStack = inventory.getStackInSlot(0);
		int maximumCost = 0;
		int i = 0;
		byte b0 = 0;

		if (inputStack == null) { return 0; }

		ItemStack inputStackCopy = inputStack.copy();
		ItemStack modifierStack = inventory.getStackInSlot(1);
		@SuppressWarnings("unchecked")
		Map<Integer, Integer> inputStackEnchantments = EnchantmentHelper.getEnchantments(inputStackCopy);
		boolean flag = false;
		int k = b0 + inputStack.getRepairCost()
				+ (modifierStack == null? 0 : modifierStack.getRepairCost());
		int stackSizeToBeUsedInRepair = 0;
		int l;
		int i1;
		int j1;
		int k1;
		int l1;
		Iterator<Integer> iterator;
		Enchantment enchantment;

		if (modifierStack != null) {
			flag = modifierStack.itemID == Item.enchantedBook.itemID
					&& Item.enchantedBook.func_92110_g(modifierStack).tagCount() > 0;

			if (inputStackCopy.isItemStackDamageable()
					&& Item.itemsList[inputStackCopy.itemID].getIsRepairable(inputStack, modifierStack)) {
				l = Math.min(inputStackCopy.getItemDamageForDisplay(), inputStackCopy.getMaxDamage() / 4);

				if (l <= 0) { return 0; }

				for (i1 = 0; l > 0 && i1 < modifierStack.stackSize; ++i1) {
					j1 = inputStackCopy.getItemDamageForDisplay() - l;
					inputStackCopy.setItemDamage(j1);
					i += Math.max(1, l / 100) + inputStackEnchantments.size();
					l = Math.min(inputStackCopy.getItemDamageForDisplay(), inputStackCopy.getMaxDamage() / 4);
				}

				stackSizeToBeUsedInRepair = i1;
			} else {
				if (!flag
						&& (inputStackCopy.itemID != modifierStack.itemID || !inputStackCopy.isItemStackDamageable())) { return 0; }

				if (inputStackCopy.isItemStackDamageable() && !flag) {
					l = inputStack.getMaxDamage()
							- inputStack.getItemDamageForDisplay();
					i1 = modifierStack.getMaxDamage()
							- modifierStack.getItemDamageForDisplay();
					j1 = i1 + inputStackCopy.getMaxDamage() * 12 / 100;
					int i2 = l + j1;
					k1 = inputStackCopy.getMaxDamage() - i2;

					if (k1 < 0) {
						k1 = 0;
					}

					if (k1 < inputStackCopy.getItemDamage()) {
						inputStackCopy.setItemDamage(k1);
						i += Math.max(1, j1 / 100);
					}
				}

				@SuppressWarnings("unchecked")
				Map<Integer, Integer> map1 = EnchantmentHelper.getEnchantments(modifierStack);
				iterator = map1.keySet().iterator();

				while (iterator.hasNext()) {
					j1 = iterator.next().intValue();
					enchantment = Enchantment.enchantmentsList[j1];
					k1 = inputStackEnchantments.containsKey(Integer.valueOf(j1))? inputStackEnchantments.get(Integer.valueOf(j1)).intValue() : 0;
					l1 = map1.get(Integer.valueOf(j1)).intValue();
					int j2;

					if (k1 == l1) {
						++l1;
						j2 = l1;
					} else {
						j2 = Math.max(l1, k1);
					}

					l1 = j2;
					int k2 = l1 - k1;
					boolean flag1 = enchantment.canApply(inputStack);

					Iterator<Integer> iterator1 = inputStackEnchantments.keySet().iterator();

					while (iterator1.hasNext()) {
						int l2 = iterator1.next().intValue();

						if (l2 != j1
								&& !enchantment.canApplyTogether(Enchantment.enchantmentsList[l2])) {
							flag1 = false;
							i += k2;
						}
					}

					if (flag1) {
						if (l1 > enchantment.getMaxLevel()) {
							l1 = enchantment.getMaxLevel();
						}

						inputStackEnchantments.put(Integer.valueOf(j1), Integer.valueOf(l1));
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

						if (flag) {
							i3 = Math.max(1, i3 / 2);
						}

						i += i3 * k2;
					}
				}
			}

			l = 0;

			for (iterator = inputStackEnchantments.keySet().iterator(); iterator.hasNext(); k += l
					+ k1 * l1) {
				j1 = iterator.next().intValue();
				enchantment = Enchantment.enchantmentsList[j1];
				k1 = inputStackEnchantments.get(Integer.valueOf(j1)).intValue();
				l1 = 0;
				++l;

				switch (enchantment.getWeight()) {
					case 1:
						l1 = 8;
						break;
					case 2:
						l1 = 4;
					case 3:
					case 4:
					case 6:
					case 7:
					case 8:
					case 9:
					default:
						break;
					case 5:
						l1 = 2;
						break;
					case 10:
						l1 = 1;
				}

				if (flag) {
					l1 = Math.max(1, l1 / 2);
				}
			}

			if (flag) {
				k = Math.max(1, k / 2);
			}

			if (flag
					&& inputStackCopy != null
					&& !Item.itemsList[inputStackCopy.itemID].isBookEnchantable(inputStackCopy, modifierStack)) {
				inputStackCopy = null;
			}

			maximumCost = k + i;

			if (i <= 0) {
				inputStackCopy = null;
			}

			if (inputStackCopy != null) {
				i1 = inputStackCopy.getRepairCost();

				if (modifierStack != null && i1 < modifierStack.getRepairCost()) {
					i1 = modifierStack.getRepairCost();
				}

				if (inputStackCopy.hasDisplayName()) {
					i1 -= 9;
				}

				if (i1 < 0) {
					i1 = 0;
				}

				i1 += 2;
				inputStackCopy.setRepairCost(i1);
				EnchantmentHelper.setEnchantments(inputStackEnchantments, inputStackCopy);

				int requiredXP = EnchantmentUtils.getExperienceForLevel(maximumCost);
				int requiredLiquid = EnchantmentUtils.XPToLiquidRatio(requiredXP);
				if (tank.getFluidAmount() >= requiredLiquid && doIt) {
					tank.drain(requiredLiquid, true);
					inventory.setInventorySlotContents(0, null);
					if (flag) {
						stackSizeToBeUsedInRepair = 1;
					}
					inventory.decrStackSize(1, Math.max(1, stackSizeToBeUsedInRepair));
					inventory.setInventorySlotContents(2, inputStackCopy);
					return 0;
				}
				return requiredLiquid;
			}
		}
		return 0;

	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) { return false; }
		if (!worldObj.isRemote) {
			openGui(player);
		}
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (i == 0 && !itemstack.isItemEnchantable()) { return false; }
		if (i == 2) { return false; }
		return inventory.isItemValidForSlot(i, itemstack);
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return xpSides.get(from.ordinal());
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] { tank.getInfo() };
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return slotSides.getSlotsForSide(side);
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side) {
		return slotSides.canInsertItem(slot, side) && isItemValidForSlot(slot, itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
		return slotSides.canExtractItem(slot, side);
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {}

	public IFluidTank getTank() {
		return tank;
	}

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return inventory.decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return inventory.getStackInSlotOnClosing(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public String getInvName() {
		return inventory.getInvName();
	}

	@Override
	public boolean isInvNameLocalized() {
		return inventory.isInvNameLocalized();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return inventory.isUseableByPlayer(entityplayer);
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}

}
