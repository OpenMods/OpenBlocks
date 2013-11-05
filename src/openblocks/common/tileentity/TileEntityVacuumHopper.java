package openblocks.common.tileentity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.*;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiVacuumHopper;
import openblocks.common.GenericInventory;
import openblocks.common.api.IActivateAwareTile;
import openblocks.common.api.IHasGui;
import openblocks.common.container.ContainerVacuumHopper;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableBoolean;
import openblocks.sync.SyncableFlags;
import openblocks.sync.SyncableTank;
import openblocks.utils.EnchantmentUtils;
import openblocks.utils.InventoryUtils;

public class TileEntityVacuumHopper extends NetworkedTileEntity implements
		IInventory, IFluidHandler, IActivateAwareTile, IHasGui {

	private static final int TANK_CAPACITY = EnchantmentUtils.XPToLiquidRatio(EnchantmentUtils.getExperienceForLevel(5));

	private SyncableTank tank = new SyncableTank(TANK_CAPACITY, OpenBlocks.XP_FLUID);
	public SyncableFlags xpOutputs = new SyncableFlags();
	public SyncableFlags itemOutputs = new SyncableFlags();
	public SyncableBoolean vacuumDisabled = new SyncableBoolean();

	public TileEntityVacuumHopper() {
		setInventory(new GenericInventory("vacuumhopper", true, 10));
	}

	public SyncableFlags getXPOutputs() {
		return xpOutputs;
	}

	public SyncableFlags getItemOutputs() {
		return itemOutputs;
	}

	public IFluidTank getTank() {
		return tank;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (!vacuumDisabled.getValue()) {
			if (worldObj.isRemote) {
				worldObj.spawnParticle("portal", xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, worldObj.rand.nextDouble() - 0.5, worldObj.rand.nextDouble() - 1.0, worldObj.rand.nextDouble() - 0.5);
			}

			@SuppressWarnings("unchecked")
			List<Entity> surroundingItems = worldObj.getEntitiesWithinAABB(Entity.class, getBB().expand(3, 3, 3));

			for (Entity entity : surroundingItems) {

				if (!entity.isDead
						&& (entity instanceof EntityItem || entity instanceof EntityXPOrb)) {

					boolean shouldPull = true;

					if (entity instanceof EntityItem) {
						ItemStack stack = ((EntityItem)entity).getEntityItem();
						shouldPull = InventoryUtils.testInventoryInsertion(this, stack) > 0;
					}
					if (entity instanceof EntityXPOrb) {
						shouldPull = getXPOutputs().getActiveSlots().size() > 0;
					}

					if (shouldPull) {

						double x = (xCoord + 0.5D - entity.posX) / 15.0D;
						double y = (yCoord + 0.5D - entity.posY) / 15.0D;
						double z = (zCoord + 0.5D - entity.posZ) / 15.0D;

						double distance = Math.sqrt(x * x + y * y + z * z);
						double var11 = 1.0D - distance;

						if (var11 > 0.0D) {
							var11 *= var11;
							entity.motionX += x / distance * var11 * 0.05;
							entity.motionY += y / distance * var11 * 0.2;
							entity.motionZ += z / distance * var11 * 0.05;
						}
					}
				}
			}
		}

		if (!worldObj.isRemote) {

			tank.autoOutputToSides(50, this, xpOutputs);

			if (OpenBlocks.proxy.getTicks(worldObj) % 10 == 0) {

				int firstUsedSlot = -1;
				for (int i = 0; i < inventory.getSizeInventory(); i++) {
					if (inventory.getStackInSlot(i) != null) {
						firstUsedSlot = i;
					}
				}

				if (firstUsedSlot > -1) {
					TileEntity tileOnSurface;
					for (Integer dir : getShuffledItemSlots()) {
						ForgeDirection directionToOutputItem = ForgeDirection.getOrientation(dir);
						tileOnSurface = getTileInDirection(directionToOutputItem);
						if (InventoryUtils.moveItemInto(this, firstUsedSlot, tileOnSurface, -1, 64, directionToOutputItem, true) > 0) {
							worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
							break;
						}
					}
				}
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

	public List<Integer> getShuffledItemSlots() {
		List<Integer> slots = new ArrayList<Integer>();
		slots.addAll(getItemOutputs().getActiveSlots());
		Collections.shuffle(slots);
		return slots;
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) {
			if (player.inventory.getStackInSlot(player.inventory.currentItem) == null) {
				vacuumDisabled.toggle();
				return true;
			}
			return false;
		} else if (!worldObj.isRemote) {
			openGui(player);
		}
		return true;
	}

	public void onEntityCollidedWithBlock(Entity entity) {
		if (!worldObj.isRemote) {
			if (entity instanceof EntityItem) {
				EntityItem item = (EntityItem)entity;
				ItemStack stack = item.getEntityItem().copy();
				InventoryUtils.insertItemIntoInventory(inventory, stack);
				if (stack.stackSize == 0) {
					item.setDead();
				} else {
					item.setEntityItemStack(stack);
				}
			} else if (entity instanceof EntityXPOrb) {
				if (getXPOutputs().getActiveSlots().size() > 0) {
					FluidStack newFluid = new FluidStack(OpenBlocks.Fluids.XPJuice, EnchantmentUtils.XPToLiquidRatio(((EntityXPOrb)entity).getXpValue()));
					tank.fill(newFluid, true);
					entity.setDead();
				}
			}
		}
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (resource == null) return null;
		if (!resource.isFluidEqual(tank.getFluid())) return null;
		return tank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] { tank.getInfo() };
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {}

}
