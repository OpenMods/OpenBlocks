package openblocks.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.Config;
import openmods.utils.ByteUtils;

public class BlockProjector extends OpenBlock {

	public static final int META_BIT_ACTIVE = 0;

	private static final float SLAB_HEIGHT = 0.5F;

	private static final int MIN_LIGHT_LEVEL = 0;
	private static final int MAX_LIGHT_LEVEL = 15;
	private static final String CONE_ICON = "openblocks:projector_cone" + (Config.renderHoloGrid? "_grid" : "");

	@SideOnly(Side.CLIENT)
	public IIcon coneIcon;

	@SideOnly(Side.CLIENT)
	private IIcon sideIcon;

	public BlockProjector() {
		super(Material.iron);
		setBlockBounds(0, 0, 0, 1, SLAB_HEIGHT, 1);
		setRenderMode(RenderMode.BOTH);
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		if (ByteUtils.get(world.getBlockMetadata(x, y, z), META_BIT_ACTIVE))
			return Math.min(Math.max(MIN_LIGHT_LEVEL, Config.projectorLightLevelValue), MAX_LIGHT_LEVEL);

		return 0;
	}

	@Override
	public int getLightValue() {
		return 1;
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
		this.sideIcon = registry.registerIcon("stone_slab_side");
		this.blockIcon = registry.registerIcon("stone_slab_top");
		this.coneIcon = registry.registerIcon(CONE_ICON);

		setTexture(ForgeDirection.NORTH, this.sideIcon);
		setTexture(ForgeDirection.SOUTH, this.sideIcon);
		setTexture(ForgeDirection.EAST, this.sideIcon);
		setTexture(ForgeDirection.WEST, this.sideIcon);
	}

	@Override
	public boolean canRenderInPass(int pass) {
		return true;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}
}
