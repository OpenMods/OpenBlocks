package openblocks.common.entity;

import cpw.mods.fml.client.FMLClientHandler;
import openblocks.client.ClientTickHandler;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3Pool;
import net.minecraft.world.World;

public class EntityAIDragPlayer extends EntityAIBase {

    World worldObj;
    EntityGhost attacker;
    EntityPlayer entityTarget;
    
    
	public EntityAIDragPlayer(EntityGhost entityGhost, float f) {
        this.attacker = entityGhost;
        this.worldObj = entityGhost.worldObj;
        this.setMutexBits(3);
	}


    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
    	
        EntityLiving entityliving = this.attacker.getAttackTarget();
        if (entityliving == null) {
            return false;
        }
        
        if (!(entityliving instanceof EntityPlayer)) { 
        	return false;
        }
        Vec3Pool pool = worldObj.getWorldVec3Pool();
        Vec3 myPos = pool.getVecFromPool(attacker.posX, attacker.posY, attacker.posZ);
        Vec3 theirPos = pool.getVecFromPool(entityliving.posX, entityliving.posY, entityliving.posZ);

        if (myPos.distanceTo(theirPos) > 4) {
        	return false;
        }
        this.entityTarget = (EntityPlayer)entityliving;
        
        return true;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
    	return !(entityTarget.isDead || attacker.isDead);
    }

    /**
     * Determine if this AI Task is interruptible by a higher (= lower value) priority task.
     */
    public boolean isInterruptible() {
        return !entityTarget.isDead;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
    	System.out.println("Start executing");
    	this.attacker.setDragPlayer(entityTarget);
    }

    /**
     * Resets the task
     */
    public void resetTask() {
    	System.out.println("Reset player");
    	this.attacker.setDragPlayer(null);
    }

    /**
     * Updates the task
     */
    public void updateTask() {

    	Vec3Pool pool = worldObj.getWorldVec3Pool();
        Vec3 myPos = pool.getVecFromPool(attacker.posX, attacker.posY, attacker.posZ);

        double yawRad = (attacker.rotationYawHead-30 / 180) * Math.PI;
        double x = Math.cos(yawRad);
        double z = Math.sin(yawRad);
        double mag = Math.sqrt(x * x + z * z);
        double cx = (x / mag) * 2;
        double cz = (z / mag) * 2;
        System.out.println(cx);
        
    	entityTarget.setPositionAndUpdate(attacker.posX + cx, attacker.posY, attacker.posZ + cz);
    	
    	entityTarget.getLookHelper().setLookPositionWithEntity(attacker, 30.0F, 30.0F);
    }
}
