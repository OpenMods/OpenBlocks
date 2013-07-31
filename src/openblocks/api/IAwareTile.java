package openblocks.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.ForgeDirection;

public interface IAwareTile {
	public void onBlockBroken();

	public void onBlockAdded();

	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ);

	public void onNeighbourChanged(int blockId);

	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, float hitX, float hitY, float hitZ);
}
