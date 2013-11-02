package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityXPDrain;
import openblocks.utils.BlockUtils;

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
		ForgeDirection dir = BlockUtils.getRotationFromMetadata(meta);
		return dir == ForgeDirection.WEST || dir == ForgeDirection.EAST? sideIcon : blockIcon;
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
