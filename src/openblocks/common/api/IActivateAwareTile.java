package openblocks.common.api;

import net.minecraft.entity.player.EntityPlayer;

public interface IActivateAwareTile {
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ);
}
