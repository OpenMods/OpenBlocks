package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityCannon;
import openmods.common.block.OpenBlock;

public class BlockCannon extends OpenBlock {

	public BlockCannon() {
		super(Config.blockCannonId, Material.ground);
		setupBlock(this, "cannon", TileEntityCannon.class);
		setBlockBounds(0.3f, 0, 0.3f, 0.6f, 0.7f, 0.7f);
		/* I don't think this should rotate */
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean shouldRenderBlock() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return isOnTopOfSolidBlock(world, x, y, z, side);
	}
}
