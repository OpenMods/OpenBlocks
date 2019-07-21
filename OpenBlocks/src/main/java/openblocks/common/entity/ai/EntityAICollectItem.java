package openblocks.common.entity.ai;

import java.util.List;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.UseAction;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraftforge.items.ItemHandlerHelper;
import openblocks.OpenBlocks;
import openblocks.common.entity.EntityLuggage;
import openmods.utils.ItemUtils;

public class EntityAICollectItem extends Goal {

	private EntityLuggage luggage;

	private final PathNavigator pathFinder;

	private ItemEntity targetItem = null;

	public EntityAICollectItem(EntityLuggage luggage) {
		this.luggage = luggage;
		this.pathFinder = luggage.getNavigator();
		setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {
		if (!pathFinder.noPath()) return false;

		if (luggage.world != null) {
			List<ItemEntity> items = luggage.world.getEntitiesWithinAABB(ItemEntity.class, luggage.getEntityBoundingBox().grow(10));
			ItemEntity closest = null;
			double closestDistance = Double.MAX_VALUE;
			for (ItemEntity item : items) {
				if (!item.isDead && item.onGround) {
					double dist = item.getDistance(luggage);
					if (dist < closestDistance
							&& luggage.canConsumeStackPartially(item.getItem())
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
		pathFinder.clearPath();
		targetItem = null;
	}

	@Override
	public boolean shouldContinueExecuting() {
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
		if (!luggage.world.isRemote) {
			if (targetItem != null && luggage.getDistance(targetItem) < 1.0) {
				final ItemStack toConsume = targetItem.getItem();
				final ItemStack leftovers = ItemHandlerHelper.insertItem(luggage.getChestInventory().getHandler(), toConsume, false);
				if (leftovers.getCount() < toConsume.getCount()) {
					if (luggage.lastSound > 15) {
						boolean isFood = toConsume.getItemUseAction() == UseAction.EAT;
						luggage.playSound(isFood? OpenBlocks.Sounds.ENTITY_LUGGAGE_EAT_FOOD : OpenBlocks.Sounds.ENTITY_LUGGAGE_EAT_ITEM,
								0.5f, 1.0f + (luggage.world.rand.nextFloat() * 0.2f));
						luggage.lastSound = 0;
					}

					ItemUtils.setEntityItemStack(targetItem, leftovers);
				}
			}
		}
	}
}
