package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityDigitalFuse;

public class BlockDigitalFuse extends OpenBlock {

	public static class Icons {
		public static Icon[] topIcons = new Icon[8];
		public static Icon side;
	}

	public BlockDigitalFuse() {
		super(Config.blockDigitalFuseId, Material.circuits);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
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
	public void registerIcons(IconRegister registry) {
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

	public Icon getUnrotatedTexture(ForgeDirection direction, IBlockAccess world, int x, int y, int z) {
		if (direction.equals(ForgeDirection.UP)) {

			TileEntityDigitalFuse tile = getTileEntity(world, x, y, z, TileEntityDigitalFuse.class);

			if (tile != null) { return Icons.topIcons[tile.getSignalFlags()]; }

		}
		return super.getUnrotatedTexture(direction, world, x, y, z);
	}

}
