package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.util.FakePlayer;
import openblocks.Config;
import openmods.api.IActivateAwareTile;
import openmods.api.INeighbourAwareTile;
import openmods.api.IPlacerAwareTile;
import openmods.inventory.GenericInventory;
import openmods.inventory.IInventoryProvider;
import openmods.sync.SyncableBoolean;
import openmods.sync.SyncableString;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.BlockUtils;

import com.google.common.base.Strings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityGrave extends SyncedTileEntity implements IPlacerAwareTile, IInventoryProvider, INeighbourAwareTile, IActivateAwareTile {

	private static final String TAG_MESSAGE = "Message";
	private SyncableString perishedUsername;
	public SyncableBoolean onSoil;

	private IChatComponent deathMessage;
	private int ticksSinceLastSound = 0;

	private GenericInventory inventory = registerInventoryCallback(new GenericInventory("grave", false, 1));

	public TileEntityGrave() {}

	@Override
	protected void createSyncedFields() {
		perishedUsername = new SyncableString();
		onSoil = new SyncableBoolean(true);
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
			if (Config.spawnSkeletons && worldObj.difficultySetting != EnumDifficulty.PEACEFUL && worldObj.rand.nextDouble() < Config.skeletonSpawnRate) {
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

	public void setDeathMessage(IChatComponent msg) {
		deathMessage = msg.createCopy();
	}

	public void setUsername(String username) {
		this.perishedUsername.setValue(username);
	}

	public void setLoot(IInventory invent) {
		inventory.clearAndSetSlotCount(invent.getSizeInventory());
		inventory.copyFrom(invent);
	}

	public boolean isOnSoil() {
		return onSoil.get();
	}

	@Override
	public void onBlockPlacedBy(EntityLivingBase placer, ItemStack stack) {
		if (!worldObj.isRemote) {
			if ((placer instanceof EntityPlayer) && !(placer instanceof FakePlayer)) {
				EntityPlayer player = (EntityPlayer)placer;

				if (stack.hasDisplayName()) setUsername(stack.getDisplayName());
				else setUsername(player.getGameProfile().getName());
				if (player.capabilities.isCreativeMode) setLoot(player.inventory);
				updateBlockBelow();
				sync();
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		inventory.writeToNBT(tag);

		if (deathMessage != null) {
			String serialized = IChatComponent.Serializer.func_150696_a(deathMessage);
			tag.setString(TAG_MESSAGE, serialized);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag);

		String serializedMsg = tag.getString(TAG_MESSAGE);

		if (!Strings.isNullOrEmpty(serializedMsg)) {
			deathMessage = IChatComponent.Serializer.func_150699_a(serializedMsg);
		}
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
	public void onNeighbourChanged(Block block) {
		updateBlockBelow();
		sync();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (player.worldObj.isRemote) return false;
		ItemStack held = player.getHeldItem();
		if (held != null && held.getItem().getToolClasses(held).contains("shovel")) {
			robGrave(player, held);
			return true;
		}

		if (deathMessage != null) player.addChatMessage(deathMessage);
		return true;
	}

	protected void robGrave(EntityPlayer player, ItemStack held) {
		boolean dropped = false;
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			final ItemStack stack = inventory.getStackInSlot(i);
			if (stack != null) {
				dropped = true;
				BlockUtils.dropItemStackInWorld(worldObj, xCoord, yCoord, zCoord, stack);
			}
		}

		inventory.clearAndSetSlotCount(0);

		if (dropped) {
			worldObj.playAuxSFXAtEntity(null, 2001, xCoord, yCoord, zCoord, Block.getIdFromBlock(Blocks.dirt));
			if (worldObj.rand.nextDouble() < Config.graveSpecialAction) ohNoes(player);
			held.damageItem(2, player);
		}
	}

	private void ohNoes(EntityPlayer player) {
		worldObj.playSoundAtEntity(player, "openblocks:grave.rob", 1, 1);

		final WorldInfo worldInfo = worldObj.getWorldInfo();
		worldInfo.setThunderTime(35 * 20);
		worldInfo.setRainTime(35 * 20);
		worldInfo.setThundering(true);
		worldInfo.setRaining(true);
	}

}
