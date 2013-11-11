package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityBigButton;

public class BlockBigButton extends OpenBlock {

	public BlockBigButton() {
		super(Config.blockBigButton, Material.circuits);
		setupBlock(this, "bigbutton", TileEntityBigButton.class);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
		setPlacementMode(BlockPlacementMode.SURFACE);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return (side != ForgeDirection.UP && side != ForgeDirection.DOWN)
				&& isNeighborBlockSolid(world, x, y, z, side);
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

		ForgeDirection direction = tile.getRotation();

		boolean pressed = tile.isButtonActive();

		switch (direction) {
			case EAST:
				setBlockBounds(pressed? 0.9375f : 0.875f, 0.0625f, 0.0625f, 1.0f, 0.9375f, 0.9375f);
				break;
			case WEST:
				setBlockBounds(0, 0.0625f, 0.0625f, pressed? 0.0625f : 0.125f, 0.9375f, 0.9375f);
				break;
			case NORTH:
				setBlockBounds(0.0625f, 0.0625f, 0, 0.9375f, 0.9375f, pressed? 0.0625f : 0.125f);
				break;
			case SOUTH:
				setBlockBounds(0.0625f, 0.0625f, pressed? 0.9375f : 0.875f, 0.9375f, 0.9375f, 1f);
				break;
			default:
				setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		}
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
		return (direction == button.getRotation() && button.isButtonActive())? 15 : 0;
	}
}
