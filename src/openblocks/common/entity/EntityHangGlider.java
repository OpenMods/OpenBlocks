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

	public static WeakHashMap<EntityPlayer, Integer> gliderMap = new WeakHashMap<EntityPlayer, Integer>();
	
	private EntityPlayer player;
	
	public EntityHangGlider(World world) {
		super(world);
	}
	
	public EntityHangGlider(World world, EntityPlayer player) {
		this(world);
		this.player = player;
	}

	@Override
	protected void entityInit() {
		if (player != null) {
			gliderMap.put(player, entityId);
		}
	}
	
	@Override
	public void onUpdate() {
		if (player == null) {
			setDead();
			gliderMap.remove(player);
		}else {
			ItemStack held = player.getHeldItem();
			if (held == null || held.getItem() == null || held.getItem() != OpenBlocks.Items.hangGlider) {
				setDead();
				gliderMap.remove(player);
			}else {
				
			    this.lastTickPosX = prevPosX = player.prevPosX;// * Math.sin(Math.toRadians(player.prevRenderYawOffset));
			    this.lastTickPosY = prevPosY = player.prevPosY;
			    this.lastTickPosZ = prevPosZ = player.prevPosZ;// * Math.cos(Math.toRadians(player.prevRenderYawOffset));

			    setPosition(posX, posY, posZ);
			    this.posX = player.posX;// * Math.sin(Math.toRadians(player.renderYawOffset));
			    this.posY = player.posY;
			    this.posZ = player.posZ;// * Math.cos(Math.toRadians(player.renderYawOffset));

			    this.prevRotationYaw = player.prevRotationYaw;
			    this.rotationYaw = player.rotationYaw;

			    this.prevRotationPitch = player.prevRotationPitch;
			    this.rotationPitch = player.rotationPitch;

			    this.motionX = this.posX - this.prevPosX;
			    this.motionY = this.posY - this.prevPosY;
			    this.motionZ = this.posZ - this.prevPosZ;
			    
				/*
				 if (!player.onGround || player.motionY < 0 && !player.isSneaking()) {
				 	player.motionY /= 3;
					motionY /= 3;
				}
				*/
			    
				System.out.println(posZ + ": "+player.posZ);
				
			}
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
		data.writeInt(player.entityId);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		player = (EntityPlayer)worldObj.getEntityByID(data.readInt());
		gliderMap.put(player, entityId);
	}

}
