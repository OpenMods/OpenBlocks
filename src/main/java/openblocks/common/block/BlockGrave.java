package openblocks.common.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockAccess;
import openmods.block.BlockRotationMode;

public class BlockGrave extends OpenBlock {

	public BlockGrave() {
		super(Material.ground);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
		setBlockBounds(0, 0, 0, 1f, 0.2f, 1f);
		setResistance(2000.0F);
		setRenderMode(RenderMode.TESR_ONLY);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int quantityDropped(Random rand) {
		return 0;
	}

	@Override
	public boolean canRotateWithTool() {
		return false;
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
		return false;
	}

}
