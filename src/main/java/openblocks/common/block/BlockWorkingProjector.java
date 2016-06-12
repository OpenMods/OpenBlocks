package openblocks.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openblocks.Config;
import openblocks.OpenBlocks;

public class BlockWorkingProjector extends BlockProjector {

	private static final int MIN_LIGHT_LEVEL = 0;
	private static final int MAX_LIGHT_LEVEL = 15;
	private static final String CONE_ICON = "openblocks:projector_cone" + (Config.renderHoloGrid? "_grid" : "");

	@SideOnly(Side.CLIENT)
	private IIcon coneIcon;

	public BlockWorkingProjector() {
		super();
	}

	@Override
	public int getLightValue() {
		return Math.min(Math.max(MIN_LIGHT_LEVEL, Config.projectorLightLevelValue), MAX_LIGHT_LEVEL);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void getSubBlocks(final Item item, final CreativeTabs tab, final List list) {
		// Don't add anything: We don't want it to show blocks
	}

	@Override
	public Item getItemDropped(final int metadata, final Random random, final int fortune) {
		return Item.getItemFromBlock(OpenBlocks.Blocks.projector);
	}

	@Override
	public Item getItem(World world, int x, int y, int z) {
		return getItemDropped(0, null, 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister registry) {
		super.registerBlockIcons(registry);
		this.coneIcon = registry.registerIcon(CONE_ICON);
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		return side >= 0? super.getIcon(world, x, y, z, side) : this.coneIcon;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean canRenderInPass(int pass) {
		return true;
	}
}
