package openblocks.common.entity;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.common.entity.ai.EntityAIBreakBlock;
import openblocks.common.entity.ai.EntityAIPickupPlayer;
import openmods.Log;
import openmods.api.VisibleForDocumentation;
import openmods.network.event.*;
import openmods.utils.ByteUtils;
import openmods.utils.io.GameProfileSerializer;

import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.properties.Property;

@VisibleForDocumentation
public class EntityMiniMe extends EntityCreature implements IEntityAdditionalSpawnData {

	@NetworkEventMeta(direction = EventDirection.S2C, compressed = true)
	public static class OwnerChangeEvent extends NetworkEvent {

		private GameProfile profile;

		private int entityId;

		public OwnerChangeEvent(int entityId, GameProfile profile) {
			this.profile = profile;
			this.entityId = entityId;
		}

		@Override
		protected void readFromStream(DataInput input) throws IOException {
			this.entityId = ByteUtils.readVLI(input);
			if (input.readBoolean()) {
				profile = GameProfileSerializer.read(input);
			}
		}

		@Override
		protected void writeToStream(DataOutput output) throws IOException {
			ByteUtils.writeVLI(output, entityId);

			if (profile != null) {
				output.writeBoolean(true);
				GameProfileSerializer.write(profile, output);
			} else {
				output.writeBoolean(false);
			}
		}

	}

	public static class OwnerChangeHandler {
		@SubscribeEvent
		public void onProfileChange(OwnerChangeEvent evt) {
			final World world = evt.sender.worldObj;

			Entity e = world.getEntityByID(evt.entityId);

			if (e instanceof EntityMiniMe) {
				((EntityMiniMe)e).owner = evt.profile;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private ResourceLocation locationSkin;

	private GameProfile owner;

	private int pickupCooldown = 0;

	private boolean wasRidden = false;

	public EntityMiniMe(World world, GameProfile owner) {
		this(world);
		this.owner = owner != null? fetchFullProfile(owner) : null;
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
		return Objects.firstNonNull(getResourceLocation(), AbstractClientPlayer.locationStevePng);
	}

	@Override
	public void setCustomNameTag(String name) {
		super.setCustomNameTag(name);

		if (!worldObj.isRemote && MinecraftServer.getServer() != null) {
			if (name != null && (owner == null || !name.equalsIgnoreCase(owner.getName()))) {
				try {
					final GameProfile profile = MinecraftServer.getServer().func_152358_ax().func_152655_a(name);
					this.owner = profile != null? fetchFullProfile(profile) : null;
					propagateOwnerChange();
				} catch (Exception e) {
					Log.warn(e, "Failed to change skin to %s", name);
				}
			}
		}
	}

	private void propagateOwnerChange() {
		NetworkEventManager.INSTANCE.dispatcher().senders.entity.sendMessage(new OwnerChangeEvent(getEntityId(), owner), this);
	}

	private ResourceLocation getResourceLocation() {
		if (owner != null) {
			Minecraft minecraft = Minecraft.getMinecraft();
			Map<?, ?> map = minecraft.func_152342_ad().func_152788_a(owner);

			if (map.containsKey(Type.SKIN)) {
				final MinecraftProfileTexture skin = (MinecraftProfileTexture)map.get(Type.SKIN);
				return minecraft.func_152342_ad().func_152792_a(skin, Type.SKIN);
			}
		}

		return null;
	}

	private static GameProfile fetchFullProfile(GameProfile profile) {
		final Property property = Iterables.getFirst(profile.getProperties().get("textures"), null);
		return property != null? profile : MinecraftServer.getServer().func_147130_as().fillProfileProperties(profile, true);
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

	public GameProfile getOwner() {
		return owner;
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		if (owner != null) {
			data.writeBoolean(true);
			try {
				GameProfileSerializer.write(owner, new ByteBufOutputStream(data));
			} catch (IOException e) {
				throw Throwables.propagate(e);
			}
		} else data.writeBoolean(false);
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		if (data.readBoolean()) {
			try {
				this.owner = GameProfileSerializer.read(new ByteBufInputStream(data));
			} catch (IOException e) {
				throw Throwables.propagate(e);
			}
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);

		if (owner != null) {
			NBTTagCompound ownerTag = new NBTTagCompound();
			NBTUtil.func_152460_a(ownerTag, owner);
			tag.setTag("Owner", ownerTag);
		}

		tag.setInteger("pickupCooldown", pickupCooldown);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		this.owner = readOwner(tag);

		// switched order, to prevent needless profile fetch in setCustomName
		super.readEntityFromNBT(tag);

		this.pickupCooldown = tag.getInteger("pickupCooldown");
	}

	private static GameProfile readOwner(NBTTagCompound tag) {
		if (tag.hasKey("owner", Constants.NBT.TAG_STRING)) {
			String ownerName = tag.getString("owner");
			return MinecraftServer.getServer().func_152358_ax().func_152655_a(ownerName);
		} else if (tag.hasKey("OwnerUUID", Constants.NBT.TAG_STRING)) {
			final String uuidStr = tag.getString("OwnerUUID");
			try {
				UUID uuid = UUID.fromString(uuidStr);
				return new GameProfile(uuid, null);
			} catch (IllegalArgumentException e) {
				Log.warn(e, "Failed to parse UUID: %s", uuidStr);
			}
		} else if (tag.hasKey("Owner", Constants.NBT.TAG_COMPOUND)) { return NBTUtil.func_152459_a(tag.getCompoundTag("Owner")); }

		return null;
	}

}
