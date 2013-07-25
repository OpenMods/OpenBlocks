package openblocks.common.entity;

import openblocks.common.GenericInventory;
import openblocks.utils.BlockUtils;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;

public class EntityGhost extends EntityMob implements IEntityAdditionalSpawnData {

	private String playerName;
	protected GenericInventory inventory = new GenericInventory("ghost", false, 40);
	
	public EntityGhost(World world) {
		super(world);
		this.setSize(0.6F, 1.8F);
		this.health = this.getMaxHealth();
		this.moveSpeed = 0.5F;
		this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackOnCollide(this, EntityPlayer.class, this.moveSpeed, false));
        this.tasks.addTask(2, new EntityAIWander(this, this.moveSpeed));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(4, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 16.0F, 0, true));
		this.getNavigator().setAvoidsWater(true);
        this.texture = "/mob/char.png";
	}

	public EntityGhost(World world, String playerName, IInventory playerInvent) {
		this(world);
		this.playerName = playerName;
		this.skinUrl = "http://skins.minecraft.net/MinecraftSkins/" + StringUtils.stripControlCodes(playerName) + ".png";
		inventory.copyFrom(playerInvent);
	}
	
	public String getTranslatedEntityName() {
		return String.format("Ghost of %s", playerName);
	}
	
	public boolean func_94062_bN() {
		return true;
    }

	@Override
	protected boolean canDespawn() {
		return false;
	}
	
	@Override
    protected boolean isAIEnabled() {
        return true;
    }

	// maybe calculate the players worth?
	@Override
	public int getMaxHealth() {
		return 60;
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (playerName != null) {
			tag.setString("playerName", playerName);
		}
		inventory.writeToNBT(tag);
    }

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("playerName")) {
			playerName = tag.getString("playerName");
			skinUrl = "http://skins.minecraft.net/MinecraftSkins/" + StringUtils.stripControlCodes(playerName) + ".png";
			
		}
		inventory.readFromNBT(tag);
    }

	public void onDeath(DamageSource damageSource) {
		if (!worldObj.isRemote){ 
			BlockUtils.dropInventory(inventory, worldObj, posX, posY, posZ);
		}
		super.onDeath(damageSource);
    }
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
	}
	
	/**
	 * These two methods are for sending data down to the client
	 * When the mob first spawns
	 */

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {
		data.writeUTF(playerName == null ? "Unknown" : playerName);
	}


	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		playerName = data.readUTF();
		skinUrl = "http://skins.minecraft.net/MinecraftSkins/" + StringUtils.stripControlCodes(playerName) + ".png";
	}
}
