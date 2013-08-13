package openblocks.trophy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public interface ITrophyBehavior {
	public void execute(TileEntity tile, EntityPlayer player);
}
