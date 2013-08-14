package openblocks.trophy;

import openblocks.common.tileentity.TileEntityTrophy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public interface ITrophyBehavior {
	public void executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player);
	public void executeTickBehavior(TileEntityTrophy tile);
}
