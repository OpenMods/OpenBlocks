package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.common.tileentity.TileEntityBigButton;
import openmods.block.BlockRotationMode;
import openmods.geometry.BlockSpaceTransform;
import openmods.geometry.Orientation;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockBigButton extends OpenBlock {

	public BlockBigButton() {
		super(Material.circuits);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
		setPlacementMode(BlockPlacementMode.SURFACE);
	}

	@Override
	public boolean canPlaceBlock(World world, EntityPlayer player, ItemStack stack, int x, int y, int z, ForgeDirection sideDir, Orientation blockDirection, float hitX, float hitY, float hitZ, int newMeta) {
		return super.canPlaceBlock(world, player, stack, x, y, z, sideDir, blockDirection, hitX, hitY, hitZ, newMeta) && isNeighborBlockSolid(world, x, y, z, blockDirection.south());
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
	public boolean isBlockSolid(IBlockAccess world, int x, int y, int z, int side) {
		return false;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {

		TileEntityBigButton tile = getTileEntity(world, x, y, z, TileEntityBigButton.class);

		if (tile == null) { return; }

		boolean pressed = tile.isButtonActive();
		final Orientation orientation = tile.getOrientation();

		final AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(0.0625, 0.0625, pressed? 0.9375 : 0.8750, 0.9375, 0.9375, 1.0);
		final AxisAlignedBB rotatedAabb = BlockSpaceTransform.instance.mapBlockToWorld(orientation, aabb);
		setBlockBounds(rotatedAabb);
	}

	@Override
	public void setBlockBoundsForItemRender() {
		setBlockBounds(0.0625f, 0.0625f, 0.4f, 0.9375f, 0.9375f, 0.525f);
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
		TileEntityBigButton te = getTileEntity(world, x, y, z, TileEntityBigButton.class);
		return te != null && te.isButtonActive()? 15 : 0;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
		ForgeDirection direction = ForgeDirection.getOrientation(side).getOpposite();
		TileEntityBigButton button = getTileEntity(world, x, y, z, TileEntityBigButton.class);
		return (button != null && direction == button.getOrientation().south() && button.isButtonActive())? 15 : 0;
	}

	@Override
	public boolean canRotateWithTool() {
		return false;
	}
}
