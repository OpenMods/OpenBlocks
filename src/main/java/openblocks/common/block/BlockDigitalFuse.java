package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.common.tileentity.TileEntityDigitalFuse;

public class BlockDigitalFuse extends OpenBlock {

	public static class Icons {
		public static IIcon[] topIcons = new IIcon[8];
		public static IIcon side;
	}

	public BlockDigitalFuse() {
		super(Material.circuits);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

	@Override
	public boolean isBlockSolid(IBlockAccess world, int x, int y, int z, int side) {
		return true;
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
		TileEntityDigitalFuse te = getTileEntity(world, x, y, z, TileEntityDigitalFuse.class);

		if (te == null) return 0;

		if (side != te.getRotation().getOpposite().ordinal()) return 0;
		return te.isOutputtingPower()? 15 : 0;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
		return isProvidingWeakPower(world, x, y, z, side);
	}

	@Override
	public boolean useTESRForInventory() {
		return false;
	}

	@Override
	public void registerBlockIcons(IIconRegister registry) {
		for (int i = 0; i < 8; i++) {
			Icons.topIcons[i] = registry.registerIcon("openblocks:digitalfuse_" + i);
		}
		Icons.side = registry.registerIcon("openblocks:digitalfuse_side");
		setTexture(ForgeDirection.EAST, Icons.side);
		setTexture(ForgeDirection.WEST, Icons.side);
		setTexture(ForgeDirection.SOUTH, Icons.side);
		setTexture(ForgeDirection.NORTH, Icons.side);
		setTexture(ForgeDirection.UP, Icons.topIcons[0]);
		setTexture(ForgeDirection.DOWN, Icons.side);
		setDefaultTexture(Icons.topIcons[0]);
	}

	@Override
	public IIcon getUnrotatedTexture(ForgeDirection direction, IBlockAccess world, int x, int y, int z) {
		if (direction.equals(ForgeDirection.UP)) {

			TileEntityDigitalFuse tile = getTileEntity(world, x, y, z, TileEntityDigitalFuse.class);

			if (tile != null) { return Icons.topIcons[tile.getSignalFlags()]; }

		}
		return super.getUnrotatedTexture(direction, world, x, y, z);
	}

}
