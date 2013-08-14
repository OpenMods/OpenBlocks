package openblocks.trophy;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.tileentity.TileEntity;

public class SkeletonBehavior implements ITrophyBehavior {

	@Override
	public void executeActivateBehavior(TileEntity tile, EntityPlayer player) {
		double midX = tile.xCoord + 0.5;
		double midZ = tile.zCoord + 0.5;
		EntityArrow entityarrow = new EntityArrow(tile.worldObj, midX, tile.yCoord + 1, midZ);
		entityarrow.setDamage(0.1);
		entityarrow.setThrowableHeading(tile.worldObj.rand.nextInt(10) - 5, 40, tile.worldObj.rand.nextInt(10) - 5, 1.0f, 6.0f);
		player.playSound("random.bow", 1.0F, 1.0F / (tile.worldObj.rand.nextFloat() * 0.4F + 0.8F));
        tile.worldObj.spawnEntityInWorld(entityarrow);
	}

	@Override
	public void executeTickBehavior(TileEntity tile) {
		// TODO Auto-generated method stub
		
	}

}
