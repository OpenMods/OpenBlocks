package openblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import openmods.infobook.BookDocumentation;
import openmods.utils.BlockNotifyFlags;
import openmods.utils.render.RenderUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@BookDocumentation(customName = "sky.normal")
public class BlockSky extends OpenBlock {

	public BlockSky() {
		super(Material.iron);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister registry) {
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
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if (!world.isRemote) {
			int meta = world.getBlockMetadata(x, y, z);
			int isPowered = world.isBlockIndirectlyGettingPowered(x, y, z)? 2 : 0;
			int isInverted = meta & 1;
			world.setBlockMetadataWithNotify(x, y, z, isPowered | isInverted, BlockNotifyFlags.ALL);
		}
	}

	public static boolean isActive(int meta) {
		boolean isPowered = (meta & 2) != 0;
		boolean isInverted = (meta & 1) != 0;
		return isPowered ^ isInverted;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return isActive(meta)? AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0) : super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

}
