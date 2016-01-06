package openblocks.common.block;

import net.minecraft.block.material.Material;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockBlockBreaker extends OpenBlock.SixDirections {

	public BlockBlockBreaker() {
		super(Material.rock);
		setPlacementMode(BlockPlacementMode.ENTITY_ANGLE);
	}

	// TODO 1.8.9 active texture
}
