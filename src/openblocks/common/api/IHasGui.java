package openblocks.common.api;

import net.minecraft.entity.player.EntityPlayer;

public interface IHasGui {
	public Object getServerGui(EntityPlayer player);

	public Object getClientGui(EntityPlayer player);
}
