package openblocks.common.entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Random;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import openblocks.OpenBlocks.Items;
import openblocks.client.renderer.entity.EntitySelectionHandler.ISelectAware;
import openblocks.common.MapDataBuilder;
import openblocks.common.MapDataBuilder.ChunkJob;
import openblocks.common.item.ItemCartographer;
import openblocks.common.item.ItemEmptyMap;
import openblocks.common.item.ItemHeightMap;
import openmods.sync.*;
import openmods.utils.BitSet;
import openmods.utils.ByteUtils;
import openmods.utils.ItemUtils;

import com.google.common.base.Preconditions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityCartographer extends EntityAssistant implements ISelectAware, ISyncHandler {

	private static final int MAP_JOB_DELAY = 5;
	private static final int MOVE_DELAY = 35;

	public static final Random RANDOM = new Random();

	@SideOnly(Side.CLIENT)
	public float eyeYaw, eyePitch, targetYaw, targetPitch;

	public static class MapJobs extends SyncableObjectBase {
		private BitSet bits = new BitSet();
		private Set<ChunkJob> jobs;
		private int size;

		public boolean test(int bit) {
			return bits.testBit(bit);
		}

		@Override
		public void readFromStream(DataInput input) throws IOException {
			size = ByteUtils.readVLI(input);
			bits.readFromStream(input);
		}

		@Override
		public void writeToStream(DataOutput output, boolean fullData) throws IOException {
			ByteUtils.writeVLI(output, size);
			bits.writeToStream(output);
		}

		@Override
		public void writeToNBT(NBTTagCompound tag, String name) {
			NBTTagCompound result = new NBTTagCompound();
			bits.writeToNBT(result);
			tag.setTag(name, result);
		}

		@Override
		public void readFromNBT(NBTTagCompound tag, String name) {
			NBTTagCompound info = tag.getCompoundTag(name);
			bits.readFromNBT(info);
		}

		public void runJob(World world, int x, int z) {
			Preconditions.checkNotNull(jobs, "Invalid usage of class");
			ChunkJob job = MapDataBuilder.doNextChunk(world, x, z, jobs);
			if (job != null) {
				jobs.remove(job);
				bits.setBit(job.bitNum);
				markDirty();
			}
		}

		public void resumeMapping(World world, int mapId) {
			MapDataBuilder builder = new MapDataBuilder(mapId);

			builder.loadMap(world);
			builder.resizeIfNeeded(bits); // better to lost progress than to
											// break world

			size = builder.size();
			jobs = builder.createJobs(bits);
			markDirty();
		}

		public void startMapping(World world, int mapId, int x, int z) {
			MapDataBuilder builder = new MapDataBuilder(mapId);

			builder.resetMap(world, x, z);
			builder.resize(bits);

			size = builder.size();
			jobs = builder.createJobs(bits);
			markDirty();
		}

		public void stopMapping() {
			jobs.clear();
			bits.resize(0);
			size = 0;
			markDirty();
		}

		public int size() {
			return size;
		}
	}

	public final SyncableInt scale = new SyncableInt(2);
	public final SyncableBoolean isMapping = new SyncableBoolean(false);
	public final MapJobs jobs = new MapJobs();

	private ItemStack mapItem;
	private int mappingDimension;

	private int countdownToAction = MAP_JOB_DELAY;
	private int countdownToMove = MOVE_DELAY;
	private float randomDelta;

	private final SyncMapEntity<EntityCartographer> syncMap = new SyncMapEntity<EntityCartographer>(this);

	{
		syncMap.autoregister();
		setSize(0.2f, 0.2f);
	}

	public EntityCartographer(World world) {
		super(world, null);
	}

	public EntityCartographer(World world, EntityPlayer owner, ItemStack stack) {
		super(world, owner);
		setSpawnPosition(owner);

		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		readOwnDataFromNBT(tag);
	}

	@Override
	protected void entityInit() {}

	@Override
	public void onUpdate() {
		if (!worldObj.isRemote) {
			float yaw = 0;
			if (isMapping.getValue()) {
				if (countdownToMove-- <= 0) {
					countdownToMove = MOVE_DELAY;
					randomDelta = 2 * (float)Math.PI * RANDOM.nextFloat();
				}
				yaw = randomDelta;
			} else {
				EntityPlayer owner = findOwner();
				if (owner != null) yaw = (float)Math.toRadians(owner.rotationYaw);

			}
			ownerOffsetX = MathHelper.sin(-yaw);
			ownerOffsetZ = MathHelper.cos(-yaw);
		}

		super.onUpdate();

		if (!worldObj.isRemote) {
			if (worldObj.provider.dimensionId == mappingDimension && isMapping.getValue() && countdownToAction-- <= 0) {
				jobs.runJob(worldObj, (int)posX, (int)posZ);
				countdownToAction = MAP_JOB_DELAY;
			}

			syncMap.sync();
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		readOwnDataFromNBT(tag);
	}

	private void readOwnDataFromNBT(NBTTagCompound tag) {
		syncMap.readFromNBT(tag);

		if (tag.hasKey("MapItem")) {
			NBTTagCompound mapItem = tag.getCompoundTag("MapItem");
			this.mapItem = ItemStack.loadItemStackFromNBT(mapItem);

			if (isMapping.getValue()) {
				int mapId = this.mapItem.getItemDamage();
				jobs.resumeMapping(worldObj, mapId);
			}
			mappingDimension = tag.getInteger("Dimension");
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		writeOwnDataToNBT(tag);
	}

	private void writeOwnDataToNBT(NBTTagCompound tag) {
		syncMap.writeToNBT(tag);

		if (mapItem != null) {
			NBTTagCompound mapItem = new NBTTagCompound();
			this.mapItem.writeToNBT(mapItem);
			tag.setTag("MapItem", mapItem);
			tag.setInteger("Dimension", mappingDimension);
		}
	}

	@Override
	public ItemStack toItemStack() {
		ItemStack result = Items.cartographer.createStack(ItemCartographer.AssistantType.CARTOGRAPHER);
		NBTTagCompound tag = ItemUtils.getItemTag(result);
		writeOwnDataToNBT(tag);
		return result;
	}

	@Override
	public boolean interactFirst(EntityPlayer player) {
		if (player instanceof EntityPlayerMP && player.isSneaking() && getDistanceToEntity(player) < 3) {
			ItemStack holding = player.getHeldItem();
			if (holding == null && mapItem != null) {
				player.setCurrentItemOrArmor(0, mapItem);
				mapItem = null;
				isMapping.toggle();
				jobs.stopMapping();
			} else if (holding != null && mapItem == null) {
				Item itemType = holding.getItem();
				if (itemType instanceof ItemHeightMap || itemType instanceof ItemEmptyMap) {
					ItemStack inserted = holding.splitStack(1);

					if (holding.stackSize <= 0) player.setCurrentItemOrArmor(0, null);

					mapItem = inserted;
					mappingDimension = worldObj.provider.dimensionId;
					isMapping.toggle();
					mapItem = MapDataBuilder.upgradeToMap(worldObj, mapItem);
					int mapId = mapItem.getItemDamage();
					jobs.startMapping(worldObj, mapId, getNewMapCenterX(), getNewMapCenterZ());
				}
			}

			return true;
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean canRenderOnFire() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public SyncMap<EntityCartographer> getSyncMap() {
		return syncMap;
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {}

	public int getNewMapCenterX() {
		return ((int)posX) & ~0x0F;
	}

	public int getNewMapCenterZ() {
		return ((int)posZ) & ~0x0F;
	}

	@SideOnly(Side.CLIENT)
	public void updateEye() {
		float diffYaw = (targetYaw - eyeYaw) % (float)Math.PI;
		float diffPitch = (targetPitch - eyePitch) % (float)Math.PI;

		if (Math.abs(diffYaw) + Math.abs(diffPitch) < 0.0001) {
			targetPitch = RANDOM.nextFloat() * 2 * (float)Math.PI;
			targetYaw = RANDOM.nextFloat() * 2 * (float)Math.PI;
		} else {
			// No, it's not supposed to be correct
			eyeYaw = eyeYaw - diffYaw / 50.0f; // HERP
			eyePitch = eyePitch - diffPitch / 50.0f; // DERP
		}
	}
}
