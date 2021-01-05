package openblocks.common.tileentity;

import com.google.common.collect.ImmutableMap;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.common.animation.TimeValues.VariableValue;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.model.animation.CapabilityAnimation;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiProjector;
import openblocks.common.HeightMapData;
import openblocks.common.MapDataManager;
import openblocks.common.block.BlockProjector;
import openblocks.common.container.ContainerProjector;
import openblocks.common.item.ItemEmptyMap;
import openblocks.common.item.ItemHeightMap;
import openblocks.rpc.IRotatable;
import openmods.OpenMods;
import openmods.fixers.GenericInventoryTeFixerWalker;
import openmods.fixers.RegisterFixer;
import openmods.inventory.GenericInventory;
import openmods.inventory.IInventoryDelegate;
import openmods.inventory.TileEntityInventory;
import openmods.sync.ISyncListener;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncMap;
import openmods.sync.SyncableByte;
import openmods.sync.SyncableInt;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.BlockNotifyFlags;
import openmods.utils.BlockUtils;

@RegisterFixer(GenericInventoryTeFixerWalker.class)
public class TileEntityProjector extends SyncedTileEntity implements IHasGui, IInventoryDelegate, ISyncListener, IRotatable {

	private final GenericInventory inventory = new TileEntityInventory(this, "openblocks.projector", false, 1) {
		@Override
		public boolean isItemValidForSlot(int i, @Nonnull ItemStack stack) {
			if (stack.isEmpty()) return false;
			Item item = stack.getItem();
			return item instanceof ItemHeightMap || item instanceof ItemEmptyMap;
		}

		@Override
		public int getInventoryStackLimit() {
			return 1;
		}

		@Override
		public void onInventoryChanged(int slotNumber) {
			super.onInventoryChanged(slotNumber);

			if (!isInvalid()) {
				if (!world.isRemote) {
					ItemStack stack = getStackInSlot(slotNumber);
					if (stack.getCount() == 1) {
						Item item = stack.getItem();
						if (item instanceof ItemHeightMap) {
							int mapId = ItemHeightMap.getMapId(stack);
							TileEntityProjector.this.mapId.set(mapId);
						} else if (item instanceof ItemEmptyMap && world != null) {
							ItemStack newStack = ItemEmptyMap.upgradeToMap(world, stack);
							setInventorySlotContents(slotNumber, newStack);
						} else TileEntityProjector.this.mapId.set(-1);
					} else TileEntityProjector.this.mapId.set(-1);
					sync();

					final boolean isActive = TileEntityProjector.this.mapId() >= 0;
					final BlockPos pos = getPos();
					final BlockState oldState = world.getBlockState(pos);
					final BlockState newState = oldState.withProperty(BlockProjector.ACTIVE, isActive);

					if (oldState != newState) {
						world.setBlockState(pos, newState, BlockNotifyFlags.ALL);
					}
				}

				markUpdated();
			}
		}
	};

	private final IAnimationStateMachine asm;
	private final VariableValue lastChange = new VariableValue(Float.NEGATIVE_INFINITY);

	private SyncableByte rotation;
	private SyncableInt mapId;

	public TileEntityProjector() {
		this.asm = OpenMods.proxy.loadAsm(OpenBlocks.location("asms/block/projector.json"),
				ImmutableMap.of("last_change", lastChange));
	}

	@Override
	protected void onSyncMapCreate(SyncMap syncMap) {
		syncMap.addUpdateListener(this);
	}

	@Override
	protected void createSyncedFields() {
		rotation = new SyncableByte();
		mapId = new SyncableInt(-1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return BlockUtils.expandAround(pos, 1, 5, 1);
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 0 || pass == 1;
	}

	@Override
	public void readFromNBT(CompoundNBT tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag);
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT tag) {
		tag = super.writeToNBT(tag);
		inventory.writeToNBT(tag);
		return tag;
	}

	@Override
	public Object getServerGui(PlayerEntity player) {
		return new ContainerProjector(player.inventory, this);
	}

	@Override
	public Object getClientGui(PlayerEntity player) {
		return new GuiProjector(new ContainerProjector(player.inventory, this));
	}

	@Override
	public boolean canOpenGui(PlayerEntity player) {
		return true;
	}

	@Override
	public void onSync(Set<ISyncableObject> changes) {
		if (changes.contains(mapId)) {
			int mapId = this.mapId.get();
			if (mapId >= 0 && MapDataManager.getMapData(world, mapId).isEmpty()) MapDataManager.requestMapData(world, mapId);
		}
	}

	@Override
	public void rotate(int delta) {
		int value = rotation.get() + delta;
		rotation.set((byte)(value & 0x3));
		sync();
	}

	public byte rotation() {
		return rotation.get();
	}

	public int mapId() {
		return mapId.get();
	}

	public HeightMapData getMap() {
		int mapId = this.mapId.get();
		if (world == null || mapId < 0) return null;

		return MapDataManager.getMapData(world, mapId);
	}

	public void markMapDirty() {
		int mapId = this.mapId.get();
		if (world != null || mapId < 0) MapDataManager.instance.markDataUpdated(world, mapId);
	}

	@Override
	public IInventory getInventory() {
		return inventory;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, Direction side) {
		return capability == CapabilityAnimation.ANIMATION_CAPABILITY ||
				capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ||
				super.hasCapability(capability, side);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, Direction side) {
		if (capability == CapabilityAnimation.ANIMATION_CAPABILITY)
			return (T)asm;

		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T)inventory.getHandler();

		return super.getCapability(capability, side);
	}

	@Override
	public void updateContainingBlockInfo() {
		super.updateContainingBlockInfo();
		updateAsmState();
	}

	@Override
	public void onLoad() {
		super.onLoad();
		updateAsmState();
	}

	private void updateAsmState() {
		if (asm != null && world != null && world.isRemote) {
			final BlockState state = world.getBlockState(this.pos);
			if (state.getValue(BlockProjector.ACTIVE)) {
				if (asm.currentState().equals("default")) {
					updateLastChangeTime();
					asm.transition("starting");
				}
			} else {
				if (asm.currentState().equals("moving")) {
					updateLastChangeTime();
					asm.transition("stopping");
				}
			}
		}
	}

	private void updateLastChangeTime() {
		lastChange.setValue(Animation.getWorldTime(getWorld(), Animation.getPartialTickTime()));
	}
}
