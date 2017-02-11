package openblocks.common.entity;

import com.google.common.collect.MapMaker;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import openblocks.Config;
import openblocks.common.IVarioController;
import openblocks.common.Vario;
import openblocks.common.item.ItemHangGlider;
import openmods.Log;
import openmods.OpenMods;

public class EntityHangGlider extends Entity implements IEntityAdditionalSpawnData {

	public static final int THERMAL_HEIGTH_MIN = 70;
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

	private static final int PROPERTY_DEPLOYED = 17;

	private static Map<EntityPlayer, EntityHangGlider> gliderMap = new MapMaker().weakKeys().weakValues().makeMap();

	private IVarioController varioControl = IVarioController.NULL;

	public static boolean isEntityHoldingGlider(Entity player) {
		EntityHangGlider glider = gliderMap.get(player);
		return glider != null;
	}

	public static boolean isGliderDeployed(Entity player) {
		EntityHangGlider glider = gliderMap.get(player);
		return glider != null && glider.isDeployed();
	}

	private static boolean isGliderValid(EntityPlayer player, EntityHangGlider glider) {
		if (player == null || player.isDead || glider == null || glider.isDead) return false;

		ItemStack held = player.getHeldItem();
		if (held == null || !(held.getItem() instanceof ItemHangGlider)) return false;
		if (player.worldObj.provider.dimensionId != glider.worldObj.provider.dimensionId) return false;
		return true;
	}

	@SideOnly(Side.CLIENT)
	public static void updateGliders(World worldObj) {
		for (Map.Entry<EntityPlayer, EntityHangGlider> e : gliderMap.entrySet()) {
			EntityPlayer player = e.getKey();
			EntityHangGlider glider = e.getValue();
			if (isGliderValid(player, glider)) glider.fixPositions(player, player instanceof EntityPlayerSP);
			else glider.setDead();
		}
	}

	private EntityPlayer player;
	private NoiseGeneratorPerlin noiseGen;
	private int ticksSinceLastVarioUpdate = 0;
	private double verticalMotionSinceLastVarioUpdate = 0;
	private double lastMotionY = 0;

	public EntityHangGlider(World world) {
		super(world);
		this.noiseGen = new NoiseGeneratorPerlin(new Random(world.getCurrentDate().get(Calendar.DAY_OF_YEAR)), 2);
	}

	public EntityHangGlider(World world, EntityPlayer player) {
		this(world);
		this.player = player;
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		int playerId = data.readInt();

		Entity e = worldObj.getEntityByID(playerId);

		if (e instanceof EntityPlayer) {
			player = (EntityPlayer)e;
			gliderMap.put(player, this);

			if (OpenMods.proxy.isClientPlayer(player))
				varioControl = Vario.instance.acquire();

		} else {
			setDead();
		}
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		if (player == null) {
			Log.warn("Got glider without player id (%s)", this);
			data.writeInt(-42);
		} else {
			data.writeInt(player.getEntityId());
		}
	}

	@Override
	protected void entityInit() {
		this.dataWatcher.addObject(PROPERTY_DEPLOYED, (byte)1);
	}

	public boolean isDeployed() {
		return this.dataWatcher.getWatchableObjectByte(PROPERTY_DEPLOYED) == 1;
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

		boolean isDeployed = !player.onGround && !player.isInWater();

		if (!worldObj.isRemote) {
			this.dataWatcher.updateObject(PROPERTY_DEPLOYED, (byte)(isDeployed? 1 : 0));
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
				updateVario(0); // well, our vertical velocity is zero, right?
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

	public EntityPlayer getPlayer() {
		return player;
	}

	public double getNoise() {
		double noise = noiseGen.func_151601_a((float)player.posX / 20f, (float)player.posZ / 20f) / 4d;
		final boolean strong = (noise > 0.7? true : false);
		final int bonus = (strong? THERMAL_STRONG_BONUS_HEIGTH : 0);
		final int biomeRain = worldObj.getBiomeGenForCoords((int)player.posX, (int)player.posZ).getIntRainfall();

		noise *= Math.min((Math.max((player.posY - THERMAL_HEIGTH_MIN), 0d) / (THERMAL_HEIGTH_OPT - THERMAL_HEIGTH_MIN)), 1d);
		noise *= Math.min((Math.max((THERMAL_HEIGTH_MAX + bonus - player.posY), 0d) / (THERMAL_HEIGTH_MAX - THERMAL_HEIGTH_OPT + bonus / 4)), 1d);

		int worldTime = (int)(worldObj.getWorldTime() % 24000);
		noise *= Math.min((worldTime / 1000d), 1);
		noise *= Math.min((Math.max((12000 - worldTime), 0) / 1000d), 1);

		if (player.dimension != 0)
			noise = 0;
		else if (worldObj.isRaining() && !strong)
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

	private void fixPositions(EntityPlayer thePlayer, boolean localPlayer) {
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

		if (!localPlayer) {
			this.posY += 1.2;
			this.prevPosY += 1.2;
			this.lastTickPosY += 1.2;
		}

		this.motionX = this.posX - this.prevPosX;
		this.motionY = this.posY - this.prevPosY;
		this.motionZ = this.posZ - this.prevPosZ;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {}

	@Override
	public boolean writeToNBTOptional(NBTTagCompound p_70039_1_) {
		return false;
	}

}
