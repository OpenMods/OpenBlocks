package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockProjector extends OpenBlock {

	@SideOnly(Side.CLIENT)
	private IIcon sideIcon;

	public BlockProjector() {
		super(Material.iron);
		setBlockBounds(0, 0, 0, 1, 0.5f, 1);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isBlockNormalCube(World world, int x, int y, int z) {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister registry) {
		sideIcon = registry.registerIcon("stone_slab_side");
		blockIcon = registry.registerIcon("stone_slab_top");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
		return (side < 2)? blockIcon : sideIcon;
	}
}
