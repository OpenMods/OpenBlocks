package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class BlockRadio extends OpenBlock {

	public IIcon iconFront;
	public IIcon iconBack;
	public IIcon iconSide;
	public IIcon iconTop;
	public IIcon iconBottom;
	public IIcon iconInside;

	public BlockRadio() {
		super(Material.wood);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

	@Override
	public void registerBlockIcons(IIconRegister registry) {
		iconFront = registry.registerIcon("openblocks:radio_front");
		iconBack = registry.registerIcon("openblocks:radio_back");
		iconSide = registry.registerIcon("openblocks:radio_side");
		iconTop = registry.registerIcon("openblocks:radio_top");
		iconBottom = registry.registerIcon("openblocks:radio_bottom");
		iconInside = registry.registerIcon("openblocks:radio_inside");
		blockIcon = iconFront;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
}
