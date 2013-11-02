package openblocks.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityBlockPlacer;
import openblocks.common.tileentity.TileEntityItemDropper;

public class ContainerItemDropper extends ContainerInventory<TileEntityItemDropper> {
    public ContainerItemDropper(IInventory playerInventory, TileEntityItemDropper itemDropper) {
        super(playerInventory, itemDropper);
        addInventoryGrid(62, 18, 3);
        addPlayerInventorySlots(85);
    }

	@Override
	public void onServerButtonClicked(EntityPlayer player, int buttonId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClientButtonClicked(int buttonId) {
		// TODO Auto-generated method stub
		
	}
}
