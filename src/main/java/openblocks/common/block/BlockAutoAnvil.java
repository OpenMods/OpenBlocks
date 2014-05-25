package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockAutoAnvil extends OpenBlock {

	public BlockAutoAnvil() {
		super(Material.anvil);
		setStepSound(soundAnvilFootstep);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
		setInventoryRenderRotation(ForgeDirection.NORTH);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return false;
	}

	@Override
	public boolean shouldRenderBlock() {
		return false;
	}

}
