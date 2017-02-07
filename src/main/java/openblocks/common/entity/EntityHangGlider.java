package openblocks.common.entity;

import com.google.common.collect.MapMaker;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import java.util.GregorianCalendar;
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
import openblocks.common.item.ItemHangGlider;
import openblocks.common.BeepGenerator;
import openmods.Log;
import openmods.OpenMods;

public class EntityHangGlider extends Entity implements IEntityAdditionalSpawnData {
	// Vario Config
	public static boolean varioActive = false;
	public static int varioVolume = 8;
	public static final int VOL_MIN = 2;
	public static final int VOL_MAX = 20;
	public static final int FREQ_MIN = 300;
	public static final int FREQ_AVG = 600;
	public static final int FREQ_MAX = 2000;
	public static final int BEEP_RATE_AVG = 4;
	public static final int BEEP_RATE_MAX = 24;
	public static final int TICKS_PER_VARIO_UPDATE = 4;
	public static final int THERMAL_HEIGTH_MIN = 70;
	public static final int THERMAL_HEIGTH_OPT = 110;
	public static final int THERMAL_HEIGTH_MAX = 136;
	public static final int THERMAL_STRONG_BONUS_HEIGTH = 100;
	public static final double VSPEED_NORMAL = -0.052;
	public static final double VSPEED_FAST = -0.176;
	public static final double VSPEED_MIN = -0.32;
	public static final double VSPEED_MAX = 0.4;

	private static final int PROPERTY_DEPLOYED = 17;

	private static Map<EntityPlayer, EntityHangGlider> gliderMap = new MapMaker().weakKeys().weakValues().makeMap();

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

	public static void toggleVario() {
		if (!varioActive) {
			varioActive = true;
		} else {
			varioActive = false;
		}
	}

	public static void incVarioVol() {
		varioVolume = Math.min((varioVolume + 2), VOL_MAX);
		BeepGenerator.setVolume((byte) varioVolume);
	}

	public static void decVarioVol() {
		varioVolume = Math.max((varioVolume - 2), VOL_MIN);
		BeepGenerator.setVolume((byte) varioVolume);
	}

	private EntityPlayer player;
	private NoiseGeneratorPerlin noiseGen;
	private BeepGenerator beeper;
	private int ticksSinceLastVarioUpdate = 0;
	private double avgVspeed = 0;
	private double lastMotionY = 0;

	public EntityHangGlider(World world) {
		super(world);
		this.noiseGen = new NoiseGeneratorPerlin(new Random(world.getCurrentDate().get(GregorianCalendar.DAY_OF_YEAR)), 2);
		BeepGenerator.setVolume((byte) varioVolume);
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

		boolean isDeployed = !player.onGround && !player.isInWater();

		if (!worldObj.isRemote) {
			this.dataWatcher.updateObject(PROPERTY_DEPLOYED, (byte)(isDeployed? 1 : 0));
			fixPositions(player, false);
		}

		if (isLocalPlayer() && Config.hanggliderEnableThermal && beeper == null)
			beeper = new BeepGenerator();

		if (isDeployed && player.motionY < lastMotionY) {
			final double horizontalSpeed;
			final double verticalSpeed;
			final double noise;

			if (Config.hanggliderEnableThermal)
				noise = getNoise();
			else
				noise = 0;

			final double vspeed = (noise >= 0 ? VSPEED_MAX : -VSPEED_MIN);

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

			if (isLocalPlayer()) {
				if (varioActive) {
					ticksSinceLastVarioUpdate++;
					avgVspeed += verticalSpeed / (double) TICKS_PER_VARIO_UPDATE;
					if (ticksSinceLastVarioUpdate > TICKS_PER_VARIO_UPDATE) {
						vario(avgVspeed);
						ticksSinceLastVarioUpdate = 0;
						avgVspeed = 0;
					}
				} else {
					stopVario();
				}
			}

			double x = Math.cos(Math.toRadians(player.rotationYawHead + 90)) * horizontalSpeed;
			double z = Math.sin(Math.toRadians(player.rotationYawHead + 90)) * horizontalSpeed;
			player.motionX += x;
			player.motionZ += z;
			player.fallDistance = 0f; /* Don't like getting hurt :( */
		} else if (isLocalPlayer() && varioActive) {
			stopVario();
		}
	}

	public EntityPlayer getPlayer() {
		return player;
	}

	public double getNoise() {
		double noise = (double) noiseGen.func_151601_a((float) player.posX / 20f,(float) player.posZ / 20f) / 4d;
		final boolean strong = (noise > 0.7 ? true : false);
		final int bonus = (strong ? THERMAL_STRONG_BONUS_HEIGTH : 0);
		final int biomeRain = worldObj.getBiomeGenForCoords((int) player.posX, (int) player.posZ).getIntRainfall();

		noise *= Math.min((Math.max((player.posY - (double) THERMAL_HEIGTH_MIN), 0d) / (double) (THERMAL_HEIGTH_OPT - THERMAL_HEIGTH_MIN)), 1d);
		noise *= Math.min((Math.max(((double) (THERMAL_HEIGTH_MAX + bonus) - player.posY), 0d) / (double) (THERMAL_HEIGTH_MAX - THERMAL_HEIGTH_OPT + bonus / 4)), 1d);

		int worldTime = (int) (worldObj.getWorldTime() % 24000);
		noise *= Math.min(((double) worldTime / 1000d), 1);
		noise *= Math.min(((double) Math.max((12000 - worldTime), 0) / 1000d), 1);

		if (player.dimension != 0)
			noise = 0;
		else if (worldObj.isRaining() && !strong)
			noise = (biomeRain > 0 ? -0.5 : 0);
		return noise;
	}

	@Override
	public void setDead() {
		super.setDead();
		gliderMap.remove(player);
		if (isLocalPlayer())
			stopVario();
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

	private boolean isLocalPlayer() {
		return worldObj.isRemote && OpenMods.proxy.isClientPlayer(player);
	}

	@SideOnly(Side.CLIENT)
	private void vario(double vspeed) {
		if (vspeed <= 0){
			vspeed = Math.max(VSPEED_MIN, vspeed);
			double freq = (vspeed - VSPEED_MIN) / Math.abs(VSPEED_MIN) * (double) (FREQ_AVG - FREQ_MIN) + (double) FREQ_MIN;
			beeper.setToneFrequency(freq);
			beeper.setBeepFrequency(0d);
		} else {
			vspeed = Math.min(VSPEED_MAX, vspeed);
			double freq = vspeed / Math.abs(VSPEED_MAX) * (double) (FREQ_MAX - FREQ_AVG) + (double) FREQ_AVG;
			double beepfreq = vspeed / Math.abs(VSPEED_MAX) * (double) (BEEP_RATE_MAX - BEEP_RATE_AVG) + (double) BEEP_RATE_AVG;
			beeper.setToneFrequency(freq);
			beeper.setBeepFrequency(beepfreq);
		}
		if (beeper.isRunning()) {
			beeper.keepAlive();
		} else {
			beeper.start();
		}
	}

	@SideOnly(Side.CLIENT)
	private void stopVario() {
		if (beeper != null) {
			beeper.stop();
			beeper = null;
		}
		ticksSinceLastVarioUpdate = 0;
		avgVspeed = 0;
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
