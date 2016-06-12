package openblocks.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityProjector;
import openmods.utils.BlockNotifyFlags;

public class BlockProjector extends OpenBlock {

	private static final float SLAB_HEIGHT = 0.5F;

	private static boolean changingState;

	@SideOnly(Side.CLIENT)
	private IIcon sideIcon;

	public BlockProjector() {
		super(Material.iron);
		setBlockBounds(0, 0, 0, 1, SLAB_HEIGHT, 1);
		setRenderMode(RenderMode.BOTH);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isBlockNormalCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(final IIconRegister registry) {
		sideIcon = registry.registerIcon("stone_slab_side");
		blockIcon = registry.registerIcon("stone_slab_top");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		return (side < 2)? blockIcon : sideIcon;
	}

	@Override
	public void breakBlock(final World world, final int x, final int y, final int z, final Block block, final int meta) {
		if (!changingState) super.breakBlock(world, x, y, z, block, meta);
	}

	public static void update(final boolean lit, final World world, final int x, final int y, final int z) {
		final int meta = world.getBlockMetadata(x, y, z);
		if (getTileEntity(world, x, y, z, TileEntityProjector.class) == null) return;
		changingState = true;
		final Block block = lit && Config.litWhenDisplayingMap? OpenBlocks.Blocks.workingProjector : OpenBlocks.Blocks.projector;
		world.setBlock(x, y, z, block, meta, BlockNotifyFlags.ALL);
		changingState = false;
	}
}
