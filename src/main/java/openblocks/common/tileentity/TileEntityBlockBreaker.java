package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent;
import openmods.GenericInventory;
import openmods.IInventoryProvider;
import openmods.api.INeighbourAwareTile;
import openmods.fakeplayer.FakePlayerPool;
import openmods.fakeplayer.FakePlayerPool.PlayerUser;
import openmods.fakeplayer.OpenModsFakePlayer;
import openmods.include.IExtendable;
import openmods.include.IncludeInterface;
import openmods.sync.SyncableBoolean;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.BlockUtils;
import openmods.utils.InventoryUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityBlockBreaker extends SyncedTileEntity implements INeighbourAwareTile, IExtendable, IInventoryProvider {

	private static final int SLOT_BUFFER = 0;

	private int redstoneAnimTimer;
	private SyncableBoolean activated;

	private final GenericInventory inventory = new GenericInventory("blockbreaker", true, 1) {
		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			return false;
		}
	};

	public TileEntityBlockBreaker() {
		syncMap.addUpdateListener(createRenderUpdateListener());
	}

	@Override
	protected void createSyncedFields() {
		activated = new SyncableBoolean(false);
	}

	@SideOnly(Side.CLIENT)
	public boolean isActivated() {
		return activated.getValue();
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote && activated.getValue()) {
			if (redstoneAnimTimer <= 0) {
				activated.setValue(false);
				sync();
			} else redstoneAnimTimer--;

		}
	}

	private void setRedstoneSignal(boolean redstoneSignal) {
		if (worldObj.isRemote) return;

		if (redstoneSignal && !activated.getValue()) {
			redstoneAnimTimer = 5;
			activated.setValue(true);
			sync();
			breakBlock();
		}
	}

	private void breakBlock() {
		if (worldObj.isRemote) return;

		final ForgeDirection direction = getRotation();
		final int x = xCoord + direction.offsetX;
		final int y = yCoord + direction.offsetY;
		final int z = zCoord + direction.offsetZ;

		if (worldObj.blockExists(x, y, z)) {
			final Block block = worldObj.getBlock(x, y, z);
			if (block != null) {
				final int metadata = worldObj.getBlockMetadata(x, y, z);
				if (block != Blocks.bedrock && block.getBlockHardness(worldObj, z, y, z) > -1.0F) {
					breakBlock(direction, x, y, z, block, metadata);
				}
			}
			worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, "tile.piston.in", 0.5F, worldObj.rand.nextFloat() * 0.15F + 0.6F);
		}
	}

	private void breakBlock(final ForgeDirection direction, final int x, final int y, final int z, final Block block, final int metadata) {
		if (!(worldObj instanceof WorldServer)) return;
		FakePlayerPool.instance.executeOnPlayer((WorldServer)worldObj, new PlayerUser() {
			@Override
			public void usePlayer(OpenModsFakePlayer fakePlayer) {
				fakePlayer.inventory.currentItem = 0;
				fakePlayer.inventory.setInventorySlotContents(0, new ItemStack(Items.diamond_pickaxe, 0, 0));

				BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(x, y, z, worldObj, block, blockMetadata, fakePlayer);
				if (MinecraftForge.EVENT_BUS.post(event)) return;

				if (ForgeHooks.canHarvestBlock(block, fakePlayer, metadata)) {
					worldObj.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (metadata << 12));
					worldObj.setBlockToAir(x, y, z);

					List<ItemStack> items = block.getDrops(worldObj, x, y, z, metadata, 0);
					if (items != null) {
						ForgeDirection back = direction.getOpposite();
						ejectAt(worldObj,
								xCoord + back.offsetX,
								yCoord + back.offsetY,
								zCoord + back.offsetZ,
								back, items);
					}
				}
			}
		});
	}

	private void ejectAt(World world, int x, int y, int z, ForgeDirection direction, List<ItemStack> itemStacks) {
		TileEntity targetInventory = getTileInDirection(direction);

		// if there's any stack in our buffer slot, eject it. Why is it
		// there?
		ItemStack currentStack = inventory.getStackInSlot(SLOT_BUFFER);
		if (currentStack != null) {
			BlockUtils.ejectItemInDirection(world, x, y, z, direction, currentStack);
		}

		for (ItemStack stack : itemStacks) {
			if (stack == null) continue;

			if (targetInventory != null) {
				// try push the item out into a pipe or inventory
				inventory.setInventorySlotContents(SLOT_BUFFER, stack);
				int amount = InventoryUtils.moveItemInto(inventory, SLOT_BUFFER, targetInventory, -1, 64, direction, true);
				inventory.setInventorySlotContents(SLOT_BUFFER, null);
				stack.stackSize -= amount;
			}

			if (stack.stackSize > 0) BlockUtils.ejectItemInDirection(world, x, y, z, direction, stack);
		}
	}

	@Override
	public void onNeighbourChanged() {
		if (!worldObj.isRemote) {
			setRedstoneSignal(worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord));
		}
	}

	@Override
	@IncludeInterface
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
