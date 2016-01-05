package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.api.IElevatorBlock;
import openblocks.common.tileentity.TileEntityElevatorRotating;
import openmods.block.BlockRotationMode;
import openmods.colors.ColorUtils;

public class BlockElevatorRotating extends OpenBlock implements IElevatorBlock {

	public BlockElevatorRotating() {
		super(Material.rock);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister registry) {
		super.registerBlockIcons(registry);
		setTexture(ForgeDirection.UP, registry.registerIcon("openblocks:elevator_rot"));
	}

	private static ColorUtils.ColorMeta getColorMeta(IBlockAccess world, int x, int y, int z) {
		TileEntityElevatorRotating te = getTileEntity(world, x, y, z, TileEntityElevatorRotating.class);
		return te != null? te.getColor() : ColorUtils.ColorMeta.WHITE;
	}

	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		return getColorMeta(world, x, y, z).rgb;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int damage) {
		return ColorUtils.vanillaBlockToColor(damage).rgb;
	}

	@Override
	public boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int colour) {
		if (world.isRemote) return false;

		final TileEntityElevatorRotating te = getTileEntity(world, x, y, z, TileEntityElevatorRotating.class);

		if (te != null) {
			ColorUtils.ColorMeta current = te.getColor();
			ColorUtils.ColorMeta next = ColorUtils.vanillaBlockToColor(colour);
			if (current != next) {
				te.setColor(next);
				return true;
			}
		}

		return false;
	}

	@Override
	public int getColor(World world, int x, int y, int z) {
		return getColorMeta(world, x, y, z).vanillaBlockId;
	}

	@Override
	public PlayerRotation getRotation(World world, int x, int y, int z) {
		final int meta = world.getBlockMetadata(x, y, z);
		final ForgeDirection rot = getOrientation(meta).north();
		switch (rot) {
			case NORTH:
				return PlayerRotation.NORTH;
			case SOUTH:
				return PlayerRotation.SOUTH;
			case WEST:
				return PlayerRotation.WEST;
			case EAST:
				return PlayerRotation.EAST;
			default:
				return PlayerRotation.NONE;
		}
	}

}
