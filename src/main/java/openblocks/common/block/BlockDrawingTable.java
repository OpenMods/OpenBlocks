package openblocks.common.block;

import net.minecraft.block.material.Material;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;

@BookDocumentation(hasVideo = true)
public class BlockDrawingTable extends OpenBlock.FourDirections {

	public BlockDrawingTable() {
		super(Material.WOOD);
	}

}
