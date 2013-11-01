package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityXPDrain;

public class BlockXPDrain extends OpenBlock {

	private Icon sideIcon;
	
	public BlockXPDrain() {
		super(Config.blockXPDrainId, Material.glass);
		setupBlock(this, "xpdrain", TileEntityXPDrain.class);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
	}
	
	@Override
	public void registerIcons(IconRegister registry) {
		super.registerIcons(registry);
		sideIcon = registry.registerIcon("openblocks:xpdrain_side");
	}
	
	@Override
	public Icon getIcon(int side, int meta) {
		int dir = (meta & 0x3) + 2;
		if (dir == 4 || dir == 5) {
			return sideIcon;
		}
		return blockIcon;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getRenderType() {
		return 0;
	}
}
