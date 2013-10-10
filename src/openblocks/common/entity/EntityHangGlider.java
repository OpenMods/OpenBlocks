package openblocks.common.entity;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemHangGlider;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityHangGlider extends Entity implements IEntityAdditionalSpawnData {

	private static Map<EntityPlayer, Integer> gliderMap = new WeakHashMap<EntityPlayer, Integer>();
	private static Map<EntityPlayer, Integer> gliderClientMap = new WeakHashMap<EntityPlayer, Integer>();

	public static Map<EntityPlayer, Integer> getMapForSide(boolean isRemote) {
		return isRemote? gliderClientMap : gliderMap;
	}

	public static boolean isEntityHoldingGlider(Entity player) {
		return gliderClientMap.containsKey(player);
	}

	public static boolean isPlayerOnGround(Entity player) {
		Integer gliderId = gliderClientMap.get(player);
		if (gliderId != null) {
			Entity glider = player.worldObj.getEntityByID(gliderId);
			if (glider instanceof EntityHangGlider) 
				return ((EntityHangGlider)glider).isPlayerOnGround();
		}
		
		return true;
	}

	@SideOnly(Side.CLIENT)
	public static void updateGliders(World worldObj) {
		Iterator<Entry<EntityPlayer, Integer>> it = gliderClientMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<EntityPlayer, Integer> next = it.next();
			EntityPlayer player = next.getKey();
			Entity entity = worldObj.getEntityByID(next.getValue());
			if (!(entity instanceof EntityHangGlider)) {
				continue;
			}
			EntityHangGlider glider = (EntityHangGlider) entity;
			if (player == null || player.isDead || glider == null || glider.isDead || player.getHeldItem() == null || !(player.getHeldItem().getItem() instanceof ItemHangGlider)
					|| player.worldObj.provider.dimensionId != glider.worldObj.provider.dimensionId) {
				glider.setDead();
				it.remove();
			} else {
				glider.fixPositions(Minecraft.getMinecraft().thePlayer);
			}

		}
	}

	private EntityPlayer player;
	/*
	 * Let the glider handle it's own disposal to centralize reference
	 * management in one place.
	 */
	private boolean shouldDespawn = false;

	public EntityHangGlider(World world) {
		super(world);
	}

	public EntityHangGlider(World world, EntityPlayer player) {
		this(world);
		this.player = player;
		gliderMap.put(player, entityId);
	}

	@Override
	protected void entityInit() {
		this.dataWatcher.addObject(2, Byte.valueOf((byte)0));
	}

	public void despawnGlider() {
		shouldDespawn = true;
	}

	public boolean isPlayerOnGround() {
		return this.dataWatcher.getWatchableObjectByte(2) == 1;
	}

	@Override
	public void onUpdate() {
		if (player == null) {
			setDead();
		} else {
			if (!worldObj.isRemote) {
				this.dataWatcher.updateObject(2, Byte.valueOf((byte)(player.onGround? 1 : 0)));
			}
			ItemStack held = player.getHeldItem();
			if (player.isDead || held == null || held.getItem() == null || held.getItem() != OpenBlocks.Items.hangGlider || shouldDespawn || player.dimension != this.dimension) {
				getMapForSide(worldObj.isRemote).remove(player);
				setDead();
			} else {
				fixPositions();
				double horizontalSpeed = 0.03;
				double verticalSpeed = 0.4;
				if (player.isSneaking()) {
					horizontalSpeed = 0.1;
					verticalSpeed = 0.7;
				}
				if (!player.onGround && player.motionY < 0) {
					player.motionY *= verticalSpeed;
					motionY *= verticalSpeed;
					double x = Math.cos(Math.toRadians(player.rotationYawHead + 90)) * horizontalSpeed;
					double z = Math.sin(Math.toRadians(player.rotationYawHead + 90)) * horizontalSpeed;
					player.motionX += x;
					player.motionZ += z;
					player.fallDistance = 0f; /* Don't like getting hurt :( */
				}
			}
		}
	}

	public EntityPlayer getPlayer() {
		return player;
	}

	private void fixPositions(EntityPlayer thePlayer) {

		if (player != null) {

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

			if (player != thePlayer) {
				this.posY += 1.2;
				this.prevPosY += 1.2;
				this.lastTickPosY += 1.2;
			}

			this.motionX = this.posX - this.prevPosX;
			this.motionY = this.posY - this.prevPosY;
			this.motionZ = this.posZ - this.prevPosZ;

		}

	}

	public void fixPositions() {
		fixPositions(null);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {
		if (player != null) {
			data.writeUTF(player.username);
		} else {
			data.writeUTF("[none]");
		}
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		String username = data.readUTF();
		if ("[none]".equals(username)) {
			setDead();
		} else {
			player = worldObj.getPlayerEntityByName(username);
			gliderClientMap.put(player, entityId);
		}
	}

}
