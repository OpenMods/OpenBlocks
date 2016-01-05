package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.common.tileentity.TileEntityXPShower;
import openmods.block.BlockRotationMode;
import openmods.geometry.BlockSpaceTransform;
import openmods.geometry.Orientation;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockXPShower extends OpenBlock {

	public BlockXPShower() {
		super(Material.rock);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
		setPlacementMode(BlockPlacementMode.SURFACE);
		setRenderMode(RenderMode.TESR_ONLY);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		return super.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		return super.getSelectedBoundingBoxFromPool(par1World, par2, par3, par4);
	}

	@Override
	public void setBoundsBasedOnOrientation(Orientation orientation) {
		final AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(7.0 / 16.0, 7.0 / 16.0, 0.0 / 16.0, 9.0 / 16.0, 9.0 / 16.0, 9.0 / 16.0);
		final AxisAlignedBB rotatedAabb = BlockSpaceTransform.instance.mapBlockToWorld(orientation, aabb);
		setBlockBounds(rotatedAabb);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		TileEntityXPShower tile = getTileEntity(world, x, y, z, TileEntityXPShower.class);
		if (tile != null) {
			Orientation orientation = tile.getOrientation();
			setBoundsBasedOnOrientation(orientation);
		}

	}
}
