package openblocks.common.entity.ai;

import java.util.List;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.world.World;
import openblocks.common.entity.EntityMiniMe;
import openmods.utils.WorldUtils;

import com.mojang.authlib.GameProfile;

public class EntityAIPickupPlayer extends EntityAIBase {

	private EntityMiniMe minime;
	private PathNavigate pathFinder;
	private EntityPlayer targetPlayer;

	public EntityAIPickupPlayer(EntityMiniMe entity) {
		this.minime = entity;
		this.pathFinder = entity.getNavigator();
		setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {
		if (minime.getPickupCooldown() > 0) return false;
		if (!pathFinder.noPath()) return false;

		if (!minime.worldObj.isRemote) {
			List<EntityPlayer> players = WorldUtils.getEntitiesWithinAABB(minime.worldObj, EntityPlayer.class, minime.boundingBox.expand(10, 10, 10));
			for (EntityPlayer player : players) {
				if (canRidePlayer(player)) {
					targetPlayer = player;
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void resetTask() {
		pathFinder.clearPathEntity();
		targetPlayer = null;
	}

	@Override
	public boolean continueExecuting() {
		return minime.isEntityAlive() &&
				!pathFinder.noPath() &&
				canRidePlayer(targetPlayer);
	}

	@Override
	public void startExecuting() {
		if (targetPlayer != null) {
			pathFinder.tryMoveToXYZ(targetPlayer.posX, targetPlayer.posY, targetPlayer.posZ, 1f);
		}
	}

	@Override
	public void updateTask() {
		super.updateTask();
		World world = minime.worldObj;
		if (!world.isRemote && canRidePlayer(targetPlayer)) {
			if (minime.getDistanceToEntity(targetPlayer) < 1.0) {
				targetPlayer.mountEntity(minime);
			}
		}
	}

	private boolean canRidePlayer(EntityPlayer player) {
		final GameProfile owner = minime.getOwner();
		return owner != null && player != null &&
				player.getGameProfile().getId().equals(owner.getId()) &&
				player.ridingEntity == null;
	}
}
