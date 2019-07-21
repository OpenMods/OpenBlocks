package openblocks.common.entity;

import com.google.common.collect.ImmutableSet;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.walkers.ItemStackData;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.OpenBlocks.Items;
import openblocks.client.renderer.entity.EntitySelectionHandler.ISelectAware;
import openblocks.common.MapDataBuilder;
import openblocks.common.MapDataBuilder.ChunkJob;
import openblocks.common.item.ItemEmptyMap;
import openblocks.common.item.ItemHeightMap;
import openmods.Log;
import openmods.api.VisibleForDocumentation;
import openmods.sync.ISyncMapProvider;
import openmods.sync.SyncMap;
import openmods.sync.SyncMapClient;
import openmods.sync.SyncMapEntity;
import openmods.sync.SyncMapServer.UpdateStrategy;
import openmods.sync.SyncObjectScanner;
import openmods.sync.SyncableBoolean;
import openmods.sync.SyncableInt;
import openmods.sync.SyncableObjectBase;
import openmods.utils.BitSet;
import openmods.utils.ItemUtils;

@VisibleForDocumentation
public class EntityCartographer extends EntityAssistant implements ISelectAware, ISyncMapProvider {

	public static final String TAG_MAP_ITEM = "MapItem";
	private static final int MAP_JOB_DELAY = 5;
	private static final int MOVE_DELAY = 35;

	public static final Random RANDOM = new Random();

	@SideOnly(Side.CLIENT)
	public float eyeYaw, eyePitch, targetYaw, targetPitch;

	public static class MapJobs extends SyncableObjectBase {
		private final BitSet bits = new BitSet();
		private Set<ChunkJob> jobs;
		private int size;

		public boolean test(int bit) {
			return bits.testBit(bit);
		}

		@Override
		public void readFromStream(PacketBuffer input) {
			size = input.readVarInt();
			bits.readFromBuffer(input);
		}

		@Override
		public void writeToStream(PacketBuffer output) {
			output.writeVarInt(size);
			bits.writeToBuffer(output);
		}

		@Override
		public void writeToNBT(CompoundNBT tag, String name) {
			CompoundNBT result = new CompoundNBT();
			bits.writeToNBT(result);
			tag.setTag(name, result);
		}

		@Override
		public void readFromNBT(CompoundNBT tag, String name) {
			CompoundNBT info = tag.getCompoundTag(name);
			bits.readFromNBT(info);
		}

