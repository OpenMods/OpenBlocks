package openblocks.common.tileentity;

import java.util.Collections;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiVacuumHopper;
import openblocks.common.LiquidXpUtils;
import openblocks.common.container.ContainerVacuumHopper;
import openblocks.common.entity.EntityItemProjectile;
import openmods.OpenMods;
import openmods.api.IActivateAwareTile;
import openmods.api.IHasGui;
import openmods.api.IValueProvider;
import openmods.include.IncludeInterface;
import openmods.inventory.GenericInventory;
import openmods.inventory.IInventoryProvider;
import openmods.inventory.TileEntityInventory;
import openmods.inventory.legacy.ItemDistribution;
import openmods.liquids.SidedFluidHandler;
import openmods.sync.SyncableBoolean;
import openmods.sync.SyncableSides;
import openmods.sync.SyncableTank;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.EnchantmentUtils;
import openmods.utils.SidedInventoryAdapter;
import openmods.utils.bitmap.*;

import com.google.common.collect.Lists;

public class TileEntityVacuumHopper extends SyncedTileEntity implements IInventoryProvider, IActivateAwareTile, IHasGui, IEntitySelector {

	public static final int TANK_CAPACITY = LiquidXpUtils.xpToLiquidRatio(EnchantmentUtils.getExperienceForLevel(5));

	private SyncableTank tank;
	public SyncableSides xpOutputs;
	public SyncableSides itemOutputs;
	public SyncableBoolean vacuumDisabled;

	private final GenericInventory inventory = registerInventoryCallback(new TileEntityInventory(this, "vacuumhopper", true, 10));

	@IncludeInterface(ISidedInventory.class)
	private final SidedInventoryAdapter sided = new SidedInventoryAdapter(inventory);

	@IncludeInterface
	private final IFluidHandler tankWrapper = new SidedFluidHandler.Source(xpOutputs, tank);

	@Override
	protected void createSyncedFields() {
		tank = new SyncableTank(TANK_CAPACITY, OpenBlocks.XP_FLUID);
		xpOutputs = new SyncableSides();
		itemOutputs = new SyncableSides();
		vacuumDisabled = new SyncableBoolean();
	}

	public TileEntityVacuumHopper() {
		sided.registerAllSlots(itemOutputs, false, true);
	}

	public IReadableBitMap<ForgeDirection> getReadableXpOutputs() {
		return xpOutputs;
	}

	public IWriteableBitMap<ForgeDirection> getWriteableXpOutputs() {
		return BitMapUtils.createRpcAdapter(createRpcProxy(xpOutputs, IRpcDirectionBitMap.class));
	}

	public IReadableBitMap<ForgeDirection> getReadableItemOutputs() {
		return itemOutputs;
	}

	public IWriteableBitMap<ForgeDirection> getWriteableItemOutputs() {
		return BitMapUtils.createRpcAdapter(createRpcProxy(itemOutputs, IRpcDirectionBitMap.class));
	}

	public IValueProvider<FluidStack> getFluidProvider() {
		return tank;
	}

	@Override
	public boolean isEntityApplicable(Entity entity) {
		if (entity.isDead) return false;

		if (entity instanceof EntityItemProjectile) return entity.motionY < 0.01;

		if (entity instanceof EntityItem) {
			ItemStack stack = ((EntityItem)entity).getEntityItem();
			return ItemDistribution.testInventoryInsertion(inventory, stack) > 0;
		}

		if (entity instanceof EntityXPOrb) return tank.getSpace() > 0;

		return false;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (vacuumDisabled.get()) return;

		if (worldObj.isRemote) {
			worldObj.spawnParticle("portal", xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, worldObj.rand.nextDouble() - 0.5, worldObj.rand.nextDouble() - 1.0, worldObj.rand.nextDouble() - 0.5);
		}

		@SuppressWarnings("unchecked")
		List<Entity> interestingItems = worldObj.selectEntitiesWithinAABB(Entity.class, getBB().expand(3, 3, 3), this);

		for (Entity entity : interestingItems) {
			double x = (xCoord + 0.5D - entity.posX);
			double y = (yCoord + 0.5D - entity.posY);
			double z = (zCoord + 0.5D - entity.posZ);

			double distance = Math.sqrt(x * x + y * y + z * z);
			if (distance < 1.1) {
				onEntityCollidedWithBlock(entity);
			} else {
				double var11 = 1.0 - distance / 15.0;

				if (var11 > 0.0D) {
					var11 *= var11;
					entity.motionX += x / distance * var11 * 0.05;
					entity.motionY += y / distance * var11 * 0.2;
					entity.motionZ += z / distance * var11 * 0.05;
				}
			}

		}

		if (!worldObj.isRemote) outputToNeighbors();
	}

	private void outputToNeighbors() {
		tank.distributeToSides(50, worldObj, getPosition(), xpOutputs.getValue());
		if (OpenMods.proxy.getTicks(worldObj) % 10 == 0) autoInventoryOutput();
	}

	private void autoInventoryOutput() {
		int firstUsedSlot = -1;
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (inventory.getStackInSlot(i) != null) {
				firstUsedSlot = i;
				break;
			}
		}

		if (firstUsedSlot < 0) return;

		List<ForgeDirection> outputSides = Lists.newArrayList(itemOutputs.getValue());
		Collections.shuffle(outputSides);

		for (ForgeDirection output : outputSides) {
			TileEntity tileOnSurface = getTileInDirection(output);
			if (ItemDistribution.moveItemInto(inventory, firstUsedSlot, tileOnSurface, output, 64, true) > 0) {
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				break;
			}
		}
	}

	@Override
	public Object getServerGui(EntityPlayer player) {
		return new ContainerVacuumHopper(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiVacuumHopper(new ContainerVacuumHopper(player.inventory, this));
	}

	@Override
	public boolean canOpenGui(EntityPlayer player) {
		return true;
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) {
			if (player.inventory.getStackInSlot(player.inventory.currentItem) == null) {
				vacuumDisabled.toggle();
				return true;
			}
		}
		return false;
	}

	public void onEntityCollidedWithBlock(Entity entity) {
		if (!worldObj.isRemote) {
			if (entity instanceof EntityItem && !entity.isDead) {
				EntityItem item = (EntityItem)entity;
				ItemStack stack = item.getEntityItem().copy();
				ItemDistribution.insertItemIntoInventory(inventory, stack);
				if (stack.stackSize == 0) {
					item.setDead();
				} else {
					item.setEntityItemStack(stack);
				}
			} else if (entity instanceof EntityXPOrb) {
				if (tank.getSpace() > 0) {
					FluidStack newFluid = new FluidStack(OpenBlocks.Fluids.xpJuice, LiquidXpUtils.xpToLiquidRatio(((EntityXPOrb)entity).getXpValue()));
					tank.fill(newFluid, true);
					entity.setDead();
				}
			}
		}
	}

	@Override
	public IInventory getInventory() {
		return inventory;
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
