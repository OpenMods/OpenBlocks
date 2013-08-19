package openblocks.common.entity.ai;

import java.util.List;

import openblocks.common.entity.EntityLuggage;
import openblocks.utils.InventoryUtils;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.AxisAlignedBB;

public class EntityAICollectItem extends EntityAIBase {

	private EntityLuggage luggage = null;

    private PathNavigate pathFinder;
    
    private EntityItem targetItem = null;
	
	public EntityAICollectItem(EntityLuggage luggage) {
		this.luggage = luggage;
		this.pathFinder = luggage.getNavigator();
        this.setMutexBits(3);
	}
	
	@Override
	public boolean shouldExecute() {
		if (!pathFinder.noPath()) {
	    	return false;
	    }
	    if (luggage.worldObj != null) {
		    List<EntityItem> items = luggage.worldObj.getEntitiesWithinAABB(
		    				EntityItem.class,
		    				AxisAlignedBB.getAABBPool().getAABB(
		    						luggage.posX-1, luggage.posY-1, luggage.posZ-1,
		    						luggage.posX+1, luggage.posY+1, luggage.posZ+1).expand(10.0, 10.0, 10.0));
			for (EntityItem item : items) {
				if (!item.isDead && item.onGround) {
					targetItem = item;
					return true;
				}
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
		return luggage.isEntityAlive() && !pathFinder.noPath() && !targetItem.isDead;
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
				InventoryUtils.insertItemIntoInventory(luggage.getInventory(), stack);
				if (stack.stackSize == 0) {
					targetItem.setDead();
				}
			}
		}
	}
}
