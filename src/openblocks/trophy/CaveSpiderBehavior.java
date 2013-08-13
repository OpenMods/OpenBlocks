package openblocks.trophy;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;

public class CaveSpiderBehavior implements ITrophyBehavior {

	@Override
	public void execute(TileEntity tile, EntityPlayer player) {
	    player.addPotionEffect(new PotionEffect(Potion.poison.id, 200, 3));
	}

}
