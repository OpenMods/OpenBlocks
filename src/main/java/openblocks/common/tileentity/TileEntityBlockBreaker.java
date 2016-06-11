package openblocks.common.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;
import openmods.api.INeighbourAwareTile;
import openmods.fakeplayer.BreakBlockAction;
import openmods.fakeplayer.FakePlayerPool;
import openmods.include.IncludeInterface;
import openmods.inventory.GenericInventory;
import openmods.inventory.legacy.ItemDistribution;
import openmods.sync.SyncableBoolean;
import openmods.tileentity.SyncedTileEntity;

public class TileEntityBlockBreaker extends SyncedTileEntity implements INeighbourAwareTile {

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
	public void updateEntity() {
		super.updateEntity();
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

	private boolean canBreakBlock(Block block, int x, int y, int z) {
		return !block.isAir(worldObj, x, y, z) && block != Blocks.bedrock && block.getBlockHardness(worldObj, z, y, z) > -1.0F;
	}

	private void triggerBreakBlock() {
		final ForgeDirection direction = getOrientation().up();
		final int x = xCoord + direction.offsetX;
		final int y = yCoord + direction.offsetY;
		final int z = zCoord + direction.offsetZ;

		if (worldObj.blockExists(x, y, z)) {
			final Block block = worldObj.getBlock(x, y, z);
			if (canBreakBlock(block, x, y, z)) sendBlockEvent(EVENT_ACTIVATE, 0);
		}

		worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, "tile.piston.in", 0.5F, worldObj.rand.nextFloat() * 0.15F + 0.6F);
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

		final ForgeDirection direction = getOrientation().up();
		final int x = xCoord + direction.offsetX;
		final int y = yCoord + direction.offsetY;
		final int z = zCoord + direction.offsetZ;

		if (!worldObj.blockExists(x, y, z)) return;

		final Block block = worldObj.getBlock(x, y, z);
		if (!canBreakBlock(block, x, y, z)) return;

		final List<EntityItem> drops = FakePlayerPool.instance.executeOnPlayer((WorldServer)worldObj, new BreakBlockAction(worldObj, x, y, z));
		tryInjectItems(drops, direction.getOpposite());
	}

	private void tryInjectItems(List<EntityItem> drops, ForgeDirection direction) {
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
			setRedstoneSignal(worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord));
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
