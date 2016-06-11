package openblocks.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.common.tileentity.TileEntityBlockBreaker;
import openmods.block.BlockRotationMode;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockBlockBreaker extends OpenBlock {

	@SideOnly(Side.CLIENT)
	private static class Icons {
		public static IIcon top;
		public static IIcon top_active;
		public static IIcon bottom;
		public static IIcon side;
	}

	public BlockBlockBreaker() {
		super(Material.rock);
		setRotationMode(BlockRotationMode.SIX_DIRECTIONS);
		setPlacementMode(BlockPlacementMode.ENTITY_ANGLE);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister registry) {
		super.registerBlockIcons(registry);

		Icons.top = registry.registerIcon("openblocks:blockBreaker");
		Icons.top_active = registry.registerIcon("openblocks:blockBreaker_active");
		Icons.bottom = registry.registerIcon("openblocks:blockBreaker_bottom");
		Icons.side = registry.registerIcon("openblocks:blockBreaker_side");

		setTexture(ForgeDirection.UP, Icons.top);
		setTexture(ForgeDirection.DOWN, Icons.bottom);
		setTexture(ForgeDirection.EAST, Icons.side);
		setTexture(ForgeDirection.WEST, Icons.side);
		setTexture(ForgeDirection.NORTH, Icons.side);
		setTexture(ForgeDirection.SOUTH, Icons.side);
	}

	@Override
	public IIcon getUnrotatedTexture(ForgeDirection direction, IBlockAccess world, int x, int y, int z) {
		if (direction.equals(ForgeDirection.UP)) {
			TileEntityBlockBreaker tile = getTileEntity(world, x, y, z, TileEntityBlockBreaker.class);
			if (tile != null && tile.isActivated()) { return Icons.top_active; }
		}
		return super.getUnrotatedTexture(direction, world, x, y, z);
	}

}
