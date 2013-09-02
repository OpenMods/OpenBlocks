package openblocks.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import openblocks.OpenBlocks;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityHangGlider extends Entity implements
		IEntityAdditionalSpawnData {

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
		OpenBlocks.proxy.gliderMap.put(player, this);
	}

	@Override
	protected void entityInit() {}

	public void despawnGlider() {
		shouldDespawn = true;
	}

	@Override
	public void onUpdate() {
		if (player == null) {
			setDead();
		} else {
			ItemStack held = player.getHeldItem();
			if (player.isDead || held == null || held.getItem() == null
					|| held.getItem() != OpenBlocks.Items.hangGlider
					|| shouldDespawn) {
				if (worldObj.isRemote) {
					OpenBlocks.proxy.gliderClientMap.remove(player);
				} else {
					OpenBlocks.proxy.gliderMap.remove(player);
				}
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
					double x = Math.cos(Math.toRadians(player.rotationYawHead + 90))
							* horizontalSpeed;
					double z = Math.sin(Math.toRadians(player.rotationYawHead + 90))
							* horizontalSpeed;
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

	public void fixPositions(EntityPlayer thePlayer) {

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
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {
		data.writeUTF(player.username);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		player = worldObj.getPlayerEntityByName(data.readUTF());
		OpenBlocks.proxy.gliderClientMap.put(player, this);
	}

}
