package openblocks.common.block;

import net.minecraft.block.material.Material;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockBlockPlacer extends OpenBlock.SixDirections {

	public BlockBlockPlacer() {
		super(Material.ROCK);
	}

}
