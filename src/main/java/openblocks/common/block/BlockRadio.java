package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import openblocks.Config;

public class BlockRadio extends OpenBlock {

	public Icon iconFront;
	public Icon iconBack;
	public Icon iconSide;
	public Icon iconTop;
	public Icon iconBottom;
	public Icon iconInside;

	public BlockRadio() {
		super(Config.blockRadioId, Material.ground);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

	@Override
	public void registerIcons(IconRegister registry) {
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
