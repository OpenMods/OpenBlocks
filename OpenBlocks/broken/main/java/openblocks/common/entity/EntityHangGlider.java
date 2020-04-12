package openblocks.common.entity;

import com.google.common.collect.MapMaker;
import io.netty.buffer.ByteBuf;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.Config;
import openblocks.common.IVarioController;
import openblocks.common.Vario;
import openblocks.common.item.ItemHangGlider;
import openmods.Log;
import openmods.OpenMods;

public class EntityHangGlider extends Entity implements IEntityAdditionalSpawnData {

	private static final int THERMAL_HEIGTH_MIN = 70;
	public static final int THERMAL_HEIGTH_OPT = 110;
	public static final int THERMAL_HEIGTH_MAX = 136;
	public static final int THERMAL_STRONG_BONUS_HEIGTH = 100;

	public static final double VSPEED_NORMAL = -0.052;
	public static final double VSPEED_FAST = -0.176;
	public static final double VSPEED_MIN = -0.32;
	public static final double VSPEED_MAX = 0.4;

	private static final int TICKS_PER_VARIO_UPDATE = 4;

	public static final int FREQ_MIN = 300;
	public static final int FREQ_AVG = 600;
	public static final int FREQ_MAX = 2000;

	public static final int BEEP_RATE_AVG = 4;
	public static final int BEEP_RATE_MAX = 24;

	private static final DataParameter<Boolean> PROPERTY_DEPLOYED = EntityDataManager.createKey(EntityHangGlider.class, DataSerializers.BOOLEAN);

	private static final Map<LivingEntity, EntityHangGlider> gliderMap = new MapMaker().weakKeys().weakValues().makeMap();

	private IVarioController varioControl = IVarioController.NULL;

	public static boolean isHeldStackDeployedGlider(LivingEntity player, @Nonnull ItemStack heldStack) {
		if (player == null) return false;

		EntityHangGlider glider = gliderMap.get(player);
		if (glider == null || glider.handHeld == null) return false;
		// identity check, since we require exact instance
		return player.getHeldItem(glider.handHeld) == heldStack;
	}

	public static boolean isGliderDeployed(LivingEntity player) {
		EntityHangGlider glider = gliderMap.get(player);
		return glider != null && glider.isDeployed();
	}

	private static boolean isItemHangglider(@Nonnull ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof ItemHangGlider;
	}

	private static boolean isGliderValid(LivingEntity player, EntityHangGlider glider) {
		if (player == null || player.isDead || glider == null || glider.isDead) return false;

		if (glider.handHeld == null || !isItemHangglider(player.getHeldItem(glider.handHeld))) return false;
		if (player.world.provider.getDimension() != glider.world.provider.getDimension()) return false;
		if (player.isElytraFlying() || ((player instanceof PlayerEntity) && ((PlayerEntity)player).isSpectator())) return false;
		return true;
	}

	@SideOnly(Side.CLIENT)
	public static void updateGliders(World world) {
		gliderMap.forEach((player, glider) -> {
			if (isGliderValid(player, glider)) glider.fixPositions(player, player instanceof ClientPlayerEntity);
			else glider.setDead();
		});
	}

	private PlayerEntity player;
	private final PerlinNoiseGenerator noiseGen;
	private int ticksSinceLastVarioUpdate = 0;
	private double verticalMotionSinceLastVarioUpdate = 0;
	private double lastMotionY = 0;
	private Hand handHeld = Hand.MAIN_HAND;

	public EntityHangGlider(World world) {
		super(world);
		this.noiseGen = new PerlinNoiseGenerator(new Random(world.getCurrentDate().get(Calendar.DAY_OF_YEAR)), 2);
	}

	public EntityHangGlider(World world, PlayerEntity player, Hand spawnedHand) {
		this(world);
		this.player = player;
		this.handHeld = spawnedHand;
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		int playerId = data.readInt();

		Entity e = world.getEntityByID(playerId);

		if (e instanceof PlayerEntity) {
			player = (PlayerEntity)e;
			gliderMap.put(player, this);

			if (OpenMods.proxy.isClientPlayer(player))
				varioControl = Vario.instance.acquire();

		} else {
			setDead();
		}

		final PacketBuffer buf = new PacketBuffer(data);
		this.handHeld = buf.readEnumValue(Hand.class);
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		if (player == null) {
			Log.warn("Got glider without player id (%s)", this);
			data.writeInt(-42);
		} else {
			data.writeInt(player.getEntityId());
		}

		final PacketBuffer buf = new PacketBuffer(data);
		buf.writeEnumValue(handHeld);
	}

	@Override
	protected void entityInit() {
		this.dataManager.register(PROPERTY_DEPLOYED, false);
	}

	public boolean isDeployed() {
		return this.dataManager.get(PROPERTY_DEPLOYED);
	}

