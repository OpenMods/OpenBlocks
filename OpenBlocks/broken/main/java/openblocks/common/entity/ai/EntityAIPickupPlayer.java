package openblocks.common.entity.ai;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.world.World;
import openblocks.common.entity.EntityMiniMe;

public class EntityAIPickupPlayer extends Goal {

	private final EntityMiniMe minime;
	private final PathNavigator pathFinder;
	private PlayerEntity targetPlayer;

	public EntityAIPickupPlayer(EntityMiniMe entity) {
		this.minime = entity;
		this.pathFinder = entity.getNavigator();
		setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {
		if (minime.getPickupCooldown() > 0) return false;
		if (!pathFinder.noPath()) return false;

		if (!minime.world.isRemote) {
			List<PlayerEntity> players = minime.world.getEntitiesWithinAABB(PlayerEntity.class, minime.getEntityBoundingBox().grow(10));
			for (PlayerEntity player : players) {
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
		pathFinder.clearPath();
		targetPlayer = null;
	}

	@Override
	public boolean shouldContinueExecuting() {
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
		World world = minime.world;
		if (!world.isRemote && canRidePlayer(targetPlayer)) {
			if (minime.getDistance(targetPlayer) < 1.0) {
				targetPlayer.startRiding(minime);
			}
		}
	}

	private boolean canRidePlayer(PlayerEntity player) {
		final GameProfile owner = minime.getOwner();
		return owner != null && player != null &&
				player.getGameProfile().getId().equals(owner.getId()) &&
				player.isRiding();
	}
}
