package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.common.tileentity.TileEntityBearTrap;
import openmods.block.BlockRotationMode;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockBearTrap extends OpenBlock {

	public BlockBearTrap() {
		super(Material.rock);
		setRotationMode(BlockRotationMode.TWO_DIRECTIONS);
		setRenderMode(RenderMode.TESR_ONLY);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		TileEntityBearTrap te = getTileEntity(world, x, y, z, TileEntityBearTrap.class);
		if (te != null) te.onEntityCollided(entity);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int x, int y, int z) {
		return AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0, y + 0.1, z + 1.0);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		setBlockBounds(0.1f, 0, 0.1f, 0.9f, 0.4f, 0.9f);
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return isOnTopOfSolidBlock(world, x, y, z, side);
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
		TileEntityBearTrap tile = getTileEntity(world, x, y, z, TileEntityBearTrap.class);
		return tile != null? tile.getComparatorLevel() : 0;
	}
}
