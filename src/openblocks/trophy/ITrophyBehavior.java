package openblocks.trophy;

import net.minecraft.entity.player.EntityPlayer;
import openblocks.common.tileentity.TileEntityTrophy;

public interface ITrophyBehavior {
	public void executeActivateBehavior(TileEntityTrophy tile,
			EntityPlayer player);

	public void executeTickBehavior(TileEntityTrophy tile);
}
