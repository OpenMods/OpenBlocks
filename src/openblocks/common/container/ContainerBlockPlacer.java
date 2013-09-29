package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityBlockPlacer;

/**
 * Created with IntelliJ IDEA.
 * User: Aleksander
 * Date: 28.09.13
 * Time: 23:26
 * To change this template use File | Settings | File Templates.
 */
public class ContainerBlockPlacer extends ContainerInventory<TileEntityBlockPlacer> {
    public ContainerBlockPlacer(IInventory playerInventory, TileEntityBlockPlacer blockPlacer) {
        super(playerInventory, blockPlacer);
        addInventoryGrid(62, 18, 3);
        addPlayerInventorySlots(85);
    }
}
