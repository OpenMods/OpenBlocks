package openblocks.common.entity;

import openblocks.common.entity.ai.EntityAIBreakCrop;
import openblocks.common.entity.ai.EntityAICollectItem;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityMiniMe extends EntityCreature implements IEntityAdditionalSpawnData {
	
	@SideOnly(Side.CLIENT)
	private ThreadDownloadImageData downloadImageSkin;
	
	@SideOnly(Side.CLIENT)
    private ResourceLocation locationSkin;
	
	private String username = "[nobody]";
	
	public EntityMiniMe(World world, String username) {
		this(world);
		this.username = username;
	}
	
	public EntityMiniMe(World world) {
		super(world);
		setSize(0.6F, 1.5F);
		func_110163_bv();
		getNavigator().setAvoidsWater(true);
		getNavigator().setCanSwim(true);
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIBreakCrop(this));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(4, new EntityAILookIdle(this));
	}
	
	@Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.3D);
    }

	@SideOnly(Side.CLIENT)
    public ResourceLocation getLocationSkin() {
		if (locationSkin == null) {
            locationSkin = AbstractClientPlayer.getLocationSkin(username);
            downloadImageSkin = AbstractClientPlayer.getDownloadImageSkin(locationSkin, username);
		}
        return locationSkin;
    }

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}
	
	@Override
    public boolean isChild() {
        return true;
    }

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {
		data.writeUTF(username);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		username = data.readUTF();
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setString("username", username);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		if (tag.hasKey("username")) {
			username = tag.getString("username");
		}
	}

}
