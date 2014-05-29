package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockGuide extends OpenBlock {

	public static class Icons {
		public static IIcon ends;
		public static IIcon side;
	}

	public BlockGuide() {
		super(Material.rock);
	}

	@Override
	public boolean isOpaqueCube() {
		return true;
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

	@Override
	public boolean useTESRForInventory() {
		return false;
	}

	@Override
	public void registerBlockIcons(IIconRegister registry) {
		Icons.ends = registry.registerIcon("openblocks:guide");
		Icons.side = registry.registerIcon("openblocks:guide_side");

		setTexture(ForgeDirection.UP, Icons.ends);
		setTexture(ForgeDirection.DOWN, Icons.ends);
		setTexture(ForgeDirection.EAST, Icons.side);
		setTexture(ForgeDirection.WEST, Icons.side);
		setTexture(ForgeDirection.NORTH, Icons.side);
		setTexture(ForgeDirection.SOUTH, Icons.side);
		setDefaultTexture(Icons.ends);
	}

	@Override
	public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
		return false;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return false;
	}

}
