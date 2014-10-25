package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockAutoAnvil extends OpenBlock {

	public BlockAutoAnvil() {
		super(Material.anvil);
		setStepSound(soundTypeAnvil);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
		setInventoryRenderRotation(ForgeDirection.NORTH);
		setRenderMode(RenderMode.TESR_ONLY);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isBlockSolid(IBlockAccess world, int x, int y, int z, int side) {
		return false;
	}
}
