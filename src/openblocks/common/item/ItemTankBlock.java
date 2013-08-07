package openblocks.common.item;

import openblocks.OpenBlocks;
import openblocks.common.block.BlockTank;

public class ItemTankBlock extends ItemOpenBlock {

	public ItemTankBlock(int id) {
		super(id);
		BlockTank.itemId = id;
	}

}
