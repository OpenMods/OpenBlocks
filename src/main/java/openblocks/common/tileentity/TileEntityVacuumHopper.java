package openblocks.common.tileentity;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiVacuumHopper;
import openblocks.common.LiquidXpUtils;
import openblocks.common.container.ContainerVacuumHopper;
import openblocks.common.entity.EntityItemProjectile;
import openmods.OpenMods;
import openmods.api.IActivateAwareTile;
import openmods.api.IHasGui;
import openmods.api.INeighbourAwareTile;
import openmods.api.IValueProvider;
import openmods.include.IncludeInterface;
import openmods.inventory.GenericInventory;
import openmods.inventory.IInventoryProvider;
import openmods.inventory.ItemMover;
import openmods.inventory.TileEntityInventory;
import openmods.liquids.SidedFluidHandler;
import openmods.sync.ISyncListener;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncMap;
import openmods.sync.SyncableBoolean;
import openmods.sync.SyncableSides;
import openmods.sync.SyncableTank;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.EnchantmentUtils;
import openmods.utils.InventoryUtils;
import openmods.utils.ItemUtils;
import openmods.utils.SidedInventoryAdapter;
import openmods.utils.SidedItemHandlerAdapter;
import openmods.utils.bitmap.BitMapUtils;
import openmods.utils.bitmap.IReadableBitMap;
import openmods.utils.bitmap.IRpcDirectionBitMap;
import openmods.utils.bitmap.IWriteableBitMap;

public class TileEntityVacuumHopper extends SyncedTileEntity implements IInventoryProvider, IActivateAwareTile, IHasGui, INeighbourAwareTile, ITickable {

	public static final int TANK_CAPACITY = LiquidXpUtils.xpToLiquidRatio(EnchantmentUtils.getExperienceForLevel(5));

	public static final String OUTPUT_ITEMS = "items";
	public static final String OUTPUT_FLUIDS = "fluids";
	public static final String OUTPUT_BOTH = "both";

	private SyncableTank tank;
	public SyncableSides xpOutputs;
	public SyncableSides itemOutputs;
	public SyncableBoolean vacuumDisabled;

	private boolean needsTankUpdate;

	private final GenericInventory inventory = registerInventoryCallback(new TileEntityInventory(this, "vacuumhopper", true, 10));

	@IncludeInterface(ISidedInventory.class)
	private final SidedInventoryAdapter sided = new SidedInventoryAdapter(inventory);

	private final SidedItemHandlerAdapter itemHandlerCapability = new SidedItemHandlerAdapter(inventory.getHandler());

	@IncludeInterface
	private final IFluidHandler tankWrapper = new SidedFluidHandler.Source(xpOutputs, tank);

	private Map<String, String> outputState = ImmutableMap.of();

	@Override
	protected void createSyncedFields() {
		tank = new SyncableTank(TANK_CAPACITY, OpenBlocks.Fluids.xpJuice);
		xpOutputs = new SyncableSides();
		itemOutputs = new SyncableSides();
		vacuumDisabled = new SyncableBoolean();
	}

	public TileEntityVacuumHopper() {
		sided.registerAllSlots(itemOutputs, false, true);

		itemHandlerCapability.registerAllSlots(itemOutputs, false, true);
	}

	@Override
	protected void onSyncMapCreate(SyncMap syncMap) {
		syncMap.addSyncListener(itemHandlerCapability.createSyncListener());

		syncMap.addUpdateListener(new ISyncListener() {
			@Override
			public void onSync(Set<ISyncableObject> changes) {
				if (changes.contains(xpOutputs) || changes.contains(itemOutputs)) {
					updateOutputStates();
					worldObj.markBlockRangeForRenderUpdate(pos, pos);
				}
			}

			private void updateOutputStates() {
				ImmutableMap.Builder<String, String> newOutputState = ImmutableMap.builder();
				for (EnumFacing side : EnumFacing.VALUES) {
					final boolean outputItems = itemOutputs.get(side);
					final boolean outputXp = xpOutputs.get(side);

					if (outputItems) {
						if (outputXp) {
							newOutputState.put(side.getName(), OUTPUT_BOTH);
						} else {
							newOutputState.put(side.getName(), OUTPUT_ITEMS);
						}
					} else if (outputXp) {
						newOutputState.put(side.getName(), OUTPUT_FLUIDS);
					}
				}

				outputState = newOutputState.build();
			}
		});
	}

	public IReadableBitMap<EnumFacing> getReadableXpOutputs() {
		return xpOutputs;
	}

	public IWriteableBitMap<EnumFacing> getWriteableXpOutputs() {
		return BitMapUtils.createRpcAdapter(createRpcProxy(xpOutputs, IRpcDirectionBitMap.class));
	}

