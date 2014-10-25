package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockGoldenEgg extends OpenBlock {

	public BlockGoldenEgg() {
		super(Material.ground);
		setRenderMode(RenderMode.TESR_ONLY);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister registry) {
		blockIcon = registry.registerIcon("openblocks:egg");
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
}
