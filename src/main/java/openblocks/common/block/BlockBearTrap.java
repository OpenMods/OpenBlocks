package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntityBearTrap;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockBearTrap extends OpenBlock.TwoDirections {

	public BlockBearTrap() {
		super(Material.rock);
		setBlockBounds(0.1f, 0, 0.1f, 0.9f, 0.4f, 0.9f);
	}

	@Override
	public int getRenderType() {
		return 2; // TESR only
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity) {
		TileEntityBearTrap te = getTileEntity(world, pos, TileEntityBearTrap.class);
		if (te != null) te.onEntityCollided(entity);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isFullCube() {
		return false;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		return isOnTopOfSolidBlock(world, pos, side);
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, BlockPos pos) {
		TileEntityBearTrap tile = getTileEntity(world, pos, TileEntityBearTrap.class);
		return tile != null? tile.getComparatorLevel() : 0;
	}
}
