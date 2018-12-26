package openblocks.common.entity.ai;

import java.util.List;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraftforge.items.ItemHandlerHelper;
import openblocks.OpenBlocks;
import openblocks.common.entity.EntityLuggage;
import openmods.utils.ItemUtils;

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
		if (!pathFinder.noPath()) return false;

		if (luggage.world != null) {
			List<EntityItem> items = luggage.world.getEntitiesWithinAABB(EntityItem.class, luggage.getEntityBoundingBox().grow(10));
			EntityItem closest = null;
			double closestDistance = Double.MAX_VALUE;
			for (EntityItem item : items) {
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
						boolean isFood = toConsume.getItemUseAction() == EnumAction.EAT;
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