		public void runJob(World world, int x, int z) {
			if (jobs == null) {
				Log.severe("STOP ABUSING CARTOGRAPHER RIGHT NOW! YOU BROKE IT!");
				jobs = ImmutableSet.of();
			}
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
			builder.resizeIfNeeded(bits); // better to lost progress than to break world

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

	@Nonnull
	private ItemStack mapItem = ItemStack.EMPTY;
	private int mappingDimension;

	private int countdownToAction = MAP_JOB_DELAY;
	private int countdownToMove = MOVE_DELAY;
	private float randomDelta;

	private final SyncMap syncMap;

	{
		setSize(0.4f, 0.4f);
	}

	public EntityCartographer(World world) {
		super(world, null);
		this.syncMap = createSyncMap(world.isRemote);
	}

	private SyncMap createSyncMap(boolean isRemote) {
		final SyncMap syncMap = isRemote? new SyncMapClient() : new SyncMapEntity(this, UpdateStrategy.WITH_INITIAL_PACKET);
		SyncObjectScanner.INSTANCE.registerAllFields(syncMap, this);
		return syncMap;
	}

	public EntityCartographer(World world, PlayerEntity owner, @Nonnull ItemStack stack) {
		super(world, owner);
		setSpawnPosition(owner);

		this.syncMap = createSyncMap(world.isRemote);

		CompoundNBT tag = ItemUtils.getItemTag(stack);
		readOwnDataFromNBT(tag);
	}

	public static void registerFixes(DataFixer fixer) {
		fixer.registerWalker(FixTypes.ENTITY, new ItemStackData(EntityCartographer.class, TAG_MAP_ITEM));
	}

	@Override
	protected void entityInit() {}

	@Override
	public void onUpdate() {
		if (!world.isRemote) {
			float yaw = 0;
			if (isMapping.get()) {
				if (countdownToMove-- <= 0) {
					countdownToMove = MOVE_DELAY;
					randomDelta = 2 * (float)Math.PI * RANDOM.nextFloat();
				}
				yaw = randomDelta;
			} else {
				PlayerEntity owner = findOwner();
				if (owner != null) yaw = (float)Math.toRadians(owner.rotationYaw);

			}
			ownerOffsetX = MathHelper.sin(-yaw);
			ownerOffsetZ = MathHelper.cos(-yaw);
		}

		super.onUpdate();

		if (!world.isRemote) {
			if (world.provider.getDimension() == mappingDimension && isMapping.get() && countdownToAction-- <= 0) {
				jobs.runJob(world, (int)posX, (int)posZ);
				countdownToAction = MAP_JOB_DELAY;
			}

			syncMap.sendUpdates();
		}
	}

	@Override
	protected void readEntityFromNBT(CompoundNBT tag) {
		super.readEntityFromNBT(tag);
		readOwnDataFromNBT(tag);
	}

	private void readOwnDataFromNBT(CompoundNBT tag) {
		syncMap.tryRead(tag); // can be called on client!

		if (tag.hasKey(TAG_MAP_ITEM)) {
			CompoundNBT mapItem = tag.getCompoundTag(TAG_MAP_ITEM);
			this.mapItem = new ItemStack(mapItem);

			if (!this.mapItem.isEmpty() && isMapping.get()) {
				int mapId = ItemHeightMap.getMapId(this.mapItem);
				jobs.resumeMapping(world, mapId);
			}
			mappingDimension = tag.getInteger("Dimension");
		}
	}

	@Override
	protected void writeEntityToNBT(CompoundNBT tag) {
		super.writeEntityToNBT(tag);
		writeOwnDataToNBT(tag);
	}

	private void writeOwnDataToNBT(CompoundNBT tag) {
		// some mods may call it on client side, see #834
		syncMap.tryWrite(tag);

		if (!mapItem.isEmpty()) {
			CompoundNBT mapItem = this.mapItem.writeToNBT(new CompoundNBT());
			tag.setTag(TAG_MAP_ITEM, mapItem);
			tag.setInteger("Dimension", mappingDimension);
		}
	}

	@Override
	@Nonnull
	public ItemStack toItemStack() {
		if (Items.cartographer == null) return ItemStack.EMPTY;
		final ItemStack result = new ItemStack(Items.cartographer);
		CompoundNBT tag = ItemUtils.getItemTag(result);
		writeOwnDataToNBT(tag);
		return result;
	}

	@Override
	public boolean processInitialInteract(PlayerEntity player, Hand hand) {
		if (hand != Hand.MAIN_HAND) return true;

		final ItemStack holding = player.getHeldItemMainhand();
		if (player instanceof ServerPlayerEntity && player.isSneaking() && getDistance(player) < 3) {
			if (holding.isEmpty() && !mapItem.isEmpty()) {
				player.setHeldItem(hand, mapItem);
				mapItem = ItemStack.EMPTY;
				isMapping.toggle();
				jobs.stopMapping();
			} else if (!holding.isEmpty() && mapItem.isEmpty()) {
				Item itemType = holding.getItem();
				if (itemType instanceof ItemHeightMap || itemType instanceof ItemEmptyMap) {
					mapItem = holding.splitStack(1);
					mappingDimension = world.provider.getDimension();
					isMapping.toggle();
					mapItem = MapDataBuilder.upgradeToMap(world, mapItem);
					if (mapItem.getItem() instanceof ItemHeightMap) {
						int mapId = ItemHeightMap.getMapId(mapItem);
						jobs.startMapping(world, mapId, getNewMapCenterX(), getNewMapCenterZ());
					}
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
	public SyncMap getSyncMap() {
		return syncMap;
	}

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

	@Override
	public void writeSpawnData(ByteBuf data) {
		try {
			getSyncMap().writeInitializationData(new PacketBuffer(data));
		} catch (IOException e) {
			throw new IllegalStateException("Failed to write initial data for EntityCartographer", e);
		}
		super.writeSpawnData(data);
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		try {
			getSyncMap().readIntializationData(new PacketBuffer(data));
		} catch (IOException e) {
			throw new IllegalStateException("Failed to read initial data for EntityCartographer", e);
		}
		super.readSpawnData(data);
	}
}
