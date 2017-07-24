package openblocks.common.entity;

import com.google.common.base.Strings;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import openblocks.OpenBlocks;
import openblocks.OpenBlocksGuiHandler;
import openblocks.common.entity.ai.EntityAICollectItem;
import openmods.api.VisibleForDocumentation;
import openmods.inventory.GenericInventory;
import openmods.utils.InventoryUtils;

@VisibleForDocumentation
public class EntityLuggage extends EntityTameable implements IEntityAdditionalSpawnData {

	private static final DataParameter<Integer> PROPERTY_INV_SIZE = EntityDataManager.<Integer> createKey(EntityLuggage.class, DataSerializers.VARINT);

	public static final int SIZE_SPECIAL = 54;

	public static final int SIZE_NORMAL = 27;

	private static final String TAG_ITEM_TAG = "ItemTag";

	private static final String TAG_SHINY = "shiny";

	protected GenericInventory inventory = createInventory(SIZE_NORMAL);

	private GenericInventory createInventory(int size) {
		return new GenericInventory("luggage", false, size) {
			@Override
			public boolean isUsableByPlayer(EntityPlayer player) {
				return !isDead && player.getDistanceSqToEntity(EntityLuggage.this) < 64;
			}
		};
	}

	public boolean special;

	public int lastSound = 0;

	private NBTTagCompound itemTag;

	public EntityLuggage(World world) {
		super(world);
		setSize(0.5F, 0.5F);
		setAIMoveSpeed(0.7F);
		setMoveForward(0);
		setTamed(true);
		enablePersistence();
		setPathPriority(PathNodeType.WATER, -1.0F);
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIFollowOwner(this, getAIMoveSpeed(), 10.0F, 2.0F));
		this.tasks.addTask(3, new EntityAICollectItem(this));
		getDataManager().register(PROPERTY_INV_SIZE, inventory.getSizeInventory());
	}

	@Override
	protected PathNavigate createNavigator(World worldIn) {
		final PathNavigateGround navigator = new PathNavigateGround(this, worldIn);
		navigator.setCanSwim(true);
		return navigator;
	}

	public void setSpecial() {
		if (special) return;
		special = true;
		GenericInventory inventory = createInventory(SIZE_SPECIAL);
		inventory.copyFrom(this.inventory);
		getDataManager().set(PROPERTY_INV_SIZE, inventory.getSizeInventory());
		this.inventory = inventory;
	}

	public boolean isSpecial() {
		if (world.isRemote) { return inventory.getSizeInventory() > SIZE_NORMAL; }
		return special;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (world.isRemote) {
			int inventorySize = getDataManager().get(PROPERTY_INV_SIZE);
			if (inventory.getSizeInventory() != inventorySize) {
				inventory = createInventory(inventorySize);
			}
		}
		lastSound++;
	}

	public GenericInventory getChestInventory() {
		return inventory;
	}

	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		return convertToItem();
	}

	@Override
	public EntityAgeable createChild(EntityAgeable entityageable) {
		return null;
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		if (!isDead) {
			final ItemStack heldItem = player.getHeldItemMainhand();
			if (heldItem.getItem() instanceof ItemNameTag) return false;

			if (world.isRemote) {
				if (player.isSneaking()) spawnPickupParticles();
			} else {
				if (player.isSneaking()) {
					ItemStack luggageItem = convertToItem();
					if (player.inventory.addItemStackToInventory(luggageItem)) setDead();
					playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.5f, world.rand.nextFloat() * 0.1f + 0.9f);

				} else {
					playSound(SoundEvents.BLOCK_CHEST_OPEN, 0.5f, world.rand.nextFloat() * 0.1f + 0.9f);
					player.openGui(OpenBlocks.instance, OpenBlocksGuiHandler.GuiId.luggage.ordinal(), player.world, getEntityId(), 0, 0);
				}
			}
		}
		return true;
	}

	@Override
	public boolean canBeLeashedTo(EntityPlayer player) {
		return false;
	}

	protected void spawnPickupParticles() {
		final double py = this.posY + this.height;
		for (int i = 0; i < 50; i++) {
			double vx = rand.nextGaussian() * 0.02D;
			double vz = rand.nextGaussian() * 0.02D;
			double px = this.posX + this.width * this.rand.nextFloat();
			double pz = this.posZ + this.width * this.rand.nextFloat();
			this.world.spawnParticle(EnumParticleTypes.PORTAL, px, py, pz, vx, -1, vz);
		}
	}

	protected ItemStack convertToItem() {
		ItemStack luggageItem = new ItemStack(OpenBlocks.Items.luggage);
		NBTTagCompound tag = itemTag != null? (NBTTagCompound)itemTag.copy() : new NBTTagCompound();

		inventory.writeToNBT(tag);
		luggageItem.setTagCompound(tag);

		String nameTag = getCustomNameTag();
		if (!Strings.isNullOrEmpty(nameTag)) luggageItem.setStackDisplayName(nameTag);
		return luggageItem;
	}

	public void restoreFromStack(ItemStack stack) {
		final NBTTagCompound tag = stack.getTagCompound();

		if (tag != null) {
			inventory.readFromNBT(tag);
			if (inventory.getSizeInventory() > SIZE_NORMAL) setSpecial();

			NBTTagCompound tagCopy = tag.copy();
			tagCopy.removeTag(GenericInventory.TAG_SIZE);
			tagCopy.removeTag(GenericInventory.TAG_ITEMS);
			this.itemTag = tagCopy.hasNoTags()? null : tagCopy;
		}

		if (stack.hasDisplayName()) setCustomNameTag(stack.getDisplayName());
	}

	public boolean canConsumeStackPartially(ItemStack stack) {
		return InventoryUtils.canInsertStack(inventory.getHandler(), stack);
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {
		playSound(OpenBlocks.Sounds.ENTITY_LUGGAGE_WALK, 0.3F, 0.7F + (world.rand.nextFloat() * 0.5f));
	}

	public void storeItemTag(NBTTagCompound itemTag) {
		this.itemTag = itemTag;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setBoolean(TAG_SHINY, special);
		inventory.writeToNBT(tag);
		if (itemTag != null) tag.setTag(TAG_ITEM_TAG, itemTag);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		if (tag.getBoolean(TAG_SHINY)) setSpecial();
		inventory.readFromNBT(tag);
		this.itemTag = tag.hasKey(TAG_ITEM_TAG, Constants.NBT.TAG_COMPOUND)? tag.getCompoundTag(TAG_ITEM_TAG) : null;
	}

	@Override
	public void onStruckByLightning(EntityLightningBolt lightning) {
		setSpecial();
	}

	@Override
	public boolean isEntityInvulnerable(DamageSource dmg) {
		return true;
	}

	@Override
	public void setHealth(float health) {
		// NO-OP
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
