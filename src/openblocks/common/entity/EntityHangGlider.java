package openblocks.common.entity;

import java.util.WeakHashMap;

import openblocks.OpenBlocks;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityHangGlider extends Entity implements IEntityAdditionalSpawnData {

	private EntityPlayer player;
	
	public EntityHangGlider(World world) {
		super(world);
	}
	
	public EntityHangGlider(World world, EntityPlayer player) {
		this(world);
		this.player = player;
		OpenBlocks.proxy.gliderMap.put(player, this);
	}

	@Override
	protected void entityInit() {
	}
	
	@Override
	public void onUpdate() {
		if (player == null) {
			setDead();
		}else {
			ItemStack held = player.getHeldItem();
			if (held == null || held.getItem() == null || held.getItem() != OpenBlocks.Items.hangGlider) {
				if (worldObj.isRemote) { 
					OpenBlocks.proxy.gliderClientMap.remove(player);
				}else {
					OpenBlocks.proxy.gliderMap.remove(player);
				}
				setDead();
			}else {
				fixPositions();
				if (!player.onGround && player.motionY < 0 && !player.isSneaking()) {
				 	player.motionY *= 0.4;
					motionY *= 0.4;
					double x = Math.cos(Math.toRadians(player.rotationYawHead+90)) * 0.05;
					double z = Math.sin(Math.toRadians(player.rotationYawHead+90)) * 0.05;
					player.motionX += x;
					player.motionZ += z;
				}
			}
		}
	}
	
	public void fixPositions() {
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
	
		    this.motionX = this.posX - this.prevPosX;
		    this.motionY = this.posY - this.prevPosY;
		    this.motionZ = this.posZ - this.prevPosZ;
		}
	    
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
