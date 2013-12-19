package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import openblocks.Config;
import openblocks.common.tileentity.TileEntitySky;
import openmods.utils.render.RenderUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSky extends OpenBlock {

	public BlockSky() {
		super(Config.blockSkyId, Material.iron);
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
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister registry) {
		blockIcon = registry.registerIcon("openblocks:sky_inactive");
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor() {
		// randomness more or less intended
		return RenderUtils.getFogColor().getColor();
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		TileEntitySky te = getTileEntity(world, x, y, z, TileEntitySky.class);
		return (te != null && te.isPowered())? AxisAlignedBB.getAABBPool().getAABB(0, 0, 0, 0, 0, 0) : super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

}
