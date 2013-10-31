package openblocks.common.block;

import net.minecraft.block.material.Material;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityAutoEnchantmentTable;

public class BlockAutoEnchantmentTable extends OpenBlock {

	public BlockAutoEnchantmentTable() {
		super(Config.blockAutoEnchantmentTableId, Material.ground);
		setupBlock(this, "autoenchantment", TileEntityAutoEnchantmentTable.class);
	}

}
