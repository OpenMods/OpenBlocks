package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openblocks.Config;
import openmods.api.IActivateAwareTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockProjector extends OpenBlock {

	@SideOnly(Side.CLIENT)
	private Icon sideIcon;

	public BlockProjector() {
		super(Config.blockProjectorId, Material.iron);
		setBlockBounds(0, 0, 0, 1, 0.5f, 1);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof IActivateAwareTile) return ((IActivateAwareTile)te).onBlockActivated(player, side, hitX, hitY, hitZ);
		return false;
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
	public void registerIcons(IconRegister registry) {
		sideIcon = registry.registerIcon("stone_slab_side");
		blockIcon = registry.registerIcon("stone_slab_top");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
		return (side < 2)? blockIcon : sideIcon;
	}
}
