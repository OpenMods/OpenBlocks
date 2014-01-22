package openblocks.common.tileentity;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import openmods.GenericInventory;
import openmods.IInventoryProvider;
import openmods.api.IPlaceAwareTile;
import openmods.api.ISurfaceAttachment;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableString;
import openmods.tileentity.SyncedTileEntity;

public class TileEntityGrave extends SyncedTileEntity implements ISurfaceAttachment, IPlaceAwareTile, IInventoryProvider {

	private SyncableString perishedUsername;
	public boolean onSoil = true;
	private int ticksSinceLastSound = 0;

	private GenericInventory inventory = new GenericInventory("grave", false, 100);

	public TileEntityGrave() {}

	@Override
	protected void createSyncedFields() {
		perishedUsername = new SyncableString();
	}

	@Override
	public void initialize() {
		Block block = Block.blocksList[worldObj.getBlockId(xCoord, yCoord - 1, zCoord)];
		if (block != null) {
			onSoil = (block == Block.dirt || block == Block.grass);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void updateEntity() {
		super.updateEntity();

		if (worldObj.isRemote) {
			if (ticksSinceLastSound++ > 100) {
				ticksSinceLastSound = 0;
			}
		}

		if (!worldObj.isRemote) {
			if (worldObj.difficultySetting > 0
					&& worldObj.rand.nextDouble() < 0.002) {
				List<Entity> mobs = worldObj.getEntitiesWithinAABB(IMob.class, getBB().expand(7, 7, 7));
				if (mobs.size() < 5) {
					double chance = worldObj.rand.nextDouble();
					EntityLiving living = chance < 0.5? new EntitySkeleton(worldObj) : new EntityBat(worldObj);
					living.setPositionAndRotation(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, worldObj.rand.nextFloat() * 360, 0);
					if (living.getCanSpawnHere()) {
						worldObj.spawnEntityInWorld(living);
					}
				}
			}
		}
	}

	public String getUsername() {
		return perishedUsername.getValue();
	}

	public void setUsername(String username) {
		this.perishedUsername.setValue(username);
	}

	public void setLoot(IInventory invent) {
		inventory.copyFrom(invent);
	}

	@Override
	public ForgeDirection getSurfaceDirection() {
		return ForgeDirection.DOWN;
	}

	public boolean isOnSoil() {
		return onSoil;
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		if (!worldObj.isRemote) {
			setUsername(player.username);
			setLoot(player.inventory);
			sync();
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

	@Override
	public IInventory getInventory() {
		return inventory;
	}
}
