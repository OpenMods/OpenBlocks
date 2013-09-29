package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityBlockPlacer;
import openblocks.common.tileentity.TileEntityItemDropper;

/**
 * Created with IntelliJ IDEA.
 * User: Aleksander
 * Date: 28.09.13
 * Time: 23:26
 * To change this template use File | Settings | File Templates.
 */
public class ContainerItemDropper extends ContainerInventory<TileEntityItemDropper> {
    public ContainerItemDropper(IInventory playerInventory, TileEntityItemDropper itemDropper) {
        super(playerInventory, itemDropper);
        addInventoryGrid(62, 18, 3);
        addPlayerInventorySlots(85);
    }
}
