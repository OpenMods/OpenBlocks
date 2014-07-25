package openblocks.common.entity;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import openblocks.common.entity.ai.EntityAIBreakBlock;
import openblocks.common.entity.ai.EntityAIPickupPlayer;
import openmods.Log;

import com.google.common.base.Strings;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityMiniMe extends EntityCreature implements IEntityAdditionalSpawnData {

	@SideOnly(Side.CLIENT)
	private ResourceLocation locationSkin;

	private UUID owner;
	private String ownerSkin = "";
	private String loadedSkin;

	private int pickupCooldown = 0;

	private boolean wasRidden = false;

	public EntityMiniMe(World world, UUID owner, String ownerSkin) {
		this(world);
		this.owner = owner;
		this.ownerSkin = Strings.emptyToNull(ownerSkin);
	}

	public EntityMiniMe(World world) {
		super(world);
		setSize(0.6F, 0.95F);
		func_110163_bv();
		getNavigator().setAvoidsWater(true);
		getNavigator().setCanSwim(true);
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIPickupPlayer(this));
		this.tasks.addTask(3, new EntityAIBreakBlock(this));
		this.tasks.addTask(4, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(6, new EntityAILookIdle(this));
	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		if (pickupCooldown > 0) pickupCooldown--;
		if (wasRidden && riddenByEntity == null) {
			wasRidden = false;
			setPickupCooldown(1200);
		} else if (riddenByEntity != null) {
			wasRidden = true;
		}
	}

	@Override
	public double getMountedYOffset() {
		return height + 0.15;
	}

	public int getPickupCooldown() {
		return pickupCooldown;
	}

	public void setPickupCooldown(int cooldown) {
		pickupCooldown = cooldown;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3D);
	}

	@SideOnly(Side.CLIENT)
	public ResourceLocation getLocationSkin() {
		String newSkin = getPlayerSkin();

		if (locationSkin == null || !newSkin.equals(loadedSkin)) {
			locationSkin = AbstractClientPlayer.getLocationSkin(newSkin);
			AbstractClientPlayer.getDownloadImageSkin(locationSkin, newSkin);
		}
		loadedSkin = newSkin;
		return locationSkin;
	}

	public String getPlayerSkin() {
		return hasCustomNameTag()? getCustomNameTag() : ownerSkin;
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

	public UUID getOwner() {
		return owner;
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		ByteBufUtils.writeUTF8String(data, Strings.nullToEmpty(ownerSkin));
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		ownerSkin = ByteBufUtils.readUTF8String(data);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);

		if (owner != null) tag.setString("OwnerUUID", owner.toString());
		tag.setString("OwnerSkin", ownerSkin);
		tag.setInteger("pickupCooldown", pickupCooldown);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);

		String uuidString;
		if (tag.hasKey("owner", Constants.NBT.TAG_STRING)) {
			String ownerName = tag.getString("owner");
			uuidString = PreYggdrasilConverter.func_152719_a(ownerName);
			ownerSkin = ownerName;
		} else {
			uuidString = tag.getString("OwnerUUID");
			ownerSkin = tag.getString("OwnerSkin");
		}

		try {
			owner = UUID.fromString(uuidString);
		} catch (IllegalArgumentException e) {
			Log.warn(e, "Failed to parse UUID: %s", uuidString);
		}
		if (tag.hasKey("pickupCooldown")) pickupCooldown = tag.getInteger("pickupCooldown");
	}

}
