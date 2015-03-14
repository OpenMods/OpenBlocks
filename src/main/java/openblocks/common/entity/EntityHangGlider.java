package openblocks.common.entity;

import io.netty.buffer.ByteBuf;

import java.util.Map;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import openblocks.common.item.ItemHangGlider;
import openmods.Log;

import com.google.common.collect.MapMaker;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityHangGlider extends Entity implements IEntityAdditionalSpawnData {
	private static final int PROPERTY_DEPLOYED = 17;

	private static Map<EntityPlayer, EntityHangGlider> gliderMap = new MapMaker().weakKeys().weakValues().makeMap();

	public static boolean isEntityHoldingGlider(Entity player) {
		EntityHangGlider glider = gliderMap.get(player);
		return glider != null;
	}

	public static boolean isGliderDeployed(Entity player) {
		EntityHangGlider glider = gliderMap.get(player);
		return glider == null || glider.isDeployed();
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

	public EntityHangGlider(World world) {
		super(world);
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

		boolean isDeployed = player.onGround || player.isInWater();

		if (!worldObj.isRemote) {
			this.dataWatcher.updateObject(PROPERTY_DEPLOYED, (byte)(isDeployed? 1 : 0));
			fixPositions(player, false);
		}

		if (!isDeployed && player.motionY < 0) {
			final double horizontalSpeed;
			final double verticalSpeed;

			if (player.isSneaking()) {
				horizontalSpeed = 0.1;
				verticalSpeed = 0.7;
			} else {
				horizontalSpeed = 0.03;
				verticalSpeed = 0.4;
			}

			player.motionY *= verticalSpeed;
			motionY *= verticalSpeed;
			double x = Math.cos(Math.toRadians(player.rotationYawHead + 90)) * horizontalSpeed;
			double z = Math.sin(Math.toRadians(player.rotationYawHead + 90)) * horizontalSpeed;
			player.motionX += x;
			player.motionZ += z;
			player.fallDistance = 0f; /* Don't like getting hurt :( */
		}

	}

	public EntityPlayer getPlayer() {
		return player;
	}

	@Override
	public void setDead() {
		super.setDead();
		gliderMap.remove(player);
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
