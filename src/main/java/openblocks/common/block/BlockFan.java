package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockFan extends OpenBlock {

	public BlockFan() {
		super(Material.circuits);
		setBlockBounds(0.2f, 0, 0.2f, 0.8f, 1.0f, 0.8f);
		setRenderMode(RenderMode.TESR_ONLY);
		/* No rotation, handled by TE */
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return isOnTopOfSolidBlock(world, x, y, z, side);
	}
}
