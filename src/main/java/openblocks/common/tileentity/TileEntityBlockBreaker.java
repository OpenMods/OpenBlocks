package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent;
import openmods.api.INeighbourAwareTile;
import openmods.context.ContextManager;
import openmods.fakeplayer.FakePlayerPool;
import openmods.fakeplayer.FakePlayerPool.PlayerUser;
import openmods.fakeplayer.OpenModsFakePlayer;
import openmods.include.IncludeInterface;
import openmods.inventory.GenericInventory;
import openmods.inventory.legacy.ItemDistribution;
import openmods.sync.SyncableBoolean;
import openmods.tileentity.SyncedTileEntity;
import openmods.world.DropCapture;
import openmods.world.DropCapture.CaptureContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityBlockBreaker extends SyncedTileEntity implements INeighbourAwareTile {

	// DON'T remove this object, even though it's unused. Without it Builcraft pipes won't connect. -B
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

		if (redstoneSignal && !activated.get()) {
			redstoneAnimTimer = 5;
			activated.set(true);
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

				CaptureContext dropsCapturer = DropCapture.instance.start(x, y, z);

				ContextManager.push(); // providing same environment as ItemInWorldManager
				BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(x, y, z, worldObj, block, metadata, fakePlayer);
				if (MinecraftForge.EVENT_BUS.post(event)) return;

				boolean canHarvest = block.canHarvestBlock(fakePlayer, metadata);

				block.onBlockHarvested(worldObj, x, y, z, metadata, fakePlayer);
				boolean canRemove = block.removedByPlayer(worldObj, fakePlayer, x, y, z, canHarvest);

				if (canRemove) {
					block.onBlockDestroyedByPlayer(worldObj, x, y, z, metadata);
					if (canHarvest) block.harvestBlock(worldObj, fakePlayer, x, y, z, metadata);
					worldObj.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (metadata << 12));
				}
				ContextManager.pop();

				List<EntityItem> drops = dropsCapturer.stop();

				if (!drops.isEmpty()) tryInjectItems(drops, direction.getOpposite());
			}
		});
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
