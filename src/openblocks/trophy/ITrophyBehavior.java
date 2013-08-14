package openblocks.trophy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public interface ITrophyBehavior {
	public void executeActivateBehavior(TileEntity tile, EntityPlayer player);
	public void executeTickBehavior(TileEntity tile);
}
