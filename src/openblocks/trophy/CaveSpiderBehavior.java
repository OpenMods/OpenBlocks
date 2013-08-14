package openblocks.trophy;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;

public class CaveSpiderBehavior implements ITrophyBehavior {

	@Override
	public void executeActivateBehavior(TileEntity tile, EntityPlayer player) {
	    player.addPotionEffect(new PotionEffect(Potion.poison.id, 200, 3));
	}

	@Override
	public void executeTickBehavior(TileEntity tile) {
		// TODO Auto-generated method stub
		
	}

}
