package openblocks.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.IBlockAccess;
import openblocks.Config;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCanvasGlass extends BlockCanvas {

	public BlockCanvasGlass() {
		super(Config.blockCanvasGlassId, Material.glass);
		setStepSound(soundGlassFootstep);
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
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		int i1 = world.getBlockId(x, y, z);
		if (i1 == this.blockID || i1 == Block.glass.blockID) { return false; }
		return super.shouldSideBeRendered(world, x, y, z, side);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void getSubBlocks(int par1, CreativeTabs creativeTabs, List list) {}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister registry) {}
}
