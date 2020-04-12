package openblocks.common.block;

import net.minecraft.block.material.Material;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockItemDropper extends OpenBlock.SixDirections {

	public BlockItemDropper() {
		super(Material.ROCK);
	}

}
