package openblocks.common.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import openblocks.common.tileentity.TileEntityDecoy;

public class EntityAIMoveTowardsDecoy extends EntityAIBase {

	private EntityCreature theEntity;
	private double movePosX;
	private double movePosY;
	private double movePosZ;
	private double movementSpeed;

	public EntityAIMoveTowardsDecoy(EntityCreature par1EntityCreature,
			double par2) {
		this.theEntity = par1EntityCreature;
		this.movementSpeed = par2;
		this.setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		TileEntityDecoy decoy = TileEntityDecoy
				.findNearestDecoyWithinRangeOfEntity(theEntity, 20.0);

		if (decoy == null) {
			return false;
		} else {
			this.movePosX = decoy.xCoord;
			this.movePosY = decoy.yCoord;
			this.movePosZ = decoy.zCoord;
			return true;
		}
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean continueExecuting() {
		return !this.theEntity.getNavigator().noPath();
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		this.theEntity.getNavigator().tryMoveToXYZ(this.movePosX,
				this.movePosY, this.movePosZ, this.movementSpeed);
	}
}
