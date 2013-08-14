package openblocks.trophy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import openblocks.common.tileentity.TileEntityTrophy;

public class ChickenBehavior implements ITrophyBehavior {

	@Override
	public void executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		if (!tile.worldObj.isRemote) {
			if (tile.sinceLastActivate() > 5000) {
				player.playSound("mob.chicken.plop", 1.0F, (tile.worldObj.rand.nextFloat() - tile.worldObj.rand.nextFloat()) * 0.2F + 1.0F);
	            player.dropItem(Item.egg.itemID, 1);
	            tile.resetActivationTimer();
			}
		}
	}

	@Override
	public void executeTickBehavior(TileEntityTrophy tile) {
		// TODO Auto-generated method stub

	}

}
