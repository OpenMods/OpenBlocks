package openblocks.trophy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import openblocks.common.tileentity.TileEntityTrophy;

public class CaveSpiderBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		player.addPotionEffect(new PotionEffect(Potion.poison.id, 200, 3));
		return 0;
	}

	@Override
	public void executeTickBehavior(TileEntityTrophy tile) {}

}
