package openblocks.common.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import io.netty.buffer.ByteBuf;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.common.entity.ai.EntityAIBreakBlock;
import openblocks.common.entity.ai.EntityAIPickupPlayer;
import openmods.Log;
import openmods.api.VisibleForDocumentation;
import openmods.network.event.EventDirection;
import openmods.network.event.NetworkEvent;
import openmods.network.event.NetworkEventMeta;
import openmods.utils.io.GameProfileSerializer;

@VisibleForDocumentation
public class EntityMiniMe extends CreatureEntity implements IEntityAdditionalSpawnData {

	@NetworkEventMeta(direction = EventDirection.S2C)
	public static class OwnerChangeEvent extends NetworkEvent {

		private GameProfile profile;

		private int entityId;

		public OwnerChangeEvent(int entityId, GameProfile profile) {
			this.profile = profile;
			this.entityId = entityId;
		}

		@Override
		protected void readFromStream(PacketBuffer input) {
			this.entityId = input.readVarInt();
			if (input.readBoolean()) {
				profile = GameProfileSerializer.read(input);
			}
		}

		@Override
		protected void writeToStream(PacketBuffer output) {
			output.writeVarInt(entityId);

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
			final World world = evt.sender.world;

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
		this.owner = owner != null? SkullTileEntity.updateGameprofile(owner) : null;
	}

	public EntityMiniMe(World world) {
		super(world);
		setSize(0.6F, 0.95F);
		enablePersistence();

		this.tasks.addTask(1, new SwimGoal(this));
		this.tasks.addTask(2, new EntityAIPickupPlayer(this));
		this.tasks.addTask(3, new EntityAIBreakBlock(this));
		this.tasks.addTask(4, new RandomWalkingGoal(this, 1.0D));
		this.tasks.addTask(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
		this.tasks.addTask(6, new LookRandomlyGoal(this));
	}

	@Override
	protected PathNavigator createNavigator(World worldIn) {
		final GroundPathNavigator navigator = new GroundPathNavigator(this, worldIn);
		setPathPriority(PathNodeType.WATER, -1.0F);
		navigator.setCanSwim(true);
		return navigator;
	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		if (pickupCooldown > 0) pickupCooldown--;
		if (wasRidden && !isBeingRidden()) {
			wasRidden = false;
			setPickupCooldown(1200);
		} else if (isBeingRidden()) {
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
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
	}

	@Override
	public void setCustomNameTag(String name) {
		super.setCustomNameTag(name);

		if (!world.isRemote) {
			if (name != null && (owner == null || !name.equalsIgnoreCase(owner.getName()))) {
				try {
					this.owner = SkullTileEntity.updateGameprofile(new GameProfile(null, name));
					propagateOwnerChange();
				} catch (Exception e) {
					Log.warn(e, "Failed to change skin to %s", name);
				}
			}
		}
	}

	private void propagateOwnerChange() {
		new OwnerChangeEvent(getEntityId(), owner).sendToEntity(this);
	}

	@SideOnly(Side.CLIENT)
	public ResourceLocation getSkinResourceLocation() {
		if (owner != null) {
			final SkinManager manager = Minecraft.getMinecraft().getSkinManager();
			Map<Type, MinecraftProfileTexture> map = manager.loadSkinFromCache(owner);

			if (map.containsKey(Type.SKIN)) {
				final MinecraftProfileTexture skin = map.get(Type.SKIN);
				return manager.loadSkin(skin, Type.SKIN);
			} else {
				UUID uuid = PlayerEntity.getUUID(owner);
				return DefaultPlayerSkin.getDefaultSkin(uuid);
			}
		}

		return null;
	}

	@Override
	protected boolean canDespawn() {
		return false;
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
			GameProfileSerializer.write(owner, new PacketBuffer(data));
		} else data.writeBoolean(false);
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		if (data.readBoolean()) {
			this.owner = GameProfileSerializer.read(new PacketBuffer(data));
		}
	}

	@Override
	public void writeEntityToNBT(CompoundNBT tag) {
		super.writeEntityToNBT(tag);

		if (owner != null) {
			CompoundNBT ownerTag = new CompoundNBT();
			NBTUtil.writeGameProfile(ownerTag, owner);
			tag.setTag("Owner", ownerTag);
		}

		tag.setInteger("pickupCooldown", pickupCooldown);
	}

	@Override
	public void readEntityFromNBT(CompoundNBT tag) {
		this.owner = readOwner(tag);

		// switched order, to prevent needless profile fetch in setCustomName
		super.readEntityFromNBT(tag);

		this.pickupCooldown = tag.getInteger("pickupCooldown");
	}

	private static GameProfile readOwner(CompoundNBT tag) {
		if (tag.hasKey("owner", Constants.NBT.TAG_STRING)) {
			String ownerName = tag.getString("owner");
			return SkullTileEntity.updateGameprofile(new GameProfile(null, ownerName));
		} else if (tag.hasKey("OwnerUUID", Constants.NBT.TAG_STRING)) {
			final String uuidStr = tag.getString("OwnerUUID");
			try {
				UUID uuid = UUID.fromString(uuidStr);
				return new GameProfile(uuid, null);
			} catch (IllegalArgumentException e) {
				Log.warn(e, "Failed to parse UUID: %s", uuidStr);
			}
		} else if (tag.hasKey("Owner", Constants.NBT.TAG_COMPOUND)) { return NBTUtil.readGameProfileFromNBT(tag.getCompoundTag("Owner")); }

		return null;
	}

}
