package openblocks.common.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.OpenBlocksGuiHandler;
import openblocks.common.entity.ai.EntityAICollectItem;
import openmods.inventory.GenericInventory;
import openmods.inventory.IInventoryProvider;
import openmods.inventory.legacy.ItemDistribution;

import com.google.common.base.Strings;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityLuggage extends EntityTameable implements IInventoryProvider, IEntityAdditionalSpawnData {

	private static final String TAG_SHINY = "shiny";

	protected GenericInventory inventory = createInventory(27);

	private GenericInventory createInventory(int size) {
		return new GenericInventory("luggage", false, size) {
			@Override
			public boolean isUseableByPlayer(EntityPlayer player) {
				return !isDead && player.getDistanceSqToEntity(EntityLuggage.this) < 64;
			}
		};
	}

	public boolean special;

	public int lastSound = 0;

	public EntityLuggage(World world) {
		super(world);
		setSize(0.5F, 0.5F);
		setAIMoveSpeed(0.7F);
		setMoveForward(0);
		setTamed(true);
		func_110163_bv(); // set persistent
		getNavigator().setAvoidsWater(true);
		getNavigator().setCanSwim(true);
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIFollowOwner(this, getAIMoveSpeed(), 10.0F, 2.0F));
		this.tasks.addTask(3, new EntityAICollectItem(this));
		this.dataWatcher.addObject(18, Integer.valueOf(inventory.getSizeInventory()));
	}

	public void setSpecial() {
		if (special) return;
		special = true;
		GenericInventory inventory = createInventory(54);
		inventory.copyFrom(this.inventory);
		if (this.dataWatcher != null) {
			this.dataWatcher.updateObject(18, Integer.valueOf(inventory.getSizeInventory()));
		}
		this.inventory = inventory;
	}

	public boolean isSpecial() {
		if (worldObj.isRemote) { return inventory.getSizeInventory() > 27; }
		return special;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (worldObj.isRemote) {
			int inventorySize = dataWatcher.getWatchableObjectInt(18);
			if (inventory.getSizeInventory() != inventorySize) {
				inventory = createInventory(inventorySize);
			}
		}
		lastSound++;
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	public GenericInventory getInventory() {
		return inventory;
	}

	@Override
	public ItemStack getPickedResult(MovingObjectPosition target) {
		return convertToItem();
	}

	@Override
	public EntityAgeable createChild(EntityAgeable entityageable) {
		return null;
	}

	@Override
	public boolean interact(EntityPlayer player) {
		if (!isDead) {
			if (worldObj.isRemote) {
				if (player.isSneaking()) spawnPickupParticles();
			} else {
				if (player.isSneaking()) {
					ItemStack luggageItem = convertToItem();
					if (player.inventory.addItemStackToInventory(luggageItem)) setDead();
					playSound("random.pop", 0.5f, worldObj.rand.nextFloat() * 0.1f + 0.9f);

				} else {
					playSound("random.chestopen", 0.5f, worldObj.rand.nextFloat() * 0.1f + 0.9f);
					player.openGui(OpenBlocks.instance, OpenBlocksGuiHandler.GuiId.luggage.ordinal(), player.worldObj, getEntityId(), 0, 0);
				}
			}
		}
		return true;
	}

	protected void spawnPickupParticles() {
		final double py = this.posY + this.height;
		for (int i = 0; i < 50; i++) {
			double vx = rand.nextGaussian() * 0.02D;
			double vz = rand.nextGaussian() * 0.02D;
			double px = this.posX + this.width * this.rand.nextFloat();
			double pz = this.posZ + this.width * this.rand.nextFloat();
			this.worldObj.spawnParticle("portal", px, py, pz, vx, -1, vz);
		}
	}

	protected ItemStack convertToItem() {
		ItemStack luggageItem = new ItemStack(OpenBlocks.Items.luggage);
		NBTTagCompound tag = new NBTTagCompound();
		inventory.writeToNBT(tag);
		luggageItem.setTagCompound(tag);

		String nameTag = getCustomNameTag();
		if (!Strings.isNullOrEmpty(nameTag)) luggageItem.setStackDisplayName(nameTag);
		return luggageItem;
	}

	public boolean canConsumeStackPartially(ItemStack stack) {
		return ItemDistribution.testInventoryInsertion(inventory, stack) > 0;
	}

	@Override
	protected void func_145780_a(int x, int y, int z, Block block) {
		playSound("openblocks:luggage.walk", 0.3F, 0.7F + (worldObj.rand.nextFloat() * 0.5f));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setBoolean(TAG_SHINY, special);
		inventory.writeToNBT(tag);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		if (tag.getBoolean(TAG_SHINY)) setSpecial();
		inventory.readFromNBT(tag);
	}

	@Override
	public void onStruckByLightning(EntityLightningBolt lightning) {
		setSpecial();
	}

	@Override
	public boolean isEntityInvulnerable() {
		return true;
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		data.writeInt(inventory.getSizeInventory());
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		inventory = createInventory(data.readInt());
	}

	@Override
	public double getMountedYOffset() {
		return 0.825;
	}
}
