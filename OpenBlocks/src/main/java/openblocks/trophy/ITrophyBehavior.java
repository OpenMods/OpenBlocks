package openblocks.trophy;

import net.minecraft.entity.player.EntityPlayer;
import openblocks.common.tileentity.TileEntityTrophy;

public interface ITrophyBehavior {
	int executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player);

	default void executeTickBehavior(TileEntityTrophy tile) {
	}
}