	@Override
	public void onUpdate() {
		if (!isGliderValid(player, this)) {
			setDead();
		}

		if (isDead) {
			gliderMap.remove(player);
			return;
		}

		varioControl.keepAlive();

		boolean isDeployed = !player.onGround && !player.isInWater() && !player.isPlayerSleeping();

		if (!world.isRemote) {
			this.dataManager.set(PROPERTY_DEPLOYED, isDeployed);
			fixPositions(player, false);
		}

		if (isDeployed) {
			if (player.motionY < lastMotionY) {

				final double noise = Config.hanggliderEnableThermal? getNoise() : 0;

				final double vspeed = (noise >= 0? VSPEED_MAX : -VSPEED_MIN);

				final double horizontalSpeed;
				final double verticalSpeed;
				if (player.isSneaking()) {
					horizontalSpeed = 0.1;
					verticalSpeed = Math.max((VSPEED_FAST + noise * vspeed), VSPEED_MIN);
				} else {
					horizontalSpeed = 0.03;
					verticalSpeed = Math.max((VSPEED_NORMAL + noise * vspeed), VSPEED_MIN);
				}

				player.motionY = verticalSpeed;
				motionY = verticalSpeed;
				lastMotionY = verticalSpeed;

				if (varioControl.isValid()) {
					ticksSinceLastVarioUpdate++;
					verticalMotionSinceLastVarioUpdate += verticalSpeed; // * 1 tick, for unit freaks
					if (ticksSinceLastVarioUpdate > TICKS_PER_VARIO_UPDATE) {
						updateVario(verticalMotionSinceLastVarioUpdate / TICKS_PER_VARIO_UPDATE);
						ticksSinceLastVarioUpdate = 0;
						verticalMotionSinceLastVarioUpdate = 0;
					}
				}

				double x = Math.cos(Math.toRadians(player.rotationYawHead + 90)) * horizontalSpeed;
				double z = Math.sin(Math.toRadians(player.rotationYawHead + 90)) * horizontalSpeed;
				player.motionX += x;
				player.motionZ += z;
				player.fallDistance = 0f; // Don't like getting hurt :( -- Mikee, probably
			}
		} else {
			if (varioControl.isValid()) {
				varioControl.setFrequencies(0, 0);
				ticksSinceLastVarioUpdate = 0;
				verticalMotionSinceLastVarioUpdate = 0;
			}
		}

	}

	private void updateVario(double vspeed) {
		if (vspeed <= 0) {
			vspeed = Math.max(VSPEED_MIN, vspeed);
			double freq = (vspeed - VSPEED_MIN) / Math.abs(VSPEED_MIN) * (FREQ_AVG - FREQ_MIN) + FREQ_MIN;
			varioControl.setFrequencies(freq, 0);
		} else {
			vspeed = Math.min(VSPEED_MAX, vspeed);
			double freq = vspeed / Math.abs(VSPEED_MAX) * (FREQ_MAX - FREQ_AVG) + FREQ_AVG;
			double beepfreq = vspeed / Math.abs(VSPEED_MAX) * (BEEP_RATE_MAX - BEEP_RATE_AVG) + BEEP_RATE_AVG;
			varioControl.setFrequencies(freq, beepfreq);
		}
	}

	public PlayerEntity getPlayer() {
		return player;
	}

	public Hand getHandHeld() {
		return handHeld;
	}

	public double getNoise() {
		double noise = noiseGen.getValue(player.posX / 20f, player.posZ / 20f) / 4d;
		final boolean strong = noise > 0.7;
		final int bonus = (strong? THERMAL_STRONG_BONUS_HEIGTH : 0);
		final BlockPos pos = player.getPosition();
		final float biomeRain = world.getBiomeForCoordsBody(pos).getRainfall();

		noise *= Math.min((Math.max((player.posY - THERMAL_HEIGTH_MIN), 0d) / (THERMAL_HEIGTH_OPT - THERMAL_HEIGTH_MIN)), 1d);
		noise *= Math.min((Math.max((THERMAL_HEIGTH_MAX + bonus - player.posY), 0d) / (THERMAL_HEIGTH_MAX - THERMAL_HEIGTH_OPT + bonus / 4)), 1d);

		int worldTime = (int)(world.getWorldTime() % 24000);
		noise *= Math.min((worldTime / 1000d), 1);
		noise *= Math.min((Math.max((12000 - worldTime), 0) / 1000d), 1);

		if (player.dimension != 0)
			noise = 0;
		else if (world.isRaining() && !strong)
			noise = (biomeRain > 0? -0.5 : 0);
		return noise;
	}

	@Override
	public void setDead() {
		super.setDead();
		gliderMap.remove(player);

		if (varioControl.isValid()) {
			varioControl.kill();
			varioControl.release();
			ticksSinceLastVarioUpdate = 0;
			verticalMotionSinceLastVarioUpdate = 0;
		}
	}

	private void fixPositions(LivingEntity thePlayer, boolean localPlayer) {
		this.lastTickPosX = prevPosX = player.prevPosX;
		this.lastTickPosY = prevPosY = player.prevPosY;
		this.lastTickPosZ = prevPosZ = player.prevPosZ;

		this.posX = player.posX;
		this.posY = player.posY;
		this.posZ = player.posZ;

		setPosition(posX, posY, posZ);
		this.prevRotationYaw = player.prevRenderYawOffset;
		this.rotationYaw = player.renderYawOffset;

		this.prevRotationPitch = player.prevRotationPitch;
		this.rotationPitch = player.rotationPitch;

		this.posY += 1.2;
		this.prevPosY += 1.2;
		this.lastTickPosY += 1.2;

		this.motionX = this.posX - this.prevPosX;
		this.motionY = this.posY - this.prevPosY;
		this.motionZ = this.posZ - this.prevPosZ;
	}

	@Override
	protected void readEntityFromNBT(CompoundNBT nbttagcompound) {}

	@Override
	protected void writeEntityToNBT(CompoundNBT nbttagcompound) {}

	@Override
	public boolean writeToNBTOptional(CompoundNBT p_70039_1_) {
		return false;
	}

}
