package openblocks.common.tileentity;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.Config;
import openblocks.common.MagnetWhitelists;
import openblocks.common.entity.EntityMiniMe;
import openmods.Log;
import openmods.api.IBreakAwareTile;
import openmods.api.IPlaceAwareTile;
import openmods.entity.EntityBlock;
import openmods.fakeplayer.FakePlayerPool;
import openmods.fakeplayer.FakePlayerPool.PlayerUser;
import openmods.fakeplayer.OpenModsFakePlayer;
import openmods.sync.SyncableEnum;
import openmods.tileentity.SyncedTileEntity;

public class TileEntityGoldenEgg extends SyncedTileEntity implements IPlaceAwareTile, IBreakAwareTile, ITickable {

	private static final float SPEED_CHANGE_RATE = 0.1f;
	private static final Random RANDOM = new Random();
	private static final int STAGE_CHANGE_TICK = 100;
	private static final int RISING_TIME = 400;
	private static final int FALLING_TIME = 10;
	public static final int MAX_HEIGHT = 5;
	private static final double STAGE_CHANGE_CHANCE = 0.8;
	private static final GameProfile MR_GLITCH = new GameProfile(UUID.fromString("d4d119aa-d410-488a-8734-0053577d4a1a"), null);

	public static enum State {
		INERT(0, 0, false) {
			@Override
			public State getNextState(TileEntityGoldenEgg target) {
				return target.tryRandomlyChangeState(STAGE_CHANGE_TICK, ROTATING_SLOW);
			}

			@Override
			public void onServerTick(TileEntityGoldenEgg target, WorldServer world) {
				target.tickCounter++;
			}
		},
		ROTATING_SLOW(1, 0, false) {
			@Override
			public State getNextState(TileEntityGoldenEgg target) {
				return target.tryRandomlyChangeState(STAGE_CHANGE_TICK, ROTATING_MEDIUM);
			}

			@Override
			public void onServerTick(TileEntityGoldenEgg target, WorldServer world) {
				target.tickCounter++;
			}
		},
		ROTATING_MEDIUM(10, 0, false) {
			@Override
			public State getNextState(TileEntityGoldenEgg target) {
				return target.tryRandomlyChangeState(STAGE_CHANGE_TICK, ROTATING_FAST);
			}

			@Override
			public void onServerTick(TileEntityGoldenEgg target, WorldServer world) {
				target.tickCounter++;
			}
		},
		ROTATING_FAST(50, 0, false) {
			@Override
			public State getNextState(TileEntityGoldenEgg target) {
				return target.tryRandomlyChangeState(STAGE_CHANGE_TICK, FLOATING);
			}

			@Override
			public void onServerTick(TileEntityGoldenEgg target, WorldServer world) {
				target.tickCounter++;
			}
		},
		FLOATING(100, 1.0f / RISING_TIME, true) {
			@Override
			public void onEntry(TileEntityGoldenEgg target) {
				target.tickCounter = RISING_TIME;
			}

			@Override
			public void onServerTick(TileEntityGoldenEgg target, WorldServer world) {
				target.tickCounter--;
				if (Config.eggCanPickBlocks && RANDOM.nextInt(6) == 0) {
					final BlockPos pos = target.getPos();
					final BlockPos targetPos = pos.add(RANDOM.nextInt(20) - 10, RANDOM.nextInt(2) - 1, RANDOM.nextInt(20) - 10);
					boolean canMove = MagnetWhitelists.instance.testBlock(target.getWorld(), targetPos);
					if (canMove) target.pickUpBlock(world, targetPos);
				}
			}

			@Override
			public State getNextState(TileEntityGoldenEgg target) {
				return (target.tickCounter <= 0)? FALLING : null;
			}
		},
		FALLING(150, -1.0f / FALLING_TIME, true) {
			@Override
			public void onEntry(TileEntityGoldenEgg target) {
				target.tickCounter = FALLING_TIME;
				target.dropBlocks();
			}

			@Override
			public void onServerTick(TileEntityGoldenEgg target, WorldServer world) {
				target.tickCounter--;
			}

			@Override
			public State getNextState(TileEntityGoldenEgg target) {
				return (target.tickCounter <= 0)? EXPLODING : null;
			}
		},
		EXPLODING(666, 0, true) {
			@Override
			public void onEntry(TileEntityGoldenEgg target) {
				target.explode();
			}

			@Override
			public State getNextState(TileEntityGoldenEgg target) {
				return null;
			}
		};

		public final float rotationSpeed;

		public final float progressSpeed;

		public final boolean specialEffects;

		public void onEntry(TileEntityGoldenEgg target) {}

		public void onServerTick(TileEntityGoldenEgg target, WorldServer world) {}

