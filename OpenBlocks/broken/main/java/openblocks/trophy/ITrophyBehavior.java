package openblocks.trophy;

import net.minecraft.entity.player.PlayerEntity;
import openblocks.common.tileentity.TileEntityTrophy;

public interface ITrophyBehavior {
	int executeActivateBehavior(TileEntityTrophy tile, PlayerEntity player);

	default void executeTickBehavior(TileEntityTrophy tile) {
	}
}
