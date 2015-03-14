package openblocks.common.entity.ai;

import java.util.List;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.AxisAlignedBB;
import openblocks.common.entity.EntityLuggage;
import openmods.inventory.legacy.ItemDistribution;

public class EntityAICollectItem extends EntityAIBase {

	private EntityLuggage luggage = null;

	private PathNavigate pathFinder;

	private EntityItem targetItem = null;

	public EntityAICollectItem(EntityLuggage luggage) {
		this.luggage = luggage;
		this.pathFinder = luggage.getNavigator();
		setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {
		if (!pathFinder.noPath()) { return false; }
		if (luggage.worldObj != null) {
			@SuppressWarnings("unchecked")
			List<EntityItem> items = luggage.worldObj.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(luggage.posX - 1, luggage.posY - 1, luggage.posZ - 1, luggage.posX + 1, luggage.posY + 1, luggage.posZ + 1).expand(10.0, 10.0, 10.0));
			EntityItem closest = null;
			double closestDistance = Double.MAX_VALUE;
			for (EntityItem item : items) {
				if (!item.isDead && item.onGround) {
					double dist = item.getDistanceToEntity(luggage);
					if (dist < closestDistance
							&& luggage.canConsumeStackPartially(item.getEntityItem())
							&& !item.isInWater()) {
						closest = item;
						closestDistance = dist;
					}
				}
			}
			if (closest != null) {
				targetItem = closest;
				return true;
			}
		}
		return false;
	}

	@Override
	public void resetTask() {
		pathFinder.clearPathEntity();
		targetItem = null;
	}

	@Override
	public boolean continueExecuting() {
		return luggage.isEntityAlive() && !pathFinder.noPath()
				&& !targetItem.isDead;
	}

	@Override
	public void startExecuting() {
		if (targetItem != null) {
			pathFinder.tryMoveToXYZ(targetItem.posX, targetItem.posY, targetItem.posZ, 0.4f);
		}
	}

	@Override
	public void updateTask() {
		super.updateTask();
		if (!luggage.worldObj.isRemote) {
			if (targetItem != null && luggage.getDistanceToEntity(targetItem) < 1.0) {
				ItemStack stack = targetItem.getEntityItem();
				int preEatSize = stack.stackSize;
				ItemDistribution.insertItemIntoInventory(luggage.getInventory(), stack);
				// Check that the size changed
				if (preEatSize != stack.stackSize) {
					if (luggage.lastSound > 15) {
						boolean isFood = stack.getItem() instanceof ItemFood;
						luggage.playSound(isFood? "openblocks:luggage.eat.food" : "openblocks:luggage.eat.item",
								0.5f, 1.0f + (luggage.worldObj.rand.nextFloat() * 0.2f));
						luggage.lastSound = 0;
					}
					if (stack.stackSize == 0) {
						targetItem.setDead();
					}
				}
			}
		}
	}
}
