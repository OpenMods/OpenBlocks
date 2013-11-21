package openmods.common.api;

import net.minecraft.entity.player.EntityPlayer;

//TODO: Refractor this system to allow moving to core
public interface IHasGui {
	public Object getServerGui(EntityPlayer player);

	public Object getClientGui(EntityPlayer player);
}
