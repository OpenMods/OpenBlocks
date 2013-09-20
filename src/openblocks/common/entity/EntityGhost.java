package openblocks.common.entity;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import openblocks.common.GenericInventory;
import openblocks.sync.ISyncHandler;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncMap;
import openblocks.sync.SyncMapEntity;
import openblocks.sync.SyncableFlags;
import openblocks.sync.SyncableFloat;
import openblocks.utils.BlockUtils;
import openblocks.utils.CompatibilityUtils;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityGhost extends EntityMob implements
		IEntityAdditionalSpawnData, ISyncHandler {

	private String playerName;
	/**
	 * Just disable it for now while I work on other stuff.
	 */
	private static final boolean DISABLE_HEAD_ANIMATION = true;

	protected GenericInventory inventory = new GenericInventory("ghost", false, 100);
	/**
	 * Is this Ghost an aggressive scary attacking ghost, or a sad wandering
	 * safe ghost
	 */
	public boolean aggresive;

	public GhostModifier modifier = GhostModifier.NONE;

	/**
	 * Modifiers to a ghost that change the way it is rendered Based on how the
	 * player died.
	 */
	public enum GhostModifier {
		NONE, FIRE, ARROW, WATER
	}

	/**
	 * Keys of values that are synced when they change
	 *
	 */
	public enum SyncKeys {
		FLAGS, OPACITY
	}

	/**
	 * Keys of booleans that are packed into the 'flags' member
	 *
	 */
	public enum FlagKeys {
		IS_FLYING, HEAD_IN_HAND, IS_IDLE
	}

	/**
	 * A map of synced values that get automatically synced down to the client
	 * It has a default tracking range of 20, and new users will receive full
	 * changes, but then they receive only changed. A change mask is
	 * automatically sent along with the changeset. Currently stores up to 16
	 * values, but I might make this configurable
	 */
	SyncMapEntity syncMap = new SyncMapEntity();

	/**
	 * 16 packed booleans in a short. If any of the booleans change the whole
	 * short gets send down to the client (boohoo).
	 */
	SyncableFlags flags = new SyncableFlags();

	/**
	 * The ghosts opacity
	 */
	SyncableFloat opacity = new SyncableFloat(0.3f);

	public EntityGhost(World world) {
		super(world);
		this.setSize(0.6F, 1.8F);
		setHealth( CompatibilityUtils.getEntityMaxHealth(this));
		setAIMoveSpeed(0.5F);
		this.tasks.addTask(0, new EntityAISwimming(this));
		// this.tasks.addTask(1, new EntityAIDragPlayer(this, 8.0F));
		this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, getAIMoveSpeed(), false));
		this.tasks.addTask(3, new EntityAIWander(this, getAIMoveSpeed() * 0.1f));
		this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));

		// this.tasks.addTask(4, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
		this.getNavigator().setAvoidsWater(true);

		syncMap.put(SyncKeys.FLAGS, flags);
		syncMap.put(SyncKeys.OPACITY, opacity);
	}

	public EntityGhost(World world, String playerName, IInventory playerInvent) {
		this(world);
		this.playerName = playerName;
		// use the dead players skin (ew)
		//if (world.isRemote) {
//			this.skinUrl = "http://skins.minecraft.net/MinecraftSkins/"
					//+ StringUtils.stripControlCodes(playerName) + ".png";
		//}
		// copy the inventory from the player inventory
		inventory.copyFrom(playerInvent);
	}

	/* Following the code of EntityFlying */
	@Override
	protected void fall(float par1) {}

	@Override
	protected void updateFallState(double par1, boolean par3) {}

	@Override
	public boolean isOnLadder() {
		return false;
	}

	private boolean shouldBeFlying() {

		EntityLivingBase attackTarget = getAITarget();

		// if we have an attack target
		if (attackTarget != null) {

			// and the distance is bigger than 2.0
			float distanceToEntity = attackTarget.getDistanceToEntity(this);
			if (distanceToEntity > 2.0f) {
				// lets fly to them!
				return true;
			}
		}
		// if we're still in the "flight" cooldown, we still want to fly
		if (flags.ticksSinceSet(FlagKeys.IS_FLYING) < 20) { return true; }

		// na, lets not fly
		return false;
	}

	public boolean shouldRenderFlying() {
		return flags.get(FlagKeys.IS_FLYING);
	}

	public float getOpacity() {
		return opacity.getValue();
	}

	public boolean hasHeadInHand() {
		return flags.get(FlagKeys.HEAD_IN_HAND);
	}

	public int ticksSinceHeadChange() {
		return flags.ticksSinceChange(FlagKeys.HEAD_IN_HAND);
	}

	@Override
	public void onLivingUpdate() {

		super.onLivingUpdate();

		if (!worldObj.isRemote) {

			opacity.setValue(0.3f);

			boolean isIdle = getAITarget() == null;

			// sync a flag that says if we should be flying or not
			flags.set(FlagKeys.IS_FLYING, shouldBeFlying());
			flags.set(FlagKeys.IS_IDLE, isIdle);

			int sinceIdle = flags.ticksSinceSet(FlagKeys.IS_IDLE);

			boolean headInHand = flags.get(FlagKeys.HEAD_IN_HAND);

			int ticksSinceHeadChange = flags.ticksSinceChange(FlagKeys.HEAD_IN_HAND);

			if (DISABLE_HEAD_ANIMATION) {
				headInHand = false;
			} else {
				// if we're not idle, we dont want the head/hand behaviour
				if (!isIdle) {
					headInHand = false;
				} else {
					if (!headInHand
							&& Math.min(sinceIdle, ticksSinceHeadChange) > 50 + (20 * worldObj.rand.nextDouble())) {
						headInHand = true;
					} else if (headInHand && ticksSinceHeadChange > 50) {
						headInHand = false;
					}
				}
			}

			if (flags.get(FlagKeys.IS_FLYING)
					&& worldObj.getHeightValue((int)posX, (int)posZ) + 1 > posY) {
				/*
				 * Flying up causes the entity onGround to be false, nullifying
				 * any movement. There is one option, override the movement
				 * methods and implement them ourselves. This does provide the
				 * opportunity for the ghost to fly through walls, but does make
				 * life harder.
				 *
				 * Also some research needs to be done regarding how this fits
				 * with AI
				 */
				motionY = Math.max(motionY, 0.1D); /* Fly over blocks */
			}

			flags.set(FlagKeys.HEAD_IN_HAND, headInHand);

		}

		// send all our data to nearby users
		syncMap.sync(worldObj, this, posX, posY, posZ);
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {
		// this is called on the client whenever a value has been sycned
		// it passes a list of the objects that have changed
	}

	@Override
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

	public String getPlayerName() {
		return playerName;
	}

	// TODO: Solve the implementation of this

	// maybe calculate the players worth?
	//@Override
	//public int getMaxHealth() {
	//	return 60;
	//}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (playerName != null) {
			tag.setString("playerName", playerName);
		}
		inventory.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("playerName")) {
			playerName = tag.getString("playerName");
			// Depreciated
			//if (worldObj.isRemote) {
//				skinUrl = "http://skins.minecraft.net/MinecraftSkins/"
						//+ StringUtils.stripControlCodes(playerName) + ".png";
			//}
		}
		inventory.readFromNBT(tag);
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		if (!worldObj.isRemote) {
			BlockUtils.dropInventory(inventory, worldObj, posX, posY, posZ);
		}
		super.onDeath(damageSource);
	}

	// private boolean hasRoomToFly(){
	// // Get my bounding box, copy it
	// AxisAlignedBB axisalignedbb = this.boundingBox.copy();
	// axisalignedbb.maxY += 1; /* Make it a meter higher to avoid all bad
	// obsticles */
	// return this.worldObj.getCollidingBoundingBoxes(this,
	// axisalignedbb).isEmpty();
	// }
	//
	// private boolean targetIsAboveMe() {
	// if(OpenBlocks.proxy.isClient() || getAttackTarget() == null)
	// return false;
	// return getAttackTarget().posY > posY;
	// }
	//
	// /* Replacement to the onLadder crap */
	// private boolean shouldFly() {
	// return (isCollidedHorizontally || targetIsAboveMe()) && hasRoomToFly();
	// }
	//
	// /* Used when moving this mob around the place */
	// @Override
	// public boolean isOnLadder() {
	// return false; /* We handle this in our onUpdate */
	// }
	//
	// @Override
	// public void onUpdate() {
	// super.onUpdate();
	// /* Small tinker with the ladder code */
	// if(shouldFly()) /* Handle the or case, which EntityLiving neglects */
	// motionY = 0.2D;
	// }

	/**
	 * These two methods are for sending data down to the client When the mob
	 * first spawns
	 */

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {
		data.writeUTF(playerName == null? "Unknown" : playerName);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		playerName = data.readUTF();
		//skinUrl = "http://skins.minecraft.net/MinecraftSkins/"
				//+ StringUtils.stripControlCodes(playerName) + ".png";
	}

	@Override
	public SyncMap getSyncMap() {
		return syncMap;
	}

	@Override
	public void writeIdentifier(DataOutputStream dos) throws IOException {
		dos.writeInt(entityId);
	}

}