	public IReadableBitMap<EnumFacing> getReadableItemOutputs() {
		return itemOutputs;
	}

	public IWriteableBitMap<EnumFacing> getWriteableItemOutputs() {
		return BitMapUtils.createRpcAdapter(createRpcProxy(itemOutputs, IRpcDirectionBitMap.class));
	}

	public IValueProvider<FluidStack> getFluidProvider() {
		return tank;
	}

	private final Predicate<Entity> entitySelector = new Predicate<Entity>() {
		@Override
		public boolean apply(@Nullable Entity entity) {
			if (entity.isDead) return false;

			if (entity instanceof EntityItemProjectile) return entity.motionY < 0.01;

			if (entity instanceof EntityItem) {
				ItemStack stack = ((EntityItem)entity).getEntityItem();
				return InventoryUtils.canInsertStack(inventory.getHandler(), stack);
			}

			if (entity instanceof EntityXPOrb) return tank.getSpace() > 0;

			return false;
		}
	};

	@Override
	public void update() {

		if (vacuumDisabled.get()) return;

		if (worldObj.isRemote) {
			spawnParticle(EnumParticleTypes.PORTAL, worldObj.rand.nextDouble() - 0.5, worldObj.rand.nextDouble() - 1.0, worldObj.rand.nextDouble() - 0.5);
		}

		List<Entity> interestingItems = worldObj.getEntitiesWithinAABB(Entity.class, getBB().expand(3, 3, 3), entitySelector);

		boolean needsSync = false;

		for (Entity entity : interestingItems) {
			double dx = (pos.getX() + 0.5D - entity.posX);
			double dy = (pos.getY() + 0.5D - entity.posY);
			double dz = (pos.getZ() + 0.5D - entity.posZ);

			double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
			if (distance < 1.1) {
				needsSync |= onEntityCollidedWithBlock(entity);
			} else {
				double var11 = 1.0 - distance / 15.0;

				if (var11 > 0.0D) {
					var11 *= var11;
					entity.motionX += dx / distance * var11 * 0.05;
					entity.motionY += dy / distance * var11 * 0.2;
					entity.motionZ += dz / distance * var11 * 0.05;
				}
			}

		}

		if (!worldObj.isRemote) {
			needsSync |= outputToNeighbors();
			if (needsSync) sync();
		}
	}

	private boolean outputToNeighbors() {
		if (OpenMods.proxy.getTicks(worldObj) % 10 == 0) {
			if (needsTankUpdate) {
				tank.updateNeighbours(worldObj, pos);
				needsTankUpdate = false;
			}

			tank.distributeToSides(50, worldObj, pos, xpOutputs.getValue());
			autoInventoryOutput();
			return true;
		}

		return false;
	}

	private void autoInventoryOutput() {
		final boolean outputSides = itemOutputs.getValue().isEmpty();
		if (outputSides) return;
		final ItemMover mover = new ItemMover(worldObj, pos).breakAfterFirstTry().randomizeSides().setSides(itemOutputs.getValue());
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (inventory.getStackInSlot(i) != null) {
				if (mover.pushFromSlot(inventory.getHandler(), i) > 0) break;
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
	public boolean onBlockActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldObj.isRemote && hand == EnumHand.MAIN_HAND && player.isSneaking()) {
			if (heldItem == null) {
				vacuumDisabled.toggle();
				return true;
			}
		}
		return false;
	}

	public boolean onEntityCollidedWithBlock(Entity entity) {
		if (!worldObj.isRemote) {
			if (entity instanceof EntityItem && !entity.isDead) {
				final EntityItem item = (EntityItem)entity;
				final ItemStack toConsume = item.getEntityItem().copy();
				final ItemStack leftover = ItemHandlerHelper.insertItem(inventory.getHandler(), toConsume, false);
				ItemUtils.setEntityItemStack(item, leftover);
				return true;
			} else if (entity instanceof EntityXPOrb) {
				if (tank.getSpace() > 0) {
					EntityXPOrb orb = (EntityXPOrb)entity;
					int xpAmount = LiquidXpUtils.xpToLiquidRatio(orb.getXpValue());
					FluidStack newFluid = new FluidStack(OpenBlocks.Fluids.xpJuice, xpAmount);
					tank.fill(newFluid, true);
					entity.setDead();
					return true;
				}
			}
		}

		return false;
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

	@Override
	public void validate() {
		super.validate();
		this.needsTankUpdate = true;
	}

	@Override
	public void onNeighbourChanged(Block block) {
		this.needsTankUpdate = true;
	}

	public Map<String, String> getOutputState() {
		return outputState;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return itemHandlerCapability.hasHandler(facing);

		return super.hasCapability(capability, facing);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T)itemHandlerCapability.getHandler(facing);

		return super.getCapability(capability, facing);
	}

}
