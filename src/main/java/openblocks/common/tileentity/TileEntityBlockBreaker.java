package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openmods.api.INeighbourAwareTile;
import openmods.fakeplayer.BreakBlockAction;
import openmods.fakeplayer.FakePlayerPool;
import openmods.include.IncludeInterface;
import openmods.inventory.GenericInventory;
import openmods.inventory.legacy.ItemDistribution;
import openmods.sync.SyncableBoolean;
import openmods.tileentity.SyncedTileEntity;

public class TileEntityBlockBreaker extends SyncedTileEntity implements INeighbourAwareTile, ITickable {

	private static final int EVENT_ACTIVATE = 3;

	// DON'T remove this object, even though it seems unused. Without it Builcraft pipes won't connect. -B
	@IncludeInterface(IInventory.class)
	private final GenericInventory inventory = registerInventoryCallback(new GenericInventory("blockbreaker", true, 1) {
		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			return false;
		}
	});

	private int redstoneAnimTimer;
	private SyncableBoolean activated;

	public TileEntityBlockBreaker() {
		syncMap.addUpdateListener(createRenderUpdateListener());
	}

	@Override
	protected void createSyncedFields() {
		activated = new SyncableBoolean(false);
	}

	@SideOnly(Side.CLIENT)
	public boolean isActivated() {
		return activated.get();
	}

	@Override
	public void update() {
		if (!worldObj.isRemote && activated.get()) {
			if (redstoneAnimTimer <= 0) {
				activated.set(false);
				sync();
			} else redstoneAnimTimer--;

		}
	}

	private void setRedstoneSignal(boolean redstoneSignal) {
		if (worldObj.isRemote) return;

		if (redstoneSignal) {
			redstoneAnimTimer = 5;
			activated.set(true);
			sync();
			triggerBreakBlock();
		}
	}

	private boolean canBreakBlock(Block block, BlockPos pos) {
		return !block.isAir(worldObj, pos) && block != Blocks.bedrock && block.getBlockHardness(worldObj, pos) > -1.0F;
	}

	private void triggerBreakBlock() {
		final EnumFacing direction = getOrientation().up();
		final BlockPos target = pos.offset(direction);

		if (worldObj.isBlockLoaded(target)) {
			final Block block = worldObj.getBlockState(target).getBlock();
			if (canBreakBlock(block, target)) sendBlockEvent(EVENT_ACTIVATE, 0);
		}

		playSoundAtBlock("tile.piston.in", 0.5F, worldObj.rand.nextFloat() * 0.15F + 0.6F);
	}

	@Override
	public boolean receiveClientEvent(int event, int param) {
		if (event == EVENT_ACTIVATE) {
			breakBlock();
			return true;
		}

		return false;
	}

	public void breakBlock() {
		if (!(worldObj instanceof WorldServer)) return;

		final EnumFacing direction = getOrientation().up();
		final BlockPos target = pos.offset(direction);

		if (!worldObj.isBlockLoaded(target)) return;

		final Block block = worldObj.getBlockState(target).getBlock();
		if (!canBreakBlock(block, target)) return;

		final List<EntityItem> drops = FakePlayerPool.instance.executeOnPlayer((WorldServer)worldObj, new BreakBlockAction(worldObj, target));
		tryInjectItems(drops, direction.getOpposite());
	}

	private void tryInjectItems(List<EntityItem> drops, EnumFacing direction) {
		TileEntity targetInventory = getTileInDirection(direction);
		if (targetInventory == null) return;

		for (EntityItem drop : drops) {
			ItemStack stack = drop.getEntityItem();
			ItemDistribution.insertItemInto(stack, targetInventory, direction, true);

			if (stack.stackSize <= 0) drop.setDead();
		}
	}

	@Override
	public void onNeighbourChanged(Block block) {
		if (!worldObj.isRemote) {
			setRedstoneSignal(worldObj.isBlockIndirectlyGettingPowered(pos) > 0);
		}
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