		public abstract State getNextState(TileEntityGoldenEgg target);

		private State(float rotationSpeed, float riseSpeed, boolean specialEffects) {
			this.rotationSpeed = rotationSpeed;
			this.progressSpeed = riseSpeed;
			this.specialEffects = specialEffects;
		}
	}

	public int tickCounter;

	private float rotation;
	private float progress;

	private float rotationSpeed;
	private float progressSpeed;

	private List<EntityBlock> blocks = Lists.newArrayList();
	private SyncableEnum<State> stage;

	private GameProfile owner;

	public float getRotation(float partialTickTime) {
		return rotation + rotationSpeed * partialTickTime;
	}

	public float getProgress(float partialTickTime) {
		return progress + progressSpeed * partialTickTime;
	}

	public float getOffset(float partialTickTime) {
		return getProgress(partialTickTime) * MAX_HEIGHT;
	}

	public State tryRandomlyChangeState(int delay, State nextState) {
		return (tickCounter % delay == 0) && (RANDOM.nextDouble() < STAGE_CHANGE_CHANCE)? nextState : null;
	}

	@Override
	protected void createSyncedFields() {
		stage = SyncableEnum.create(State.INERT);
	}

	private void pickUpBlock(final WorldServer world, final BlockPos pos) {
		FakePlayerPool.instance.executeOnPlayer(world, new PlayerUser() {

			@Override
			public void usePlayer(OpenModsFakePlayer fakePlayer) {
				EntityBlock block = EntityBlock.create(fakePlayer, world, pos);
				if (block != null) {
					block.setHasAirResistance(false);
					block.setHasGravity(false);
					block.motionY = 0.1;
					blocks.add(block);
					world.spawnEntity(block);
				}
			}
		});

	}

	private void dropBlocks() {
		for (EntityBlock block : blocks) {
			block.motionY = -0.9;
			block.setHasGravity(true);
		}

		blocks.clear();
	}

	private void explode() {
		world.setBlockToAir(pos);
		world.createExplosion(null, 0.5 + pos.getX(), 0.5 + pos.getY(), 0.5 + pos.getZ(), 2, true);
		EntityMiniMe miniMe = new EntityMiniMe(world, Objects.firstNonNull(owner, MR_GLITCH));
		miniMe.setPositionAndRotation(0.5 + pos.getX(), 0.5 + pos.getY(), 0.5 + pos.getZ(), 0, 0);
		world.spawnEntity(miniMe);
	}

	public State getState() {
		return stage.get();
	}

	@Override
	public void update() {
		State state = getState();

		if (world.isRemote) {
			rotationSpeed = (1 - SPEED_CHANGE_RATE) * rotationSpeed + SPEED_CHANGE_RATE * state.rotationSpeed;
			rotation += rotationSpeed;

			progressSpeed = (1 - SPEED_CHANGE_RATE) * progressSpeed + SPEED_CHANGE_RATE * state.progressSpeed;
			progress += progressSpeed;
		} else {
			if (world instanceof WorldServer) state.onServerTick(this, (WorldServer)world);

			State nextState = state.getNextState(this);
			if (nextState != null) {
				stage.set(nextState);
				nextState.onEntry(this);
				sync();
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);

		if (owner != null) {
			NBTTagCompound ownerTag = new NBTTagCompound();
			NBTUtil.writeGameProfile(ownerTag, owner);
			nbt.setTag("Owner", ownerTag);
		}

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		if (nbt.hasKey("owner", Constants.NBT.TAG_STRING)) {
			String ownerName = nbt.getString("owner");

			this.owner = TileEntitySkull.updateGameprofile(new GameProfile(null, ownerName));
		} else if (nbt.hasKey("OwnerUUID", Constants.NBT.TAG_STRING)) {
			final String uuidStr = nbt.getString("OwnerUUID");
			try {
				UUID uuid = UUID.fromString(uuidStr);
				this.owner = new GameProfile(uuid, null);
			} catch (IllegalArgumentException e) {
				Log.warn(e, "Failed to parse UUID: %s", uuidStr);
			}
		} else if (nbt.hasKey("Owner", Constants.NBT.TAG_COMPOUND)) {
			this.owner = NBTUtil.readGameProfileFromNBT(nbt.getCompoundTag("Owner"));
		}

	}

	@Override
	public void onBlockBroken() {
		dropBlocks();
	}

	@Override
	public void onBlockPlacedBy(IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (!world.isRemote && placer instanceof EntityPlayer) {
			this.owner = ((EntityPlayer)placer).getGameProfile();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.getX(), -1024, pos.getZ(), pos.getX() + 1, 1024, pos.getZ() + 1);
	}

}
