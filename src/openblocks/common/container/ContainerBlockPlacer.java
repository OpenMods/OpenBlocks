package openblocks.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityBlockPlacer;

public class ContainerBlockPlacer extends ContainerInventory<TileEntityBlockPlacer> {
    public ContainerBlockPlacer(IInventory playerInventory, TileEntityBlockPlacer blockPlacer) {
        super(playerInventory, blockPlacer);
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
