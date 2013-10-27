package openblocks.common.tileentity;

import java.util.*;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.*;
import openblocks.OpenBlocks;
import openblocks.common.GenericInventory;
import openblocks.sync.SyncableFlags;
import openblocks.sync.SyncableInt;
import openblocks.utils.BlockUtils;
import openblocks.utils.EnchantmentUtils;

public class TileEntityAutoAnvil extends BaseTileEntityXPMachine implements ISidedInventory, IFluidHandler {

	public static final int TOTAL_COOLDOWN = 40;

	private int cooldown = 0;

	private GenericInventory inventory = new GenericInventory("autoanvil", true, 3);

	public enum Keys {
		toolSides,
		modifierSides,
		outputSides,
		xpSides,
		xpLevel,
		autoFlags
	}
	
	private SyncableFlags toolSides = new SyncableFlags();
	private SyncableFlags modifierSides = new SyncableFlags();
	private SyncableFlags outputSides = new SyncableFlags();
	private SyncableFlags xpSides = new SyncableFlags();
	private SyncableInt xpLevel = new SyncableInt();
	private SyncableFlags autoFlags = new SyncableFlags();
	
	public enum AutoSides {
		tool,
		modifier,
		output,
		xp
	}
	
	public TileEntityAutoAnvil() {
		addSyncedObject(Keys.toolSides, toolSides);
		addSyncedObject(Keys.modifierSides, modifierSides);
		addSyncedObject(Keys.outputSides, outputSides);
		addSyncedObject(Keys.xpSides, xpSides);
		addSyncedObject(Keys.xpLevel, xpLevel);
		addSyncedObject(Keys.autoFlags, autoFlags);
		tank = new FluidTank(EnchantmentUtils.getLiquidForLevel(45));
	}
	
	public SyncableFlags getToolSides() {
		return toolSides;
	}
	
	public SyncableFlags getModifierSides() {
		return modifierSides;
	}
	
	public SyncableFlags getOutputSides() {
		return outputSides;
	}
	
	public SyncableFlags getXPSides() {
		return xpSides;
	}

	public SyncableFlags getAutoFlags() {
		return autoFlags;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) { 
			
			if (OpenBlocks.proxy.getTicks(worldObj) % 20 == 0) {
				refreshSurroundingTanks();
			}
			
			if (autoFlags.get(AutoSides.xp)) {
				trySuckXP();
			}
			if (cooldown == 0) {
				int liquidRequired = updateRepairOutput(false);
				if (liquidRequired > 0 && tank.getFluidAmount() >= liquidRequired) {
					liquidRequired = updateRepairOutput(true);
					cooldown = TOTAL_COOLDOWN;
				}
			} else if (cooldown > 0) {
				cooldown--;
			}
		}
	}
	
	public int updateRepairOutput(boolean doIt) {
		ItemStack inputStack = inventory.getStackInSlot(0);
		int maximumCost = 0;
		int i = 0;
		byte b0 = 0;

		if (inputStack == null) { return 0; }

		ItemStack inputStackCopy = inputStack.copy();
		ItemStack modifierStack = inventory.getStackInSlot(1);
		Map inputStackEnchantments = EnchantmentHelper.getEnchantments(inputStackCopy);
		boolean flag = false;
		int k = b0 + inputStack.getRepairCost()
				+ (modifierStack == null? 0 : modifierStack.getRepairCost());
		int stackSizeToBeUsedInRepair = 0;
		int l;
		int i1;
		int j1;
		int k1;
		int l1;
		Iterator iterator;
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

				Map map1 = EnchantmentHelper.getEnchantments(modifierStack);
				iterator = map1.keySet().iterator();

				while (iterator.hasNext()) {
					j1 = ((Integer)iterator.next()).intValue();
					enchantment = Enchantment.enchantmentsList[j1];
					k1 = inputStackEnchantments.containsKey(Integer.valueOf(j1))? ((Integer)inputStackEnchantments.get(Integer.valueOf(j1))).intValue() : 0;
					l1 = ((Integer)map1.get(Integer.valueOf(j1))).intValue();
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

					Iterator iterator1 = inputStackEnchantments.keySet().iterator();

					while (iterator1.hasNext()) {
						int l2 = ((Integer)iterator1.next()).intValue();

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
				j1 = ((Integer)iterator.next()).intValue();
				enchantment = Enchantment.enchantmentsList[j1];
				k1 = ((Integer)inputStackEnchantments.get(Integer.valueOf(j1))).intValue();
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
	public void onBlockBroken() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBlockAdded() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) { return false; }
		if (!worldObj.isRemote) {
			openGui(player, OpenBlocks.Gui.autoAnvil);
		}
		return true;
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		setRotation(BlockUtils.get2dOrientation(player));
		sync();
	}

	@Override
	public boolean onBlockEventReceived(int eventId, int eventParam) {
		// TODO Auto-generated method stub
		return false;
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
	public ItemStack decrStackSize(int stackIndex, int byAmount) {
		return inventory.decrStackSize(stackIndex, byAmount);
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
	public void openChest() {
		inventory.openChest();
	}

	@Override
	public void closeChest() {
		inventory.closeChest();
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return inventory.isItemValidForSlot(i, itemstack);
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (resource.containsFluid(xpFluid)) {
			return tank.fill(resource, doFill);
		}
		return 0;
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
		Set<Integer> slots = new HashSet<Integer>();
		if (toolSides.get(side)) {
			slots.add(0);
		}
		if (modifierSides.get(side)) {
			slots.add(1);
		}
		if (outputSides.get(side)) {
			slots.add(2);
		}
		int[] ret = new int[slots.size()];
		int i = 0;
		for(int k : slots)
			ret[i++] = k;
		return ret;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side) {
		if (slot == 0)  {
			return toolSides.get(side);
		}
		if (slot == 1) {
			return modifierSides.get(side);
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
		if (slot == 2) {
			return outputSides.get(side);
		}
		return false;
	}

	public void updateGuiValues() {
		xpLevel.setValue(tank.getFluidAmount());
	}

	public double getXPBufferRatio() {
		return Math.max(0, Math.min(1, (double)xpLevel.getValue() / (double)tank.getCapacity()));
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		inventory.writeToNBT(tag);
		tank.writeToNBT(tag);
		toolSides.writeToNBT(tag, "toolsides");
		modifierSides.writeToNBT(tag, "modifiersides");
		outputSides.writeToNBT(tag, "outputsides");
		xpSides.writeToNBT(tag, "xpsides");
		autoFlags.writeToNBT(tag, "autoflags");
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag);
		tank.readFromNBT(tag);
		toolSides.readFromNBT(tag, "toolsides");
		modifierSides.readFromNBT(tag, "modifiersides");
		outputSides.readFromNBT(tag, "outputsides");
		xpSides.readFromNBT(tag, "xpsides");
		autoFlags.readFromNBT(tag, "autoflags");
	}
}
