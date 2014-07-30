package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.ForgeDirection;
import openmods.GenericInventory;
import openmods.IInventoryProvider;
import openmods.api.INeighbourAwareTile;
import openmods.api.IPlaceAwareTile;
import openmods.sync.SyncableBoolean;
import openmods.sync.SyncableString;
import openmods.tileentity.SyncedTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityGrave extends SyncedTileEntity implements IPlaceAwareTile, IInventoryProvider, INeighbourAwareTile {

	private SyncableString perishedUsername;
	public SyncableBoolean onSoil;
	private int ticksSinceLastSound = 0;

	private GenericInventory inventory = registerInventoryCallback(new GenericInventory("grave", false, 100));

	public TileEntityGrave() {}

	@Override
	protected void createSyncedFields() {
		perishedUsername = new SyncableString();
		onSoil = new SyncableBoolean();
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
			if (worldObj.difficultySetting != EnumDifficulty.PEACEFUL && worldObj.rand.nextDouble() < 0.002) {
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

	public boolean isOnSoil() {
		return onSoil.get();
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		if (!worldObj.isRemote && !(player instanceof FakePlayer)) {
			setUsername(player.getGameProfile().getName());
			if (player.capabilities.isCreativeMode) setLoot(player.inventory);
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

	protected void updateBlockBelow() {
		Block block = worldObj.getBlock(xCoord, yCoord - 1, zCoord);
		onSoil.set(block == Blocks.dirt || block == Blocks.grass);
	}

	@Override
	public void initialize() {
		updateBlockBelow();
	}

	@Override
	public void onNeighbourChanged() {
		updateBlockBelow();
		sync();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
	}

}
