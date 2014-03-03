package openblocks.enchantments.flimflams;

import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import openblocks.api.IAttackFlimFlam;

public class TeleportFlimFlam implements IAttackFlimFlam {

	@Override
	public boolean execute(EntityPlayer attacker, EntityPlayer target, FlimFlammer flimFlammers) {
		final World world = target.worldObj;
		if (world.isRemote) return true;
		
		// lols
		if (flimFlammers == FlimFlammer.ATTACKER) {
			
			EntityEnderPearl e = new EntityEnderPearl(world, target);
			e.setPosition(target.posX, target.posY + 1, target.posZ);
			e.motionX = world.rand.nextGaussian();
			e.motionY = 0.5;
			e.motionZ = world.rand.nextGaussian();
			world.spawnEntityInWorld(e);
			
			return true;
			
		// If the defender has flimflam too, swap their positions!	
		} else if (flimFlammers == FlimFlammer.BOTH) {
			
			double x = target.posX;
			double y = target.posY;
			double z = target.posZ;
			float pitch = target.rotationPitch;
			float yaw = target.rotationYaw;
			target.rotationPitch = target.prevRotationPitch = attacker.rotationPitch;
			target.rotationYaw = target.prevRotationYaw = attacker.rotationYaw;
			target.setPositionAndUpdate(attacker.posX, attacker.posY, attacker.posZ);
			attacker.setPositionAndUpdate(x, y, z);
			attacker.rotationPitch = attacker.prevRotationPitch = pitch;
			attacker.rotationYaw = attacker.prevRotationYaw = yaw;
			
			return true;
		}
		
		return false;
	}

}
